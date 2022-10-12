package com.sacdev.moneydelivery;

import static com.sacdev.moneydelivery.R.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity  {
 private TextView tittle , Loginbtn;
  private EditText mobileedt ;
  private  Boolean getotp = true;
  private String mobilenumber;
  private ProgressBar progressBar;
  private String mVerificationId ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_login);
        mobileedt = findViewById(R.id.Edittextlogin_id);
       Loginbtn = findViewById(R.id.buttonlogin_id);
       progressBar = findViewById(id.progresssloginid);
        tittle = findViewById(id.Tittletextlogin_id);

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(LoginActivity.this, "Verification Failed"+e, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mobileedt.setError("Invalid Otp");

                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                Toast.makeText(LoginActivity.this, "Otp Send On "+mobilenumber, Toast.LENGTH_LONG).show();
                tittle.setText("Enter OTP");
                mVerificationId = verificationId;
                mobileedt.setText(null);
                Loginbtn.setText(" Verify");
                getotp = false;
                progressBar.setVisibility(View.GONE);
            }
        };
       Loginbtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (getotp){
                   mobilenumber = mobileedt.getText().toString();
                   if(TextUtils.isEmpty(mobilenumber)){
                       mobileedt.setError("Enter Mobile Number");
                   }else if (mobilenumber.length()!=10){
                       mobileedt.setError("Enter Valid  Number");
                   }else {
                      progressBar.setVisibility(View.VISIBLE);
                       PhoneAuthOptions options =
                               PhoneAuthOptions.newBuilder(starterclass.firebaseAuth)
                                       .setPhoneNumber("+91"+mobilenumber)       // Phone number to verify
                                       .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                       .setActivity(LoginActivity.this)                 // Activity (for callback binding)
                                       .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                       .build();
                       PhoneAuthProvider.verifyPhoneNumber(options);
                   }
               }else{
                   String code  = mobileedt.getText().toString();
                   if(TextUtils.isEmpty(code)){
                       mobileedt.setError("Enter Otp ");
                   }else if (! (code.length()==6)){
                       mobileedt.setError("Enter Valid  Otp");
                   }else{
                       progressBar.setVisibility(View.VISIBLE);
                       PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                       signInWithPhoneAuthCredential(credential);
                   }
               }
           }
       });



    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        starterclass.firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Log In Success", Toast.LENGTH_SHORT).show();
                            startActivity(starterclass.changeActivity(LoginActivity.this,MainActivity.class));
                        } else {
                            progressBar.setVisibility(View.GONE);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                mobileedt.setError("Invalid Error");
                            }
                        }
                    }
                });
    }

}