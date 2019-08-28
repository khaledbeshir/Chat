package khaledbeshir.android.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail , mPassword ;
    private Button mLoginButton , mPhoneLogin;
    private TextView mNeedNewAccount , mForgetPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEmail = (EditText)findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mNeedNewAccount = (TextView) findViewById(R.id.need_new_account);
        mForgetPassword = (TextView) findViewById(R.id.forget_password);
        mPhoneLogin = (Button) findViewById(R.id.phone_login_button);
        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mNeedNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToRegisterActivity();
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogin();
            }
        });

        mPhoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginWithPhone();
            }
        });
    }




    private void LoginWithPhone() {
        Intent intent = new Intent(LoginActivity.this , PhoneLoginActivity.class);
        startActivity(intent);
    }



    private void AllowUserToLogin() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "please enter your email ...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter your password ...", Toast.LENGTH_SHORT).show();
        }
        else{
            mProgressDialog.setTitle("Login");
            mProgressDialog.setMessage("please until confirm your login");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        sendUserToChatActivity();
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    }
                else {
                        mProgressDialog.dismiss();
                        String message = task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this ,
                RegisterActivity.class);
        startActivity(intent);

    }

    private void sendUserToChatActivity() {
        Intent intent = new Intent(LoginActivity.this,
                ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
