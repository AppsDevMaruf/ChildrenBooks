/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 2/21/21 12:34 PM
 */

package com.marufalam.childrenbooks.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marufalam.childrenbooks.Adapters.AllInOneAdapter;
import com.marufalam.childrenbooks.Models.AllInOneModel;
import com.marufalam.childrenbooks.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;


public class BirdsFragment extends Fragment {


    public BirdsFragment() {
        // Required empty public constructor
    }
    AllInOneAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_animals, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.itemrecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        // set LayoutManager to RecyclerView
        FirebaseRecyclerOptions<AllInOneModel> options =
                new FirebaseRecyclerOptions.Builder<AllInOneModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Birds"), AllInOneModel.class)
                        .build();

        adapter = new AllInOneAdapter(options);
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