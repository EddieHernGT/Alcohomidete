package com.automatizacion.alcohomidete.activities;


import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Handler;
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
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;

public class AlcohomideteActivity extends AppCompatActivity{

    HomeFragment home=new HomeFragment();
    ScoreFragment score=new ScoreFragment();
    SettingsFragment settings=new SettingsFragment();
    TextView alcoholLv=null;

    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();

    ConnectedThread mConnectedThread=null;
    BluetoothManager bluetoothManager=null;
    BluetoothAdapter bluetoothAdapter=null;
    BluetoothSocket btSocket=null;
    Handler bluetoothIn = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == handlerState) {          //if message is what we want
                String readMessage = (String) msg.obj;
                int endOfLineIndex = recDataString.indexOf(";");                    // determine the end-of-line
                if (endOfLineIndex > 0) {                                           // make sure there data before ~
                    String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                    alcoholLv.setText(dataInPrint);
                    int dataLength = dataInPrint.length();       //get length of data received
                    // strIncom =" ";
                    dataInPrint = " ";
                }
            }
        }
    };


    private InputStream mmInStream=null;
    private SimpleBluetoothDeviceInterface deviceInterface=null;


    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");



    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohomidete);

        bluetoothManager= BluetoothManager.getInstance();
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();


        if (bluetoothManager == null) {
            // Bluetooth unavailable on this device :( tell the user
            Toast.makeText(this, "Bluetooth not available.", Toast.LENGTH_LONG).show(); // Replace context with your context instance.
            finish();
        }

        ImageButton btConnections = findViewById(R.id.connectionsList);
        BottomNavigationView navegation = findViewById(R.id.navegationMenu);
        alcoholLv=findViewById(R.id.alcohol_level);

        navegation.setOnNavigationItemSelectedListener(navegationSelectedListener);

        btConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConnections();
            }
        });

        loadFragment(home);
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
                String address=device.substring(device.length()-17);
                connetTo(address);
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
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer,fragment);
        transaction.commit();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public void connetTo(String mac){
        BluetoothDevice device=bluetoothAdapter.getRemoteDevice(mac);
        try {
            Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show();
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread=new ConnectedThread(btSocket, bluetoothIn,handlerState);
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    }

}

