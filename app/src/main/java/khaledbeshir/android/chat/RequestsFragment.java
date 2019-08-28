package khaledbeshir.android.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestsFragment extends Fragment {

    RecyclerView mRecycleView;
    DatabaseReference RequestRef,UserRef;
    String CurrentUserId ;
    FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_requests, container, false);
        mRecycleView = (RecyclerView) v.findViewById(R.id.requests_list);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        RequestRef = FirebaseDatabase.getInstance().getReference().child("Request Chat");
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid().toString();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions
                .Builder<Contacts>()
                .setQuery(RequestRef.child(CurrentUserId) ,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts , RequestViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contacts model) {

                        final String ListUserId = getRef(position).getKey();
                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    String type = dataSnapshot.getValue().toString();


                                    if (type.equals("recieved")){
                                        UserRef.child(ListUserId).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild("image")){
                                                    String profileImage = dataSnapshot.child("image").getValue().toString();

                                                    Picasso.get().load(profileImage).into(holder.circleImageView);

                                                }
                                                    String profileName = dataSnapshot.child("name").getValue().toString();
                                                    String profileStatus = dataSnapshot.child("status").getValue().toString();

                                                    holder.username.setText(profileName);
                                                    holder.userstatus.setText(profileStatus);

                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            CharSequence options[] = new CharSequence[]{
                                                                    "Accept",
                                                                    "Cancel"
                                                            };

                                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                                                    .setTitle(ListUserId + "Chat Request")
                                                                    .setItems(options, new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                                            if (i==0){

                                                                            }
                                                                            if (i==1){

                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    });


                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout , parent ,false);
                        Button acceptButton = (Button) v.findViewById(R.id.request_accept_btn);
                        Button cancelButton = (Button) v.findViewById(R.id.request_cancel_btn);
                        acceptButton.setVisibility(View.VISIBLE);
                        cancelButton.setVisibility(View.VISIBLE);
                        return new RequestViewHolder(v);
                    }
                };

        mRecycleView.setAdapter(adapter);
        adapter.startListening();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView username , userstatus;
        CircleImageView circleImageView;
        Button acceptButton , cancelButton;

        public RequestViewHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.user_profile_name);
            userstatus = (TextView) itemView.findViewById(R.id.user_status);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.user_profile_image);
            acceptButton = (Button) itemView.findViewById(R.id.request_accept_btn);
            cancelButton = (Button) itemView.findViewById(R.id.request_cancel_btn);
        }

    }

}