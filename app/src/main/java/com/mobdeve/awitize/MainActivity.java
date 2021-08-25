package com.mobdeve.awitize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobdeve.awitize.services.PlayerService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        Intent service = new Intent(this, PlayerService.class);
        startService(service);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent i = new Intent(MainActivity.this, SigninActivity.class);
        startActivity(i);
        finish();
    }
}