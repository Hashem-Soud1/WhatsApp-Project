package com.example.whatsapp.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsapp.R;
import com.example.whatsapp.adapter.userAdapter;
import com.example.whatsapp.model.Chat;
import com.example.whatsapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
RecyclerView recyclerView;
List<User> userList;
List<String>userId;
FirebaseUser firebaseUser;
DatabaseReference reference;

    public final static  String NaAc="ChatFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.activity_chat_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Here write your code

        recyclerView=view.findViewById(R.id.rec);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        userList=new ArrayList<>();
        userId=new ArrayList<>();


        reference= FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userId.clear();
                for(DataSnapshot snapshot2 : snapshot.getChildren()) {

                    Chat chat = snapshot2.getValue(Chat.class);

                    if (firebaseUser.getUid().equals(chat.getSender())) {
                        if (!userId.contains(chat.getReceiver()))

                            userId.add(chat.getReceiver());
                    }

                    else if (firebaseUser.getUid().equals(chat.getReceiver())) {
                        if (!userId.contains(chat.getSender()))

                            userId.add(chat.getSender());
                    }

                }

                getUserChats();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUserChats() {

        reference=FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
userList.clear();
                for(DataSnapshot snapshot2 : snapshot.getChildren()){
                    User user=snapshot2.getValue(User.class);

                    for(int i=0;i<userId.size();i++){
                        if(user.getId().equals(userId.get(i))){
                            if(!userList.contains(user))
                                userList.add(user);
                        }

                    }

                }

                userAdapter userAdapter=new userAdapter(getContext(),userList,NaAc,true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(userAdapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}