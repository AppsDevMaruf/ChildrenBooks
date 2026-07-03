/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/15/21 2:46 AM
 */

package com.marufalam.childrenbooks.Models;

public class WeeksItemModel {

    int image,weeksounds;

    public WeeksItemModel() {
    }

    public WeeksItemModel(int image, int weeksounds) {
        this.image = image;
        this.weeksounds = weeksounds;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getWeeksounds() {
        return weeksounds;
    }

    public void setWeeksounds(int weeksounds) {
        this.weeksounds = weeksounds;
    }
}
