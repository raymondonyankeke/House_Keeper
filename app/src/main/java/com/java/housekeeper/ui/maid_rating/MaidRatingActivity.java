package com.java.housekeeper.ui.maid_rating;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.java.housekeeper.R;
import com.java.housekeeper.model.MaidModel;
import com.java.housekeeper.model.RatingModel;
import com.java.housekeeper.ui.maids.MaidViewHolder;

public class MaidRatingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    MaidRatingRecyclerAdapter adapter;

    RecyclerView.LayoutManager layoutManager;

    private String maidIdSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maid_rating);
        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.rating_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        loadRating();
    }

    public void loadRating() {
        final String TAG = "loadRating()";

        maidIdSelected = getIntent().getStringExtra("maidId");

        FirebaseRecyclerOptions<RatingModel> options =
                new FirebaseRecyclerOptions.Builder<RatingModel>()
                        .setQuery(
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Rating")
                                        .child(maidIdSelected), RatingModel.class)
                                        .build();


        adapter = new MaidRatingRecyclerAdapter(options);
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

