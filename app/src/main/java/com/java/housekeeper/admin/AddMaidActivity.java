package com.java.housekeeper.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.java.housekeeper.R;
import com.java.housekeeper.model.MaidModel;
import com.java.housekeeper.model.ServicesModel;

import java.util.UUID;

public class AddMaidActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1234;
    private Uri imageUri = null;

    StorageReference maidImagesRef;

    private ImageView addImage;

    private EditText maidName, maidContactNumber, maidDescription;

    private Button submitButton;

    private DatabaseReference maidReference;
    private FirebaseDatabase mDatabase;

    ServicesModel servicesModel;
    MaidModel maidModel;

    private String name, details, number;
    private String downloadImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_maid);
        initializeContent();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPictureToFirebase();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void submitValues() {
        final String maidId = maidReference.push().getKey();

        name = maidName.getText().toString();
        details = maidDescription.getText().toString();
        number = maidContactNumber.getText().toString();

        if(TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Name cannot be empty.", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(details)) {
            Toast.makeText(this, "Description cannot be empty.", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Phone number cannot be empty.", Toast.LENGTH_SHORT).show();
        } else if(downloadImageUrl == null) {
            Toast.makeText(this, "Picture cannot be empty.", Toast.LENGTH_SHORT).show();
        } else if(number.length() < 11) {
            Toast.makeText(this, "Incorrect phone format. ex: 11 Digits Philippine standard.", Toast.LENGTH_LONG).show();
        } else {
            maidModel.setMaidId(maidId);
            maidModel.setMaidName(name);
            maidModel.setMaidDetails(details);
            maidModel.setMaidContactNumber(number);
            maidModel.setMaidPicture(downloadImageUrl);

            maidReference.child(maidId).setValue(maidModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddMaidActivity.this, "Maid added!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddMaidActivity.this, "[Err]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Maid Reference", "Error: " + e.getMessage());
                }
            });
        }
    }

    public void initializeContent() {
        maidImagesRef  = FirebaseStorage.getInstance().getReference().child("Images");
        maidReference = FirebaseDatabase.getInstance().getReference().child("Maid");

        addImage = findViewById(R.id.add_image);
        maidName = findViewById(R.id.maid_name);
        maidContactNumber = findViewById(R.id.maid_contact_number);
        maidDescription = findViewById(R.id.maid_description);
        submitButton = findViewById(R.id.submit_maid_button);

        maidModel = new MaidModel();
        servicesModel = new ServicesModel();
    }

    private void uploadPictureToFirebase() {
        if(imageUri == null) {
            Toast.makeText(this, "Please upload an image.", Toast.LENGTH_SHORT).show();
        }

        String unique_name = UUID.randomUUID().toString();
        final StorageReference filePath = maidImagesRef.child(imageUri.getLastPathSegment() + unique_name);

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddMaidActivity.this, "Upload failed.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddMaidActivity.this, "Upload success.", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()) {
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()) {
                            downloadImageUrl = task.getResult().toString();
                            maidModel.setMaidPicture(downloadImageUrl);
                            Toast.makeText(AddMaidActivity.this, "I got the image", Toast.LENGTH_SHORT).show();
                            submitValues();
                        }
                    }
                });
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
        }
    }

    private void uploadData() {

    }


}
