/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/16/21 10:20 PM
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
import com.marufalam.childrenbooks.Models.FruitsModel;
import com.marufalam.childrenbooks.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class FruitsAdapter extends FirebaseRecyclerAdapter<FruitsModel, FruitsAdapter.FruitsViewHolder> {


    public FruitsAdapter(@NonNull FirebaseRecyclerOptions<FruitsModel> options) {
        super(options);
    }


    protected void onBindViewHolder(@NonNull FruitsViewHolder holder, int position, @NonNull FruitsModel model) {

        Glide.with(holder.fruitsimage.getContext()).load(model.getFruitsimg()).placeholder(R.drawable.placeholder_image).into(holder.fruitsimage);
        holder.fruitsname.setText(model.getFruitsname());
        /*holder.fruitsname.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case 0:
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        Intent intent = new Intent(activity, Aboutme.class);
                        activity.startActivity(intent);
                        String refId = getRef(position).getKey();
                        Toast.makeText(activity, "testing Successful" +refId, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        AppCompatActivity activity2 = (AppCompatActivity) view.getContext();
                        Intent intent2 = new Intent(activity2, Aboutme.class);
                        activity2.startActivity(intent2);
                        String refId2 = getRef(position).getKey();
                        Toast.makeText(activity2, "testing Successful" +refId2, Toast.LENGTH_SHORT).show();
                        break;
                }

                

            }
        });*/
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(holder.fruitsimage.getContext(), Aboutme.class);
                intent.putExtra("img", model.getFruitsimg());
                intent.putExtra("name", model.getFruitsname());

                holder.fruitsimage.getContext().startActivity(intent);

            }
        });*/


    }

    @NonNull
    @Override
    public FruitsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fruits_item_model, parent, false);
        return new FruitsViewHolder(view);
    }

    public static class FruitsViewHolder extends RecyclerView.ViewHolder {
        final ImageView fruitsimage;
        final TextView fruitsname;

        public FruitsViewHolder(@NonNull View itemView) {
            super(itemView);

            fruitsimage = itemView.findViewById(R.id.fruitsImgId);
            fruitsname = itemView.findViewById(R.id.fruitsNameId);


        }
    }
}
