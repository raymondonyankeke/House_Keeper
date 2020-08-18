package com.java.housekeeper.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.java.housekeeper.HomeActivity;
import com.java.housekeeper.LoginActivity;
import com.java.housekeeper.R;
import com.java.housekeeper.model.MaidModel;
import com.java.housekeeper.model.UserModel;
import com.squareup.picasso.Picasso;

public class AdminActivity extends AppCompatActivity {

    private Button addMaids;
    private Button addCategory;
    private Button signOutAdmin;

    private EditText searchBar;
    private RecyclerView recyclerView;

    private String maidId;

    FirebaseRecyclerOptions<MaidModel> options;
    FirebaseRecyclerAdapter<MaidModel, AdminViewHolder> adapter;
    DatabaseReference maidReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        initializeContent();

        addMaids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMaids();
            }
        });

        signOutAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

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
    }

    public void initializeContent() {
        addMaids = findViewById(R.id.add_maids);
        //addCategory = findViewById(R.id.add_services);
        signOutAdmin = findViewById(R.id.log_out);

        recyclerView = findViewById(R.id.admin_maid_list);
        searchBar = findViewById(R.id.admin_search_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        maidReference = FirebaseDatabase.getInstance().getReference().child("Maid");
    }

    private void loadData(String data) {
        Query query = maidReference.orderByChild("maidName").startAt(data).endAt(data + "\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<MaidModel>().setQuery(query, MaidModel.class).build();
        adapter = new FirebaseRecyclerAdapter<MaidModel, AdminViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final AdminViewHolder adminViewHolder, final int i, @NonNull final MaidModel maidModel) {
                maidId = maidModel.getMaidId().toString();

                adminViewHolder.maidName.setText(maidModel.getMaidName());
                adminViewHolder.maidContactNumber.setText(maidModel.getMaidContactNumber());
                Picasso.get().load(maidModel.getMaidPicture()).fit().into(adminViewHolder.maidCircleImage);

                adminViewHolder.editMaid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext(), EditMaidActivity.class);
                        intent.putExtra("maidId", getRef(i).getKey());
                        intent.putExtra("maidName", maidModel.getMaidName());
                        intent.putExtra("maidDetails", maidModel.getMaidDetails());
                        intent.putExtra("maidContactNumber", maidModel.getMaidContactNumber());
                        intent.putExtra("maidPicture", maidModel.getMaidPicture());
                        startActivity(intent);
                    }
                });

                adminViewHolder.deleteMaid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String maidIdDeleteKey = getRef(i).getKey();
                        deleteMaid(maidIdDeleteKey);
                    }
                });
            }

            @NonNull
            @Override
            public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maid_card_view_admin, parent, false);
                return new AdminViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void deleteMaid(final String maidIdDeleteKey) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this Maid entry?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                maidReference.child(maidIdDeleteKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AdminActivity.this, "Entry has been deleted.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminActivity.this, "Delete failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    private void signOut() {
        UserModel userModel = null;

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }
    private void addMaids() {
        Intent intent = new Intent(AdminActivity.this, AddMaidActivity.class);
        startActivity(intent);
    }

    private void addCategory() {
        Intent intent = new Intent(AdminActivity.this, CreateCategoryActivity.class);
        startActivity(intent);
    }

    /*Prevents the user from going back to the non-admin interface.*/

    @Override
    public void onBackPressed() {

    }

    /*Prevents the user from going back to the non-admin interface.*/
    @Override
    public boolean onKeyDown(int key_code, KeyEvent key_event) {
        if (key_code== KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(key_code, key_event);
            return true;
        }
        return false;
    }
}
