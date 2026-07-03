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

import com.marufalam.childrenbooks.Adapters.SeasonsAdapter;
import com.marufalam.childrenbooks.Activites.MainActivity;
import com.marufalam.childrenbooks.R;


public class SeasonsFragment extends Fragment {
    SeasonsAdapter adapter;

    public SeasonsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


                View view = inflater.inflate(R.layout.fragment_seasons, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.seasonsrecyView);
// set a LinearLayoutManager with default vertical orientaion
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
// call the constructor of CustomAdapter to send the reference and data to Adapter
        adapter = new SeasonsAdapter(getActivity(), MainActivity.seasonsArrayList);
        recyclerView.setAdapter(adapter);
        return view;
    }
}