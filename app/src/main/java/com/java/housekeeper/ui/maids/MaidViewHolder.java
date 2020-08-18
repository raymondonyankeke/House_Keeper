package com.java.housekeeper.ui.maids;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.housekeeper.R;

public class MaidViewHolder extends RecyclerView.ViewHolder {
    ImageView maidPicture;
    TextView maidName;

    RatingBar ratingBar;

    Button proceed;

    public MaidViewHolder(@NonNull View itemView) {
        super(itemView);

        maidPicture = itemView.findViewById(R.id.maid_picture);
        maidName = itemView.findViewById(R.id.maid_name_fragment);
        //proceed = itemView.findViewById(R.id.button_proceed);
    }
}
