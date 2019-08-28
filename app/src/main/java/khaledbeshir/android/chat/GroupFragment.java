package khaledbeshir.android.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Mohamed Amr on 6/12/2019.
 */

public class GroupFragment extends Fragment {


    public final static String GroupName = "groupName";
    private ListView mListView;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayList<String> GroupList = new ArrayList<String>();
    private DatabaseReference GroupRoot ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group , container , false);
        mListView =(ListView) v.findViewById(R.id.group_list_view);
        mArrayAdapter = new ArrayAdapter<String>(getContext() ,android.R.layout.simple_list_item_1,GroupList);
        mListView.setAdapter(mArrayAdapter);
        GroupRoot = FirebaseDatabase.getInstance().getReference().child("Groups");

        showGroups();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String Groupname = adapterView.getItemAtPosition(i).toString();
                Intent intent = new Intent(getContext() , GroupChatActivity.class);
                intent.putExtra(GroupName , Groupname );
                startActivity(intent);
            }
        });
        return v;
    }
    private void showGroups (){

        GroupRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                Set<String> GroupSet = new HashSet<>();
                while (iterator.hasNext()){
                    GroupSet.add(((DataSnapshot)iterator.next()).getKey());
                }
                GroupList.clear();
                GroupList.addAll(GroupSet);
                mArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
