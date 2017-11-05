package com.isuri.ambulancetracker.apptracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

//importing JSON Parsers
public class RegisterActivity extends AppCompatActivity {

    EditText busname,busroute,busdesc,busidtext;
    Button insert,view,viewmyid;
    TextView result;
    RequestQueue requestQueue;

    String inserturl = "http://sksc.dx.am/clay_ptb/create_user.php";

    int busidjson;
    String busnamejson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.isuri.ambulancetracker.apptracker.R.layout.activity_register);

        busname = (EditText) findViewById(com.isuri.ambulancetracker.apptracker.R.id.busnametxt);
        busroute = (EditText) findViewById(com.isuri.ambulancetracker.apptracker.R.id.busroutetxt);
        busdesc = (EditText) findViewById(com.isuri.ambulancetracker.apptracker.R.id.busdesctxt);

        insert = (Button) findViewById(com.isuri.ambulancetracker.apptracker.R.id.regbusbtn);


        requestQueue = Volley.newRequestQueue(getApplicationContext());


        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InternetChecker netcheck = new InternetChecker();

                if(busname.getText().toString().equals("") || busname.getText().toString().trim().length() == 0 || busname.getText().toString().equals(null)){
                    Toast.makeText(RegisterActivity.this, "Please Fill the Required Fields", Toast.LENGTH_LONG).show();
                }
                else if(busroute.getText().toString().equals("") || busroute.getText().toString().equals(" ") || busroute.getText().toString().equals(null)){
                    Toast.makeText(RegisterActivity.this, "Please Fill the Required Fields", Toast.LENGTH_LONG).show();
                }
                else if (!netcheck.haveNetworkConnection(RegisterActivity.this)) { //say user to enable internet if device isn't connected to internet.
                    Toast.makeText(RegisterActivity.this, "Please Enable Mobile Data/Wi-Fi", Toast.LENGTH_LONG).show();
                }
                else {

                    StringRequest request = new StringRequest(Request.Method.POST, inserturl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            final String uid = response;

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                            builder1.setMessage("Successfully Registered.\nYour User ID is " + uid);
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            //save the user id inside the app for later use
                                            SharedPreferences settings = RegisterActivity.this.getSharedPreferences("userdetails", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.clear();
                                            editor.putInt("userid", Integer.parseInt(uid));
                                            editor.commit();
                                            Toast.makeText(RegisterActivity.this, "Login ID is saved..", Toast.LENGTH_SHORT).show();

                                            busname.setText("");
                                            busroute.setText("");
                                            busdesc.setText("");

                                            startNameActivity();
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }

                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> parameters = new HashMap<String, String>();
                            parameters.put("name", busname.getText().toString());
                            parameters.put("route", busroute.getText().toString());
                            parameters.put("desc", busdesc.getText().toString());

                            return parameters;
                        }
                    };

                    requestQueue.add(request);

                }
            }
        });

    }

    public void startNameActivity(){
        Intent intent = new Intent(this, NameActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,NameActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }
}
