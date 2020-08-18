package com.java.housekeeper.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.housekeeper.R;
import com.java.housekeeper.model.MaidModel;
import com.java.housekeeper.model.ServicesModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditMaidActivity extends AppCompatActivity {

    private EditText editMaidName, editMaidDetails, editMaidContactNumber;
    private CheckBox laundry, washing, dryCleaning, lightCleaning, clothesDuties, runningErrands, preparingMeals;
    private Button updateButton;

    ServicesModel servicesModel;
    MaidModel maidModel;
    
    DatabaseReference maidReference;
    DatabaseReference servicesReference;

    private String maidId, maidName, maidDetails, maidContactNumber, maidPicture;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_maid);
        initializeContent();
        retrieveDetails();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editMaidDetails();
            }
        });

    }

    public void initializeContent() {
        servicesModel = new ServicesModel();
        maidModel = new MaidModel();
        
        maidReference = FirebaseDatabase.getInstance().getReference().child("Maid");
        servicesReference = FirebaseDatabase.getInstance().getReference().child("Services");

        editMaidName = findViewById(R.id.edit_maid_name);
        editMaidDetails = findViewById(R.id.edit_maid_description);
        editMaidContactNumber = findViewById(R.id.edit_maid_contact_number);

        laundry = findViewById(R.id.rb_laundry);
        washing = findViewById(R.id.rb_washing);
        dryCleaning = findViewById(R.id.rb_dry_cleaning);
        lightCleaning = findViewById(R.id.rb_light_cleaning);
        clothesDuties = findViewById(R.id.rb_clothes_duties);
        runningErrands = findViewById(R.id.rb_running_errands);
        preparingMeals = findViewById(R.id.rb_preparing_meals);

        updateButton = findViewById(R.id.update_maid_button);
    }

    private void retrieveDetails() {
        String maidName, maidDetails, maidContactNumber;

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            //maidId = extras.getString("maidId");

            maidId = getIntent().getStringExtra("maidId");

            maidReference.child(maidId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String maidName = snapshot.child("maidName").getValue().toString();
                    String maidDetails = snapshot.child("maidDetails").getValue().toString();
                    String maidNumber = snapshot.child("maidContactNumber").getValue().toString();
                    String maidPictureLink = snapshot.child("maidPicture").getValue().toString();

                    editMaidName.setText(maidName);
                    editMaidDetails.setText(maidDetails);
                    editMaidContactNumber.setText(maidNumber);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void editMaidDetails() {
        Map<String, Object> updateData = new HashMap<>();

        updateData.put("maidName", editMaidName.getText().toString());
        updateData.put("maidDetails", editMaidDetails.getText().toString());
        updateData.put("maidContactNumber", editMaidContactNumber.getText().toString());

        updateMaid(updateData);
        addServicesToMaid();
    }

    private void updateMaid(Map<String, Object> updateData) {
        FirebaseDatabase.getInstance().getReference("Maid").child(maidId).updateChildren(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditMaidActivity.this, "Update success.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditMaidActivity.this, "Update failed.", Toast.LENGTH_SHORT).show();
            }
        });
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
            servicesReference.child(maidId).child(serviceId).removeValue();
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
            servicesReference.child(maidId).child(serviceId).removeValue();
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
            servicesReference.child(maidId).child(serviceId).removeValue();
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
            servicesReference.child(maidId).child(serviceId).removeValue();
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
            servicesReference.child(maidId).child(serviceId).removeValue();
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
            servicesReference.child(maidId).child(serviceId).removeValue();
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
            servicesReference.child(maidId).child(serviceId).removeValue();
        }

    }

    private void sendToFirebase(String serviceId, final String serviceName, ServicesModel servicesModel) {
        servicesReference.child(maidId).child(serviceId).setValue(servicesModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditMaidActivity.this, "Firebase Okay.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(EditMaidActivity.this, serviceName + " added.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
