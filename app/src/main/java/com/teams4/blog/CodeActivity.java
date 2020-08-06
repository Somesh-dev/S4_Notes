package com.teams4.blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teams4.blog.Databases.UserHelperClass;
import com.teams4.blog.forget.ResetPassword;

import java.util.concurrent.TimeUnit;

public class CodeActivity extends AppCompatActivity {

    private ImageView img;
    private PinView pin_view;
    private String phoneNo, codeBySystem, name, username, email, password, toDo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUI();
    }

    public void initializeUI() {

        setContentView(R.layout.activity_code);

        pin_view = findViewById(R.id.pin_view);

        //get data from previous activity
        phoneNo = getIntent().getStringExtra("phoneNo");
        name = getIntent().getStringExtra("fullname");
        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        toDo = getIntent().getStringExtra("toDo");


        //send OTP to the no
        sendVerificationCodeToUser(phoneNo);

        //inform the user to wait
        Toast.makeText(this, "Wait for the OTP", Toast.LENGTH_SHORT).show();

    }


    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;

                }

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();

                    if (code != null) {
                        pin_view.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(CodeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if (toDo.equals("New Data")) {
                                storeNewUserData();

                            } else {

                                updateOldUserData();

                            }

                            Toast.makeText(CodeActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();


                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(CodeActivity.this, "Verification not completed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void registerUserByOtp(View view) {

        String code = pin_view.getText().toString();
        if (!code.isEmpty()) {
            verifyCode(code);
        }
    }

    private void storeNewUserData() {

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Users");

        //instance of UserClass
        UserHelperClass addNewUser = new UserHelperClass(phoneNo, name, username, email, password);

        reference.child(phoneNo).setValue(addNewUser);

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();


    }

    private void updateOldUserData(){

        Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
        intent.putExtra("phoneNo", phoneNo);
        startActivity(intent);
        finish();
    }


}
