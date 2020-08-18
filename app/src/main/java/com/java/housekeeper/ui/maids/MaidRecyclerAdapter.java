package com.java.housekeeper.ui.maids;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.housekeeper.R;

public class MaidRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView maidName;
        ImageView maidPicture;
        RatingBar maidRatingBar;
        ImageView maidProceedButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            maidPicture = itemView.findViewById(R.id.maid_picture);
            maidName = itemView.findViewById(R.id.maid_name_fragment);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


}
