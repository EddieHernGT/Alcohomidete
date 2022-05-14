package com.automatizacion.alcohomidete.activities;


import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Handler;
import androidx.fragment.app.FragmentManager;
import com.automatizacion.alcohomidete.bluetooth.ConnectedThread;
import com.harrysoft.androidbluetoothserial.BluetoothManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.automatizacion.alcohomidete.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;



public class AlcohomideteActivity extends AppCompatActivity{
    FragmentManager manager=null;

    HomeFragment home=null;
    ScoreFragment score=null;
    SettingsFragment settings=null;


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohomidete);

        home=new HomeFragment();
        score=new ScoreFragment();
        settings=new SettingsFragment();

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

