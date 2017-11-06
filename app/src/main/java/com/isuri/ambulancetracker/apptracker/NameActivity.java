package com.isuri.ambulancetracker.apptracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

//importing JSON Parsers

public class NameActivity extends AppCompatActivity {

    EditText busname,busroute,busdesc,busidtext;
    Button insert,view,viewmyid;
    TextView result;
    RequestQueue requestQueue;

    String viewurl = "http://sksc.dx.am/clay_ptb/get_user_details.php?busid=";

    int busidjson;
    String busnamejson , busroutejson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.isuri.ambulancetracker.apptracker.R.layout.activity_name);

        //final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etID = (EditText) findViewById(com.isuri.ambulancetracker.apptracker.R.id.etID);

        Button bSaveName = (Button) findViewById(com.isuri.ambulancetracker.apptracker.R.id.btnDone);


        requestQueue = Volley.newRequestQueue(getApplicationContext());

        bSaveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LocationManager lm = (LocationManager) NameActivity.this.getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                boolean network_enabled = false;

                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch (Exception ex) {
                }

                try {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception ex) {
                }

                InternetChecker netcheck = new InternetChecker();

                if(!netcheck.haveNetworkConnection(NameActivity.this)){ //say user to enable internet if device isn't connected to internet.
                    Toast.makeText(NameActivity.this,"Please Enable Mobile Data for Accurate Service", Toast.LENGTH_LONG).show();
                }
                else if (!gps_enabled && !network_enabled) {
                    // notify user
                    AlertDialog.Builder dialog = new AlertDialog.Builder(NameActivity.this);
                    dialog.setMessage(NameActivity.this.getResources().getString(com.isuri.ambulancetracker.apptracker.R.string.gps_network_not_enabled));
                    dialog.setPositiveButton(NameActivity.this.getResources().getString(com.isuri.ambulancetracker.apptracker.R.string.open_location_settings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            NameActivity.this.startActivity(myIntent);
                            //get gps
                        }
                    });
                    dialog.setNegativeButton(NameActivity.this.getString(com.isuri.ambulancetracker.apptracker.R.string.Cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub

                        }
                    });
                    dialog.show();
                }
                else {



                    //////////Getting login details from the database

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                            viewurl+etID.getText().toString(),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try{
                                        JSONObject userobj = response.getJSONObject("user");

                                        busidjson = userobj.getInt("busid");
                                        busnamejson = userobj.getString("name");
                                        busroutejson = userobj.getString("route");
                                        String busdescjson = userobj.getString("desc");
                                        String createdat = userobj.getString("created_at");
                                        String updatedat = userobj.getString("updated_at");

                                        //result.append(busidjson+"\n"+busnamejson+"\n"+busroutejson+"\n"+busdescjson+"\n"+createdat+"\n"+updatedat+"\n");




                                        Intent intent = new Intent(NameActivity.this, MapsActivity.class); //Passing data to MapsActivity
                                        intent.putExtra("Username", "["+busnamejson + " / " + busroutejson+"]");
                                        intent.putExtra("UserID", String.valueOf(busidjson));
                                        intent.putExtra("UserDetails","Your ID : "+busidjson+"\nName : "+busnamejson+"\nTelephone : "+busroutejson+"\nDescription : "+busdescjson);
                                        intent.putExtra("IsPassenger",false);
                                        startActivity(intent);


                                        SharedPreferences settings = NameActivity.this.getSharedPreferences("userdetails", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.clear();
                                        editor.putInt("userid", busidjson);
                                        editor.commit();


                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {


                            //alert dialog display for errors

                            android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(NameActivity.this);
                            builder1.setMessage("Entered ID doesn't exists. Please try again!");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            android.app.AlertDialog alert11 = builder1.create();
                            alert11.show();


                        }
                    });
                    requestQueue.add(jsonObjectRequest);



                    //////////Getting login details from the database (end)


                }
            }
        });


        //autofill login id from shared preferences

        SharedPreferences userDetails = NameActivity.this.getSharedPreferences("userdetails", MODE_PRIVATE);
        int Uname = userDetails.getInt("userid",-1);
//                String Uname = "sajkglasjg";

        if(Uname != -1){
            etID.setText(String.valueOf(Uname));
        }

    }


    public void onClickRegister(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }

}
