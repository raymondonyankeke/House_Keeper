package com.java.housekeeper.ui.maid_rating;

import android.content.Context;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.java.housekeeper.R;
import com.java.housekeeper.model.RatingModel;

public class MaidRatingRecyclerAdapter extends FirebaseRecyclerAdapter<RatingModel, MaidRatingRecyclerAdapter.ViewHolder> {

    public MaidRatingRecyclerAdapter(@NonNull FirebaseRecyclerOptions<RatingModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull RatingModel model) {
        holder.raterName.setText(model.getRaterName());
        holder.raterDescription.setText(model.getComment());
        holder.raterBar.setRating(model.getRatingValue());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_rating, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView raterName, raterDescription;
        RatingBar raterBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            raterName = (TextView) itemView.findViewById(R.id.rater_name);
            raterDescription = (TextView) itemView.findViewById(R.id.rater_description);
            raterBar = (RatingBar) itemView.findViewById(R.id.rater_bar);
        }
    }
}
