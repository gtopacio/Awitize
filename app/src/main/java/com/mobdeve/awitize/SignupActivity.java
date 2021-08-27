package com.mobdeve.awitize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobdeve.awitize.helpers.DatabaseHelper;

public class SignupActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText cfmPassword;
    private Button btnSignUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        initComponents();
    }

    private void initComponents() {
        email = findViewById(R.id.et_email_signup);
        password = findViewById(R.id.et_password_signup);
        cfmPassword = findViewById(R.id.et_cfm_password_signup);
        btnSignUp = findViewById(R.id.bt_signup);

        btnSignUp.setOnClickListener(v -> {
            String emailInput = email.getText().toString();
            String passwordInput = password.getText().toString();
            String cfmPasswordInput = cfmPassword.getText().toString();

            if(!passwordInput.equals(cfmPasswordInput)){
                Toast.makeText(this, "Passwords are not the same", Toast.LENGTH_SHORT).show();
                return;
            }

            if(passwordInput.length() < 8){
                Toast.makeText(this, "Passwords needs a minimum of 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        DatabaseHelper helper = new DatabaseHelper(SignupActivity.this);
                        FirebaseUser user = task.getResult().getUser();
                        helper.addUser(user.getEmail(), user.getUid());
                        Intent i = new Intent(SignupActivity.this, DashboardActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        Toast.makeText(SignupActivity.this, "Signup Not Successful", Toast.LENGTH_SHORT).show();
                        Log.w("Error", task.getException().toString());
                    }
                }
            });

        });

    }
}