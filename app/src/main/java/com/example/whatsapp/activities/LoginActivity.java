package com.example.whatsapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.databinding.ActivityLoginBinding;
import com.example.whatsapp.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth auth;
    String email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth=FirebaseAuth.getInstance();

        binding.signInBtn.setOnClickListener(v -> {

            email=binding.EmailLogin.getEditText().getText().toString();
            password=binding.PasswordLogin.getEditText().getText().toString();

            checkuser(email,password);
            });

        binding.signUpActivity.setOnClickListener(v -> {

            Intent intent =new Intent(LoginActivity.this,SignUpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });


    }

    private void checkuser(String email, String password) {

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {

            if(task.isSuccessful())
            {
                Intent intent =new Intent(LoginActivity.this,HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(this, task.getException()+"", Toast.LENGTH_SHORT).show();

                Log.d("login", task.getException()+"");
            }

    });

    }
}