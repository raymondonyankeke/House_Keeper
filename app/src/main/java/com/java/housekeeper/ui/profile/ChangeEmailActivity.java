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

public class ChangeEmailActivity extends AppCompatActivity {

    private EditText currentEmail, newEmail, confirmPasswordChange;
    private Button updateEmailButton;

    private String newEmailString, currentId;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    FirebaseDatabase mDatabase;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        initializeViews();

        mDatabase = FirebaseDatabase.getInstance();
        userRef = mDatabase.getReference("User");

        updateEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterInput();
            }
        });
    }

    public void initializeViews() {
        currentEmail = findViewById(R.id.current_email_edit_text);
        newEmail = findViewById(R.id.new_email_edit_text);
        updateEmailButton = findViewById(R.id.confirm_change_email_button);
        confirmPasswordChange = findViewById(R.id.confirm_change_email_password);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public void filterInput() {
        String currentEmailString = currentEmail.getText().toString();
        newEmailString = newEmail.getText().toString().trim();
        String currentPassword = confirmPasswordChange.getText().toString();

        if(TextUtils.isEmpty(currentEmailString)) {
            Toast.makeText(this, "Current Email field cannot be empty.", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(newEmailString)) {
            Toast.makeText(this, "New Email field cannot be empty.", Toast.LENGTH_SHORT).show();
        }
        
        if(TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, "Current password field cannot be empty.", Toast.LENGTH_SHORT).show();
        }

        if(currentEmailString.equals(newEmailString)) {
            Toast.makeText(this, "Your E-mail address cannot be the same.", Toast.LENGTH_SHORT).show();
        }
        
        if(mUser != null) {
            reAuthenticateUser(currentEmailString, currentPassword);
        }
    }

    private void reAuthenticateUser(final String email, String password) {
        final String TAG = "reAuthenticateUser";
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);
        Log.d(TAG, "Email: " + email.toString().trim());
        Log.d(TAG, "Password: " + password.toString().trim());

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User re-authenticated.");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail(newEmailString)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User email address updated.");
                                            Toast.makeText(ChangeEmailActivity.this, "User email address updated.", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "New Email: " + newEmailString);
                                            updateDatabase(newEmailString);
                                        } else {
                                            Toast.makeText(ChangeEmailActivity.this, "Wrong password.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });


    }

    private void updateDatabase(String email) {
        final String TAG = "updateDatabase";
        currentId = mUser.getUid();

        userRef.child(currentId).child("email").setValue(email)
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
