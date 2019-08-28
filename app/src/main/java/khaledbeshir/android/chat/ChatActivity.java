package khaledbeshir.android.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAdapter mTabsAdapter;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private static final String TAG ="ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("chat");

        mTabLayout = findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.tabs_pager);
        mTabsAdapter = new TabsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mFirebaseUser == null){
            Log.i(TAG , "user not found ");
            sendUserToLoginActivity();
        }else {
            Log.i(TAG , "current user :" + mAuth.getCurrentUser() );
            VerifyUserExistance ();
        }
    }

    private void VerifyUserExistance() {
        String CurrentUserID = mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists())){
                    Toast.makeText(ChatActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendUsertoSettingsActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendUserToLoginActivity (){

        Intent loginIntent = new Intent(ChatActivity.this ,
                LoginActivity.class);
        startActivity(loginIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.options_menu , menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
             case R.id.main_logout_option :
                 mAuth.signOut();
                 sendUserToLoginActivity();
                 return true;

            case R.id.main_create_group_option:
                CreateGroupDialog();
                return true;

            case R.id.main_find_friend_option:
                sendUsertoFindFriendsActivity();
                return true;

            case R.id.main_settings_option:
                 sendUsertoSettingsActivity();
                 return true;

             default:
                 super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void CreateGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this , R.style.AlertDialog);
        builder.setTitle("Create New Group :");

        final EditText GroupName = new EditText(ChatActivity.this);
        GroupName.setHint("e.g Coding Cafe");
        builder.setView(GroupName);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String CreatedGroupName = GroupName.getText().toString();

                if (TextUtils.isEmpty(CreatedGroupName)){
                    Toast.makeText(ChatActivity.this, "Please Enter your Group name ", Toast.LENGTH_SHORT).show();
                }
                else {
                    CreateNewGroup(CreatedGroupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void CreateNewGroup(String GroupName) {
        RootRef.child("Groups").child(GroupName).setValue("");
    }

    private void sendUsertoSettingsActivity(){
        Intent intent = new Intent(ChatActivity.this ,
                SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }




    private void sendUsertoFindFriendsActivity(){
        Intent intent = new Intent(ChatActivity.this ,
                FindFriendsActivity.class);
        startActivity(intent);
    }

}
