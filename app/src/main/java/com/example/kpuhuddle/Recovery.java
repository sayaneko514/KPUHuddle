package com.example.kpuhuddle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

public class Recovery extends AppCompatActivity implements View.OnClickListener {

    private TextView register;
    private EditText editTextEmail;
    private Button recoveryBtn;

    private FirebaseAuth mAuth;
    AuditLog log = new AuditLog();
    private DatabaseReference aLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        editTextEmail = findViewById(R.id.RecoveryEmail);
        editTextEmail.setOnClickListener(this);
        recoveryBtn = findViewById(R.id.RecoveryBtn);
        recoveryBtn.setOnClickListener(this);
        register = findViewById(R.id.RecoverySignUp);
        register.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.RecoveryBtn:
                resetPassword();
                break;
            case R.id.RecoverySignUp:
                startActivity(new Intent(Recovery.this, SignUp.class));
        }
    }

    public void resetPassword()
    {
        String email = editTextEmail.getText().toString().trim();

        if (email.isEmpty())
        {
            editTextEmail.setError("Email cannot be blank!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Please enter a valid Email address!");
            editTextEmail.requestFocus();
            return;
        }

        //enable this validation after testing complete
        /*if (!email.endsWith("email.kpu.ca"))
        {
            editTextEmail.setError("Only KPU emails are acceptable!");
            editTextEmail.requestFocus();
            return;
        }*/

        logPasswordRecovery(email);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(Recovery.this, "An password recovery email has been sent to your email, please check your inbox", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(Recovery.this, "Something wrong with our server, please try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void logPasswordRecovery(String email)
    {
        aLog = FirebaseDatabase.getInstance().getReference().child("Logs");
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String logTimeStamp = dateFormat.format(currentTime);
        String logDesc = "Password Recovery Request For " + email;
        log.setLogDesc(logDesc);
        log.setLogTimeStamp(logTimeStamp);
        log.setLogUser("NULL");
        log.setLogID("LOG"+Long.toString(System.currentTimeMillis()));
        aLog.child(log.getLogID()).setValue(log);
    }
}