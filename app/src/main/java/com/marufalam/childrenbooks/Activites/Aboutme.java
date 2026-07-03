/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/17/21 7:00 PM
 */

package com.marufalam.childrenbooks.Activites;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.marufalam.childrenbooks.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class Aboutme extends AppCompatActivity {

    Intent intent;


    String imgLink, nameStr;


    CircleImageView profile_image;

    TextView profileTetile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutme);


        /*intent=getIntent();
        profile_image=findViewById(R.id.profile_image);
        profileTetile=findViewById(R.id.profileTetile);

       imgLink=intent.getStringExtra("img");
        nameStr=intent.getStringExtra("name");
        Glide.with(this).load(imgLink).placeholder(R.drawable.placeholder_image).into(profile_image);
        profileTetile.setText(nameStr);*/


    }

    public void sendMail(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL  , new String[] { "marufalam120@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        startActivity(Intent.createChooser(intent, "Email via..."));
    }

    public void gotoWebsite(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://www.marufalam.com"));
        startActivity(intent);
    }


    public void goFacebook(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/marufalam.porosh.90"));
            startActivity(intent);
        } catch(Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/marufalam.porosh.90")));
        }

    }

    public void gowhatsapps(View view) {

        String number = "+880 1687422428";
        String url = "https://api.whatsapp.com/send?phone="+number;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void goMap(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/@23.7490521,90.4247799,16z"));
        startActivity(intent);
    }

    public void myProfile(View view) {
        Toast.makeText(this, "CV Update Ongoing", Toast.LENGTH_SHORT).show();
    }
}