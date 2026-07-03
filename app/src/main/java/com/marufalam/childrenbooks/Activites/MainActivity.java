/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/23/21 3:27 AM
 */

package com.marufalam.childrenbooks.Activites;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;
import com.marufalam.childrenbooks.Adapters.SideItemAdapter;
import com.marufalam.childrenbooks.Fragments.AlphabetFragment;
import com.marufalam.childrenbooks.Models.MonthsItemModel;
import com.marufalam.childrenbooks.Models.NumberItemModel;
import com.marufalam.childrenbooks.Models.RainboModel;
import com.marufalam.childrenbooks.Models.SeasonsModel;
import com.marufalam.childrenbooks.Models.WeeksItemModel;
import com.marufalam.childrenbooks.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;


public class MainActivity extends AppCompatActivity {
    AlertDialog.Builder ExitDialog;

    public static ArrayList<WeeksItemModel> weeksItemModels;
    public static ArrayList<MonthsItemModel> MonthsArrayList;
    public static ArrayList<NumberItemModel> numberArrayList;
    public static ArrayList<SeasonsModel> seasonsArrayList;
    public static ArrayList<RainboModel> RainbowArrayList;

    // ArrayList for item names
    final ArrayList<String> itemNames = new ArrayList<>(Arrays.asList(" Alphabet ", " Number ", "7 Days", "12\nMonths", "6\nSeasons", "Rainbow Colours", "Animals", "Fishes", "Birds", "Flowers", "Fruits", "Vegetables"));
    final ArrayList<Integer> itemImages = new ArrayList<>(Arrays.asList(R.drawable.alphabet, R.drawable.number, R.drawable.days, R.drawable.months, R.drawable.seasons, R.drawable.rainbo, R.drawable.animal, R.drawable.fish, R.drawable.birds, R.drawable.flower_cover, R.drawable.fruits, R.drawable.vegetable));
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize ad
      MobileAds.initialize(this, initializationStatus -> {
      });

