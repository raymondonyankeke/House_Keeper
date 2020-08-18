package com.java.housekeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.java.housekeeper.model.UserModel;


public class SignUpActivity extends AppCompatActivity {

    private EditText emailAddressSignUp;
    private EditText passwordSignUp;
    private EditText confirmPasswordSignUp;
    private EditText inputFullName, inputPersonalAddress, inputContactNumber;
    
    private Button buttonSubmit;

    private boolean isAdmin = false;

    DatabaseReference userReference;
    FirebaseUser mUser;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeContent();
        
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateUserInput();
            }
        });
        
    }

    private void test() {
        String fullName = inputFullName.getText().toString();
        String contactNumber = inputContactNumber.getText().toString();
        String personalAddress = inputPersonalAddress.getText().toString();

        UserModel userModel = new UserModel();


        if(TextUtils.isEmpty(fullName)) {
            Toast.makeText(getApplicationContext(), "Please enter complete name.", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(contactNumber)) {
            Toast.makeText(getApplicationContext(), "Please enter your Contact Number.", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(personalAddress)) {
            Toast.makeText(getApplicationContext(), "Please enter your Personal Address.", Toast.LENGTH_SHORT).show();
        }

        if (!fullName.equals("") && !contactNumber.equals("") && !personalAddress.equals("")) {

        } else {

        }
    }

    private void validateUserInput() {
        final String email = emailAddressSignUp.getText().toString().trim();
        final String password = passwordSignUp.getText().toString().trim();
        final String confirmPassword = confirmPasswordSignUp.getText().toString().trim();
        final String fullName = inputFullName.getText().toString();
        final String personalAddress = inputPersonalAddress.getText().toString();
        final String contactNumber = inputContactNumber.getText().toString();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Your e-mail address cannot be empty.", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Your password cannot be empty.", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Confirm password field cannot be empty.", Toast.LENGTH_SHORT).show();
        }

        if(!password.equals(confirmPassword)) {
            Toast.makeText(this, "Your passwords do not match.", Toast.LENGTH_SHORT).show();
        }

        if(password.length() < 5) {
            Toast.makeText(this, "Password cannot be less than 5 characters.", Toast.LENGTH_SHORT).show();
        }
        
        if(TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Full name cannot be empty.", Toast.LENGTH_SHORT).show();
        }
        
        if(TextUtils.isEmpty(personalAddress)) {
            Toast.makeText(this, "Personal address cannot be empty.", Toast.LENGTH_SHORT).show();
        }
        
        if(TextUtils.isEmpty(contactNumber)) {
            Toast.makeText(this, "Contact number cannot be empty.", Toast.LENGTH_SHORT).show();
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mUser = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = mUser.getUid();
                        isAdmin = false;
                        //String userId = mDatabase.push().getKey();

                        UserModel userModel = new UserModel();
                        userModel.setUserId(userId);
                        userModel.setFullName(fullName);
                        userModel.setContactNumber(contactNumber);
                        userModel.setAddress(personalAddress);
                        userModel.setEmail(email);
                        userModel.setPassword(password);
                        userModel.isAdmin(isAdmin);

                        userReference.child(userId).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                        if(!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Register failed." + task.getException(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Register success!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void initializeContent() {
        emailAddressSignUp = findViewById(R.id.email_sign_up_field);
        passwordSignUp = findViewById(R.id.password_sign_up_field);
        confirmPasswordSignUp = findViewById(R.id.confirm_password_sign_up_field);

        inputFullName = findViewById(R.id.full_name_sign_up_field);
        inputContactNumber = findViewById(R.id.contact_number_sign_up_field);
        inputPersonalAddress = findViewById(R.id.address_sign_up_field);

        buttonSubmit = findViewById(R.id.submit_sign_up_button);

        userReference = FirebaseDatabase.getInstance().getReference("User");

        mAuth = FirebaseAuth.getInstance();
    }


}
