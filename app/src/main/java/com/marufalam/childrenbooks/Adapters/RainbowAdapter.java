/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/24/21 3:52 AM
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

import com.marufalam.childrenbooks.Models.RainboModel;
import com.marufalam.childrenbooks.R;
import com.marufalam.childrenbooks.SoundInterfare;

import java.util.ArrayList;

public class RainbowAdapter extends RecyclerView.Adapter<RainbowAdapter.RainbowViewHolder> {
   final Context context;
   final ArrayList<RainboModel> RainbowArrayList;

    public RainbowAdapter(Context context, ArrayList<RainboModel> rainbowArrayList) {
        this.context = context;
        RainbowArrayList = rainbowArrayList;
    }

    @NonNull
    @Override
    public RainbowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rainbow_item_model,parent,false);
        return new RainbowViewHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull RainbowViewHolder holder, int position) {

        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition_animation));
        holder.rinbowImg.setImageResource(RainbowArrayList.get(holder.getAdapterPosition()).getRainbowImg());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundInterfare.playSound(RainbowArrayList.get(holder.getAdapterPosition()).getRainbowsound(),context);
            }

        });



    }


    @Override
    public int getItemCount() {
        return RainbowArrayList.size();
    }

    public static class RainbowViewHolder extends RecyclerView.ViewHolder {
        final ImageView rinbowImg;
        public RainbowViewHolder(@NonNull View itemView) {
            super(itemView);

            rinbowImg = itemView.findViewById(R.id.RainboModImg);

        }
    }
}
