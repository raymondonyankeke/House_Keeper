package com.java.housekeeper.ui.maid_detailed;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.java.housekeeper.R;
import com.java.housekeeper.model.MaidModel;
import com.java.housekeeper.model.RatingModel;
import com.java.housekeeper.model.ServicesModel;
import com.java.housekeeper.ui.maid_rating.MaidRatingActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MaidDetailedActivity extends AppCompatActivity {

    private DatabaseReference maidReference;
    private DatabaseReference totalReference;
    private DatabaseReference maidServicesReference;

    private FirebaseDatabase mDatabase;

    private RecyclerView recyclerView;
    ServicesRecyclerAdapter adapter;

    private ImageView maidImage;
    private TextView maidNameDetailed, maidContactNumber, maidDescription;
    private Button showCommentsButton, callButton, textButton, maidServicesButton;
    private RatingBar ratingBar;
    private FloatingActionButton fab_rating;
    private Map<String, Object> updateData;

    private String toServices;

    MaidModel maidModel = new MaidModel();

    private String maidIdSelected, pushId;
    private String maidName, maidDetails, maidNumber, maidPictureLink; // Retrieve from Firebase snapshot
    FirebaseRecyclerOptions<MaidModel> options;

    private FirebaseAuth mAuth;

    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detailed_maid);
        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.maid_services_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        initializeContent();
        onStart();
//        loadServices();
        loadMaidData();

        fab_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRatingDialog();
            }
        });

        maidServicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MaidDetailedActivity.this, ShowServicesActivity.class);
                intent.putExtra("maidId", pushId);
                startActivity(intent);
            }
        });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void loadServices() {
        maidIdSelected = getIntent().getStringExtra("maidIdSelected");
        toServices = maidIdSelected.toString();

        final String TAG = "loadServices()";
        Log.d(TAG, "Log ID: " + maidIdSelected);

        FirebaseRecyclerOptions<ServicesModel> setOptions =
                new FirebaseRecyclerOptions.Builder<ServicesModel>()
                        .setQuery(
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Services")
                                        .child(maidIdSelected)
                                , ServicesModel.class)
                        .build();
        adapter = new ServicesRecyclerAdapter(setOptions);
        recyclerView.setAdapter(adapter);
    }

    public void openRatingDialog() {
        mAuth = FirebaseAuth.getInstance();

        AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MaidDetailedActivity.this);
        builder.setTitle("Rating");
        builder.setMessage("Rate this Maid");

        View itemView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.rate_maid_dialog, null); //Change

        final RatingBar ratingBarStars = itemView.findViewById(R.id.rating_bar_comment);
        final EditText ratingBarEditText = itemView.findViewById(R.id.rating_edit_text);

        final String userId = mAuth.getCurrentUser().getUid();
        final String userEmail = mAuth.getCurrentUser().getEmail();

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                RatingModel ratingModel = new RatingModel();
                ratingModel.setComment(ratingBarEditText.getText().toString());
                ratingModel.setUid(userId);
                ratingModel.setRaterName(userEmail);
                ratingModel.setMaidName(maidName);
                ratingModel.setRatingValue(ratingBarStars.getRating());
                submitRatingToFirebase(ratingModel);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setView(itemView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void submitRatingToFirebase(final RatingModel ratingModel) {
        FirebaseDatabase.getInstance().getReference("Rating")
                .child(maidIdSelected)
                .push()
                .setValue(ratingModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            addRatingToMaid(ratingModel.getRatingValue());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MaidDetailedActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void addRatingToMaid(final float ratingValue) {
        FirebaseDatabase.getInstance()
                .getReference("Rating")
                .child(maidIdSelected)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    MaidModel maidModel = new MaidModel();

                    if(maidModel.getRatingValue() == null) {
                        maidModel.setRatingValue(0d);
                    }

                    if(maidModel.getRatingCount() == 0) {
                        maidModel.setRatingCount(0);
                    }
                    double sumRating = maidModel.getRatingValue() + ratingValue;
                    int ratingCount = maidModel.getRatingCount();
                    ratingCount++;

                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("totalRating", sumRating);
                    updateData.put("totalCount", ratingCount);

                    snapshot.getRef()
                            .child("Total")
                            .updateChildren(updateData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(MaidDetailedActivity.this, "Thank you!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MaidDetailedActivity.this, "Error: " , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(MaidDetailedActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MaidDetailedActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMaidData() {
        maidIdSelected = getIntent().getStringExtra("maidIdSelected");
        maidModel.setMaidId(maidIdSelected);
        pushId = maidIdSelected;

        Query queryMaidData = maidServicesReference.child(maidModel.getMaidId());
        options = new FirebaseRecyclerOptions.Builder<MaidModel>().setQuery(queryMaidData, MaidModel.class).build();

        maidReference.child(maidModel.getMaidId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    maidName = snapshot.child("maidName").getValue().toString();
                    maidDetails = snapshot.child("maidDetails").getValue().toString();
                    maidNumber = snapshot.child("maidContactNumber").getValue().toString();
                    maidPictureLink = snapshot.child("maidPicture").getValue().toString();

                    maidNameDetailed.setText(maidName);
                    maidDescription.setText(maidDetails);
                    maidContactNumber.setText(maidNumber);
                    Picasso.get().load(maidPictureLink).fit().into(maidImage);

                    showCommentsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loadRatingData();
                        }
                    });

                    textButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textButton();
                        }
                    });
                    
                    callButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            callButton();
                        }
                    });
                    
                    
                } else {
                    Toast.makeText(MaidDetailedActivity.this, "Failed to load.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MaidDetailedActivity.this, "E: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseDatabase.getInstance().getReference("Rating").child(maidModel.getMaidId()).child("Total").child("totalRating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    float rating = Float.parseFloat(snapshot.getValue().toString());
                    ratingBar.setRating(rating);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadRatingData() {
        Intent intent = new Intent(MaidDetailedActivity.this, MaidRatingActivity.class);
        intent.putExtra("maidId", pushId);
        startActivity(intent);
    }

    private boolean isTelephonyEnabled(){
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        return telephonyManager != null && telephonyManager.getSimState()== TelephonyManager.SIM_STATE_READY;
    }

    private void callButton() {

        if(isTelephonyEnabled()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + maidNumber));
            startActivity(intent);
        }
    }

    private void textButton() {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("sms:"));
        smsIntent.putExtra("address", maidNumber);
        startActivity(smsIntent);
    }

    private void initializeContent() {
        maidReference = FirebaseDatabase.getInstance().getReference().child("Maid");
        totalReference = FirebaseDatabase.getInstance().getReference().child("Total");
        maidServicesReference = FirebaseDatabase.getInstance().getReference().child("Services");

        maidImage = findViewById(R.id.maid_detailed_view_picture);
        maidNameDetailed = findViewById(R.id.maid_detailed_view_name);
        maidContactNumber = findViewById(R.id.maid_detailed_view_contact_number);
        maidDescription = findViewById(R.id.maid_detailed_view_description);

        showCommentsButton = findViewById(R.id.maid_detailed_view_show_comments_button);
        callButton = findViewById(R.id.maid_detailed_view_call_button);
        textButton = findViewById(R.id.maid_detailed_view_text_button);
        maidServicesButton = findViewById(R.id.maid_services_button);

        fab_rating = findViewById(R.id.btn_rating);
        ratingBar = findViewById(R.id.rating_bar);
    }

}
