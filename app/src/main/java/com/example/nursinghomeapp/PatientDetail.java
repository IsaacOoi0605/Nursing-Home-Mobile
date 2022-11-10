package com.example.nursinghomeapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class PatientDetail extends AppCompatActivity{
    private String STRURL="http://192.168.56.1/api/";
    Button newBtn;
    TextView textView,textIC,textPhone,PatientDetail,textBed,textDate,textDay;
    ImageView imageView;
    LinearLayout layoutHospitalised,layoutDisease,layoutMedication;
    RelativeLayout layoutPatient;
    String medicineName="" ;
    int ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //intent from previous activity to get patient name
        Intent intent=getIntent();
        ID=intent.getIntExtra(MainActivity.PATIENT_ID,0);
        loadLayout();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //load the textview and imageview with patient info
        loadPatientInfo();
        loadHospitalised();
        loadDisease();
        loadMedicine();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadButton(RelativeLayout layout){
        newBtn=new Button(PatientDetail.this);
        newBtn.setText("new button");
        layout.addView(newBtn);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadButton(layout);
            }
        });
    }

    public void loadLayout(){
        //set up for widget
        setContentView(R.layout.activity_patient_detail2);
        textView=findViewById(R.id.textView);
        imageView=findViewById(R.id.imageView);
        PatientDetail=findViewById(R.id.patientDetail);
        textIC=findViewById(R.id.textIC);
        textPhone=findViewById(R.id.textContact);
        textBed=findViewById(R.id.bedInfo);
        textDate=findViewById(R.id.Date);
        textDay=findViewById(R.id.Day);
        //layout setup
        layoutPatient=(RelativeLayout) findViewById(R.id.Patient);
        layoutPatient.setBackground(ContextCompat.getDrawable(PatientDetail.this, R.drawable.custom_border));
        layoutHospitalised=(LinearLayout) findViewById(R.id.Hospitalised);
        layoutDisease=(LinearLayout) findViewById(R.id.Disease);
        layoutMedication=(LinearLayout) findViewById(R.id.Medicine);
        //action bar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void loadHospitalised(){
        String HOSURL= STRURL+"hospitalised.php?id="+ID;
        StringRequest stringRequest= new StringRequest(Request.Method.GET, HOSURL,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject hospitalised=new JSONObject(response);
                            int bedNo=hospitalised.getInt("BedID");
                            String BedLevel=hospitalised.getString("BedLevel");
                            String bedNoStr=String.valueOf(bedNo);
                            if (bedNo<10){
                                bedNoStr="00"+bedNoStr;
                            }
                            else if(bedNo<100){
                                bedNoStr="0"+bedNoStr;
                            }
                            textBed.setText(String.valueOf("Bed Using:"+BedLevel+"-"+bedNoStr));
                            //date
                            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
                            String date1=hospitalised.getString("Date");
                            Date date=formatter.parse(date1);
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String dateNew=dateFormat.format(date);
                            textDate.setText(String.valueOf("Check In Date:"+dateNew));
                            LocalDate localDate2 = LocalDate.parse(date1);
                            LocalDate localDate = LocalDate.now();
                            long noOfDaysBetween = ChronoUnit.DAYS.between(localDate2,localDate)+1;
                            textDay.setText("Days:"+String.valueOf(noOfDaysBetween));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        Volley.newRequestQueue(PatientDetail.this).add(stringRequest);
    }

    public void loadPatientInfo(){
        String PATURL=STRURL+"patient.php?id="+ID;
        StringRequest stringRequest= new StringRequest(Request.Method.GET, PATURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject patient=new JSONObject(response);
                            textView.setText("Patient Name:"+String.valueOf(patient.getString("name")));
                            textIC.setText("Patient IC:"+String.valueOf(patient.getString("IC")));
                            textPhone.setText("Contact Number:"+String.valueOf(patient.getString("contact")));
                            String img=patient.optString("Image");
                            byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                            Bitmap imgBitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imageView.setImageBitmap(imgBitMap);
                            imageView.getLayoutParams().height = 400;
                            imageView.getLayoutParams().width=600;
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Bitmap bitmap = imageView.getDrawingCache();
                                    //Intent intent = new Intent(this, imageActivity.class);
                                    //intent.putExtra("BitmapImage", bitmap);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),String.valueOf(error),Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(PatientDetail.this).add(stringRequest);
    }

    public void loadDisease(){
        String DISURL=STRURL+"disease.php?id="+ID;
        StringRequest stringRequest= new StringRequest(Request.Method.GET, DISURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray disease=new JSONArray(response);
                            Boolean haveDisease=disease.getBoolean(0);
                            if(haveDisease){
                            for (int i=1;i<disease.length();i++){
                                JSONObject diseaseObject=disease.getJSONObject(i);

                                String disName=diseaseObject.getString("name");

                                TextView dis=new TextView(PatientDetail.this);

                                dis.setText(String.valueOf(disName));

                                dis.setTextSize(18);

                                layoutDisease.addView(dis);
                            }}
                            else{
                                TextView dis=new TextView(PatientDetail.this);
                                dis.setText("Not Having Disease");
                                dis.setTextSize(18);
                                dis.setTypeface(null, Typeface.BOLD);
                                layoutDisease.addView(dis);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),String.valueOf(error),Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(PatientDetail.this).add(stringRequest);
    }

    public void loadMedicine(){
        String MEDURL=STRURL+"medicine.php?id="+ID;
        StringRequest stringRequest= new StringRequest(Request.Method.GET, MEDURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray medicine=new JSONArray(response);
                            Boolean haveMedicine=medicine.getBoolean(0);
                            if(haveMedicine){
                                for (int i=1;i<medicine.length();i++){
                                    JSONObject diseaseObject=medicine.getJSONObject(i);
                                    LinearLayout layoutvariant=new LinearLayout(PatientDetail.this);
                                    layoutvariant.setOrientation(LinearLayout.VERTICAL);
                                    layoutvariant.setBackgroundDrawable(ContextCompat.getDrawable(PatientDetail.this, R.drawable.custom_border) );
                                    String medName=diseaseObject.getString("name");

                                    if(!(medicineName.equals(medName))) {
                                        medicineName=medName;
                                        TextView med=new TextView(PatientDetail.this);

                                        med.setText(String.valueOf(medName));

                                        med.setTextSize(18);

                                        layoutMedication.addView(med);
                                    }
                                    TextView varTab=new TextView(PatientDetail.this);
                                    //set text for tablet
                                    int medTablet=diseaseObject.getInt("tablet");
                                    varTab.setText("Number of tablet:"+String.valueOf(medTablet));
                                    layoutvariant.addView(varTab);
                                    TextView varTime=new TextView(PatientDetail.this);
                                    //set text for number of times taken each day
                                    int medTime=diseaseObject.getInt("time");
                                    varTime.setText("Number of times per day:"+String.valueOf(medTime));
                                    layoutvariant.addView(varTime);
                                    //set text for weight
                                    TextView varWeight=new TextView(PatientDetail.this);
                                    int medWeight=diseaseObject.getInt("weight");
                                    varWeight.setText("Weight:"+String.valueOf(medWeight));
                                    layoutvariant.addView(varWeight);
                                    layoutMedication.addView(layoutvariant);
                                }
                            }
                            else{
                                TextView med=new TextView(PatientDetail.this);
                                med.setText("No Taking Medicine");
                                med.setTextSize(18);
                                med.setTypeface(null, Typeface.BOLD);
                                layoutMedication.addView(med);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),String.valueOf(error),Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(PatientDetail.this).add(stringRequest);
    }
}