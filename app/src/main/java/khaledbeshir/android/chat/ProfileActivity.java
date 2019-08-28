package khaledbeshir.android.chat;

import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView userProfileImage;
    private TextView userProfileName , userProfileStatus;
    private Button sendMessageButton , cancelRequestButton ;
    private DatabaseReference userRef,RequestChatRef,ContactsRef;
    private String recieverUserId , senderUserId ,current_state;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        RequestChatRef = FirebaseDatabase.getInstance().getReference().child("Request Chat");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        userProfileImage = (CircleImageView)findViewById(R.id.activity_profile_profile_image);
        userProfileName = (TextView)findViewById(R.id.activity_profile_user_name);
        userProfileStatus = (TextView)findViewById(R.id.activity_profile_user_status);
        sendMessageButton = (Button)findViewById(R.id.activity_profile_send_message_button);
        cancelRequestButton = (Button) findViewById(R.id.activity_profile_decline_button);

        current_state = "new";
        recieverUserId = getIntent().getExtras().get(FindFriendsActivity.Pressed_user_id).toString();
        Toast.makeText(this, "user_id = " +recieverUserId, Toast.LENGTH_SHORT).show();

        retrieveuserinfo();
    }



    private void retrieveuserinfo (){
        userRef.child(recieverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))){
                    String username = dataSnapshot.child("name").getValue().toString();
                    String userstatus = dataSnapshot.child("status").getValue().toString();
                    String userimage = dataSnapshot.child("image").getValue().toString();

                    userProfileName.setText(username);
                    userProfileStatus.setText(userstatus);
                    Picasso.get().load(userimage).into(userProfileImage);
                    manageRequestChat();
                }
                else {

                    String username = dataSnapshot.child("name").getValue().toString();
                    String userstatus = dataSnapshot.child("status").getValue().toString();

                    userProfileName.setText(username);
                    userProfileStatus.setText(userstatus);
                    manageRequestChat();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void manageRequestChat() {

        RequestChatRef.child(senderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(recieverUserId)){
                    String request_type = dataSnapshot.child(recieverUserId).child("request_type").getValue().toString();
                    if (request_type.equals("sent")){
                        current_state = "request_sent";
                        sendMessageButton.setText("Cancel Chat Request");
                    }
                    if (request_type.equals("recieved")){
                        cancelRequestButton.setEnabled(true);
                        sendMessageButton.setText("Accept chat Request");
                        current_state = "request_recieved";

                        cancelRequestButton.setVisibility(View.VISIBLE);
                        cancelRequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CancelChatRequest();
                            }
                        });
                    }
                }
                else {
                    ContactsRef.child(senderUserId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(recieverUserId)){
                                        current_state = "friends";
                                        sendMessageButton.setText("Remove this Contact");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if(!senderUserId.equals(recieverUserId))
        {
            sendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    sendMessageButton.setEnabled(false);
                    if (current_state.equals("new")) {
                        SendChatRequest();
                    }
                    if (current_state.equals("request_sent")){
                        CancelChatRequest();
                    }
                    if (current_state.equals("request_recieved")){
                        AcceptChatRequest();
                    }
                    if (current_state.equals("friends")){
                        RemoveSpecifiedContact();
                    }
                }
            });
        }
        else {

            sendMessageButton.setVisibility(View.INVISIBLE);
        }
    }





    private void RemoveSpecifiedContact() {

        ContactsRef.child(senderUserId).child(recieverUserId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    ContactsRef.child(recieverUserId).child(senderUserId)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                sendMessageButton.setText("Send Message");
                                sendMessageButton.setEnabled(true);
                                current_state = "new";

                                cancelRequestButton.setVisibility(View.INVISIBLE);
                                cancelRequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }





    private void AcceptChatRequest() {

        ContactsRef.child(senderUserId).child(recieverUserId)
                .child("Contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            ContactsRef.child(recieverUserId).child(senderUserId)
                                    .child("Contacts").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                RequestChatRef.child(senderUserId).child(recieverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                RequestChatRef.child(recieverUserId).child(senderUserId)
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                sendMessageButton.setEnabled(true);
                                                                                sendMessageButton.setText("Remove this Contact");
                                                                                current_state = "friends";

                                                                                cancelRequestButton.setEnabled(false);
                                                                                cancelRequestButton.setVisibility(View.INVISIBLE);
                                                                            }
                                                                        });

                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });

    }



    private void CancelChatRequest() {

        RequestChatRef.child(senderUserId).child(recieverUserId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                RequestChatRef.child(recieverUserId).child(senderUserId)
                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            sendMessageButton.setText("Send Message");
                            sendMessageButton.setEnabled(true);
                            current_state = "new";

                            cancelRequestButton.setVisibility(View.INVISIBLE);
                            cancelRequestButton.setEnabled(false);
                        }
                    }
                });
            }
            }
        });
    }




    private void SendChatRequest() {

        RequestChatRef.child(senderUserId).child(recieverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            RequestChatRef.child(recieverUserId).child(senderUserId)
                                    .child("request_type").setValue("recieved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            sendMessageButton.setEnabled(true);
                                            current_state = "request_sent";
                                            sendMessageButton.setText("Cancel Chat Request");

                                        }
                                    });
                        }
                    }
                });
    }


}
