package khaledbeshir.android.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmail , mPassword ;
    private Button mRegisterButton ;
    private TextView mAlreadyHaveAccount ;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private DatabaseReference rootreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mEmail = (EditText)findViewById(R.id.register_email);
        mPassword = (EditText) findViewById(R.id.register_password);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mAlreadyHaveAccount = (TextView) findViewById(R.id.already_have_account);
        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        rootreference = FirebaseDatabase.getInstance()
                .getReference();

        mAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToLoginActivity();
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {


        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this , "please enter email ..." , Toast.LENGTH_SHORT ).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this , "please enter password ..." , Toast.LENGTH_SHORT).show();
        }
        else {
            mProgressDialog.setMessage("please wait until create your new account");
            mProgressDialog.setTitle("Create new Account");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.show();

            mAuth.createUserWithEmailAndPassword(email , password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String CurrentUserId = mAuth.getCurrentUser().getUid();
                                rootreference.child("Users").child(CurrentUserId).setValue("");
                                sendUserToChatActivity();
                                Toast.makeText(RegisterActivity.this , "Account Created Successfully ..." ,Toast.LENGTH_SHORT ).show();
                                mProgressDialog.dismiss();
                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error : "+message , Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            }
                        }
                    });
        }

    }

    private void sendUserToChatActivity(){
    Intent intent = new Intent(RegisterActivity.this,
            ChatActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this,
                LoginActivity.class);
        startActivity(loginIntent);
    }
}
