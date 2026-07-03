/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/28/21 11:10 PM
 */

package com.marufalam.childrenbooks.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marufalam.childrenbooks.Models.MonthsItemModel;
import com.marufalam.childrenbooks.SoundInterfare;
import com.marufalam.childrenbooks.R;

import java.util.ArrayList;

public class MonthsAdapter extends RecyclerView.Adapter<MonthsAdapter.MonthViewHolder> {
    Context context;
    ArrayList<MonthsItemModel> MonthsArrayList;

    public MonthsAdapter() {
    }

    public MonthsAdapter(Context context, ArrayList<MonthsItemModel> monthsArrayList) {
        this.context = context;
        MonthsArrayList = monthsArrayList;
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.months_item_model, parent, false);
        return new MonthViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {

        MonthsItemModel monthsItemModel = MonthsArrayList.get(position);
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        holder.monthsimage.setImageResource(monthsItemModel.getImg());
        holder.monthsName.setText(monthsItemModel.getName());
        holder.itemView.setOnClickListener(view -> {
//                playSound(monthsItemModel.getMsound());
            SoundInterfare.playSound(monthsItemModel.getMsound(),context);
        });


    }

    @Override
    public int getItemCount() {
        return MonthsArrayList.size();
    }

    public static class MonthViewHolder extends RecyclerView.ViewHolder {
        final ImageView monthsimage;
        final TextView monthsName;

        public MonthViewHolder(@NonNull View itemView) {
            super(itemView);
            monthsimage = itemView.findViewById(R.id.monthsImgId);
            monthsName = itemView.findViewById(R.id.monthsNameId);

        }
    }
}
