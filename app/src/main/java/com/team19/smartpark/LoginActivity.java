package com.team19.smartpark;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private Button loginBtn, loginSkipButton;
    private TextView createBtn, forgotPassword;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.loginBtn);
        createBtn = findViewById(R.id.createText);
        forgotPassword = findViewById(R.id.forgotPassword);
        loginSkipButton = findViewById(R.id.loginSkipButton);

        //redirect user to map activity when skip button is pressed
        loginSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                finish();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all the information from user input fields
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                //if email is empty --> prompt user to re-enter the email address
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }
                //if password is less than 6 characters --> prompt user to re-enter the email address
                if (password.length() < 6) {
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }
                //if password is empty --> prompt user to re-enter the password
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //get user input value to authenticate with firebase authentication service
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if sucessfully login, promt user to map activity
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                            finish();
                        }
                        //if failed display a toast notify the user there is an error loggin
                        else {
                            Toast.makeText(LoginActivity.this, "Error in Login ! ", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });

            }
        });
        //direct user to register activity when create account text is clicked
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
                finish();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up UI elements for the dialog
                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder ResetDialog = new AlertDialog.Builder(v.getContext());
                ResetDialog.setTitle("Password Reset");
                ResetDialog.setMessage("Enter Your Email Address To Get Reset Email");
                ResetDialog.setView(resetMail);
                ResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // get the user input email
                        String mail = resetMail.getText().toString();
                        // using firebase authentication service to send an reset password email to user
                        if (!mail.equals("")) {
                            fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(LoginActivity.this, "Reset Email Sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "Error ! Reset Email is Not Sent", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                });

                ResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });
                //show the dialog
                ResetDialog.create().show();
            }
        });


    }
}