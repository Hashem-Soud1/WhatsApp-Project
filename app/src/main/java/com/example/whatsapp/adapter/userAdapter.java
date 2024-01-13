package com.example.whatsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.activities.MessageAc;
import com.example.whatsapp.model.Chat;
import com.example.whatsapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    String className;
    String lastM;
    boolean isChat;

    public userAdapter(Context mContext, List<User> userList,String className,boolean isChat){
        this.mContext = mContext;
        this.mUsers = userList;
        this.className=className;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = mUsers.get(position);
        holder.contactName.setText(user.getUsername());




        if(isChat){
            if(user.getState().equals("online"))
                 holder.on.setVisibility(View.VISIBLE);
            else
                holder.off.setVisibility(View.VISIBLE);

            lastMessage(user.getId(), holder.contactStatus);
        }
        else
            holder.contactStatus.setText(user.getStatus());



        if(user.getImgUrl().equals("default")){
            holder.profileImg.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(user.getImgUrl()).into(holder.profileImg);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, MessageAc.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("id",user.getId());

            if (className.equals("ViewContacts"))
                intent.putExtra("classname",className);
            else
                intent.putExtra("classname",className);

         mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{



        TextView contactName, contactStatus;
        ImageView profileImg;
        CircleImageView off,on;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            contactName = itemView.findViewById(R.id.ContactName);
            contactStatus = itemView.findViewById(R.id.ContactStatus);
            profileImg = itemView.findViewById(R.id.imgProfile);
            off = itemView.findViewById(R.id.img_off);
            on = itemView.findViewById(R.id.img_on);
        }
    }

    void lastMessage(String id,TextView lastMessage){
         lastM="";

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat=dataSnapshot.getValue(Chat.class);

                  if(  chat.getReceiver().equals(id)&&chat.getSender().equals(firebaseUser.getUid())
                            || chat.getReceiver().equals(firebaseUser.getUid())&&chat.getSender().equals(id)) {

                      lastM=chat.getMessage();
                  }


                }

                if(lastM!="") lastMessage.setText(lastM);
                else lastMessage.setText("No Message");

                lastM="";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

}
