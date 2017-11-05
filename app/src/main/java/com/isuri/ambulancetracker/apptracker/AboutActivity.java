package com.isuri.ambulancetracker.apptracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.isuri.ambulancetracker.apptracker.R.layout.activity_about);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,FirstActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }

}
