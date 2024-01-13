package com.example.whatsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.whatsapp.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    String name,status,email,password;

    FirebaseAuth auth;

    DatabaseReference dreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent =new Intent(SignUpActivity.this,HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        binding.signInActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(SignUpActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name=binding.TextInputNameLayout.getEditText().getText().toString();
                status=binding.TextInputStatusLayout.getEditText().getText().toString();
                email=binding.TextInputEmailLayout.getEditText().getText().toString();
                password=binding.TextInputPassword.getEditText().getText().toString();

                if(name.isEmpty()||status.isEmpty()||email.isEmpty()||password.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "you must Fill all Field", Toast.LENGTH_SHORT).show();
                }
                else {
                    regester(name,status,email,password);
                }
            }
        });


    }

    private void regester(String name, String status, String email, String passwrod) {

        auth.createUserWithEmailAndPassword(email,passwrod).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
           if(task.isSuccessful())
           {
               FirebaseUser firebaseUser=auth.getCurrentUser();
               String userId =firebaseUser.getUid();

               dreference =FirebaseDatabase.getInstance().getReference("users").child(userId);

               HashMap<String,String> hashMap=new HashMap<String, String>();
               hashMap.put("id",userId);
               hashMap.put("username",name);
               hashMap.put("status",status);
               hashMap.put("email",email);
               hashMap.put("password",password);
               hashMap.put("imgUrl","default");
               hashMap.put("state","offline");

               dreference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful())
                       {
                           Intent intent =new Intent(SignUpActivity.this,HomeActivity.class);
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                           startActivity(intent);
                               finish();
                       }else {
                           Toast.makeText(SignUpActivity.this, "error2", Toast.LENGTH_SHORT).show();

                       }
                   }
               });
           }
                else
               Toast.makeText(SignUpActivity.this, "error1", Toast.LENGTH_SHORT).show();
            }
        });
    }
}