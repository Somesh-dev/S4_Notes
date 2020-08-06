package com.teams4.blog.forget;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teams4.blog.LoginActivity;
import com.teams4.blog.R;
import com.teams4.blog.checks.MobileInternetConnectionDetector;

public class ResetPassword extends AppCompatActivity {

    private MobileInternetConnectionDetector cd;
    private Boolean isConnectionExist = false;

    private TextInputLayout password, confirm_password;
    private String val_phoneNo, val_password, val_password_confirm;
    String passwordPattern = "^" +
            "(?=.*[a-zA-Z])" +      //any letter
            "(?=.*[@#$%^&+=])" +    //at least 1 special character
            "(?=\\S+$)" +           //no white spaces
            ".{4,}" +               //at least 4 characters
            "$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();

    }

    private void initializeUI(){

        setContentView(R.layout.activity_reset_password);

        //Intialize Views
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.password_confirm);

        //get data from Intent
        val_phoneNo = getIntent().getStringExtra("phoneNo");


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

    private Boolean validateConfirmPassword() {
        val_password_confirm = confirm_password.getEditText().getText().toString();

        if (!val_password_confirm.equals(val_password)){

            confirm_password.setError("Password doesn't matches");
            return false;
        }
        else{
            confirm_password.setError(null);
            confirm_password.setErrorEnabled(false);
            return true;
        }



    }

    //calling the function for onClick
    public void setNewPassword(View view) {


        if (!validatePassword() || !validateConfirmPassword()){
            return;
        }

        //update Data in Firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(val_phoneNo).child("password").setValue(val_password);

        Toast.makeText(this, "Password Updated", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();



    }



}
