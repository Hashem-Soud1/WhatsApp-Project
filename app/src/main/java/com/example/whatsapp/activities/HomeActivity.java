package com.example.whatsapp.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.whatsapp.R;
import com.example.whatsapp.adapter.MyAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    FloatingActionButton fab;
    FirebaseAuth auth;
    final  String  userid=FirebaseAuth.getInstance().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        toolbar = (Toolbar) findViewById(R.id.homeToolbar);
        fab = (FloatingActionButton) findViewById(R.id.viewContactActivity);


        auth = FirebaseAuth.getInstance();

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    int id = item.getItemId();

                    if (id == R.id.menu_profile) {
                        startActivity(new Intent(HomeActivity.this, ViewProfile.class));
                        finish();

                    }

                    if(id == R.id.menu_Logout){
                        auth.signOut();
                        auth.signOut();
                        Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    return true;
                }
            });

//            tabLayout.addTab(tabLayout.newTab().setText("CHATS"));
//            tabLayout.addTab(tabLayout.newTab().setText("STATUS"));
//            tabLayout.addTab(tabLayout.newTab().setText("CALLS"));


            final MyAdapter adapter = new MyAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(adapter);

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                    tab.view.setScaleX(1.12f);
                    tab.view.setScaleY(1.12f);

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    tab.view.setScaleX(1.0f);
                    tab.view.setScaleY(1.0f);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            //start Contact Activity
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, ViewContacts.class));
                }
            });

        }// End onCreate method

 void state(String s){
     DatabaseReference reference;
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
     reference= FirebaseDatabase.getInstance().getReference("users").child(userid);
else{
         reference= FirebaseDatabase.getInstance().getReference("users").child(auth.getUid());
         HashMap<String, Object> hashMap = new HashMap();
         hashMap.put("state", s);
         reference.updateChildren(hashMap);
     }
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
