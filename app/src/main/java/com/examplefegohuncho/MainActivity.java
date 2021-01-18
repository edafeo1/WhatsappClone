package com.examplefegohuncho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText mPhoneNumber, mcode;
    private Button msendverify;
    private String mverificationId;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Firebase
        FirebaseApp.initializeApp(this);

        //To check if an instance of the user already exists
        LoggedIn();

        String verificationId;
        mPhoneNumber = findViewById(R.id.phoneNumber);
        mcode = findViewById(R.id.code);
        msendverify = findViewById(R.id.verify);

        //Event Listener for sending codes
        msendverify.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(mverificationId!=null)
                    verifyNumberWithCode(mverificationId, mcode.getText().toString());
                else
                    beginNumberVerification();
            }

        });

        //Callback Functions with multiple sign in Actions
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken){
                super.onCodeSent(verificationId, forceResendingToken);

                mverificationId = verificationId;
                msendverify.setText("verify Code");
            }
        };
    }

    private void verifyNumberWithCode(String verificationId, String Code){
         PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, Code);
         signInWithPhoneCredentials(credential );
    }


    //Attempt to sign in to firebas with log in credentials
    private void signInWithPhoneCredentials(PhoneAuthCredential phoneAuthCredential){
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    LoggedIn();
                }
            }
        });
    }

    //Getting a user Instance IF Firebase is authenticated
    private void LoggedIn(){
        //Storing signed in credentials as Firebase User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
            return;
        }
    }


    private void beginNumberVerification() {
        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                        mPhoneNumber.getText().toString(),
                        60,
                        TimeUnit.SECONDS,
                        this,
                        mCallbacks );

    }

}