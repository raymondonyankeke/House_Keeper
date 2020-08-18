package com.java.housekeeper.admin;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.housekeeper.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView maidName, maidNumber;
        CircleImageView maidImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            maidImage = itemView.findViewById(R.id.maid_image);
            maidName = itemView.findViewById(R.id.name_of_maid);
            maidNumber = itemView.findViewById(R.id.number_of_maid);
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
