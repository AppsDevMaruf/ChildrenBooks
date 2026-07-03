/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/10/21 12:44 AM
 */

package com.marufalam.childrenbooks.MyPager;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.marufalam.childrenbooks.Activites.MainActivity;
import com.marufalam.childrenbooks.Fragments.NumberItemFragment;
import com.marufalam.childrenbooks.Models.NumberItemModel;
import com.marufalam.childrenbooks.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NumFragActivity extends AppCompatActivity {

    ViewPager viewPager;
    vAdapterTwo adapter;
    TabLayout tabLayout;

    Intent intent;
    String name;


    List<NumberItemModel> models;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_num_frag);


        tabLayout = findViewById(R.id.tab);
        intent = getIntent();
        name = intent.getStringExtra("name");

        adapter = new vAdapterTwo(getSupportFragmentManager());
        //viewPager = findViewById(R.id.v2);

        models = new ArrayList<>();

        models.addAll(MainActivity.numberArrayList);


        for (NumberItemModel mod : models) {
            adapter.addFragment(new NumberItemFragment(mod));
        }


        String[] keys = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                "13", "14", "15", "16", "17", "18", "19", "20"};

        viewPager = findViewById(R.id.v2);
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(Arrays.asList(keys).indexOf(name));

//
        tabLayout.setupWithViewPager(viewPager);
    }
}