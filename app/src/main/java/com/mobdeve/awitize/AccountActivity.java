package com.mobdeve.awitize;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity {

    private TextView emailView;
    private TextView signOut;
    private TextView delete;
    private TextView playlistView;
    private ImageButton back;

    private FirebaseAuth mAuth;
    private String prevClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        prevClass = getIntent().getStringExtra(IntentKeys.PREVIOUS_CLASS.name());
        mAuth = FirebaseAuth.getInstance();
        loadComponents();
    }

    private void loadComponents() {
        emailView = findViewById(R.id.tv_acc_email);
        signOut = findViewById(R.id.tv_acc_signout);
        delete = findViewById(R.id.tv_acc_delete);
        playlistView = findViewById(R.id.tv_acc_playlist);
        back = findViewById(R.id.ib_back_acc);

        emailView.setText(mAuth.getCurrentUser().getEmail());

        back.setOnClickListener(v -> {
            try {
                Intent i = new Intent(AccountActivity.this, Class.forName(prevClass));
                startActivity(i);
                finish();
            }
            catch (ClassNotFoundException ex) {
                Log.d("Error", "Class Not Found");
                Toast.makeText(this,"Error Class Not Found", Toast.LENGTH_SHORT).show();
            }
        });

        signOut.setOnClickListener(v -> {
            mAuth.signOut();
            Intent i = new Intent(AccountActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });

        delete.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            user.delete().addOnCompleteListener(task -> {
               if(task.isSuccessful()){
                   Toast.makeText(this, "Account Deletion is successful", Toast.LENGTH_SHORT).show();
               }
               else{
                   Toast.makeText(this, "Account Deletion is NOT successful", Toast.LENGTH_SHORT).show();
               }
                Intent i = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            });
        });
    }
}