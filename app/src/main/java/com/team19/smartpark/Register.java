package com.team19.smartpark;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private EditText mFullName, mEmail, mPassword, mPhone;
    private Button mRegisterBtn, registerSkipButton;
    private TextView mLoginBtn;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.createText);
        registerSkipButton = findViewById(R.id.loginSkipButton);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        //if a user is already login, direct user to map activity
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();
        }
        //if a skip button is clicked, direct user to map activity
        registerSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                finish();
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all the required info from the user email, password, name and phone number
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String fullName = mFullName.getText().toString();
                String phone = mPhone.getText().toString();

                //if email is empty-->prompt an error to user
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }
                //if password is less than 6 character-->prompt an error to user
                if (password.length() < 6) {
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }
                //if password is empty-->prompt an error to user
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // register the user in firebase using the provided email and password
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // send verification email to user when sucessfully register
                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Error", "onFailure: Email not sent " + e.getMessage());
                                }
                            });
                            //store all the user information name, email, phone number and user type to its corresponding ID in firebase and direct user to map activity
                            userID = fAuth.getCurrentUser().getUid();
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName", fullName);
                            user.put("email", email);
                            user.put("phone", phone);
                            user.put("type", "A");
                            FirebaseDatabase.getInstance().getReference(userID).child("userInfo").updateChildren(user);
                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                            finish();

                        } else {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        //redirect user to login activity when login button is pressed
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

    }
}