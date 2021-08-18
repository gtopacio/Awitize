package com.mobdeve.awitize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SigninActivity extends AppCompatActivity {

    private TextView signUp;
    private EditText email;
    private EditText password;
    private Button btnSignin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initComponents();
        mAuth = FirebaseAuth.getInstance();
        if(getIntent().getBooleanExtra("Illegal Access", false)){
            Toast.makeText(this,"Illegal Access, login again", Toast.LENGTH_SHORT).show();
        }
    }

    private void initComponents(){
        signUp = findViewById(R.id.tv_sign_up);
        email = findViewById(R.id.et_email_login);
        password = findViewById(R.id.et_password_login);
        btnSignin = findViewById(R.id.bt_login);

        signUp.setOnClickListener(v -> {
            Intent i = new Intent(SigninActivity.this, SignupActivity.class);
            startActivity(i);
        });

        btnSignin.setOnClickListener(v -> {
            String emailInput = email.getText().toString();
            String passwordInput = password.getText().toString();

            mAuth.signInWithEmailAndPassword(emailInput, passwordInput)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(SigninActivity.this, DashboardActivity.class);
                            startActivity(i);
                            finish();

                        } else {
                            Toast.makeText(SigninActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        });
    }
}