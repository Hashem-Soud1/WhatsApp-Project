package com.example.whatsapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewProfile extends AppCompatActivity {

    Toolbar toolbar;
    TextView n,e,s;
    ImageView changP;
    CircleImageView profIm;
    private Uri imagUri;
    private StorageTask storageTask;
    DatabaseReference reference;
    StorageReference referenceS;
    FirebaseUser firebaseUser;

    private final static int IMAG_REQUEST=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

         changP=findViewById(R.id.changp);
         profIm=findViewById(R.id.imgProfile);
         firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
         referenceS=FirebaseStorage.getInstance().getReference("uploads");



        toolbar = (Toolbar) findViewById(R.id.viewProfileToolbar);
        n=findViewById(R.id.username);
        e=findViewById(R.id.email);
        s=findViewById(R.id.status);

        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Profile");
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProfile.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        String id= FirebaseAuth.getInstance().getUid();

         reference= FirebaseDatabase.getInstance().getReference("users");

      reference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              if(snapshot!=null)
              {
                  User user = snapshot.child(id).getValue(User.class);
                  n.setText(user.getUsername());
                  e.setText(user.getEmail());
                  s.setText(user.getStatus());

                  if(user.getImgUrl().equals("default"))
                      profIm.setImageResource(R.drawable.ic_launcher_background);
                  else
                      Glide.with(getApplicationContext()).load(user.getImgUrl()).into(profIm);
              }
              else
                  Toast.makeText(ViewProfile.this, "nooooooooooo", Toast.LENGTH_SHORT).show();


          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });


      changP.setOnClickListener( v -> {

          updateImage();
      });



    }//End onCreate

    private void updateImage() {

        Intent intent =new Intent();
        intent.setType("imag/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAG_REQUEST);

    }


    private String getFileExtension(Uri uri){

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void uploadImage(){

        if(imagUri != null){
            final StorageReference fileReference = referenceS.child(System.currentTimeMillis()
                    +"."+getFileExtension(imagUri));

            storageTask = fileReference.putFile(imagUri);
            storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();


                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("imgUrl",mUri);

                        reference.updateChildren(map);
                    }else{
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });

        }else{
            Toast.makeText(getApplicationContext(),"No image selected!",Toast.LENGTH_LONG).show();
        }
    }//End of uploadImage method :)


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAG_REQUEST && resultCode ==  RESULT_OK && data != null && data.getData() != null){
            imagUri = data.getData();

            if(storageTask != null && storageTask.isInProgress()){
                Toast.makeText(getApplicationContext(),"Upload in progress!",Toast.LENGTH_LONG).show();
            }else{
                uploadImage();
            }
        }
    }//End of onActivityResult method



}