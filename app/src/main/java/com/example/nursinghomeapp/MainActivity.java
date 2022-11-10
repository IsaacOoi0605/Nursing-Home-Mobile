package com.example.nursinghomeapp;

import static android.os.Build.ID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.SearchManager;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface{
    RecyclerView recyclerview;
    private List<Patient> patientList;
    private static final String URL="http://192.168.56.1/api/hospitalisedPatient.php";
    public static final String PATIENT_ID="0";
    private String Name;
    boolean doubleBackToExitPressedOnce=false;
    boolean clicked=false;
    ArrayList<Patient> filteredlist = new ArrayList<Patient>();
    int ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        patientList=new ArrayList<>();
        recyclerview=findViewById(R.id.recyclerview);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        loadPatient();
    }
    private void loadPatient(){
        patientList.clear();
        StringRequest stringRequest= new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray patient=new JSONArray(response);
                            for (int i=0;i<patient.length();i++){
                                JSONObject patientObject=patient.getJSONObject(i);
                                int id=patientObject.getInt("id");
                                int ic=patientObject.getInt("IC");
                                String name=patientObject.getString("Name");

                                Patient patientNew=new Patient();
                                patientNew.setIC(ic);
                                patientNew.setId(id);
                                patientNew.setName(name);

                                patientList.add(patientNew);
                            }
                            PatientAdapter adapter=new PatientAdapter(MainActivity.this,patientList,MainActivity.this);
                            recyclerview.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(this).add(stringRequest);
            }
    @Override
    public void onPatientClick(int position) {
        if(clicked){
            ID=filteredlist.get(position).getId();
        }
        else{
        ID=patientList.get(position).getId();}
        Intent intent=new Intent(MainActivity.this,PatientDetail.class);
        intent.putExtra(PATIENT_ID,ID);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.main_action,menu);
        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                clicked=true;
                filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        filteredlist.clear();
        // running a for loop to compare elements.
        for (Patient item : patientList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getName().contains(text)) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            PatientAdapter adapter=new PatientAdapter(MainActivity.this,filteredlist,MainActivity.this);
            recyclerview.setAdapter(adapter);
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Patient Found", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            PatientAdapter adapter=new PatientAdapter(MainActivity.this,filteredlist,MainActivity.this);
            recyclerview.setAdapter(adapter);
        }


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.refresh:
                loadPatient();
                Toast.makeText(getApplicationContext(), "Refresh Successfully", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                SharedPreferences sp=getApplicationContext().getSharedPreferences("UserProf", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putBoolean("Login",false);
                editor.commit();
                finish();
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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