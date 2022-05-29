package com.automatizacion.alcohomidete.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.automatizacion.alcohomidete.R;
import com.automatizacion.alcohomidete.bluetooth.ConnectedThread;
import com.automatizacion.alcohomidete.dbconnections.DBCConnectionHelper;
import com.automatizacion.alcohomidete.people.User;
import com.automatizacion.alcohomidete.ui.login.LoginActivity;
import com.harrysoft.androidbluetoothserial.BluetoothManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import static android.content.Intent.getIntent;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    User actualUser=null;


    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();
    private static  String address=null;

    ConnectedThread mConnectedThread=null;
    BluetoothManager bluetoothManager=null;
    BluetoothAdapter bluetoothAdapter=null;
    BluetoothSocket btSocket=null;
    Handler bluetoothIn = null;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    TextView alcoholLv=null;
    ImageButton btnUpload=null;
    Button btnOpenUber=null;
    Button btnCallHelp=null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("Range")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_home, container, false);
        alcoholLv=v.findViewById(R.id.alcohol_level);
        ImageButton btConnections = v.findViewById(R.id.connectionsList);
        btnUpload=v.findViewById(R.id.updateScoreButton);
        btnOpenUber=v.findViewById(R.id.openUber);
        btnCallHelp=v.findViewById(R.id.callHelp);
        TextView userName=v.findViewById(R.id.userName);

        userName.setText(actualUser.getName());
        btnUpload.setOnClickListener(uploadScore);

        btnOpenUber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.ubercab"));
                    startActivity(i);
                }catch (Exception e){e.printStackTrace();}
            }
        });
        btnCallHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.contacts"));
                    startActivity(i);
                }catch (Exception e){e.printStackTrace();}
            }
        });

        bluetoothManager= BluetoothManager.getInstance();
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();

        if (bluetoothManager == null) {
            // Bluetooth unavailable on this device :( tell the user
            Toast.makeText(getActivity(), "Bluetooth not available.", Toast.LENGTH_LONG).show(); // Replace context with your context instance.
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
                        try {
                            alcoholLv.setText(lvAlhl);
                        }catch (Exception e){e.printStackTrace();}
                    }
                }
                recDataString.delete(0, recDataString.length());      //clear all string data
            }
        };

        btConnections.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                showConnections();
            }
        });
        return v;
    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public void  showConnections(){
        AlertDialog.Builder aConnectionBuilder=new AlertDialog.Builder(getActivity());
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
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
                    Toast.makeText(getActivity(), "Can't reach the device", Toast.LENGTH_LONG).show();
            }
        });
        aConnectionBuilder.show();
    }

    public boolean connetTo(){
        BluetoothDevice device=bluetoothAdapter.getRemoteDevice(address);
        try {
            Toast.makeText(getActivity(), "Connecting...", Toast.LENGTH_SHORT).show();
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getActivity().getBaseContext(), "Socket creation filed", Toast.LENGTH_LONG).show();
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
        Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
        return true;
    }

    View.OnClickListener uploadScore=new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View v) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat shf = new SimpleDateFormat("HH:mm:ss");
            String date = sdf.format(new Date());
            String hour=shf.format(new Date());
            String alcoholLevel= alcoholLv.getText().toString();
            try {
                if (Integer.parseInt(String.valueOf(Math.round(Double.parseDouble(alcoholLevel))))>500){
                    Toast.makeText(getContext(), "You drank too much...", Toast.LENGTH_SHORT).show();
                    btnOpenUber.setEnabled(true);
                    btnCallHelp.setEnabled(true);
                }
            }catch (Exception e){e.printStackTrace();}
            if (!alcoholLevel.equals("---")) {
                if (actualUser.getScoreID() == null) {
                    UUID randomUUID = UUID.randomUUID();
                    String id = randomUUID.toString().replaceAll("_", "");
                    try {
                        DBCConnectionHelper conn = new DBCConnectionHelper(getContext());
                        SQLiteDatabase db = conn.getWritableDatabase();
                        db.execSQL("UPDATE Users SET RECORD_ID='" + id + "' WHERE USER_NAME='" + actualUser.getUserName() + "';");
                        conn.close();
                        actualUser.setScoreID(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    DBCConnectionHelper conn = new DBCConnectionHelper(getContext());
                    SQLiteDatabase db = conn.getWritableDatabase();
                    ContentValues scoreData = new ContentValues();
                    scoreData.put("RECORD_ID", actualUser.getScoreID());
                    scoreData.put("ALCOHOLIC_GRADE", alcoholLevel);
                    scoreData.put("REGISTER_DATE", date);
                    scoreData.put("REGISTER_TIME", hour);
                    db.insert("Score", null, scoreData);
                    conn.close();
                    Toast.makeText(getContext(), "Successful update", Toast.LENGTH_SHORT).show();
                    alcoholLv.setText("---");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else Toast.makeText(getContext(), "No Score received", Toast.LENGTH_SHORT).show();
        }
    };

    public  void setNameUsers(User user){
        this.actualUser=user;
    }
}