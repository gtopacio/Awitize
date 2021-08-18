package com.mobdeve.awitize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseUser user;
    private TextView emailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_login);

        user = FirebaseAuth.getInstance().getCurrentUser();

        emailView = findViewById(R.id.tv_test_email);

        if(user != null){
            String email = user.getEmail();
            emailView.setText(email);
        }
        else{
            Intent i = new Intent(DashboardActivity.this, SigninActivity.class);
            i.putExtra("Illegal Access", true);
            startActivity(i);
            finish();
        }
    }
}