package com.java.housekeeper.ui.maid_detailed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.java.housekeeper.R;
import com.java.housekeeper.model.ServicesModel;

public class ShowServicesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private String currentUser, maidIdSelected;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    ServicesRecyclerAdapter adapter;

    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_services);

        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.service_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        loadServices();
    }

    private void loadServices() {
        final String TAG = "loadServices()";

        currentUser = mUser.getUid();
        maidIdSelected = getIntent().getStringExtra("maidId");

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

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
