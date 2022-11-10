package com.example.nursinghomeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Random;

public class Verification extends AppCompatActivity{
    String username,password;
    Boolean verified;
    SharedPreferences sp;
    ConstraintLayout splash;
    TextView textQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.splash_layout);
        splash=findViewById(R.id.splash);
        textQuote=findViewById(R.id.textQuote);
        Resources res = getResources();
        TypedArray myImages = res.obtainTypedArray(R.array.apptour);
        String[] myText=res.getStringArray(R.array.textArray);
        Random random = new Random();
        int randomInt = random.nextInt(myImages.length());
        int randomIntText=random.nextInt(myText.length);
        int drawableID = myImages.getResourceId(randomInt, -1);
        splash.setBackgroundResource(drawableID);
        textQuote.setText(String.valueOf(myText[randomIntText]));
        sp=getApplicationContext().getSharedPreferences("UserProf", Context.MODE_PRIVATE);
        boolean logged=sp.getBoolean("Login",false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(logged){
                    username=sp.getString("username","");
                    password=sp.getString("password","");
                     loginValidate();}
                else{
                    Intent intent=new Intent(Verification.this,LoginActivity.class);
                    finish();
                    startActivity(intent);}
            }
        }, 2000);

        }

        public void loginValidate(){
            String URL="http://192.168.56.1/api/login.php"+"?username="+username+"&password="+password;
            StringRequest stringRequest= new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            verified=Boolean.parseBoolean(response);
                            loginRedirect();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),String.valueOf(error),Toast.LENGTH_SHORT).show();
                        }
                    });
            Volley.newRequestQueue(Verification.this).add(stringRequest);
        }

    public void loginRedirect(){
        if(verified){
            Intent intent=new Intent(Verification.this,MainActivity.class);
            Toast.makeText(getApplicationContext(),"Welcome Back",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(intent);}
        else{
            SharedPreferences.Editor editor=sp.edit();
            editor.putBoolean("Login",false);
            editor.commit();
            Toast.makeText(getApplicationContext(),"Password Changed or User has been removed. Please re-login again.",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(Verification.this,LoginActivity.class);
            finish();
            startActivity(intent);}
    }
    }

