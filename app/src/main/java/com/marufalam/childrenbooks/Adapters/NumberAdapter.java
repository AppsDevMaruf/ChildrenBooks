/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/21/21 1:16 PM
 */

package com.marufalam.childrenbooks.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marufalam.childrenbooks.Models.NumberItemModel;
import com.marufalam.childrenbooks.MyPager.NumFragActivity;
import com.marufalam.childrenbooks.R;

import java.util.ArrayList;

public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.NumberViewHolder> {
    Context context;
    ArrayList<NumberItemModel> numberArrayList;

    public NumberAdapter() {
    }

    public NumberAdapter(Context context, ArrayList<NumberItemModel> numberArrayList) {
        this.context = context;
        this.numberArrayList = numberArrayList;
    }

    @NonNull
    @Override
    public NumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.number_item_model,parent,false);
        return new NumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NumberViewHolder holder, int position) {
        NumberItemModel numberItemModel = numberArrayList.get(position);
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition_animation));
        holder.numberImg.setImageResource(numberItemModel.getNumImg());
        holder.numberImg.setOnClickListener(v -> {
//
            Intent intent = new Intent(context, NumFragActivity.class);
            intent.putExtra("name", numberItemModel.getNm());
            context.startActivity(intent);

        });


    }

    @Override
    public int getItemCount() {
        return numberArrayList.size();
    }

    public class NumberViewHolder extends RecyclerView.ViewHolder {
        final ImageView numberImg;
        public NumberViewHolder(@NonNull View itemView) {
            super(itemView);
            numberImg = itemView.findViewById(R.id.numImgLeft);

        }
    }
}
