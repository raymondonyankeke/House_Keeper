package com.java.housekeeper.ui.profile;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.java.housekeeper.R;
import com.java.housekeeper.admin.EditMaidActivity;
import com.java.housekeeper.model.MaidModel;
import com.java.housekeeper.model.ServicesModel;
import com.java.housekeeper.model.UserModel;

import java.util.UUID;

public class CreateUserMaidActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1234;
    private Uri imageUri = null;

    StorageReference maidImagesRef;

    private ImageView addImageAsMaid;
    private TextView maidName, maidContactNumber;
    private EditText maidDescription;

    private Button submitButton;

    private DatabaseReference maidReference;
    private DatabaseReference servicesReference;
    private DatabaseReference userRef;

    private FirebaseDatabase mDatabase;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    private UserModel userModel;

    private String currentUserId, currentUserFullName, currentUserPhoneNumber;

    private CheckBox laundry, washing, dryCleaning, lightCleaning, clothesDuties, runningErrands, preparingMeals;
    private Button updateButton;

    ServicesModel servicesModel;
    MaidModel maidModel;

    String name, details, number;
    String downloadImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_as_maid);
        initializeContent();
        retrieveValues();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPictureToFirebase();
            }
        });

        addImageAsMaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    public void initializeContent() {
        maidImagesRef  = FirebaseStorage.getInstance().getReference().child("Images");
        maidReference = FirebaseDatabase.getInstance().getReference().child("Maid");
        servicesReference = FirebaseDatabase.getInstance().getReference().child("Services");
        userRef = FirebaseDatabase.getInstance().getReference().child("User");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        addImageAsMaid = findViewById(R.id.add_image_as_maid);
        maidName = findViewById(R.id.user_name);
        maidContactNumber = findViewById(R.id.user_contact_number);
        maidDescription = findViewById(R.id.user_as_maid_description);
        submitButton = findViewById(R.id.submit_maid_button);

        maidModel = new MaidModel();
        servicesModel = new ServicesModel();

        laundry = findViewById(R.id.cb_laundry);
        washing = findViewById(R.id.cb_washing);
        dryCleaning = findViewById(R.id.cb_dry_cleaning);
        lightCleaning = findViewById(R.id.cb_light_cleaning);
        clothesDuties = findViewById(R.id.cb_clothes_duties);
        runningErrands = findViewById(R.id.cb_running_errands);
        preparingMeals = findViewById(R.id.cb_preparing_meals);

        updateButton = findViewById(R.id.update_maid_button);
    }

    private void retrieveValues() {
        final String TAG = "retrieveValues();";

        userRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    currentUserFullName = snapshot.child("fullName").getValue().toString();
                    currentUserPhoneNumber = snapshot.child("contactNumber").getValue().toString();

                    maidName.setText(currentUserFullName);
                    maidContactNumber.setText(currentUserPhoneNumber);

                    maidModel.setMaidName(currentUserFullName);
                    maidModel.setMaidContactNumber(currentUserPhoneNumber);
                } catch (NullPointerException e) {
                    Toast.makeText(CreateUserMaidActivity.this, "E: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "User ID: " + currentUserId);
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void submitValues() {
        String name = maidName.getText().toString();
        details = maidDescription.getText().toString();
        number = maidContactNumber.getText().toString();
        
        if(TextUtils.isEmpty(details)) {
            Toast.makeText(this, "Description cannot be empty.", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Phone number cannot be empty.", Toast.LENGTH_SHORT).show();
        } else if(downloadImageUrl == null) {
            Toast.makeText(this, "Picture cannot be empty.", Toast.LENGTH_SHORT).show();
        } else {
            maidModel.setMaidId(currentUserId);
            maidModel.setMaidName(name);
            maidModel.setMaidDetails(details);
            maidModel.setMaidContactNumber(number);
            maidModel.setMaidPicture(downloadImageUrl);

            maidReference.child(currentUserId).setValue(maidModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(CreateUserMaidActivity.this, "Maid added!", Toast.LENGTH_SHORT).show();
                    addServicesToMaid();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateUserMaidActivity.this, "[Err]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Maid Reference", "Error: " + e.getMessage());
                }
            });
        }
    }


    private void uploadPictureToFirebase() {
        if(imageUri == null) {
            Toast.makeText(this, "Please upload an image.", Toast.LENGTH_SHORT).show();
        }

        String unique_name = UUID.randomUUID().toString();

        try {

            final StorageReference filePath = maidImagesRef.child(imageUri.getLastPathSegment() + unique_name);
            final UploadTask uploadTask = filePath.putFile(imageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateUserMaidActivity.this, "Upload failed.", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(CreateUserMaidActivity.this, "Upload success.", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(CreateUserMaidActivity.this, "I got the image", Toast.LENGTH_SHORT).show();
                                submitValues();
                            }
                        }
                    });
                }
            });
        } catch (NullPointerException e) {
            Toast.makeText(this, "Please add an image.", Toast.LENGTH_SHORT).show();
        }
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

    public void addServicesToMaid() {
        String serviceId, serviceName, serviceDescription, serviceImageUrl;

        if(laundry.isChecked()) {
            serviceId = "1";
            serviceName = "Laundry";
            serviceDescription = "The service maid will perform laundry duties.";
            serviceImageUrl = "https://i.imgur.com/glJ48xH.png";

            servicesModel.setServiceId(serviceId);
            servicesModel.setServiceName(serviceName);
            servicesModel.setServiceDescription(serviceDescription);
            servicesModel.setServiceImage(serviceImageUrl);

            sendToFirebase(serviceId, serviceName, servicesModel);
        } else {
            serviceId = "1";
            servicesReference.child(currentUserId).child(serviceId).removeValue();
        }

        if(washing.isChecked()) {
            serviceId = "2";
            serviceName = "Washing";
            serviceDescription = "The service maid will wash your selected items of choice..";
            serviceImageUrl = "https://i.imgur.com/jrvVn5N.png";

            servicesModel.setServiceId(serviceId);
            servicesModel.setServiceName(serviceName);
            servicesModel.setServiceDescription(serviceDescription);
            servicesModel.setServiceImage(serviceImageUrl);


            sendToFirebase(serviceId, serviceName, servicesModel);
        } else {
            serviceId = "2";
            servicesReference.child(currentUserId).child(serviceId).removeValue();
        }

        if(dryCleaning.isChecked()) {
            serviceId = "3";
            serviceName = "Dry Cleaning";
            serviceDescription = "The service maid will perform dry cleaning on selected laundry items.";
            serviceImageUrl ="https://i.imgur.com/AZPMGPn.png";

            servicesModel.setServiceId(serviceId);
            servicesModel.setServiceName(serviceName);
            servicesModel.setServiceDescription(serviceDescription);
            servicesModel.setServiceImage(serviceImageUrl);

            sendToFirebase(serviceId, serviceName, servicesModel);
        } else {
            serviceId = "3";
            servicesReference.child(currentUserId).child(serviceId).removeValue();
        }

        if(lightCleaning.isChecked()) {
            serviceId = "4";
            serviceName = "Light Cleaning";
            serviceDescription = "The service maid will perform small or light cleaning on your household.";
            serviceImageUrl = "https://i.imgur.com/dKkaeYT.png";

            servicesModel.setServiceId(serviceId);
            servicesModel.setServiceName(serviceName);
            servicesModel.setServiceDescription(serviceDescription);
            servicesModel.setServiceImage(serviceImageUrl);

            sendToFirebase(serviceId, serviceName, servicesModel);
        } else {
            serviceId = "4";
            servicesReference.child(currentUserId).child(serviceId).removeValue();
        }

        if(clothesDuties.isChecked()) {
            serviceId = "5";
            serviceName = "Clothes Duties";
            serviceDescription = "The service maid will fold or iron your choice of clothes.";
            serviceImageUrl = "https://i.imgur.com/gpoYJ4b.png";

            servicesModel.setServiceId(serviceId);
            servicesModel.setServiceName(serviceName);
            servicesModel.setServiceDescription(serviceDescription);
            servicesModel.setServiceImage(serviceImageUrl);

            sendToFirebase(serviceId, serviceName, servicesModel);
        } else {
            serviceId = "5";
            servicesReference.child(currentUserId).child(serviceId).removeValue();
        }

        if(runningErrands.isChecked()) {
            serviceId = "6";
            serviceName = "Running Errands";
            serviceDescription = "The service maid shall perform errand duties for you.";
            serviceImageUrl = "https://i.imgur.com/Zuqspqg.png";

            servicesModel.setServiceId(serviceId);
            servicesModel.setServiceName(serviceName);
            servicesModel.setServiceDescription(serviceDescription);
            servicesModel.setServiceImage(serviceImageUrl);

            sendToFirebase(serviceId, serviceName, servicesModel);
        } else {
            serviceId = "6";
            servicesReference.child(currentUserId).child(serviceId).removeValue();
        }

        if(preparingMeals.isChecked()) {
            serviceId = "7";
            serviceName = "Preparing Meals";
            serviceDescription = "The service maid shall prepare meals for you such as food or kitchen duties.";
            serviceImageUrl = "https://i.imgur.com/o3R1eNE.png";

            servicesModel.setServiceId(serviceId);
            servicesModel.setServiceName(serviceName);
            servicesModel.setServiceDescription(serviceDescription);
            servicesModel.setServiceImage(serviceImageUrl);

            sendToFirebase(serviceId, serviceName, servicesModel);
        } else {
            serviceId = "7";
            servicesReference.child(currentUserId).child(serviceId).removeValue();
        }

    }

    private void sendToFirebase(String serviceId, final String serviceName, ServicesModel servicesModel) {
        servicesReference.child(currentUserId).child(serviceId).setValue(servicesModel)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                Toast.makeText(CreateUserMaidActivity.this, "Firebase Okay.", Toast.LENGTH_SHORT).show();
//                Toast.makeText(CreateUserMaidActivity.this, serviceName + " added.", Toast.LENGTH_SHORT).show();
            }
            });
    }

}