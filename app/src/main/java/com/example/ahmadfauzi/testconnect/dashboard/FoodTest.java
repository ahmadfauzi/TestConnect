package com.example.ahmadfauzi.testconnect.dashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.ahmadfauzi.testconnect.ClientSocket;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Ahmad Fauzi on 5/26/2015.
 */
public class FoodTest {

    private Context context;


    public int idFT;
    public String nameFT;
    public String reagentFT;
    public String resultFT;
    public Bitmap photoFT;

    public FoodTest() {

    }

    public FoodTest(Context context){
        this.context = context;
    }

    public FoodTest(int id, String name, String reagent, String result, Bitmap photo) {
        this.idFT = id;
        this.nameFT = name;
        this.reagentFT = reagent;
        this.resultFT = result;
        this.photoFT = photo;
    }

    public int getIdFT() {
        return idFT;
    }

    public void setIdFT(int idFT) {
        this.idFT = idFT;
    }

    public String getNameFT() {
        return nameFT;
    }

    public void setNameFT(String nameFT) {
        this.nameFT = nameFT;
    }

    public String getReagentFT() {
        return reagentFT;
    }

    public void setReagentFT(String reagentFT) {
        this.reagentFT = reagentFT;
    }

    public String getResultFT() {
        return resultFT;
    }

    public void setResultFT(String resultFT) {
        this.resultFT = resultFT;
    }

    public Bitmap getPhotoFT() {
        return photoFT;
    }

    public void setPhotoFT(Bitmap photoFT) {
        this.photoFT = photoFT;
    }

    //================================================================================================

    @Override
    public boolean equals(Object o){
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;

        return  true;
    }

    //================================================================================================
}
