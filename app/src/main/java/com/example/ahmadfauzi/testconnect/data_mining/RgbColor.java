package com.example.ahmadfauzi.testconnect.data_mining;

import java.util.ArrayList;

/**
 * Created by Ahmad Fauzi on 5/22/2015.
 */
public class RgbColor {

    public int r;
    public int g;
    public int b;


    public RgbColor(){

    }

    public RgbColor(int r, int g, int b){
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public ArrayList<Integer> getRGB(){
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(r);
        colors.add(g);
        colors.add(b);

        return colors;
    }
}
