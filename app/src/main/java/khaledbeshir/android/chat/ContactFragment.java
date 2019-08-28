package khaledbeshir.android.chat;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohamed Amr on 6/12/2019.
 */

public class ContactFragment extends Fragment {

    private RecyclerView mRecycleView;
    private DatabaseReference ContactsRf , UserRef;
    private String UserId;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact , container , false);
        mRecycleView = v.findViewById(R.id.contacts_list);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();

        UserId = mAuth.getCurrentUser().getUid();

        ContactsRf = FirebaseDatabase.getInstance().getReference().child("Contacts").child(UserId);

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return v;
    }



    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ContactsRf , Contacts.class)
                .build();

        final FirebaseRecyclerAdapter<Contacts, contactViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, contactViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final contactViewHolder holder, int position, @NonNull Contacts model) {

                String ProfileId = getRef(position).getKey();


                UserRef.child(ProfileId).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("image")) {

                            String profileName = dataSnapshot.child("name").getValue().toString();
                            String profileStatus = dataSnapshot.child("status").getValue().toString();
                            String profileImage = dataSnapshot.child("image").getValue().toString();

                            holder.username.setText(profileName);
                            holder.userstatus.setText(profileStatus);
                            Picasso.get().load(profileImage).into(holder.userimage);
                        }
                        else {
                            String profileName = dataSnapshot.child("name").getValue().toString();
                            String profileStatus = dataSnapshot.child("status").getValue().toString();

                            holder.username.setText(profileName);
                            holder.userstatus.setText(profileStatus);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public contactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout , parent  ,false);
                return new contactViewHolder(view);

            }

        };


        mRecycleView.setAdapter(adapter);
        adapter.startListening();

    }


    public static class contactViewHolder extends RecyclerView.ViewHolder{

        TextView username , userstatus;
        private CircleImageView userimage;

        public contactViewHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.user_profile_name);
            userstatus = (TextView) itemView.findViewById(R.id.user_status);
            userimage = (CircleImageView) itemView.findViewById(R.id.user_profile_image);

        }
    }



}
