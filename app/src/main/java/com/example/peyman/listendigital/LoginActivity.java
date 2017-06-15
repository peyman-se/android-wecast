package com.example.peyman.listendigital;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.peyman.listendigital.REST.ApiUtils;
import com.example.peyman.listendigital.REST.Calls;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText et_email, et_password;
    Button bt_login, bt_link_to_register;
    private Calls serverCall;
    private ProgressDialog progressDialog;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ApiUtils server = new ApiUtils();
        serverCall = server.callGuestServer();
        progressDialog = new ProgressDialog(this);
        prefs = this.getSharedPreferences("token", Context.MODE_PRIVATE);

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        bt_login = (Button) findViewById(R.id.bt_login);
        bt_link_to_register = (Button) findViewById(R.id.bt_link_to_register);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                if (!email.isEmpty() && !password.isEmpty()) {
                    checkLogin(email, password);
                } else {
                    Toast.makeText(LoginActivity.this, R.string.login_field_empty_alert, Toast.LENGTH_LONG).show();
                }
            }
        });

        bt_link_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerPage = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerPage);
                finish();
            }
        });
    }

    private void checkLogin(final String email, final String password) {
        progressDialog.show();
        serverCall.login(email, password).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.dismiss();
                if (!response.body().equals("302")) {
                    String token = response.body();
                    //store token in shared preferences.
                    prefs.edit().putString("token", "Bearer " + token).commit();
                    Log.i("peyman", "Login activity token is " + token);
                    Intent loginIntent = new Intent(LoginActivity.this, EnterActivity.class);
                    startActivity(loginIntent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login or Password are wrong. please try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Something wrong with server connection.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
