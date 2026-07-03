/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/4/21 6:44 PM
 */

package com.marufalam.childrenbooks;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.marufalam.childrenbooks.MyPager.vAdapter.fragmentList;

public class NumberItemAdapter extends FragmentPagerAdapter {

    public  static final List<Fragment> NumfragmentList = new ArrayList<>();
    public NumberItemAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return NumfragmentList.get(position);
    }

    @Override
    public int getCount() {
        return NumfragmentList.size();
    }
    public void addFragment(Fragment fragment) {
        //  fragmentList.clear();
        fragmentList.add(fragment);

    }
}
