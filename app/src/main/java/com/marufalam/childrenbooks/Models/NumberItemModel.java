/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/21/21 1:07 PM
 */

package com.marufalam.childrenbooks.Models;

public class NumberItemModel {
    int numImg,NumCountImg,numSound,InWord,inwordSound;
    String nm;

    public NumberItemModel(int numImg, int numCountImg, int numSound, int inWord, int inwordSound, String nm) {
        this.numImg = numImg;
        NumCountImg = numCountImg;
        this.numSound = numSound;
        InWord = inWord;
        this.inwordSound = inwordSound;
        this.nm = nm;
    }

    public NumberItemModel() {
    }




    public NumberItemModel(int numImg, int numSound) {
        this.numImg = numImg;
        this.numSound = numSound;

    }

    public int getInwordSound() {
        return inwordSound;
    }

    public void setInwordSound(int inwordSound) {
        this.inwordSound = inwordSound;
    }

    public int getNumCountImg() {
        return NumCountImg;
    }

    public void setNumCountImg(int numCountImg) {
        NumCountImg = numCountImg;
    }

    public int getInWord() {
        return InWord;
    }

    public void setInWord(int inWord) {
        InWord = inWord;
    }

    public int getNumImg() {
        return numImg;
    }

    public void setNumImg(int numImg) {
        this.numImg = numImg;
    }

    public int getNumSound() {
        return numSound;
    }

    public void setNumSound(int numSound) {
        this.numSound = numSound;
    }


    public String getNm() {

        return nm;
    }
}
