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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private TextView register, recovery;
    private EditText editTextEmail, editTextPassword;
    private Button logIn;
    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference aLog;
    private String userid;
    AuditLog log = new AuditLog();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        register = (TextView)findViewById(R.id.LoginSignUp);
        register.setOnClickListener(this);

        logIn = (Button)findViewById(R.id.LoginBtn);
        logIn.setOnClickListener(this);

        recovery = (TextView)findViewById(R.id.LoginForgot);
        recovery.setOnClickListener(this);

        editTextEmail = (EditText)findViewById(R.id.LoginEmail);
        editTextPassword = (EditText)findViewById(R.id.LoginPassword);

    }

    @Override
    public void onClick(View view)
    {
       switch (view.getId())
       {
           case R.id.LoginSignUp:
               startActivity(new Intent(this, SignUp.class));
               break;
           case R.id.LoginBtn:
               userLogin();
               break;
           case R.id.LoginForgot:
               startActivity(new Intent(this, Recovery.class));
               break;
       }
    }

    private void userLogin()
    {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String upperCaseChars = "(.*[A-Z].*)";
        String specialChars = "(.*[@,#,$,%,!,?,^,&].*$)";

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

        if (password.isEmpty())
        {
            editTextPassword.setError("Password cannot be blank!");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6 || password.length() > 10 || !password.matches(upperCaseChars) || !password.matches(specialChars) )
        {
            editTextPassword.setError("Incorrect password format! \nHINT: password is between 6-10 characters, and must have at least one uppercase character and one special symbol of @,#,$,%,!,?,^,&");
            editTextPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified())
                    {
                        startActivity(new Intent(Login.this, MainDash.class));
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        reference = FirebaseDatabase.getInstance().getReference().child("Users");
                        aLog = FirebaseDatabase.getInstance().getReference().child("Logs");
                        userid = user.getUid();

                        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User userInfo = snapshot.getValue(User.class);

                                if(userInfo != null)
                                {
                                    long currentTime = System.currentTimeMillis();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String logTimeStamp = dateFormat.format(currentTime);
                                    String logDesc = "User Login";
                                    log.setLogDesc(logDesc);
                                    log.setLogTimeStamp(logTimeStamp);
                                    log.setLogUser(userid);
                                    log.setLogID("LOG"+Long.toString(System.currentTimeMillis()));
                                    aLog.child(log.getLogID()).setValue(log);
                                    String userFirstname = userInfo.firstName;
                                    String userLastname = userInfo.lastName;
                                    Toast.makeText(Login.this, "Welcome, "+userFirstname + " " + userLastname, Toast.LENGTH_LONG).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {
                            }
                        });
                    }

                    else
                    {
                        user.sendEmailVerification();
                        Toast.makeText(Login.this, "The email has not been verified, please check your email to verify your email before login", Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    Toast.makeText(Login.this, "Email or password is incorrect!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}