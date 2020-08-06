package com.teams4.blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.teams4.blog.Databases.SessionManager;
import com.teams4.blog.checks.MobileInternetConnectionDetector;
import com.teams4.blog.forget.ForgetActivity;
import com.teams4.blog.forget.ResetPassword;

import java.net.InetAddress;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    //declare the Views and Variables
    private Button callSignUp;
    private MobileInternetConnectionDetector cd;
    private Boolean isConnectionExist = false;
    private CountryCodePicker ccp;
    private ActivityOptions options;
    private TextView txtSubtitle, txtLogo;
    private TextInputLayout password, phoneNo;
    private ImageView img;
    private Button btnGo, btnNew, btnForget;
    private ProgressBar progressBar;
    private CheckBox rememberMe;
    private EditText password_editText, phoneNo_editText;
    String val_ccp, val_fullNo, val_phoneNo, val_password, val_name, val_email, val_username;
    String passwordPattern = "^" +
            "(?=.*[a-zA-Z])" +      //any letter
            "(?=.*[@#$%^&+=])" +    //at least 1 special character
            "(?=\\S+$)" +           //no white spaces
            ".{4,}" +               //at least 4 characters
            "$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Required
        super.onCreate(savedInstanceState);

        initializeUI();

        //INTERNET check Dialog Box
        {

            cd = new MobileInternetConnectionDetector(getApplicationContext());

            // get Internet status
            isConnectionExist = cd.checkMobileInternetConn();

            // check for Internet status
            if (isConnectionExist) {
                // Internet Connection exists
            } else {
                // Internet connection doesn't exist
                showAlertDialog(LoginActivity.this, "No Internet Connection",
                        "Your device doesn't have mobile internet. Please turn your data on.", false);
            }
        }

        //check External Storage Permission
    }

    private void initializeUI() {

        //set the Layout
        setContentView(R.layout.activity_login);

        //Initialize the Views
        callSignUp = findViewById(R.id.btn_callSingUp);
        img = findViewById(R.id.image_logo);
        txtLogo = findViewById(R.id.txt_name);
        txtSubtitle = findViewById(R.id.txt_signIn);
        password = findViewById(R.id.password);
        phoneNo = findViewById(R.id.phoneNo);
        btnGo = findViewById(R.id.button_go);
        ccp = findViewById(R.id.ccp);
        progressBar = findViewById(R.id.progressBar);
        rememberMe = findViewById(R.id.checkBox);
        password_editText = findViewById(R.id.password_editText);
        phoneNo_editText = findViewById(R.id.phoneNo_editText);



        //onClick Listener attach
        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                sharedScreen();
                startActivity(intent, options.toBundle());
            }
        });

        SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_REMEMBERME);
        if(sessionManager.checkRememberMe()){
            HashMap<String, String> rememberMeDetails = sessionManager.RememberMeDetailFromSession();

            phoneNo_editText.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPHONENO));
            password_editText.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPASSWORD));
        }
    }


    //function to validate fields
    private boolean validateFields() {

        val_phoneNo = phoneNo.getEditText().getText().toString().trim();

        if (val_phoneNo.isEmpty()) {
            phoneNo.setError("Field cannot be empty");
            return false;
        } else {
            phoneNo.setError(null);
            phoneNo.setErrorEnabled(false);
        }


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
        }


        return true;

    }

    private void sharedScreen() {
        Pair[] pairs = new Pair[8];
        pairs[0] = new Pair<View, String>(img, "logo_image");
        pairs[1] = new Pair<View, String>(txtLogo, "logo_text");
        pairs[2] = new Pair<View, String>(txtSubtitle, "logo_Signtext");
        pairs[3] = new Pair<View, String>(phoneNo, "logo_phone");
        pairs[4] = new Pair<View, String>(password, "logo_pass");
        pairs[5] = new Pair<View, String>(btnGo, "logo_go");
        pairs[6] = new Pair<View, String>(callSignUp, "logo_new");
        pairs[7] = new Pair<View, String>(ccp, "logo_ccp");


        options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
    }


    public void login(View view) {

        //validating on click of the button
        if (!validateFields()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        val_ccp = ccp.getSelectedCountryCodeWithPlus();
        val_fullNo = String.format("%s%s", val_ccp, val_phoneNo);

        if(rememberMe.isChecked()){
            SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_REMEMBERME);
            sessionManager.createRememberMeSession(val_phoneNo, val_password);
        }

        //Database Query
        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(val_fullNo);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    phoneNo.setError(null);
                    phoneNo.setErrorEnabled(false);

                    String systemPassword = dataSnapshot.child(val_fullNo).child("password").getValue(String.class);
                    if (systemPassword.equals(val_password)) {
                        password.setError(null);
                        password.setErrorEnabled(false);

                        //Retrive data
                        val_phoneNo = dataSnapshot.child(val_fullNo).child("phoneNo").getValue(String.class);
                        val_name = dataSnapshot.child(val_fullNo).child("name").getValue(String.class);
                        val_username = dataSnapshot.child(val_fullNo).child("username").getValue(String.class);
                        val_email = dataSnapshot.child(val_fullNo).child("email").getValue(String.class);
                        val_password = dataSnapshot.child(val_fullNo).child("password").getValue(String.class);

                        //create session
//                        SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_USERSESSION);
//                        sessionManager.createLoginSession(val_name, val_username, val_phoneNo, val_email, val_password);



                        //show in Toast
                        Toast.makeText(LoginActivity.this, "Welcome "+val_name, Toast.LENGTH_SHORT).show();
                        //remove the progressbar
                        progressBar.setVisibility(View.GONE);

                        Intent intent = new Intent(getApplicationContext(), UserDashboard.class);
                        intent.putExtra("phoneNo",val_phoneNo );
                        intent.putExtra("Name",val_name );
                        startActivity(intent);
                        finish();


                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Password does not match!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "No susch user exist!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    public void forgetPassword1(View view) {

        //SharedScreen Animation
        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<View, String>(img, "logo_image");
        pairs[1] = new Pair<View, String>(txtLogo, "logo_text");
        pairs[2] = new Pair<View, String>(txtSubtitle, "logo_Signtext");
        pairs[3] = new Pair<View, String>(btnGo, "logo_go");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);

        //Intent calling
        Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
        startActivity(intent, options.toBundle());

    }


    //function for dialog box creation
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.fail : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


}
