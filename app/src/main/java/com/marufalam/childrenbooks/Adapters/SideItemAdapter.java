/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/15/21 2:18 PM
 */

package com.marufalam.childrenbooks.Adapters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marufalam.childrenbooks.Fragments.AlphabetFragment;
import com.marufalam.childrenbooks.Fragments.AnimalsFragment;
import com.marufalam.childrenbooks.Fragments.BirdsFragment;
import com.marufalam.childrenbooks.Fragments.FishsFragment;
import com.marufalam.childrenbooks.Fragments.FlowerFragment;
import com.marufalam.childrenbooks.Fragments.FruitsFragment;
import com.marufalam.childrenbooks.Fragments.MonthsFragment;
import com.marufalam.childrenbooks.Fragments.NumberFragment;
import com.marufalam.childrenbooks.Fragments.RainbowFragment;
import com.marufalam.childrenbooks.Fragments.SeasonsFragment;
import com.marufalam.childrenbooks.Fragments.VegetablesFragment;
import com.marufalam.childrenbooks.Fragments.WaitForNextUpdate;
import com.marufalam.childrenbooks.Fragments.WeeksFragment;
import com.marufalam.childrenbooks.R;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SideItemAdapter extends RecyclerView.Adapter<SideItemAdapter.SideViewHolder> {

    final ArrayList<String> itemNames;
    final ArrayList<Integer> itemImages;

    final Context context;


    private InterstitialAd mInterstitialAd;

    public SideItemAdapter(ArrayList<String> itemNames, ArrayList<Integer> itemImages, Context context) {
        this.itemNames = itemNames;
        this.itemImages = itemImages;
        this.context = context;
    }

    @NonNull
    public SideViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.side_item_model, parent, false);
        return new SideViewHolder(view);

    }

    public void onBindViewHolder(SideViewHolder holder, int position) {

        String name = itemNames.get(holder.getAdapterPosition());
        Integer img = itemImages.get(holder.getAdapterPosition());


        holder.name.setText(name);
        holder.image.setImageResource(img);


        holder.itemView.setOnClickListener(v -> {

          /*  setupInterAds();

            if (mInterstitialAd != null) {
                mInterstitialAd.show(activity);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }*/


            switch (holder.getAdapterPosition()) {
                case 0:
                    Activity activity = (Activity) context;
                    FragmentManager fm = activity.getFragmentManager();
                    Fragment fragment = new AlphabetFragment();
                    fm.beginTransaction().replace(R.id.container, fragment).commit();
                    break;

                case 1:
                    Activity activity1 = (Activity) context;
                    FragmentManager fm1 = activity1.getFragmentManager();
                    Fragment fragment1 = new NumberFragment();
                    fm1.beginTransaction().replace(R.id.container, fragment1).commit();
                    break;
                case 2:
                    Activity activity2 = (Activity) context;
                    FragmentManager fm2 = activity2.getFragmentManager();
                    Fragment fragment2 = new WeeksFragment();
                    fm2.beginTransaction().replace(R.id.container, fragment2).commit();
                    break;
                case 3:
                    Activity activity3 = (Activity) context;
                    FragmentManager fm3 = activity3.getFragmentManager();
                    Fragment fragment3 = new MonthsFragment();
                    fm3.beginTransaction().replace(R.id.container, fragment3).commit();
                    break;
                case 4:
                    Activity activity4 = (Activity) context;
                    FragmentManager fm4 = activity4.getFragmentManager();
                    Fragment fragment4 = new SeasonsFragment();
                    fm4.beginTransaction().replace(R.id.container, fragment4).commit();
                    break;
                case 5:
                    Activity activity5 = (Activity) context;
                    FragmentManager fm5 = activity5.getFragmentManager();
                    Fragment fragment5 = new RainbowFragment();
                    fm5.beginTransaction().replace(R.id.container, fragment5).commit();
                    break;
                case 6:
                    Activity activity6 = (Activity) context;
                    holder.cheekInternet();
                    FragmentManager fm6 = activity6.getFragmentManager();
                    Fragment fragment6 = new AnimalsFragment();
                    fm6.beginTransaction().replace(R.id.container, fragment6).commit();
                    break;
                case 7:
                    Activity activity7 = (Activity) context;
                    holder.cheekInternet();
                    FragmentManager fm7 = activity7.getFragmentManager();
                    Fragment fragment7 = new FishsFragment();
                    fm7.beginTransaction().replace(R.id.container, fragment7).commit();
                    break;
                case 8:

                    Activity activity8 = (Activity) context;
                    holder.cheekInternet();
                    FragmentManager fm8 = activity8.getFragmentManager();
                    Fragment fragment8 = new BirdsFragment();
                    fm8.beginTransaction().replace(R.id.container, fragment8).commit();
                    break;
                case 9:
                    Activity activity9 = (Activity) context;
                    holder.cheekInternet();
                    FragmentManager fm9 = activity9.getFragmentManager();
                    Fragment fragment9 = new FlowerFragment();
                    fm9.beginTransaction().replace(R.id.container, fragment9).commit();
                    break;

                case 10:
                    Activity activity10 = (Activity) context;
                    holder.cheekInternet();
                    FragmentManager fm10 = activity10.getFragmentManager();
                    Fragment fragment10 = new FruitsFragment();
                    fm10.beginTransaction().replace(R.id.container, fragment10).commit();
                    break;
                case 11:
                    Activity activity11 = (Activity) context;
                    holder.cheekInternet();
                    FragmentManager fm11 = activity11.getFragmentManager();
                    Fragment fragment11 = new VegetablesFragment();
                    fm11.beginTransaction().replace(R.id.container, fragment11).commit();
                    break;

                default:
                    Activity activitydefault = (Activity) context;
                    FragmentManager fmdefault = activitydefault.getFragmentManager();
                    Fragment fmdefaultupdate = new WaitForNextUpdate();
                    fmdefault.beginTransaction().replace(R.id.container, fmdefaultupdate).commit();


            }


        });

    }


    public int getItemCount() {
        return itemNames.size();
    }

    public class SideViewHolder extends RecyclerView.ViewHolder {

        public final TextView name;
        public final ImageView image;

        public SideViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewId);
            image = itemView.findViewById(R.id.imageViewId);
        }
        public void cheekInternet(){
            ConnectivityManager conMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                    || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {

                // notify user you are online

            }
            else if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                    || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {

                Toast.makeText(context, "please check your internet connection and try again", Toast.LENGTH_LONG).show();
            }
        }

    }


    /*public void setupInterAds() {


        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context, "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i("TAG", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i("TAG", loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });
    }*/

}