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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.housekeeper.admin.AdminActivity;

/**
 * Login intent
 */



public class LoginActivity extends AppCompatActivity {

    private EditText login, password;
    private TextView forgotPasswordTextView, signUpTextView;
    private Button loginButton;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeContent();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if(mUser != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }else {
            Toast.makeText(this, "Please log in to continue.", Toast.LENGTH_SHORT).show();
        }
    }

    public void initializeContent() {
        login = findViewById(R.id.email_sign_in_field);
        password = findViewById(R.id.password_sign_in_field);
        forgotPasswordTextView = findViewById(R.id.reset_password_text_view);
        signUpTextView = findViewById(R.id.sign_up_text);
        loginButton = findViewById(R.id.submit_sign_in_button);

        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
    }

    public void loginUser() {
        String loginInfo = login.getText().toString().trim();
        String passwordInfo = password.getText().toString().trim();

        if(TextUtils.isEmpty(loginInfo)) {
            Toast.makeText(this, "E-mail address must not be empty.", Toast.LENGTH_SHORT).show();
        }
        
        if(TextUtils.isEmpty(passwordInfo)) {
            Toast.makeText(this, "Password must not be empty.", Toast.LENGTH_SHORT).show();
        }

        mAuth.signInWithEmailAndPassword(loginInfo, passwordInfo)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Sign in failed.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                        }
                    }
        });
    }
}
