/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/26/21 12:41 AM
 */

package com.marufalam.childrenbooks.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.marufalam.childrenbooks.Models.NumberItemModel;
import com.marufalam.childrenbooks.R;
import com.marufalam.childrenbooks.SoundInterfare;


public class NumberItemFragment extends Fragment {

    NumberItemModel model;


    public NumberItemFragment(NumberItemModel model) {
        this.model = model;
    }

    public void setModel(NumberItemModel model) {
        this.model = model;
    }

    ImageView image_one, image_two;
    TextView inWord;
    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_number_item, container, false);
        image_one = root.findViewById(R.id.image_one);
        image_two = root.findViewById(R.id.image_two);
        inWord = root.findViewById(R.id.inWord);

        image_one.setImageResource(model.getNumImg());
        image_two.setImageResource(model.getNumCountImg());
        inWord.setText(model.getInWord());


        image_one.setOnClickListener(v -> {

          //  playSound(model.getNumSound());

            SoundInterfare.playSound(model.getNumSound(),getContext());

        });

        inWord.setOnClickListener(v -> {
         //   playSound(model.getInwordSound());

            SoundInterfare.playSound(model.getInwordSound(),getContext());
        });

        return root;
    }

}