package com.teams4.blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;


public class RegisterActivity extends AppCompatActivity {

    //Declare Views and Variables
    private ImageView img;
    private ProgressBar progressbar;
    private Intent intent;
    private Button btnGo, btnNew;
    private TextView txtSubtitle, txtLogo;
    private TextInputLayout name, username, email, phoneNo, password;
    private ActivityOptions options;
    private String val_fullNo, val_ccp, val_name, val_username, val_email, val_password, val_phoneNo;
    private CountryCodePicker ccp;
    String noWhiteSpace = "\\A\\w{4,20}\\z";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String passwordPattern = "^" +
            "(?=.*[a-zA-Z])" +      //any letter
            "(?=.*[@#$%^&+=])" +    //at least 1 special character
            "(?=\\S+$)" +           //no white spaces
            ".{4,}" +               //at least 4 characters
            "$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //required
        super.onCreate(savedInstanceState);

        initializeUI();
    }

    private Boolean validateName() {
        val_name = name.getEditText().getText().toString().trim();

        if (val_name.isEmpty()) {
            name.setError("Field cannot be empty");
            return false;
        } else {
            name.setError(null);
            name.setErrorEnabled(false);
            return true;
        }

    }

    private Boolean validateUserName() {
        val_username = username.getEditText().getText().toString().trim();

        if (val_username.isEmpty()) {
            username.setError("Field cannot be empty");
            return false;
        } else if (val_username.length() >= 15) {
            username.setError("Username too long");
            return false;
        }
        else if (!val_username.matches(noWhiteSpace)) {
            username.setError("White spaces are not allowed");
            return false;
        }
        else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }

    }

    private Boolean validateEmail() {
       val_email = email.getEditText().getText().toString().trim();

        if (val_email.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else if (!val_email.matches(emailPattern)) {

            email.setError("Invalid email address");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }

    }

    private Boolean validatePhoneNo() {
        val_phoneNo = phoneNo.getEditText().getText().toString().trim();

        if (val_phoneNo.isEmpty()) {
            phoneNo.setError("Field cannot be empty");
            return false;
        } else {
            phoneNo.setError(null);
            phoneNo.setErrorEnabled(false);
            return true;
        }

    }

    private Boolean validatePassword() {
        val_password = password.getEditText().getText().toString();

        if (val_password.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else if (!val_password.matches(passwordPattern)) {
            password.setError("Password is too weak");
            return false;

        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }

    }


    private void initializeUI() {
        //setup Layout
        setContentView(R.layout.activity_register);

        //setup Views
        img = findViewById(R.id.image_logo);
        txtLogo = findViewById(R.id.txt_name);
        txtSubtitle = findViewById(R.id.txt_signIn);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phoneNo = findViewById(R.id.phoneNo);
        password = findViewById(R.id.password);
        btnGo = findViewById(R.id.button_go);
        ccp = findViewById(R.id.ccp);
        progressbar = findViewById(R.id.progressBar);
        btnNew = findViewById(R.id.button_new);

        //set onclick listener
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //change Activity to LoginActivity
                sharedScreen();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent, options.toBundle());
                finish();
            }
        });

    }

    public void registerUser(View view)
    {

        if (!validateName() || !validateUserName() || !validateEmail() || !validatePhoneNo()|| !validatePassword() )
        {
            return;
        }


        val_ccp = ccp.getSelectedCountryCodeWithPlus();
        val_fullNo = String.format("%s%s", val_ccp,val_phoneNo);

        progressbar.setVisibility(View.VISIBLE);

        //Database Query
        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(val_fullNo);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "User already exists", Toast.LENGTH_SHORT).show();

                }else{

                    nextActivity();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressbar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





    }

    private void sharedScreen(){
        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<View, String>(img, "logo_image");
        pairs[1] = new Pair<View, String>(txtLogo, "logo_text");
        pairs[2] = new Pair<View, String>(txtSubtitle, "logo_Signtext");
        pairs[3] = new Pair<View, String>(btnGo, "logo_go");
        options = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this, pairs);


    }

    private void nextActivity(){
        intent = new Intent(RegisterActivity.this, CodeActivity.class);
        //pass data to next activity
        intent.putExtra("fullname", val_name);
        intent.putExtra("email", val_email);
        intent.putExtra("username", val_username);
        intent.putExtra("password", val_password);
        intent.putExtra("phoneNo", val_fullNo);
        intent.putExtra("toDo", "New Data");
        Log.i("Phone", val_fullNo);
        sharedScreen();
        startActivity(intent, options.toBundle() );
        finish();
    }

}
