package com.mobdeve.awitize;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobdeve.awitize.services.PlayerService;

public class AccountActivity extends AppCompatActivity {

    private TextView emailView;
    private TextView signOut;
    private TextView delete;
    private TextView playlistView;
    private ImageButton back;

    private FirebaseAuth mAuth;
    private String prevClass;

    private PlayerService playerService;
    private boolean isServiceBounded = false;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder) service;
            playerService = binder.getService();
            isServiceBounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBounded = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        prevClass = getIntent().getStringExtra(IntentKeys.PREVIOUS_CLASS.name());
        mAuth = FirebaseAuth.getInstance();

        Intent i = new Intent(this, PlayerService.class);
        bindService(i, connection, Context.BIND_AUTO_CREATE);

        loadComponents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
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

                if(prevClass == null){
                    Intent i = new Intent(AccountActivity.this, DashboardActivity.class);
                    startActivity(i);
                    finish();
                    return;
                }

                Intent i = new Intent(AccountActivity.this, Class.forName(prevClass));
                startActivity(i);
                finish();
            }
            catch (ClassNotFoundException ex) {
                Log.d("Error", "Class Not Found");
                Toast.makeText(this,"Error Class Not Found", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(AccountActivity.this, DashboardActivity.class);
                startActivity(i);
                finish();
            }
        });

        signOut.setOnClickListener(v -> {
            mAuth.signOut();
            Intent i = new Intent(AccountActivity.this, MainActivity.class);
            startActivity(i);
            finish();
            if(isServiceBounded){
                playerService.destroySession();
            }
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
                if(isServiceBounded){
                    playerService.destroySession();
                }
                Intent i = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            });
        });
    }
}