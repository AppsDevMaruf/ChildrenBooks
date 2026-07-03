/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 2/17/21 3:25 AM
 */

package com.marufalam.childrenbooks.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marufalam.childrenbooks.Adapters.FruitsAdapter;
import com.marufalam.childrenbooks.Models.FruitsModel;
import com.marufalam.childrenbooks.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FruitsFragment extends Fragment {


    public FruitsFragment() {
        // Required empty public constructor
    }
    FruitsAdapter adapter;
   DatabaseReference myref;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fruits, container, false);

        //myref = FirebaseDatabase.getInstance().getReference();

        RecyclerView recyclerView = view.findViewById(R.id.fruitsrecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

       FirebaseRecyclerOptions<FruitsModel> options =
                new FirebaseRecyclerOptions.Builder<FruitsModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Fruits"), FruitsModel.class)
                        .build();

            adapter = new FruitsAdapter(options);
            recyclerView.setAdapter(adapter);
        return view;

    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}