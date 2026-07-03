/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 1/25/21 12:26 AM
 */

package com.marufalam.childrenbooks.Models;

public class MonthsItemModel {
    int img,name,msound;

    public MonthsItemModel() {
    }

    public MonthsItemModel(int img, int name, int msound) {
        this.img = img;
        this.name = name;
        this.msound = msound;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getMsound() {
        return msound;
    }

    public void setMsound(int msound) {
        this.msound = msound;
    }
}
