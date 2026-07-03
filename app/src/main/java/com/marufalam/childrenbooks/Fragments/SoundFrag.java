/*
 * Created by Maruf Alam Porosh on 4/10/21 12:10 AM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 4/10/21 12:10 AM
 */

package com.marufalam.childrenbooks.Fragments;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

public class SoundFrag  extends Fragment {

    SimpleExoPlayer player;


    public SoundFrag(SimpleExoPlayer player) {
        this.player = player;
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public void setPlayer(SimpleExoPlayer player) {
        this.player = player;
    }

    public  void playSound(int sounRes, Context context){
       // pauseSound();

        player =  new SimpleExoPlayer.Builder(context).build();
        DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(sounRes));
        final RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(context);
        try {
            rawResourceDataSource.open(dataSpec);
        } catch (RawResourceDataSource.RawResourceDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = () -> rawResourceDataSource;
        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource
                .Factory(factory)
                .createMediaSource(rawResourceDataSource.getUri());

        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
        player.setVolume(1.0f);

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    player.release();
                }
            }
        });
    }

    public  void pauseSound(){


        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.seekTo(0);
            player.release();
        }
    }
}
