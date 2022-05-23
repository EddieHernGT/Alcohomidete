package com.automatizacion.alcohomidete.registation;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.automatizacion.alcohomidete.R;
import com.automatizacion.alcohomidete.dbconnections.DBCConnectionHelper;

public class ForgotPassword extends AppCompatActivity {
    Button update=null;
    EditText name=null;
    EditText surname=null;
    EditText userName=null;
    EditText password=null;
    EditText confirmPass=null;
    DBCConnectionHelper conn=null;
    SQLiteDatabase db=null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        name=findViewById(R.id.afName);
        surname=findViewById(R.id.afName);
        userName=findViewById(R.id.afUserName);
        password=findViewById(R.id.afPassword);
        confirmPass=findViewById(R.id.afPasswordConfirm);
        update=findViewById(R.id.ChangeButton);
        update.setOnClickListener(updatePass);
    }
    View.OnClickListener updatePass=new View.OnClickListener() {
        @SuppressLint("Range")
        @Override
        public void onClick(View view) {
            String confirmedPass=confirmPass.getText().toString();
            String usrName=userName.getText().toString();
            String pass=password.getText().toString();
            String pName=name.getText().toString();
            String pSurname=surname.getText().toString();
            System.out.println(pass+"*****"+confirmedPass);
            if(!usrName.isEmpty()|!pass.isEmpty()|!pSurname.isEmpty()){
                try {
                    conn = new DBCConnectionHelper(ForgotPassword.this);
                    db = conn.getWritableDatabase();
                    Cursor fullName = db.rawQuery("SELECT P_NAME,SURNAME,USER_NAME FROM People WHERE P_NAME='" + pName
                            + "' AND USER_NAME='" + usrName + "';", null);
                    if (!fullName.moveToFirst()) {
                        name.setError("This person have no account");
                        surname.setError("This person have no account");
                        db.close();
                    } else if (!pass.equals(confirmedPass)) {
                        password.setError("Password don't match");
                        confirmPass.setError("Password don't match");
                        db.close();
                    }
                    updateUserPass(db, usrName, pass);
                    db.close();
                    Toast.makeText(ForgotPassword.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(ForgotPassword.this, "All fields must be fill", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void updateUserPass(SQLiteDatabase db, String usrName, String pass) {
        db.execSQL("UPDATE Users SET PASS='"+pass+"' WHERE USER_NAME='"+usrName+"';");
    }
}