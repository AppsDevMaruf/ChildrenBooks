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
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marufalam.childrenbooks.Models.WeeksItemModel;
import com.marufalam.childrenbooks.R;
import com.marufalam.childrenbooks.SoundInterfare;

import java.util.ArrayList;

public class WeeksItemAdapter extends RecyclerView.Adapter<WeeksItemAdapter.WeeksItemViewHolder> {
    final Context context;
    final ArrayList<WeeksItemModel>  weeksItemAdapterArrayList;


    public WeeksItemAdapter(Context context, ArrayList<WeeksItemModel> weeksItemAdapterArrayList) {
        this.context = context;
        this.weeksItemAdapterArrayList = weeksItemAdapterArrayList;
    }

    @NonNull
    @Override
    public WeeksItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.weeks_item_model,parent,false);
        return new WeeksItemViewHolder(view);
     }

    @Override
    public void onBindViewHolder(@NonNull WeeksItemViewHolder holder, int position) {
        WeeksItemModel weeksItemModel= weeksItemAdapterArrayList.get(position);

        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition_animation));
        holder.image.setImageResource(weeksItemModel.getImage());

        holder.itemView.setOnClickListener(v -> {
           // playSound(weeksItemModel.getWeeksounds());
            SoundInterfare.playSound(weeksItemModel.getWeeksounds(),context);

        });
        
    }


    @Override
    public int getItemCount() {
        return weeksItemAdapterArrayList.size();
    }

    public static class WeeksItemViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        public WeeksItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image =   itemView.findViewById(R.id.weeksModImg);


        }
    }




}
