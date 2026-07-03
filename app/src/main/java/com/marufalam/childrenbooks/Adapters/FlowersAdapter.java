/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/25/21 3:35 AM
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
import com.marufalam.childrenbooks.Models.FlowersModel;
import com.marufalam.childrenbooks.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class
FlowersAdapter extends FirebaseRecyclerAdapter<FlowersModel, FlowersAdapter.FlowerViewHolder> {

    public FlowersAdapter(FirebaseRecyclerOptions<FlowersModel> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull FlowerViewHolder holder, int position, @NonNull FlowersModel model) {
        Glide.with(holder.flowersimg.getContext()).load(model.getFlowersimg()).placeholder(R.drawable.placeholder_image).into(holder.flowersimg);
        holder.flowersname.setText(model.getFlowersname());

/*
        holder.flowersname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    //String url = "https://firebasestorage.googleapis.com/v0/b/children-books-94aba.appspot.com/o/All%20Sounds%2FNumbers%2Fzero.mp3?alt=media&token=b24df09a-7a88-4a71-9f10-38423110ebe7";

                    mediaPlayer.setDataSource(model.getSound());
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mPlayer) {
                            mPlayer.start();
                        }
                    });

                    mediaPlayer.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                */
/*MediaPlayer mPlayer = MediaPlayer.create(holder.flowersimg.getContext(),"" );

                if (mPlayer != null) {
                    mPlayer.start();

                }*//*



            }
        });
*/


    }

    @NonNull
    @Override
    public FlowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flowers_item_model, parent, false);
        return new FlowerViewHolder(view);

    }

    public static class FlowerViewHolder extends RecyclerView.ViewHolder {
        final ImageView flowersimg;
        final TextView flowersname;

        public FlowerViewHolder(@NonNull View itemView) {
            super(itemView);
            flowersimg = itemView.findViewById(R.id.flowersImgId);
            flowersname = itemView.findViewById(R.id.flowersNameId);
        }
    }
}
