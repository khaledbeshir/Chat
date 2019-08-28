package khaledbeshir.android.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private EditText UserMessageInput;
    private TextView displayTextMessage;
    private ScrollView mScrollView;
    private ImageButton sendMessageButton;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef ,  GroupNameRef , GroupMessageKeyRef;
    private String GroupName ,CurrentDate , CurrentTime , CurrentUserName , CurrentUserId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        GroupName = getIntent().getExtras().get(GroupFragment.GroupName).toString();

        mToolbar = (Toolbar) findViewById(R.id.group_chat_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(GroupName);
        sendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        UserMessageInput = (EditText) findViewById(R.id.input_group_message);
        displayTextMessage =(TextView) findViewById(R.id.group_chat_text_display);
        mScrollView = (ScrollView) findViewById(R.id.scroll_view);


        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(GroupName);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        Toast.makeText(this,  GroupName, Toast.LENGTH_SHORT).show();


        getUserInfo();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveMessagetoDataBase();
                UserMessageInput.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }






    @Override
    protected void onStart() {
        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    DisplayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ///DisplayMessage(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }








    private void DisplayMessage(DataSnapshot dataSnapshot) {

        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            displayTextMessage.append(chatName +" : \n"
                    + chatMessage + "\n" + chatTime + "     " +chatDate + "\n \n \n" );
        }

    }





    private void SaveMessagetoDataBase() {
        String message = UserMessageInput.getText().toString();
        String MessageKey = GroupNameRef.push().getKey();

        if (TextUtils.isEmpty(message)){
            Toast.makeText(GroupChatActivity.this, "Pleasee Enter Your Message ...", Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar DateCal = Calendar.getInstance();
            SimpleDateFormat DateFormat = new SimpleDateFormat("mmm dd,yyyy");
            CurrentDate = DateFormat.format(DateCal.getTime());

            Calendar TimeCal = Calendar.getInstance();
            SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm");
            CurrentTime = TimeFormat.format(TimeCal.getTime());

        //    HashMap<String,Object> groupMessageKey = new HashMap<>();
          //  GroupNameRef.updateChildren(groupMessageKey);

            HashMap<String , Object> MessageInfMap = new HashMap<>();
            MessageInfMap.put("name", CurrentUserName);
            MessageInfMap.put("message", message);
            MessageInfMap.put("date", CurrentDate);
            MessageInfMap.put("time", CurrentTime);


            GroupMessageKeyRef = GroupNameRef.child(MessageKey);
            GroupMessageKeyRef.updateChildren(MessageInfMap);
        }

    }




    private void getUserInfo() {
        UserRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    CurrentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }





}
