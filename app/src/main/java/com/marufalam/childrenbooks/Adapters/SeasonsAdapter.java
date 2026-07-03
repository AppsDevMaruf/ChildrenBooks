/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/26/21 12:41 AM
 */

package com.marufalam.childrenbooks.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marufalam.childrenbooks.Models.SeasonsModel;
import com.marufalam.childrenbooks.R;
import com.marufalam.childrenbooks.SoundInterfare;

import java.util.ArrayList;

public class SeasonsAdapter extends RecyclerView.Adapter<SeasonsAdapter.SeasonViewHolder> {
    @NonNull
    final
    Context context;
    final ArrayList<SeasonsModel> seasonsModelArrayList;

    public SeasonsAdapter(@NonNull Context context, ArrayList<SeasonsModel> seasonsModelArrayList) {
        this.context = context;
        this.seasonsModelArrayList = seasonsModelArrayList;
    }

    @NonNull
    public SeasonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seasons_item_model,parent,false);
        return new SeasonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonViewHolder holder, int position) {
        holder.textView.setText(seasonsModelArrayList.get(holder.getAdapterPosition()).getSname());
        holder.imageView.setImageResource(seasonsModelArrayList.get(holder.getAdapterPosition()).getSimg());
        holder.itemView.setOnClickListener(v -> {
           // playSound(seasonsModelArrayList.get(position).getSsound());
            SoundInterfare.playSound(seasonsModelArrayList.get(holder.getAdapterPosition()).getSsound(), context);

        });

    }

    @Override
    public int getItemCount() {
        return seasonsModelArrayList.size();
    }

    public static class SeasonViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView textView;
        public SeasonViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.seasomsNameId);
            imageView = itemView.findViewById(R.id.seasonsImgId);
        }
    }
}
