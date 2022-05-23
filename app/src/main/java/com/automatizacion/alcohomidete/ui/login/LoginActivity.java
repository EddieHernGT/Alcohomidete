package com.automatizacion.alcohomidete.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.automatizacion.alcohomidete.R;
import com.automatizacion.alcohomidete.databinding.ActivityLoginBinding;
import com.automatizacion.alcohomidete.dbconnections.DBCConnectionHelper;
import com.automatizacion.alcohomidete.main.AlcohomideteActivity;
import com.automatizacion.alcohomidete.registation.ForgotPassword;
import com.automatizacion.alcohomidete.registation.JoinIn;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    Cursor loginData=null;
    DBCConnectionHelper conn=null;
    SQLiteDatabase db=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final Button forgotPass=binding.forgot;
        final Button singIn=binding.SignIn;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                String iUserName=usernameEditText.getText().toString();
                String iPass=passwordEditText.getText().toString();
                String userName=null;
                String userPass=null;
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
                try {
                    conn = new DBCConnectionHelper(LoginActivity.this);
                    db = conn.getWritableDatabase();
                    loginData = db.rawQuery("SELECT USER_NAME,PASS FROM Users WHERE USER_NAME='"
                            + usernameEditText.getText() + "';", null);
                    if (loginData.moveToFirst()) {
                        //Complete and destroy login activity once successful
                        loginData.moveToFirst();
                        do {
                            userName= loginData.getString(loginData.getColumnIndex("USER_NAME"));
                            userPass=loginData.getString(loginData.getColumnIndex("PASS"));
                        }while (loginData.moveToNext());
                        conn.close();
                        if(userName.equals(iUserName)&&iPass.equals(userPass)) {
                            Intent alcohomideteActivity = new Intent(LoginActivity.this, AlcohomideteActivity.class);
                            alcohomideteActivity.putExtra("userName", userName);
                            startActivity(alcohomideteActivity);
                        }else {
                            Toast.makeText(LoginActivity.this, "Not valid user or password", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(LoginActivity.this, "Couldn't find user", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        singIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent JoinIn = new Intent(LoginActivity.this, JoinIn.class);
                startActivity(JoinIn);
            }
        });
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotActivity = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(forgotActivity);
            }
        });
    }
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        //Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_SHORT).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        //Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}