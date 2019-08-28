package khaledbeshir.android.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private Button updateProfileButton;
    private EditText userName , userStatus;
    private String ProfileImage;
    private CircleImageView userImage;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String CurrentUserID ;
    private DatabaseReference RootRef;
    private static final int ProfileImageRequest=1;
    private StorageReference ProfileImagesRef;
    private ProgressDialog loadingBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        updateProfileButton = (Button) findViewById(R.id.set_update_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus =(EditText) findViewById(R.id.set_profile_status);
        userImage = (CircleImageView) findViewById(R.id.set_profile_image);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        ProfileImagesRef = FirebaseStorage.getInstance().getReference().child("profile images").child(CurrentUserID+".jpg");
        RootRef = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view){
                   UpdateSettings();
                                                   }
         });
        RetrieveProfileData();

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent ,ProfileImageRequest);
            }
        });
    }






    private void RetrieveProfileData() {

        RootRef.child("Users").child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))) {
                            String UserName = dataSnapshot.child("name").getValue().toString();
                            String UserStatus = dataSnapshot.child("status").getValue().toString();
                            ProfileImage = dataSnapshot.child("image").getValue().toString();

                            userName.setText(UserName);
                            userStatus.setText(UserStatus);
                            Picasso.get().load(ProfileImage).into(userImage);

                        }else if ((dataSnapshot.exists()) && dataSnapshot.hasChild("name")){

                            String UserName = dataSnapshot.child("name").getValue().toString();
                            String UserStatus = dataSnapshot.child("status").getValue().toString();

                            userName.setText(UserName);
                            userStatus.setText(UserStatus);
                        }
                        else {
                            Toast.makeText(SettingActivity.this, "please set and update your profile information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }







    private void UpdateSettings() {

        String SetUserName = userName.getText().toString();
        String SetUserStatus = userStatus.getText().toString();


        if (TextUtils.isEmpty(SetUserName)){
            Toast.makeText(this, "Please Write Your Name Fisrt ....", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(SetUserStatus)){
            Toast.makeText(this, "Please Write Your Status ....", Toast.LENGTH_SHORT).show();
        }


        else {
            HashMap<String,String> profileMap = new HashMap<>();
            profileMap.put("uid", CurrentUserID);
            profileMap.put("name" , SetUserName);
            profileMap.put("status" , SetUserStatus);
            if (userImage.getDrawable() != null){
                profileMap.put("image",ProfileImage);
            }

            RootRef.child("Users").child(CurrentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendUserToChatActivity();
                                Toast.makeText(SettingActivity.this, "Profile Updated successfully...", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(SettingActivity.this, "Error : "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }







    private void sendUserToChatActivity(){
        Intent intent = new Intent(SettingActivity.this,
                ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ProfileImageRequest && resultCode == RESULT_OK && data != null) {

            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            Uri resultUri = result.getUri();

            if (resultCode == RESULT_OK) {

                loadingBar.setTitle("set profile image");
                loadingBar.setMessage("please wait your profile image is updating ...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                ProfileImagesRef.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingActivity.this, "Your Image Uploaded Successfully ...", Toast.LENGTH_SHORT).show();
                            final String imageUri =task.getResult().getDownloadUrl().toString();
                            RootRef.child("Users").child(CurrentUserID).child("image")
                                    .setValue(imageUri)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                Toast.makeText(SettingActivity.this, "image uploaded to database successuflly ..", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else {
                                                Toast.makeText(SettingActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SettingActivity.this, "Error :" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        }
    }

}

