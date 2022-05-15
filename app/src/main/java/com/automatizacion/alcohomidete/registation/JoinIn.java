package com.automatizacion.alcohomidete.registation;

import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.automatizacion.alcohomidete.R;


public class JoinIn extends AppCompatActivity {
    Button signIn=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_in);
        signIn=findViewById(R.id.btSignIn);
    }
    View.OnClickListener joinIn=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };
}