/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/15/21 2:18 PM
 */

package com.marufalam.childrenbooks.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marufalam.childrenbooks.Activites.MainActivity;
import com.marufalam.childrenbooks.Adapters.RainbowAdapter;
import com.marufalam.childrenbooks.R;

public class RainbowFragment extends Fragment {


    public RainbowFragment() {
        // Required empty public constructor
    }
    RainbowAdapter adapter;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_rainbow, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.RainboRecy);
// set a LinearLayoutManager with default vertical orientaion
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
// call the constructor of CustomAdapter to send the reference and data to Adapter
        adapter = new RainbowAdapter(getActivity(), MainActivity.RainbowArrayList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}