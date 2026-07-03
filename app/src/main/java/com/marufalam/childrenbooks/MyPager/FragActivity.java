/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/22/21 6:05 PM
 */

package com.marufalam.childrenbooks.MyPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.marufalam.childrenbooks.Fragments.AlphabetBaseFragment;
import com.marufalam.childrenbooks.Models.AlphabetItemModel;
import com.marufalam.childrenbooks.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragActivity extends AppCompatActivity {

    ViewPager viewPager;
    vAdapter adapter;
    TabLayout tabLayout;

    Intent intent;
    String name;


    SimpleExoPlayer player;
    Context context;

    List<AlphabetItemModel> models;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frag);

        context = FragActivity.this;
        player = new SimpleExoPlayer.Builder(context).build();


        tabLayout = findViewById(R.id.tab);

        intent = getIntent();
        name = intent.getStringExtra("name");

        adapter = new vAdapter(getSupportFragmentManager());


        models = new ArrayList<>();
        models.add(new AlphabetItemModel(R.string.AaText, R.drawable.a, R.drawable.apple,R.drawable.small_a,R.string.Axe,R.string.Ant,R.raw.a,R.raw.a_is));
        models.add(new AlphabetItemModel(R.string.BbText, R.drawable.b,R.drawable.bee,R.drawable.small_b,R.string.Bat,R.string.Box,R.raw.b,R.raw.b_is));
        models.add(new AlphabetItemModel(R.string.CcText, R.drawable.c, R.drawable.cat,R.drawable.small_c,R.string.Cap,R.string.Cup,R.raw.c,R.raw.c_is));
        models.add(new AlphabetItemModel(R.string.DdText, R.drawable.d, R.drawable.drum,R.drawable.small_d,R.string.Doll,R.string.Dog,R.raw.d,R.raw.d_is));
        models.add(new AlphabetItemModel(R.string.EeText, R.drawable.e, R.drawable.egg,R.drawable.small_e,R.string.Eye,R.string.Ear,R.raw.e,R.raw.e_is));
        models.add(new AlphabetItemModel(R.string.FfText, R.drawable.f, R.drawable.fish,R.drawable.small_f,R.string.Fan,R.string.Fox,R.raw.f,R.raw.f_is));
        models.add(new AlphabetItemModel(R.string.GgText, R.drawable.g, R.drawable.gloves,R.drawable.small_g,R.string.Girl,R.string.Goat,R.raw.g,R.raw.g_is));
        models.add(new AlphabetItemModel(R.string.HhText, R.drawable.h, R.drawable.house,R.drawable.small_h,R.string.Hat,R.string.Hut,R.raw.h,R.raw.h_is));
        models.add(new AlphabetItemModel(R.string.IiText, R.drawable.i, R.drawable.ice_house,R.drawable.small_i,R.string.Ice,R.string.Ink,R.raw.i,R.raw.i_is));
        models.add(new AlphabetItemModel(R.string.JjText, R.drawable.j, R.drawable.jar,R.drawable.small_j,R.string.Jar,R.string.Jeep,R.raw.j,R.raw.j_is));
        models.add(new AlphabetItemModel(R.string.KkText, R.drawable.k, R.drawable.key,R.drawable.small_k,R.string.Kite,R.string.Knife,R.raw.k,R.raw.k_is));
        models.add(new AlphabetItemModel(R.string.LlText, R.drawable.l, R.drawable.lock,R.drawable.small_l,R.string.Lion,R.string.Leg,R.raw.l,R.raw.l_is));
        models.add(new AlphabetItemModel(R.string.MmText, R.drawable.m, R.drawable.mug,R.drawable.small_m,R.string.Man,R.string.Map,R.raw.m,R.raw.m_is));
        models.add(new AlphabetItemModel(R.string.NnText, R.drawable.n, R.drawable.net,R.drawable.small_n,R.string.Nose,R.string.Nut,R.raw.n,R.raw.n_is));
        models.add(new AlphabetItemModel(R.string.OoText, R.drawable.o, R.drawable.orange,R.drawable.small_o,R.string.Onion,R.string.Ox,R.raw.o,R.raw.o_is));
        models.add(new AlphabetItemModel(R.string.PpText, R.drawable.p, R.drawable.pumpkin,R.drawable.small_p,R.string.Pen,R.string.Pot,R.raw.p,R.raw.p_is));
        models.add(new AlphabetItemModel(R.string.QqText, R.drawable.q, R.drawable.queen,R.drawable.small_q,R.string.Quiz,R.string.Quill,R.raw.q,R.raw.q_is));
        models.add(new AlphabetItemModel(R.string.RrText, R.drawable.r, R.drawable.robot,R.drawable.small_r,R.string.Rain,R.string.Run,R.raw.r,R.raw.r_is));
        models.add(new AlphabetItemModel(R.string.SsText, R.drawable.s, R.drawable.snake,R.drawable.small_s,R.string.Sun,R.string.Star,R.raw.s,R.raw.s_is));
        models.add(new AlphabetItemModel(R.string.TtText, R.drawable.t, R.drawable.tiger,R.drawable.small_t,R.string.Table,R.string.Tree,R.raw.t,R.raw.t_is));
        models.add(new AlphabetItemModel(R.string.UuText, R.drawable.u, R.drawable.umbrella,R.drawable.small_u,R.string.Uniform,R.string.Uncle,R.raw.u,R.raw.u_is));
        models.add(new AlphabetItemModel(R.string.VvText, R.drawable.v, R.drawable.virus,R.drawable.small_v,R.string.Violin,R.string.Van,R.raw.v,R.raw.v_is));
        models.add(new AlphabetItemModel(R.string.WwText, R.drawable.w, R.drawable.watch,R.drawable.small_w,R.string.Window,R.string.Win,R.raw.w,R.raw.w_is));
        models.add(new AlphabetItemModel(R.string.XxText, R.drawable.x, R.drawable.xylophone,R.drawable.small_x,R.string.Xray,R.string.Xmas,R.raw.x,R.raw.x_is));
        models.add(new AlphabetItemModel(R.string.YyText, R.drawable.y, R.drawable.yo_yo,R.drawable.small_y,R.string.Yarn,R.string.Young,R.raw.y,R.raw.y_is));
        models.add(new AlphabetItemModel(R.string.ZzText, R.drawable.z, R.drawable.zipper,R.drawable.small_z,R.string.Zoo,R.string.Zero,R.raw.z,R.raw.z_is));

        for (AlphabetItemModel mod : models) {
            adapter.addFragment(new AlphabetBaseFragment(mod,player));
        }

        String[] keys = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
                "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};


        viewPager = findViewById(R.id.v2);

        //viewPager.setOffscreenPageLimit(Arrays.asList(keys).indexOf(name));
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(Arrays.asList(keys).indexOf(name));
        //int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        //viewPager.setOffscreenPageLimit(limit);

        tabLayout.setupWithViewPager(viewPager);

    }
}