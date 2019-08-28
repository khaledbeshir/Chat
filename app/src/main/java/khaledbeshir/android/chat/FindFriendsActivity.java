package khaledbeshir.android.chat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseArray;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {


    private RecyclerView mRecycleView;
    private Toolbar mToolbar;
    private DatabaseReference userRef;
    public static String Pressed_user_id = "user_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mRecycleView = (RecyclerView) findViewById(R.id.find_friends_recycle_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mToolbar = (Toolbar) findViewById(R.id.find_friends_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");


    }




    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions
                .Builder<Contacts>()
                .setQuery(userRef , Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {

            @Override
            public FindFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout , parent , false);
                return new FindFriendViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {
                holder.username.setText(model.getName());
                holder.userstatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).into(holder.profileimage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String user_id = getRef(position).getKey();
                        Intent intent = new Intent(FindFriendsActivity.this , ProfileActivity.class);
                        intent.putExtra(Pressed_user_id , user_id);
                        startActivity(intent);
                    }
                });
            }
        };

        mRecycleView.setAdapter(adapter);
        adapter.startListening();
    }




   public static class FindFriendViewHolder extends RecyclerView.ViewHolder{

        private TextView username , userstatus;
        private CircleImageView profileimage;

        public FindFriendViewHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.user_profile_name);
            userstatus = (TextView) itemView.findViewById(R.id.user_status);
            profileimage = (CircleImageView) itemView.findViewById(R.id.user_profile_image);
        }
    }


}
