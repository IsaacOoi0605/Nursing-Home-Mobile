package com.example.nursinghomeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    ImageView imageView;
    Button btnLogin;
    SharedPreferences sp;
    String URL="http://192.168.56.1/api/login.php";
    EditText editName,editPass;
    boolean verified;
    boolean doubleBackToExitPressedOnce=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.login_layout);
        imageView=findViewById(R.id.imgHospital);
        imageView.setImageResource(R.drawable.hospital);
        btnLogin=findViewById(R.id.btnLogin);
        editName=findViewById(R.id.editName);
        editPass=findViewById(R.id.editPass);
        sp=getSharedPreferences("UserProf",MODE_PRIVATE);
        getSupportActionBar().hide();
        btnClick();
    }

    public void btnClick(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginVerify();
            }
        });
    }

    public void loginVerify(){
        String STRURL=URL+"?username="+editName.getText()+"&password="+editPass.getText();
        StringRequest stringRequest= new StringRequest(Request.Method.GET, STRURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        verified=Boolean.parseBoolean(response);
                        loginValidate();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),String.valueOf(error),Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(LoginActivity.this).add(stringRequest);

    }

    public void loginValidate(){
        if(verified){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            SharedPreferences.Editor editor=sp.edit();
            editor.putBoolean("Login",true);
            editor.putString("username",String.valueOf(editName.getText()));
            editor.putString("password",String.valueOf(editPass.getText()));
            Toast.makeText(getApplicationContext(),"Welcome",Toast.LENGTH_SHORT).show();
            editor.commit();
            finish();
            startActivity(intent);}
        else{
            Toast.makeText(getApplicationContext(),"Incorrect Username and Password!",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 1000);
    }
}
