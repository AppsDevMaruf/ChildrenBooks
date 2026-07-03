/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/21/21 7:59 PM
 */

package com.marufalam.childrenbooks.Models;

public class AlphabetItemModel {

    private int details;
    private int image_one;
    private int image_two;
    private int smallLetter;
    private int word_one;
    private int word_two;
    private  int sounLink;
    private  int Inwordsound;


    public AlphabetItemModel() {
    }

    public AlphabetItemModel(int details, int image_one, int image_two, int smallLetter, int word_one, int word_two, int sounLink, int inwordsound) {
        this.details = details;
        this.image_one = image_one;
        this.image_two = image_two;
        this.smallLetter = smallLetter;
        this.word_one = word_one;
        this.word_two = word_two;
        this.sounLink = sounLink;
        Inwordsound = inwordsound;
    }

    public int getInwordsound() {
        return Inwordsound;
    }

    public void setInwordsound(int inwordsound) {
        Inwordsound = inwordsound;
    }

    public int getDetails() {
        return details;
    }

    public void setDetails(int details) {
        this.details = details;
    }

    public int getImage_one() {
        return image_one;
    }

    public void setImage_one(int image_one) {
        this.image_one = image_one;
    }

    public int getImage_two() {
        return image_two;
    }

    public void setImage_two(int image_two) {
        this.image_two = image_two;
    }

    public int getSmallLetter() {
        return smallLetter;
    }

    public void setSmallLetter(int smallLetter) {
        this.smallLetter = smallLetter;
    }

    public int getWord_one() {
        return word_one;
    }

    public void setWord_one(int word_one) {
        this.word_one = word_one;
    }

    public int getWord_two() {
        return word_two;
    }

    public void setWord_two(int word_two) {
        this.word_two = word_two;
    }

    public int getSounLink() {
        return sounLink;
    }

    public void setSounLink(int sounLink) {
        this.sounLink = sounLink;
    }
}
