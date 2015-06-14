package com.example.ahmadfauzi.testconnect.data_mining;

/**
 * Created by Ahmad Fauzi on 5/21/2015.
 */
public class LabColor {
    public LabColor() {

    }
    public LabColor(double L ,double a, double b) {
        this.L = L;
        this.a = a;
        this.b = b;
    }
    public double getL() {
        return this.L;
    }
    public void setL(double l) {
        this.L = l;
    }
    public double getA() {
        return this.a;
    }
    public void setA(double a) {
        this.a = a;
    }
    public double getB() {
        return this.b;
    }
    public void setB(double b) {
        this.b = b;
    }
    public double L;
    public double a;
    public double b;
}
