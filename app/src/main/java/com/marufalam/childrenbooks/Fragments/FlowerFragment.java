/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 2/20/21 2:30 PM
 */

package com.marufalam.childrenbooks.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marufalam.childrenbooks.Adapters.FlowersAdapter;
import com.marufalam.childrenbooks.Models.FlowersModel;
import com.marufalam.childrenbooks.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;


public class FlowerFragment extends Fragment {


    public FlowerFragment() {
        // Required empty public constructor
    }

    FlowersAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_flower, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.flowersrecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        // set LayoutManager to RecyclerView
        FirebaseRecyclerOptions<FlowersModel> options =
                new FirebaseRecyclerOptions.Builder<FlowersModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Flowers"), FlowersModel.class)
                        .build();

        adapter = new FlowersAdapter(options);
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
