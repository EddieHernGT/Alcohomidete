package com.automatizacion.alcohomidete.main;


import android.content.Intent;
import android.location.GnssAntennaInfo;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.automatizacion.alcohomidete.R;
import com.automatizacion.alcohomidete.people.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.URISyntaxException;

import static android.content.Intent.getIntent;


public class AlcohomideteActivity extends AppCompatActivity{
    User actualUser=null;
    FragmentManager manager=null;
    String nameUser=null;
    Intent intent=null;
    HomeFragment home=null;
    ScoreFragment score=null;
    SettingsFragment settings=null;


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohomidete);
        intent=getIntent();
        nameUser = intent.getStringExtra("userName");
        actualUser=new User(nameUser,this);

        home=new HomeFragment();
        score=new ScoreFragment();
        settings=new SettingsFragment();
        settings.setUserData(actualUser);
        home.setNameUsers(actualUser);
        score.setScore(actualUser);

        BottomNavigationView navegation = findViewById(R.id.navegationMenu);

        loadFragment(home);



        navegation.setOnNavigationItemSelectedListener(navegationSelectedListener);
    }


    private final BottomNavigationView.OnNavigationItemSelectedListener navegationSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.go_home:
                    loadFragment(home);
                    return true;
                case R.id.go_score:
                    loadFragment(score);
                    return true;
                case R.id.go_settings:
                    loadFragment(settings);
                    return true;
                case R.id.go_exit:
                    finish();
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        manager=getSupportFragmentManager();
        FragmentTransaction transaction= manager.beginTransaction();
        transaction.replace(R.id.frameContainer,fragment);
        transaction.commit();
    }

}

