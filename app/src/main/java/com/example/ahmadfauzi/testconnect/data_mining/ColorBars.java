package com.example.ahmadfauzi.testconnect.data_mining;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Ahmad Fauzi on 5/21/2015.
 */
public class ColorBars {
//    private String PATH_PREFIX = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

    private ArrayList<ArrayList<String>> colorBarPath;
    private ArrayList<ArrayList<String>> colorBarValue;
    private ArrayList<String> foodTestNames;
    private ArrayList<String> foodTestCode;

    private Context context;

    public ColorBars(Context context) {
        this.context = context;
        colorBarPath = new ArrayList<ArrayList<String>>();
        colorBarValue = new ArrayList<ArrayList<String>>();
        foodTestNames = new ArrayList<String>();
        foodTestCode = new ArrayList<String>();
        getAllColorBars();
    }

    public ArrayList<ArrayList<String>> getColorBarPath() {
        return colorBarPath;
    }

    public void setColorBarPath(ArrayList<ArrayList<String>> colorBarPath) {
        this.colorBarPath = colorBarPath;
    }

    public ArrayList<ArrayList<String>> getColorBarValue() {
        return colorBarValue;
    }

    public void setColorBarValue(ArrayList<ArrayList<String>> colorBarValue) {
        this.colorBarValue = colorBarValue;
    }

    public ArrayList<String> getFoodTestNames() {
        return foodTestNames;
    }

    public void setFoodTestNames(ArrayList<String> foodTestNames) {
        this.foodTestNames = foodTestNames;
    }

    public ArrayList<String> getFoodTestCode() {
        return foodTestCode;
    }

    public void setFoodTestCode(ArrayList<String> foodTestCode) {
        this.foodTestCode = foodTestCode;
    }

    /*
     properties[n]
     n = 0 , kode uji makanan
     n = 1 , nama uji makanan
     n = 2 , path ke file direktori gambar warna standar uji makanan
     n = 3 , nilai dari warna standar uji makanan

     */
    private void getAllColorBars() {
        String line;
        String testName = null;
        String splitBy = ",";

        try {
            AssetManager assetManager = context.getAssets();
            InputStream input = assetManager.open("path_reagent.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

            while ((line = bufferedReader.readLine()) != null) {
                String[] properties = line.split(splitBy);

                if(!properties[0].equals(testName)) {
                    testName = properties[0];
                    foodTestNames.add(properties[0]);
                }
                this.foodTestCode.add(properties[1]);

                ArrayList<String> property = new ArrayList<String>();
                property.add(properties[1]);
                property.add(properties[2]);
                this.colorBarPath.add(property);

                property = new ArrayList<String>();
                property.add(properties[1]);
                property.add(properties[3]);
                this.colorBarValue.add(property);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
