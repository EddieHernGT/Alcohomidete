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

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

public class AlcohomideteActivity extends AppCompatActivity{
    FragmentManager manager=null;

    HomeFragment home=null;
    ScoreFragment score=null;
    SettingsFragment settings=null;


    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();
    private static  String address=null;

    ConnectedThread mConnectedThread=null;
    BluetoothManager bluetoothManager=null;
    BluetoothAdapter bluetoothAdapter=null;
    BluetoothSocket btSocket=null;
    Handler bluetoothIn = null;



    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohomidete);
        home=new HomeFragment();
        score=new ScoreFragment();
        settings=new SettingsFragment();

        loadFragment(home);

        ImageButton btConnections = findViewById(R.id.connectionsList);
        BottomNavigationView navegation = findViewById(R.id.navegationMenu);

        bluetoothManager= BluetoothManager.getInstance();
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        if (bluetoothManager == null) {
            // Bluetooth unavailable on this device :( tell the user
            Toast.makeText(this, "Bluetooth not available.", Toast.LENGTH_LONG).show(); // Replace context with your context instance.
            finish();
        }


        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                String readMessage = (String) msg.obj;
                recDataString.append(readMessage);              //keep appending to string until ~
                int endOfLineIndex = recDataString.indexOf(";");
                String dataInPrint = recDataString.substring(0, endOfLineIndex);
                int dataLength = dataInPrint.length();       //get length of data received
                if (endOfLineIndex > 0) {
                    if (recDataString.charAt(0) == '#') {
                        String lvAlhl = recDataString.substring(1, dataLength);
                        Toast.makeText(AlcohomideteActivity.this, lvAlhl, Toast.LENGTH_SHORT).show();
                    }
                }
                recDataString.delete(0, recDataString.length());      //clear all string data
            }
        };

        navegation.setOnNavigationItemSelectedListener(navegationSelectedListener);

        btConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConnections();
            }
        });

    }


    public void  showConnections(){
        AlertDialog.Builder aConnectionBuilder=new AlertDialog.Builder(this);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(AlcohomideteActivity.this, android.R.layout.simple_list_item_1);
        Collection<BluetoothDevice> pairedDevices = bluetoothManager.getPairedDevicesList();
        for (BluetoothDevice device : pairedDevices) {
            adapter.add(device.getName()+"/"+device.getAddress());
        }
        aConnectionBuilder.setTitle("Name/Address");
        aConnectionBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String device = adapter.getItem(i);
                address=device.substring(device.length()-17);
                if(connetTo())
                    mConnectedThread.start();
                else
                    Toast.makeText(AlcohomideteActivity.this, "Can't reach the device", Toast.LENGTH_LONG).show();
            }
        });
        aConnectionBuilder.show();
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


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public boolean connetTo(){
        BluetoothDevice device=bluetoothAdapter.getRemoteDevice(address);
        try {
            Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show();
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation filed", Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
                return false;
            }
            return false;
        }
        mConnectedThread=new ConnectedThread(btSocket, bluetoothIn,handlerState);
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        return true;
    }

}

