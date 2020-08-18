package com.java.housekeeper.ui.maids;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.java.housekeeper.R;
import com.java.housekeeper.ui.maid_detailed.MaidDetailedActivity;
import com.java.housekeeper.model.MaidModel;
import com.squareup.picasso.Picasso;

public class MaidFragment extends Fragment {

    // TODO Attempt to retrieve rating value for each maid, independent as is.

    private MaidViewModel maidViewModel;
    private EditText searchBar;
    private RecyclerView recyclerView;

    private String maidId;

    private View view;

    FirebaseRecyclerOptions<MaidModel> options;
    FirebaseRecyclerAdapter<MaidModel, MaidViewHolder> adapter;
    DatabaseReference maidReference;
    DatabaseReference ratingReference;

    FragmentTransaction fragmentTransaction;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        maidViewModel =
                ViewModelProviders.of(this).get(MaidViewModel.class);
        View root = inflater.inflate(R.layout.fragment_maid, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.maid_list);
        searchBar = root.findViewById(R.id.maid_search);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        recyclerView.setHasFixedSize(true);

        maidReference = FirebaseDatabase.getInstance().getReference().child("Maid");
        ratingReference = FirebaseDatabase.getInstance().getReference().child("Rating");

        initializeContent();

        loadData("");

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString() != null) {
                    loadData(editable.toString());
                } else {
                    loadData("");
                }
            }
        });
        return root;
    }

    public void initializeContent() {

    }

    private void loadData(String data) {
        Query query = maidReference.orderByChild("maidName").startAt(data).endAt(data + "\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<MaidModel>().setQuery(query, MaidModel.class).build();
        adapter = new FirebaseRecyclerAdapter<MaidModel, MaidViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MaidViewHolder maidViewHolder, final int i, @NonNull final MaidModel maidModel) {
                maidId = maidModel.getMaidId().toString();
                final String maidName = maidModel.getMaidName().toString();

                maidViewHolder.maidName.setText(maidModel.getMaidName());
                Picasso.get().load(maidModel.getMaidPicture()).fit().into(maidViewHolder.maidPicture);

                maidViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), MaidDetailedActivity.class);
                        intent.putExtra("maidIdSelected", getRef(i).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public MaidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maid_card_view, parent, false);
                return new MaidViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}
