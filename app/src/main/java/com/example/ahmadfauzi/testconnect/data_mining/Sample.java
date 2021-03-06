package com.example.ahmadfauzi.testconnect.data_mining;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Ahmad Fauzi on 5/21/2015.
 */
public class Sample {
    private ArrayList<LabColor> labSample;
    private ArrayList<Color> RGBSample;
    private String result_mgl;
    private String path;
    private String dateTime;
    private String testName;

    private Context context;

    public Sample(Context context){
        this.context = context;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void setSamplePath(String samplePath){
        //this.path = ".\\assets\\TestColorBar\\Borax\\test2.PNG";
        this.path = samplePath;
    }

    public String getSamplePath() {
        return this.path;
    }

    public ArrayList<LabColor> getLabSample() {
        return labSample;
    }

    public void setLabSample(ArrayList<LabColor> labSample) {
        this.labSample = labSample;
    }

    public ArrayList<Color> getRGBSample() {
        return RGBSample;
    }

    public void setRGBSample(ArrayList<Color> rGBSample) {
        RGBSample = rGBSample;
    }

    public Bitmap getSample() {
        Bitmap bitmapSample = null;
        String sampleImagePath = this.path;

        bitmapSample = BitmapFactory.decodeFile(this.getSamplePath());

        Log.d("Sample", "sample path = " + sampleImagePath);
        Log.d("Sample", " Sample = " + this.getSamplePath());
        return bitmapSample;
    }

    /*public void setSample(Image sample) {
        this.sample = sample;
    }
*/
    public String getResult_mgl() {
        return result_mgl;
    }

    public void setResult_mgl(String result_mgl) {
        this.result_mgl = result_mgl;
    }
}
