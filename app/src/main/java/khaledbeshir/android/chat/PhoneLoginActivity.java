package khaledbeshir.android.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {


    private Button verifyCodeButton , sendCodeButton;
    private EditText PhoneNumberEditText , VerifyCodeEditText;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();

        verifyCodeButton =(Button) findViewById(R.id.verify_code_button);
        sendCodeButton = (Button) findViewById(R.id.send_ver_code_button);
        PhoneNumberEditText = (EditText) findViewById(R.id.phone_number_input);
        VerifyCodeEditText = (EditText) findViewById(R.id.verification_code_input);
        loadingbar = new ProgressDialog(this);

        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = PhoneNumberEditText.getText().toString();

                if (TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(PhoneLoginActivity.this, "Enter your phone number", Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingbar.setTitle("Phone Verfication");
                    loadingbar.setMessage("Please wait until verifying your phone");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            PhoneLoginActivity.this,
                            mCallbacks
                    );
                }
            }
        });


        verifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Verificationcode = VerifyCodeEditText.getText().toString();
                if (TextUtils.isEmpty(Verificationcode)) {
                    Toast.makeText(PhoneLoginActivity.this, "please insert verification code", Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingbar.setTitle("Code Verfication");
                    loadingbar.setMessage("Please wait until verifying your code");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, Verificationcode);
                    SignInWithAuthCredential(credential);
                }
            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(PhoneLoginActivity.this, "Your Phone Verified", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneLoginActivity.this, "Write correct phone number with your country code"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(String VerificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(VerificationId, forceResendingToken);
                mVerificationId = VerificationId;
                mResendingToken =forceResendingToken;
                loadingbar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Your code sent ", Toast.LENGTH_SHORT).show();
                PhoneNumberEditText.setVisibility(View.INVISIBLE);
                VerifyCodeEditText.setVisibility(View.VISIBLE);
                sendCodeButton.setVisibility(View.INVISIBLE);
                verifyCodeButton.setVisibility(View.VISIBLE);

            }
        };
    }



    private void SignInWithAuthCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        loadingbar.dismiss();
                        if (task.isSuccessful()){
                            Toast.makeText(PhoneLoginActivity.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                            sendUsertochatActivity();
                        }
                        else {
                            Toast.makeText(PhoneLoginActivity.this, "Error :" + task.getException().toString() , Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }




    private void sendUsertochatActivity() {

        Intent intent = new Intent(PhoneLoginActivity.this , ChatActivity.class);
        startActivity(intent);
        finish();
    }

}
