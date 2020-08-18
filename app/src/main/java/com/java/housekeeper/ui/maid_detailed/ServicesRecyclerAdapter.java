package com.java.housekeeper.ui.maid_detailed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.java.housekeeper.R;
import com.java.housekeeper.model.ServicesModel;
import com.java.housekeeper.ui.maid_rating.MaidRatingRecyclerAdapter;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServicesRecyclerAdapter extends FirebaseRecyclerAdapter<ServicesModel, ServicesRecyclerAdapter.ViewHolder> {

    public ServicesRecyclerAdapter(@NonNull FirebaseRecyclerOptions<ServicesModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ServicesModel model) {
        holder.serviceName.setText(model.getServiceName());
        holder.serviceDescription.setText(model.getServiceDescription());
        Picasso.get().load(model.getServiceImage()).fit().into(holder.serviceImage);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maid_detailed_services_card_view, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView serviceName, serviceDescription;
        private CircleImageView serviceImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceName = (TextView) itemView.findViewById(R.id.service_name);
            serviceDescription = itemView.findViewById(R.id.service_description);
            serviceImage = itemView.findViewById(R.id.service_image);
        }
    }
}
