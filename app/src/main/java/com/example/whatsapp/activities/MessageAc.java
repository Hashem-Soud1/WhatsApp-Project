package com.example.whatsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.adapter.MessageAdapter;
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
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAc extends AppCompatActivity {

    Intent intent;
    TextView name;
    CircleImageView img;

    DatabaseReference reference;

    ImageView send;
    EditText m_txt;
    FirebaseUser firebaseUser;

    RecyclerView recyclerView;
    List<Chat> chatList;
    MessageAdapter messageAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        name=findViewById(R.id.usernamem);
        img=findViewById(R.id.profile_img);

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        send=findViewById(R.id.btn_send);
        m_txt=findViewById(R.id.text_send);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        intent =getIntent();
        String id=intent.getStringExtra("id");
        String className=intent.getStringExtra("classname");

        Toolbar toolbar = findViewById(R.id.MessageToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ViewContacts.NaAc.equals(className)) {
                    intent = new Intent(MessageAc.this,ViewContacts.class);
                    startActivity(intent);
                    finish();
                }

                else {
                    intent = new Intent(MessageAc.this,HomeActivity.class);
                    startActivity(intent);
                    finish();

                }



            }
        });



        reference= FirebaseDatabase.getInstance().getReference("users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);

                name.setText(user.getUsername());

                if(user.getImgUrl().equals("default"))
                    img.setImageResource(R.drawable.ic_launcher_background);
                else
                    Glide.with(getApplicationContext()).load(user.getImgUrl()).into(img);

                readMessages(firebaseUser.getUid(),user.getImgUrl(),id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=m_txt.getText().toString().trim();
                if(!msg.isEmpty())
                senMessage(msg,firebaseUser.getUid(),id);
                else
                    Toast.makeText(MessageAc.this, "you Can't send Empty message", Toast.LENGTH_SHORT).show();

                     m_txt.setText("");

            }

        });

    }

    private void readMessages(String uid, String ImgUrl, String id) {
        chatList =new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatList.clear();
                for(DataSnapshot i: snapshot.getChildren()){
                    Chat chat=i.getValue(Chat.class);


                    if(chat.getReceiver().equals(uid)&&chat.getSender().equals(id)
                            || chat.getReceiver().equals(id)&&chat.getSender().equals(uid) ) {

                        chatList.add(chat);
                    }

                    messageAdapter=new MessageAdapter(getApplicationContext(),chatList,ImgUrl);
                    recyclerView.setAdapter(messageAdapter);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void senMessage(String msg, String uid, String id) {

     DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("sender",uid);
        hashMap.put("receiver",id);
        hashMap.put("msg",msg);

        reference.child("chats").push().setValue(hashMap);

    }

    void state(String s){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid());

        HashMap<String, Object> hashMap=new HashMap();
        hashMap.put("state",s);
        reference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        state("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        state("offline");
    }
}