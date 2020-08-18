package com.java.housekeeper.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.housekeeper.R;
import com.java.housekeeper.model.CategoryModel;

public class CreateCategoryActivity extends AppCompatActivity {

    private EditText categoryId, categoryName, categoryImageUrl;
    private Button categorySubmit;

    private CategoryModel categoryModel;

    private DatabaseReference categoryRef;
    private FirebaseDatabase databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);

        databaseReference = FirebaseDatabase.getInstance();
        categoryRef = databaseReference.getReference("Category");

        categoryModel = new CategoryModel();

        categoryId = findViewById(R.id.category_id_edit_text);
        categoryName = findViewById(R.id.category_name_edit_text);
        categoryImageUrl = findViewById(R.id.category_image_link_edit_text);
        categorySubmit = findViewById(R.id.category_submit_button);

        categorySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryIdToString, categoryNameToString, categoryImageToString;

                categoryIdToString = categoryId.getText().toString();
                categoryNameToString = categoryName.getText().toString();
                categoryImageToString = categoryImageUrl.getText().toString();

                categoryModel.setCategoryId(categoryIdToString);
                categoryModel.setCategoryName(categoryNameToString);
                categoryModel.setCategoryImage(categoryImageToString);

                categoryRef.child(categoryIdToString).setValue(categoryModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(CreateCategoryActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateCategoryActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }


}