   /*     mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        Toolbar toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        TextView tbartext = toolbar.findViewById(R.id.tbartextId);

        DrawerLayout drawerLayout = findViewById(R.id.drawerId);

        NavigationView navigationView = findViewById(R.id.nevViewId);


        ///NavigationView Item Icon Tint

        navigationView.setItemIconTintList(null);
        navigationView.setBackgroundResource(R.color.white);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.homeId:
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(Gravity.LEFT); //CLOSE Nav Drawer!
                    } else {
                        drawerLayout.openDrawer(Gravity.LEFT); //OPEN Nav Drawer!
                    }

                    Activity activity3 = MainActivity.this;
                    FragmentManager fm3 = activity3.getFragmentManager();
                    Fragment fragment3 = new AlphabetFragment();
                    fm3.beginTransaction().replace(R.id.container, fragment3).commit();
                    break;
                case R.id.mailId:
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"marufalam120@gmail.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    startActivity(Intent.createChooser(intent, "Email via..."));
                    break;
                case R.id.shareId:
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app at: https://play.google.com/store/apps/details?id=" + getPackageName());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    break;
                case R.id.ppolicyId:
                    Intent policy = new Intent();
                    policy.setAction(Intent.ACTION_VIEW);
                    policy.addCategory(Intent.CATEGORY_BROWSABLE);
                    policy.setData(Uri.parse("https://learningforkidsapp.blogspot.com/2021/04/learning-for-kids-children-books-apps.html"));
                    startActivity(policy);
                    break;
                case R.id.moreApps:
                     Intent browerintent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/developer?id=Maruf+Alam"));
                     startActivity(browerintent);
                    break;
                case R.id.ratingId:
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(myAppLinkToMarket);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, " unable to find market app", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.suggestionId:

                                           //Create dialog
                    Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.dialog_update);
                    //initialize width
                    int width = WindowManager.LayoutParams.MATCH_PARENT;
                    //initialize height
                    int height = WindowManager.LayoutParams.WRAP_CONTENT;
                    //set layout
                    dialog.getWindow().setLayout(width, height);
                    //show dialog
                    EditText editText = dialog.findViewById(R.id.edit_text);
                    Button btsubmit = dialog.findViewById(R.id.btnsubmitId);
                    Button btcancel = dialog.findViewById(R.id.btnCancel);
                    dialog.setCancelable(false);
                    dialog.show();
                    btcancel.setOnClickListener(v -> {
                        Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                    btsubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                            processInsert();

                        }

                        private void processInsert() {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("feedback", editText.getText().toString());
                            FirebaseDatabase.getInstance().getReference().child("feedback").push()
                                    .setValue(map)
                                    .addOnSuccessListener(aVoid -> {
                                        editText.setText(" ");
                                        Toast.makeText(MainActivity.this, "Feedback Send Successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Feedback Send Faild", Toast.LENGTH_SHORT).show());
                        }
                    });


                    break;
            }
            return true;
        });

        FragmentManager fm1 = this.getFragmentManager();
        Fragment fragment1 = new AlphabetFragment();
        fm1.beginTransaction().replace(R.id.container, fragment1).addToBackStack(null).commit();


        weeksItemModels = new ArrayList<>();
        weeksItemModels.add(new WeeksItemModel(R.drawable.sunday, R.raw.sunday));
        weeksItemModels.add(new WeeksItemModel(R.drawable.monday, R.raw.monday));
        weeksItemModels.add(new WeeksItemModel(R.drawable.tuesday, R.raw.tuesday));
        weeksItemModels.add(new WeeksItemModel(R.drawable.wednsday, R.raw.wednesday));
        weeksItemModels.add(new WeeksItemModel(R.drawable.thursday, R.raw.thursday));
        weeksItemModels.add(new WeeksItemModel(R.drawable.friday, R.raw.friday));
        weeksItemModels.add(new WeeksItemModel(R.drawable.saturday, R.raw.saturday));

        MonthsArrayList = new ArrayList<>();
        MonthsArrayList.add(new MonthsItemModel(R.drawable.januray, R.string.January, R.raw.january));
        MonthsArrayList.add(new MonthsItemModel(R.drawable.february, R.string.February, R.raw.february));
        MonthsArrayList.add(new MonthsItemModel(R.drawable.march, R.string.March, R.raw.march));
        MonthsArrayList.add(new MonthsItemModel(R.drawable.april, R.string.April, R.raw.april));
        MonthsArrayList.add(new MonthsItemModel(R.drawable.may, R.string.May, R.raw.may));
        MonthsArrayList.add(new MonthsItemModel(R.drawable.june, R.string.June, R.raw.june));
        MonthsArrayList.add(new MonthsItemModel(R.drawable.july, R.string.July, R.raw.july));
        MonthsArrayList.add(new MonthsItemModel(R.drawable.august, R.string.August, R.raw.august));
        MonthsArrayList.add(new MonthsItemModel(R.drawable.septembere, R.string.September, R.raw.september));
        MonthsArrayList.add(new MonthsItemModel(R.drawable.october, R.string.October, R.raw.october));
        MonthsArrayList.add(new MonthsItemModel(R.drawable.november, R.string.November, R.raw.november));
        MonthsArrayList.add(new MonthsItemModel(R.drawable.december, R.string.December, R.raw.december));

        numberArrayList = new ArrayList<>();
        numberArrayList.add(new NumberItemModel(R.drawable.zero, R.drawable.c_num_zero, R.raw.zero, R.string.zero, R.raw.zero_w, "0"));
        numberArrayList.add(new NumberItemModel(R.drawable.one, R.drawable.c_num_one, R.raw.one, R.string.one, R.raw.one_w, "1"));
        numberArrayList.add(new NumberItemModel(R.drawable.two, R.drawable.c_num_two, R.raw.two, R.string.two, R.raw.two_w, "2"));
        numberArrayList.add(new NumberItemModel(R.drawable.three, R.drawable.c_num_three, R.raw.three, R.string.three, R.raw.three_w, "3"));
        numberArrayList.add(new NumberItemModel(R.drawable.four, R.drawable.c_num_four, R.raw.four, R.string.four, R.raw.four_w, "4"));
        numberArrayList.add(new NumberItemModel(R.drawable.five, R.drawable.c_num_five, R.raw.five, R.string.five, R.raw.five_w, "5"));
        numberArrayList.add(new NumberItemModel(R.drawable.six, R.drawable.c_num_six, R.raw.six, R.string.six, R.raw.sis_w, "6"));
        numberArrayList.add(new NumberItemModel(R.drawable.seven, R.drawable.c_num_seven, R.raw.seven, R.string.seven, R.raw.seven_w, "7"));
        numberArrayList.add(new NumberItemModel(R.drawable.eight, R.drawable.c_num_eight, R.raw.eight, R.string.eight, R.raw.eight_w, "8"));
        numberArrayList.add(new NumberItemModel(R.drawable.nine, R.drawable.c_num_nine, R.raw.nine, R.string.nine, R.raw.nine_w, "9"));
        numberArrayList.add(new NumberItemModel(R.drawable.ten, R.drawable.c_num_ten, R.raw.ten, R.string.ten, R.raw.ten_w, "10"));
        numberArrayList.add(new NumberItemModel(R.drawable.eleven, R.drawable.c_num_eleven, R.raw.eleven, R.string.eleven, R.raw.eleven_w, "11"));
        numberArrayList.add(new NumberItemModel(R.drawable.twelve, R.drawable.c_num_twelve, R.raw.twelve, R.string.twelve, R.raw.twelve_w, "12"));
        numberArrayList.add(new NumberItemModel(R.drawable.thirteen, R.drawable.c_num_thirteen, R.raw.thirteen, R.string.thirteen, R.raw.thirteen_w, "13"));
        numberArrayList.add(new NumberItemModel(R.drawable.fourteen, R.drawable.c_num_fourteen, R.raw.fourteen, R.string.fourteen, R.raw.fourteen_w, "14"));
        numberArrayList.add(new NumberItemModel(R.drawable.fifteen, R.drawable.c_num_fifteen, R.raw.fifteen, R.string.fifteen, R.raw.fifteen_w, "15"));
        numberArrayList.add(new NumberItemModel(R.drawable.sixteen, R.drawable.c_num_sixteen, R.raw.sixteen, R.string.sixteen, R.raw.sixteen_w, "16"));
        numberArrayList.add(new NumberItemModel(R.drawable.seventeen, R.drawable.c_num_seventeen, R.raw.seventeen, R.string.seventeen, R.raw.seventeen_w, "17"));
        numberArrayList.add(new NumberItemModel(R.drawable.eighteen, R.drawable.c_num_eihteen, R.raw.eighteen, R.string.eighteen, R.raw.eighteen_w, "18"));
        numberArrayList.add(new NumberItemModel(R.drawable.nineteen, R.drawable.c_num_ninteen, R.raw.nineteen, R.string.nineteen, R.raw.ninteen_w, "19"));
        numberArrayList.add(new NumberItemModel(R.drawable.twenty, R.drawable.c_num_twenty, R.raw.twenty, R.string.twenty, R.raw.twemty_w, "20"));


        seasonsArrayList = new ArrayList<>();
        seasonsArrayList.add(new SeasonsModel(R.drawable.summer, R.string.summer, R.raw.summer_s));
        seasonsArrayList.add(new SeasonsModel(R.drawable.ranyday, R.string.rainy, R.raw.rainy_season_s));
        seasonsArrayList.add(new SeasonsModel(R.drawable.autumn, R.string.autumn, R.raw.autumn_s));
        seasonsArrayList.add(new SeasonsModel(R.drawable.late_autum, R.string.lateautumn, R.raw.late_autumn_s));
        seasonsArrayList.add(new SeasonsModel(R.drawable.winter, R.string.winter, R.raw.winter_s));
        seasonsArrayList.add(new SeasonsModel(R.drawable.spring, R.string.spring, R.raw.spring_s));
        //Rainbow
        RainbowArrayList = new ArrayList<>();
        RainbowArrayList.add(new RainboModel(R.drawable.red,R.raw.red_s));
        RainbowArrayList.add(new RainboModel(R.drawable.orangecolor,R.raw.orange_s));
        RainbowArrayList.add(new RainboModel(R.drawable.yellow,R.raw.yellow_s));
        RainbowArrayList.add(new RainboModel(R.drawable.green,R.raw.green_s));
        RainbowArrayList.add(new RainboModel(R.drawable.blue,R.raw.blue_s));
        RainbowArrayList.add(new RainboModel(R.drawable.indigo,R.raw.indigo_s));
        RainbowArrayList.add(new RainboModel(R.drawable.violet,R.raw.violet_s));


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
// set a LinearLayoutManager with default vertical orientaion
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
// call the constructor of CustomAdapter to send the reference and data to Adapter
        SideItemAdapter sideItemAdapter = new SideItemAdapter(itemNames, itemImages, MainActivity.this);
        recyclerView.setAdapter(sideItemAdapter); // set the Adapter to RecyclerView


        //setSupportActionBar(tb);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutmenu:
                loadExitDialog();
                break;
            case R.id.website:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.marufalam.com"));
                startActivity(intent);
                break;
            case R.id.aboutme:
                Intent intent2 = new Intent(MainActivity.this, Aboutme.class);
                startActivity(intent2);
                break;

        }
        return true;
    }


    @Override
    public void onBackPressed() {

        loadExitDialog();

        //    super.onBackPressed();

    }


    public void loadExitDialog() {


      MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setTitle("Children Books")
                .setMessage("Do you Want to Exit ?")
                .setCancelable(false)

                .setPositiveButton("Yes", R.drawable.ic_baseline_check_24, (dialogInterface, which) -> finish())
                .setNegativeButton("No", R.drawable.ic_baseline_close_24, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        // Show Dialog
        mDialog.show();

    }

}