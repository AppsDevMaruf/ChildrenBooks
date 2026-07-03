/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 1/29/21 11:55 PM
 */

package com.marufalam.childrenbooks.Models;

public class SeasonsModel {
    int simg, sname, ssound;

    public SeasonsModel() {
    }

    public SeasonsModel(int simg, int sname, int ssound) {
        this.simg = simg;
        this.sname = sname;
        this.ssound = ssound;
    }

    public int getSimg() {
        return simg;
    }

    public void setSimg(int simg) {
        this.simg = simg;
    }

    public int getSname() {
        return sname;
    }

    public void setSname(int sname) {
        this.sname = sname;
    }

    public int getSsound() {
        return ssound;
    }

    public void setSsound(int ssound) {
        this.ssound = ssound;
    }
}