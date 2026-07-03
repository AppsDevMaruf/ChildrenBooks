/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 2/11/21 11:47 PM
 */

package com.marufalam.childrenbooks.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marufalam.childrenbooks.Adapters.WeeksItemAdapter;
import com.marufalam.childrenbooks.Activites.MainActivity;
import com.marufalam.childrenbooks.R;


public class WeeksFragment extends Fragment {


    public WeeksFragment() {
        // Required empty public constructor
    }


    WeeksItemAdapter adapter;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_weeks, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.WeeksrecyclerView);
// set a LinearLayoutManager with default vertical orientaion
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
// call the constructor of CustomAdapter to send the reference and data to Adapter
        adapter = new WeeksItemAdapter(getActivity(), MainActivity.weeksItemModels);
        recyclerView.setAdapter(adapter);

        return view;


    }
}