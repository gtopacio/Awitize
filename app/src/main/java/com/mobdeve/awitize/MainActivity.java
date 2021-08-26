package com.mobdeve.awitize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.awitize.services.DatabaseUpdater;
import com.mobdeve.awitize.services.PlayerService;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        Intent service = new Intent(this, PlayerService.class);
        startService(service);

        Intent database = new Intent(this, DatabaseUpdater.class);
        startService(database);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent i = new Intent(MainActivity.this, SigninActivity.class);
        startActivity(i);
        finish();
    }
}