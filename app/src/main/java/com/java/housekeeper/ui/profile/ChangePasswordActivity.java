package com.java.housekeeper.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.java.housekeeper.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentEmail, currentPassword, newChangePassword;
    private Button updateChangePassword;

    private String newPasswordToString, currentId;

    FirebaseUser mUser;
    FirebaseAuth mAuth;

    FirebaseDatabase mDatabase;
    DatabaseReference passwordRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initializeContent();
        validateInput();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        passwordRef = mDatabase.getReference("User");

        updateChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });
    }

    public void initializeContent() {
        currentEmail = findViewById(R.id.change_email_edit_text_password);
        currentPassword = findViewById(R.id.confirm_change_password_edit_text);
        updateChangePassword = findViewById(R.id.confirm_change_password_button);
        newChangePassword = findViewById(R.id.new_change_password_edit_text);
    }

    private void validateInput() {
        String currentEmailToString = currentEmail.getText().toString();
        String currentPasswordToString = currentPassword.getText().toString();
        newPasswordToString = newChangePassword.getText().toString();

        if(TextUtils.isEmpty(currentEmailToString)) {
            Toast.makeText(this, "Email field cannot be empty.", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(currentPasswordToString)) {
            Toast.makeText(this, "Password field cannot be empty.", Toast.LENGTH_SHORT).show();
        }

        if(mUser != null) {
            processChangePassword(currentEmailToString, currentPasswordToString);
        }

    }

    private void processChangePassword(String email, String password) {
        final String TAG = "processChangePassword";

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);

        mUser.reauthenticate(credential)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mUser.updatePassword(newPasswordToString)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Password updated");
                                    updateDatabase(newPasswordToString);
                                } else {
                                    Log.d(TAG, "Error password not updated");
                                    Toast.makeText(ChangePasswordActivity.this, "Wrong account details.", Toast.LENGTH_SHORT).show();
                                }
                        }
                        });
                    } else {
                        Log.d(TAG, "Error auth failed");
                        Toast.makeText(ChangePasswordActivity.this, "Incorrect Account Details.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void updateDatabase(String password) {
        final String TAG = "updateDatabase";
        currentId = mUser.getUid();

        passwordRef.child(currentId).child("password").setValue(password)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Database changed, success");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            });
    }

}
