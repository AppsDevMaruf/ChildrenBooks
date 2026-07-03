/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 2/12/21 1:39 AM
 */

package com.marufalam.childrenbooks.Models;

public class FruitsModel {
    String fruitsimg,fruitsname;

    public FruitsModel() {
    }

    public FruitsModel(String fruitsimg, String fruitsname) {
        this.fruitsimg = fruitsimg;
        this.fruitsname = fruitsname;
    }

    public String getFruitsimg() {
        return fruitsimg;

}

    public void setFruitsimg(String fruitsimg) {
        this.fruitsimg = fruitsimg;
    }

    public String getFruitsname() {
        return fruitsname;
    }

    public void setFruitsname(String fruitsname) {
        this.fruitsname = fruitsname;
    }
}
