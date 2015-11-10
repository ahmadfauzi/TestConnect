package com.example.ahmadfauzi.testconnect.data_mining;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Ahmad Fauzi on 5/21/2015.
 */
public class Reagent {
    public String testName;
    public ArrayList<String> colorBarsPath;
    public ArrayList<Bitmap> colorBarsImage;
    public ArrayList<String> colorBarValue;

    private Context context;

    public Reagent(Context context){
        this.context = context;
    }

    public ArrayList<String> getColorBarValue() {
        return colorBarValue;
    }
    public String getTestName() {
        return testName;
    }
    public void setTestName(String testName) {
        this.testName = testName;
    }
    public ArrayList<String> getColorBarsPath() {
        return colorBarsPath;
    }
    public void setColorBarsPath(ArrayList<String> colorBarsPath) {
        this.colorBarsPath = colorBarsPath;
    }

    public ArrayList<Bitmap> getColorBarsImage() {
        setColorBarsImage();
        return colorBarsImage;
    }

    public void setColorBarsImage() {
        colorBarsImage = new ArrayList<Bitmap>();
        try {
            for (String path : this.colorBarsPath) {
                Log.d("Reagent", "the path : " + path);
//                Bitmap selectedImage = BitmapFactory.decodeFile(path);
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open(path);
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                Bitmap selectedImage = ((BitmapDrawable) drawable).getBitmap();
                this.colorBarsImage.add(selectedImage);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setColorBarValue(ArrayList<String> colorBarValue) {
        this.colorBarValue = colorBarValue;
    }
    public String getColorBarValue(int category) {
        return this.colorBarValue.get(category);
    }
}
