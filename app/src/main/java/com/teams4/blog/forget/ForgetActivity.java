package com.teams4.blog.forget;

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
import com.teams4.blog.CodeActivity;
import com.teams4.blog.LoginActivity;
import com.teams4.blog.R;
import com.teams4.blog.RegisterActivity;

public class ForgetActivity extends AppCompatActivity {

    private ImageView img;
    private ProgressBar progressBar;
    private CountryCodePicker ccp;
    private Button btnGo;
    private TextView txtSubtitle, txtLogo;
    private TextInputLayout name, username, email, phoneNo, password;
    private ActivityOptions options;
    private String val_ccp, val_fullNo, val_phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();
    }


    private void initializeUI() {
        //setup Layout
        setContentView(R.layout.activity_forget);

        //setup Views
        img = findViewById(R.id.image_logo);
        txtLogo = findViewById(R.id.txt_name);
        txtSubtitle = findViewById(R.id.txt_signIn);
        phoneNo = findViewById(R.id.phoneNo);
        btnGo = findViewById(R.id.button_go);
        ccp = findViewById(R.id.ccp);
        progressBar = findViewById(R.id.progressBar);
    }

    private void sharedScreen() {
        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<View, String>(img, "logo_image");
        pairs[1] = new Pair<View, String>(txtLogo, "logo_text");
        pairs[2] = new Pair<View, String>(txtSubtitle, "logo_Signtext");
        pairs[3] = new Pair<View, String>(btnGo, "logo_go");
        options = ActivityOptions.makeSceneTransitionAnimation(ForgetActivity.this, pairs);
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


    public void sendCode(View view) {

        if (!validatePhoneNo()) {
            return;
        }

        val_ccp = ccp.getSelectedCountryCodeWithPlus();
        val_fullNo = String.format("%s%s", val_ccp, val_phoneNo);
        Log.i("Phone", val_fullNo);

        progressBar.setVisibility(View.VISIBLE);

        //Database Query
        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(val_fullNo);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    Intent intent = new Intent(ForgetActivity.this, CodeActivity.class);
                    intent.putExtra("phoneNo", val_fullNo);
                    intent.putExtra("toDo", "Update Data");
                    sharedScreen();
                    startActivity(intent, options.toBundle());
                    finish();

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ForgetActivity.this, "No susch user exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ForgetActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
