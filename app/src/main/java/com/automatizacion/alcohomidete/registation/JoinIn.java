package com.automatizacion.alcohomidete.registation;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.automatizacion.alcohomidete.R;
import com.automatizacion.alcohomidete.dbconnections.DBCConnectionHelper;



public class JoinIn extends AppCompatActivity {
    Button signIn=null;
    EditText name=null;
    EditText surname=null;
    EditText age=null;
    RadioGroup genderGroup=null;
    RadioButton rdoGender=null;
    EditText userName=null;
    EditText password=null;
    EditText confirmPass=null;
    DBCConnectionHelper conn=null;
    SQLiteDatabase db=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_in);
        name=findViewById(R.id.PersonName);
        surname=findViewById(R.id.PersonSurname);
        age=findViewById(R.id.peopleAge);
        genderGroup=findViewById(R.id.grpGender);
        userName=findViewById(R.id.PersonUserName);
        password=findViewById(R.id.Password);
        confirmPass=findViewById(R.id.PasswordConfirm);
        signIn=findViewById(R.id.btSignIn);
        signIn.setOnClickListener(joinIn);


    }
    View.OnClickListener joinIn=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int genderid=genderGroup.getCheckedRadioButtonId();
            rdoGender=findViewById(genderid);
            String usrName=userName.getText().toString();
            String pass=password.getText().toString();
            String confirmedPass=confirmPass.getText().toString();
            String pName=name.getText().toString();
            String pSurname=surname.getText().toString();
            String pAge=age.getText().toString();
            String gender=rdoGender.getText().toString();
            if(!usrName.isEmpty()|!pass.isEmpty()|!pSurname.isEmpty()|!pAge.isEmpty()){
                try {
                    conn = new DBCConnectionHelper(JoinIn.this);
                    db = conn.getWritableDatabase();
                    Cursor fullName = db.rawQuery("SELECT P_NAME,SURNAME FROM People WHERE P_NAME='" + pName
                            + "' AND USER_NAME='" + pSurname + "';", null);
                    Cursor usrNme = db.rawQuery("SELECT USER_NAME FROM Users WHERE USER_NAME='" + pName + "';", null);
                    if (fullName.moveToFirst()) {
                        name.setError("This person already have an account");
                        surname.setError("This person already have an account");
                        return;
                    } else if (usrNme.moveToFirst()) {
                        userName.setError("User Name not available");
                        return;
                    }
                    if (!pass.equals(confirmedPass)) {
                        password.setError("Password don't match");
                        System.out.println(pass);
                        System.out.println(confirmedPass);
                        confirmPass.setError("Password don't match");
                        return;
                    }
                    joinInNewUser(db, usrName, pass, pName, pSurname, pAge, gender);
                    db.close();
                    Toast.makeText(JoinIn.this, "New registration succeeded", Toast.LENGTH_SHORT).show();
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(JoinIn.this, "All fields must to be fill", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };


    public void joinInNewUser(SQLiteDatabase db,String usrName,String pass,String pName,String pSurname,String pAge,String pGender){
        ContentValues userValues=new ContentValues();
        ContentValues peopleValues=new ContentValues();
        userValues.put("USER_NAME",usrName);
        userValues.put("PASS",pass);
        peopleValues.put("P_NAME",pName);
        peopleValues.put("SURNAME",pSurname);
        peopleValues.put("AGE",pAge);
        peopleValues.put("GENDER",pGender);
        peopleValues.put("USER_NAME",usrName);
        db.insert("Users",null,userValues);
        db.insert("People",null,peopleValues);
    }
}