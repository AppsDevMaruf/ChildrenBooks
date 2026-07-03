/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 2/21/21 12:10 PM
 */

package com.marufalam.childrenbooks.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marufalam.childrenbooks.Models.AllInOneModel;
import com.marufalam.childrenbooks.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class AllInOneAdapter extends FirebaseRecyclerAdapter<AllInOneModel,AllInOneAdapter.AllInOneViewHolder> {


    public AllInOneAdapter(@NonNull FirebaseRecyclerOptions<AllInOneModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AllInOneViewHolder holder, int position, @NonNull AllInOneModel model) {
        Glide.with(holder.itemimg.getContext()).load(model.getImg()).placeholder(R.drawable.placeholder_image).into(holder.itemimg);
        holder.itemname.setText(model.getName());

    }

    @NonNull
    @Override
    public AllInOneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_in_one_item_model, parent, false);
        return new AllInOneViewHolder(view);

    }

    public static class AllInOneViewHolder extends RecyclerView.ViewHolder {
        final ImageView itemimg;
        final TextView itemname;
        public AllInOneViewHolder(@NonNull View itemView) {
            super(itemView);
            itemimg = itemView.findViewById(R.id.itemImgId);
            itemname = itemView.findViewById(R.id.itemnameId);
        }
    }
}
