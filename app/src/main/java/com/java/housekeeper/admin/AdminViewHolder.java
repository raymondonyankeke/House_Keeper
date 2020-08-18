package com.java.housekeeper.admin;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.housekeeper.R;

public class AdminViewHolder extends RecyclerView.ViewHolder {
    ImageView maidCircleImage;
    TextView maidName, maidContactNumber;

    Button deleteMaid, editMaid;

    public AdminViewHolder(@NonNull View itemView) {
        super(itemView);

        maidCircleImage = itemView.findViewById(R.id.maid_image);
        maidName = itemView.findViewById(R.id.name_of_maid);
        maidContactNumber = itemView.findViewById(R.id.number_of_maid);

        deleteMaid = itemView.findViewById(R.id.delete_maid_button);
        editMaid = itemView.findViewById(R.id.edit_maid_button);
    }
}
