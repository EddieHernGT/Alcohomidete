package com.automatizacion.alcohomidete.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.automatizacion.alcohomidete.R;
import com.automatizacion.alcohomidete.people.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    User actualUser=null;
    EditText txtUserName=null;
    EditText txtPass=null;
    ImageButton btnEnableUserName=null;
    ImageButton btnEnablePass=null;
    Button btnUpdate=null;
    private String mParam1;
    private String mParam2;
    private String userName=null;
    private String password=null;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_settings, container, false);

        txtUserName=v.findViewById(R.id.fsUserName);
        txtPass=v.findViewById(R.id.fsPassword);

        btnEnableUserName = v.findViewById(R.id.enableUser);
        btnEnablePass = v.findViewById(R.id.enablePassword);
        btnUpdate=v.findViewById(R.id.updateButton);

        updateInfo();

        btnEnableUserName.setOnClickListener(enableUserName);
        btnEnablePass.setOnClickListener(enablePass);
        btnUpdate.setOnClickListener(updateData);

        return v;
    }

    private void updateInfo() {
        txtUserName.setText(actualUser.getUserName());
        txtPass.setText(actualUser.getPassword());
    }

    View.OnClickListener updateData=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actualUser.updateData(getContext(),String.valueOf(txtUserName.getText()),String.valueOf(txtPass.getText()));
            updateInfo();
            Toast.makeText(getContext(), "Data Updated", Toast.LENGTH_SHORT).show();
            txtUserName.setEnabled(false);
            txtPass.setEnabled(false);
        }
    };


    View.OnClickListener enableUserName=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            txtUserName.setEnabled(true);
            txtUserName.requestFocus();
        }
    };
    View.OnClickListener enablePass=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            txtPass.setEnabled(true);
            txtPass.requestFocus();
        }
    };

    public void setUserData(User actual) {
        this.actualUser=actual;
    }
}