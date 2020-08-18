package com.java.housekeeper.ui.maid_detailed;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class MaidDetailedRecyclerAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<HashMap> aryList;
    private Context context;


    public MaidDetailedRecyclerAdapter(ArrayList<HashMap> aryList, Context context) {
        this.aryList = aryList;
        this.context = context;
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
        return aryList.size();
    }
}
