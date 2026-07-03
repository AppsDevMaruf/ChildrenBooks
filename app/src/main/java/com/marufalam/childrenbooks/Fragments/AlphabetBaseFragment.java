/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/26/21 12:37 AM
 */

package com.marufalam.childrenbooks.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.marufalam.childrenbooks.Models.AlphabetItemModel;
import com.marufalam.childrenbooks.R;


public class AlphabetBaseFragment extends SoundFrag {

    private AdView mAdView;
    final AlphabetItemModel model;
    Context context;

    public AlphabetBaseFragment(AlphabetItemModel AlphabetItemModel,SimpleExoPlayer player) {
        super(player);

        // Required empty public constructor
        this.model = AlphabetItemModel;

    }


    ImageView image_one, image_two,smallLetter;
    //VideoView videoView;

    TextView word_one,word_two,details;
    View root;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_alphabet_item, container, false);
        context = getContext();

       /* MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });*/

       // setPlayer(new SimpleExoPlayer.Builder(context).build());

        image_one = root.findViewById(R.id.image_one);
        image_two = root.findViewById(R.id.image_two);
        smallLetter = root.findViewById(R.id.smallLetterId);
        word_one = root.findViewById(R.id.word_one);
        word_two = root.findViewById(R.id.word_two);
        details = root.findViewById(R.id.details);



        image_one.setImageResource(model.getImage_one());
        image_two.setImageResource(model.getImage_two());
        smallLetter.setImageResource(model.getSmallLetter());
        word_one.setText(model.getWord_one());
        word_two.setText(model.getWord_two());
        details.setText(model.getDetails());

        //banner ads
        mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        pauseSound();
        image_one.setOnClickListener(v -> playSound(model.getSounLink(),context));

        image_two.setOnClickListener(v -> playSound(model.getInwordsound(),context));

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        //Toast.makeText(getContext(), "aa", Toast.LENGTH_SHORT).show();
        pauseSound();
    }
}