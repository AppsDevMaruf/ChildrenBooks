/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 2/23/21 4:31 PM
 */

package com.marufalam.childrenbooks.Models;

public class FlowersModel {
    String flowersimg,flowersname, sound;

    public FlowersModel() {
    }

    public FlowersModel(String flowersimg, String flowersname, String sound) {
        this.flowersimg = flowersimg;
        this.flowersname = flowersname;
        this.sound = sound;
    }

    public String getFlowersimg() {
        return flowersimg;
    }

    public void setFlowersimg(String flowersimg) {
        this.flowersimg = flowersimg;
    }

    public String getFlowersname() {
        return flowersname;
    }

    public void setFlowersname(String flowersname) {
        this.flowersname = flowersname;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }
}
