/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/4/21 7:42 PM
 */

package com.marufalam.childrenbooks.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.marufalam.childrenbooks.MyPager.FragActivity;
import com.marufalam.childrenbooks.R;


public class AlphabetFragment extends Fragment {

    ImageView Aa, Bb, Cc, Dd, Ee, Ff, Gg, Hh, Ii, Jj, Kk, Ll, Mm, Nn, Oo, Pp, Qq, Rr, Ss, Tt, Uu, Vv, Ww, Xx, Yy, Zz;
    private InterstitialAd mInterstitialAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.tbAlphabet);
        View view = inflater.inflate(R.layout.fragment_alphabet, container, false);


        //setupInterAds();

        Aa = view.findViewById(R.id.Aa);
        Bb = view.findViewById(R.id.Bb);
        Cc = view.findViewById(R.id.Cc);
        Dd = view.findViewById(R.id.Dd);
        Ee = view.findViewById(R.id.Ee);
        Ff = view.findViewById(R.id.Ff);
        Gg = view.findViewById(R.id.Gg);
        Hh = view.findViewById(R.id.Hh);
        Ii = view.findViewById(R.id.Ii);
        Jj = view.findViewById(R.id.Jj);
        Kk = view.findViewById(R.id.Kk);
        Ll = view.findViewById(R.id.Ll);
        Mm = view.findViewById(R.id.Mm);
        Nn = view.findViewById(R.id.Nn);
        Oo = view.findViewById(R.id.Oo);
        Pp = view.findViewById(R.id.Pp);
        Qq = view.findViewById(R.id.Qq);
        Rr = view.findViewById(R.id.Rr);
        Ss = view.findViewById(R.id.Ss);
        Tt = view.findViewById(R.id.Tt);
        Uu = view.findViewById(R.id.Uu);
        Vv = view.findViewById(R.id.Vv);
        Ww = view.findViewById(R.id.Ww);
        Xx = view.findViewById(R.id.Xx);
        Yy = view.findViewById(R.id.Yy);
        Zz = view.findViewById(R.id.Zz);



        ClickHandler(Aa, "a");
        ClickHandler(Bb, "b");
        ClickHandler(Cc, "c");
        ClickHandler(Dd, "d");
        ClickHandler(Ee, "e");
        ClickHandler(Ff, "f");
        ClickHandler(Gg, "g");
        ClickHandler(Hh, "h");
        ClickHandler(Ii, "i");
        ClickHandler(Jj, "j");
        ClickHandler(Kk, "k");
        ClickHandler(Ll, "l");
        ClickHandler(Mm, "m");
        ClickHandler(Nn, "n");
        ClickHandler(Oo, "o");
        ClickHandler(Pp, "p");
        ClickHandler(Qq, "q");
        ClickHandler(Rr, "r");
        ClickHandler(Ss, "s");
        ClickHandler(Tt, "t");
        ClickHandler(Uu, "u");
        ClickHandler(Vv, "v");
        ClickHandler(Ww, "w");
        ClickHandler(Xx, "x");
        ClickHandler(Yy, "y");
        ClickHandler(Zz, "z");


        return view;
    }


    private void ClickHandler(ImageView imageView, String s) {

        imageView.setOnClickListener(v -> {

            //setupInterAds();

            if (mInterstitialAd != null) {
                mInterstitialAd.show(getActivity());
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }


            Intent intent = new Intent(getActivity(), FragActivity.class);
            intent.putExtra("name", s);
            startActivity(intent);

            //Toast.makeText(getActivity(), "this is " + v.getContentDescription(), Toast.LENGTH_SHORT).show();

        });


    }

    /*public void setupInterAds() {


        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getActivity(), "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
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