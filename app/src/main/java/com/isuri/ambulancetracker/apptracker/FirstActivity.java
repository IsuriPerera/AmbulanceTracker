package com.isuri.ambulancetracker.apptracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.isuri.ambulancetracker.apptracker.R.layout.activity_first);
    }

    public void onClickServiceButton(View view){
        Intent intent = new Intent(this, NameActivity.class);
        startActivity(intent);
    }

    public void onClickPassengerButton(View view){

        InternetChecker netcheck = new InternetChecker();

        if(!netcheck.haveNetworkConnection(FirstActivity.this)){ //say user to enable internet if device isn't connected to internet.
            Toast.makeText(FirstActivity.this, "Please Enable Mobile Data/Wi-Fi", Toast.LENGTH_LONG).show();
        }else {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("IsPassenger", true);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.isuri.ambulancetracker.apptracker.R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RelativeLayout layoutmain = (RelativeLayout) findViewById(com.isuri.ambulancetracker.apptracker.R.id.layoutmain);

        switch (item.getItemId()){
            case com.isuri.ambulancetracker.apptracker.R.id.about:
                Intent intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
