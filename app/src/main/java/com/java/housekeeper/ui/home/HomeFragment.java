package com.java.housekeeper.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.housekeeper.R;
import com.java.housekeeper.model.CategoryModel;
import com.java.housekeeper.model.ServicesModel;
import com.java.housekeeper.ui.maid_detailed.ServicesRecyclerAdapter;

public class HomeFragment extends Fragment {

    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference categoryRef;

    private HomeRecyclerAdapter adapter;

    private RecyclerView recyclerView;

    private CategoryModel categoryModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.category_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);

        categoryModel = new CategoryModel();
        //retrieveDatabase();
        loadCategories();
        return view;
    }

    private void loadCategories() {
        final String TAG = "loadCategories()";

        FirebaseRecyclerOptions<CategoryModel> setOptions =
                new FirebaseRecyclerOptions.Builder<CategoryModel>()
                        .setQuery(
                            FirebaseDatabase.getInstance().getReference()
                                .child("Category")
                            , CategoryModel.class)
                        .build();

        adapter = new HomeRecyclerAdapter(setOptions);
        recyclerView.setAdapter(adapter);
    }

    /** This is to retrieve Categories for POJO */
    private void retrieveDatabase() {
        final String TAG = "retrieveDatabase()";

        mDatabase = FirebaseDatabase.getInstance();
        categoryRef = mDatabase.getReference("Category");

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String categoryId = dataSnapshot.child("categoryId").getValue().toString();
                    String categoryImage = dataSnapshot.child("categoryImage").getValue().toString();
                    String categoryName = dataSnapshot.child("categoryName").getValue().toString();

                    Log.d(TAG, "Category ID: " + categoryId);
                    Log.d(TAG, "Category Name: " + categoryName);
                    Log.d(TAG, "Category Image: " + categoryImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Error: " + error.getMessage());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
