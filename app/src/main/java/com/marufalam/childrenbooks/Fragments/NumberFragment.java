/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/5/21 12:00 AM
 */

package com.marufalam.childrenbooks.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marufalam.childrenbooks.Activites.MainActivity;
import com.marufalam.childrenbooks.Adapters.NumberAdapter;
import com.marufalam.childrenbooks.R;


public class NumberFragment extends Fragment {
    public NumberFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_number, container, false);




        RecyclerView recyclerView = view.findViewById(R.id.numbersrecyclerView);
// set a LinearLayoutManager with default vertical orientaion
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
// call the constructor of CustomAdapter to send the reference and data to Adapter
        NumberAdapter adapter = new NumberAdapter(getActivity(), MainActivity.numberArrayList);
        recyclerView.setAdapter(adapter);
        return view;

    }

}

