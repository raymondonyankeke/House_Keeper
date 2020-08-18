package com.java.housekeeper.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.java.housekeeper.LoginActivity;
import com.java.housekeeper.R;
import com.java.housekeeper.model.UserModel;

public class ProfileFragment extends Fragment {

    private Button changeEmail, changePassword, registerAsMaid, updateInformation;
    private EditText inputFullName, inputPersonalAddress, inputContactNumber;
    private TextView profileEmail, profileId;



    private String email, id;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference userRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_fragment, container, false);;
        initializeViews(root);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser != null) {
            email = mUser.getEmail();
            id = mUser.getUid();

            profileEmail.setText(email);
            profileId.setText(id);
        } else { // Return to Login page.
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeEmail();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        registerAsMaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateUserMaidActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }


    public void initializeViews(View root) {
        mDatabase = FirebaseDatabase.getInstance();
        userRef = mDatabase.getReference("User");

        profileEmail = root.findViewById(R.id.account_email_text_view);
        profileId = root.findViewById(R.id.account_id_number_text_view);
        changeEmail = root.findViewById(R.id.account_change_email_button);
        changePassword = root.findViewById(R.id.account_change_password_button);
        registerAsMaid = root.findViewById(R.id.register_as_maid_button);
    }

    private void changeEmail() {
        Intent intent = new Intent(getContext(), ChangeEmailActivity.class);
        startActivity(intent);
    }

    private void changePassword() {
        Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
        startActivity(intent);
    }


}
