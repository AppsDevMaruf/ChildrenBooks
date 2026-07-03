/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/4/21 6:35 PM
 */

package com.marufalam.childrenbooks.MyPager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;



import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class vAdapter extends FragmentPagerAdapter {



  public  static final List<Fragment> fragmentList = new ArrayList<>();



    public vAdapter(@NonNull @NotNull FragmentManager fm) {
        super(fm);

    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {


        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment) {
      //  fragmentList.clear();
        fragmentList.add(fragment);

    }
}
