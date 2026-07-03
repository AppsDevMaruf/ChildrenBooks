/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/23/21 2:04 AM
 */

package com.marufalam.childrenbooks.Models;

public class RainboModel {
    int RainbowImg,Rainbowsound;

    public RainboModel() {
    }

    public RainboModel(int rainbowImg, int rainbowsound) {
        RainbowImg = rainbowImg;
        Rainbowsound = rainbowsound;
    }

    public int getRainbowImg() {
        return RainbowImg;
    }

    public void setRainbowImg(int rainbowImg) {
        RainbowImg = rainbowImg;
    }

    public int getRainbowsound() {
        return Rainbowsound;
    }

    public void setRainbowsound(int rainbowsound) {
        Rainbowsound = rainbowsound;
    }
}
