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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;


public class SignUp extends AppCompatActivity implements View.OnClickListener
{

    private TextView login, registerUser;
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private FirebaseAuth mAuth;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference, aLog;
    AuditLog log = new AuditLog();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        registerUser = (Button)findViewById(R.id.SignUpBtn);
        registerUser.setOnClickListener(this);

        login = findViewById(R.id.SignUpLogin);
        login.setOnClickListener(this);

        editTextFirstName = findViewById(R.id.SignUpFirstName);
        editTextLastName = findViewById(R.id.SignUpLastName);
        editTextEmail = findViewById(R.id.SignUpEmail);
        editTextPassword = findViewById(R.id.SignUpPassword);
        editTextConfirmPassword = findViewById(R.id.SignUpPassword2);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.SignUpBtn:
                registerUser();
                break;
            case R.id.SignUpLogin:
                startActivity(new Intent(SignUp.this, Login.class));
                break;
        }
    }

    private void registerUser()
    {
        final String firstName = editTextFirstName.getText().toString().trim();
        final String lastName = editTextLastName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String password2 = editTextConfirmPassword.getText().toString().trim();
        String upperCaseChars = "(.*[A-Z].*)";
        String specialChars = "(.*[@,#,$,%,!,?,^,&].*$)";

        if (firstName.isEmpty())
        {
            editTextFirstName.setError("First name cannot be blank!");
            editTextFirstName.requestFocus();
            return;
        }

        if (lastName.isEmpty())
        {
            editTextLastName.setError("Last name cannot be blank!");
            editTextLastName.requestFocus();
            return;
        }

        if (email.isEmpty())
        {
            editTextEmail.setError("Email cannot be blank!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Please enter a valid E-mail!");
            editTextEmail.requestFocus();
            return;
        }

        /*if (!email.endsWith("kpu.ca"))
        {
            editTextEmail.setError("Only KPU emails are acceptable!");
            editTextEmail.requestFocus();
            return;
        }*/

        if (password.isEmpty())
        {
            editTextPassword.setError("Password cannot be blank!");
            editTextPassword.requestFocus();
            return;
        }

        if (password2.isEmpty())
        {
            editTextConfirmPassword.setError("Please confirm your password!");
            editTextConfirmPassword.requestFocus();
            return;
        }

        if (password.length() > 10 || password.length() < 6 || !password.matches(upperCaseChars) || !password.matches(specialChars)) {
            editTextPassword.setError("Password must be between 6-10 characters, and must have at least one uppercase character and one special symbol of @,#,$,%,!,?,^,&");
            editTextPassword.requestFocus();
            return;
        }

        if (!password2.equals(password))
        {
            editTextConfirmPassword.setError("Your password does not match");
            editTextConfirmPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    rootNode = FirebaseDatabase.getInstance();
                    reference = rootNode.getReference("Users");
                    User user = new User(firstName,lastName,email);
                    logSignUp(firstName, lastName, email);
                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(SignUp.this,"Sign up successful, an verification email has been sent to your mailbox", Toast.LENGTH_LONG).show();
                                        mAuth.getCurrentUser().sendEmailVerification();
                                        startActivity(new Intent(SignUp.this, Login.class));
                                    }
                                    else
                                    {
                                        Toast.makeText(SignUp.this,"Unknown error, please try again!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                else
                {
                    editTextEmail.setError("Email has already been registered!");
                    editTextEmail.requestFocus();
                    return;
                }
            }
        });
    }

    public void logSignUp(String firstName, String lastName, String email)
    {
        aLog = FirebaseDatabase.getInstance().getReference().child("Logs");
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String logTimeStamp = dateFormat.format(currentTime);
        String logDesc = "User Sign Up For " + firstName + " " + lastName + ", " + email;
        log.setLogDesc(logDesc);
        log.setLogTimeStamp(logTimeStamp);
        log.setLogUser("NULL");
        log.setLogID("LOG"+Long.toString(System.currentTimeMillis()));
        aLog.child(log.getLogID()).setValue(log);
    }
}