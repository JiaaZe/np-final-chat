package com.example.jezer.chat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Fragment_chat extends Fragment {
    private FragmentManager fm;
    private RecyclerView mUserList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String selfID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getFragmentManager();

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            selfID = mAuth.getCurrentUser().getUid();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat,container,false);
        mUserList = (RecyclerView) v.findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(mUserDatabase, Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options){
            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.users_single_layout, viewGroup, false);
                UsersViewHolder viewHolder = new UsersViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(final UsersViewHolder holder, int position, Users model) {

                final String user_id = getRef(position).getKey();
                final String user_name = model.getName();

                if(user_id.equals(selfID)){
                    holder.hide(1);
                }else {
                    holder.hide(0);
                    holder.userName.setText(model.getName());
                    holder.mView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            Intent goToChatIntent = new Intent(getContext(), Activity_chat.class);
                            goToChatIntent.putExtra("user_id", user_id);
                            goToChatIntent.putExtra("user_name", user_name);
                            startActivity(goToChatIntent);
                        }

                    });
                }
            }
        };

        mUserList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public class UsersViewHolder extends RecyclerView.ViewHolder{
            View mView;
            TextView userName;
        public UsersViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            userName = itemView.findViewById(R.id.user_single_name);
        }

        public void hide(int i){
            if(i == 1) {
                mView.setVisibility(View.GONE);
                mView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            }
            else
                mView.setVisibility(View.VISIBLE);
        }
    }
}
