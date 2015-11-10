package com.example.ahmadfauzi.testconnect.data_mining;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Ahmad Fauzi on 5/21/2015.
 */
public class ColorComparators {
    private ArrayList<LabColor> testResultLab = new ArrayList<LabColor>();
    private ArrayList<ArrayList<LabColor> > referenceLab = new ArrayList<ArrayList<LabColor> >();
    private ArrayList<ArrayList<Double> > distance = new ArrayList<ArrayList<Double> >();
    private ArrayList<Integer> classResult = new ArrayList<Integer>();
    private ArrayList<Integer> votes = new ArrayList<Integer>();
    private Sample sample;
    private Reagent reagent;
    private ArrayList<Bitmap> colorBars;

    private Integer imageWidth = 10;
    private Integer imageHeight = 30;

    public ColorComparators() {

    }

    public ColorComparators(Sample sample, Reagent reagent) {
        this.sample = sample;
        this.reagent = reagent;
    }

    public Integer getCompareResult() {
        colorBarConversion();
        sampleImageConversion();
        similarityMeasure();
        voteSimilarity();
        //System.out.println(voteCount());
        return voteCount()-1;
    }

    private void colorBarConversion() {
        colorBars = new ArrayList<Bitmap>();
        colorBars = reagent.getColorBarsImage();
        //ArrayList<Color> colors = new ArrayList<Color>();
        ArrayList<RgbColor> colors = new ArrayList<RgbColor>();
        Log.d("ColorComparator", "Color Bar = " + colorBars.toString());
        ArrayList<Bitmap> referenceSamples = new ArrayList<Bitmap>();
        for (Bitmap image : colorBars) {
            int width  = image.getWidth();
            int height = image.getHeight();
            int x = (width/2) - this.imageWidth ;
            int y = (height/2) - this.imageHeight ;
            Bitmap croppedImage = Bitmap.createBitmap(image, x, y, this.imageWidth, this.imageHeight);
            colors = getRGB(croppedImage, this.imageWidth, this.imageHeight);
            referenceLab.add(RGBtoLab(colors));
            Log.d("ColorComparator", "x = " + String.valueOf(x) + "Width = " + String.valueOf(this.imageWidth));
        }
        //  printLabReference();
    }

    private void sampleImageConversion(){
        Bitmap bufferedImage = sample.getSample();
        int width  = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int x = (width/2) - this.imageWidth/2;
        int y = (height/2) - this.imageHeight/2;
        System.out.println("width : " + width+ " height : "+height);
        System.out.println("midX : " + x+ " midY : "+y);
        //Bitmap croppedImage = bufferedImage.getSubimage(x, y, this.imageWidth, this.imageHeight);
        Bitmap croppedImage = Bitmap.createBitmap(bufferedImage, x, y, this.imageWidth, this.imageHeight);
        ArrayList<RgbColor> colors = new ArrayList<RgbColor>();
        colors = getRGB(croppedImage, this.imageWidth, this.imageHeight);
        testResultLab = RGBtoLab(colors);
        // printTestResult();
    }

    private void printLabReference() {
        for (ArrayList<LabColor> labColors : referenceLab) {
            for (LabColor labColor : labColors) {
                System.out.println("reference :" + labColor.getL()+" "+labColor.getA()+" "+labColor.getB());
            }
        }
    }
    private ArrayList<RgbColor> getRGB(Bitmap bufferedImage, int width, int height) {
        //int[] pixel = null;
        Log.d("ColorComparator", "width = " + width + "/height = " + height);

        ArrayList<RgbColor> colors = new ArrayList<RgbColor>();
        for(int i=0;i<height;i++) {
            for(int j=0;j<width;j++) {
                int pixel = bufferedImage.getPixel(j,i);
                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);

                RgbColor rgbColor = new RgbColor();
                rgbColor.setR(redValue);
                rgbColor.setG(greenValue);
                rgbColor.setB(blueValue);

                colors.add(rgbColor);
            }
        }
        return colors;
    }

    private ArrayList<LabColor> RGBtoLab(ArrayList<RgbColor> colors ) {
        ArrayList<LabColor> LabValues = new ArrayList<LabColor>();
        for (RgbColor color : colors) {
            double Red = (double)color.getR()/255;
            double Green = (double)color.getG()/255;
            double Blue = (double)color.getB()/255;
            //System.out.println(Red+" "+Green+" "+Blue);
            if(Red > 0.04045) {
                Red = Math.pow(((Red + 0.055) / 1.055), 2.4);
                // System.out.println("red"+ Math.pow(( (Red+0.055)/1.055 ), 2.4));
            }
            else {
                Red = Red / 12.92;
            }
            if(Green > 0.04045) {
                Green = Math.pow(((Green + 0.055) / 1.055), 2.4);
                //  System.out.println("Green"+ Math.pow(( (Green+0.055)/1.055 ), 2.4));
            }
            else {
                Green = Green / 12.92;
            }
            if(Blue > 0.04045) {
                Blue = Math.pow(((Blue + 0.055) / 1.055), 2.4);
            }
            else {
                Blue = Blue / 12.92;
            }

            Red = Red*100;
            Green = Green*100;
            Blue = Blue*100;

            double X = (Red*0.4124) + (Green*0.3576) + (Blue*0.1805);
            double Y = (Red*0.2126) + (Green*0.7152) + (Blue*0.0722);
            double Z = (Red*0.0193) + (Green*0.1192) + (Blue*0.9505);

            //  System.out.println(X+" "+Y+" "+Z);

            //XYZ to CIE-Lab
            double ref_X = 95.047;
            double ref_Y = 100.0;
            double ref_Z = 108.883;

            X = X / ref_X;
            Y = Y / ref_Y;
            Z = Z / ref_Z;
            //  System.out.println("XYZ" +X+" "+Y+" "+Z);
            if(X > 0.008856) {
                X = Math.pow(X, ((double) 1 / 3));
            }
            else {
                X = (7.787 * X) + ((double)16/116);

            }
            if(Y > 0.008856) {
                Y = Math.pow(Y, ((double) 1 / 3));
            }
            else {
                Y = (7.787 * Y) + ((double)16/116);
            }
            if(Z > 0.008856) {
                Z = Math.pow(Z, ((double) 1 / 3));
            }
            else {
                Z = (7.787 * Z) + ((double)16/116);
            }

            double L = (116*Y) - 16;
            double a = 500 * (X - Y);
            double b = 200 * (Y - Z);

            //   System.out.println("Lab space : "+L+" "+a+" "+b);

            LabValues.add(new LabColor(L,a,b));
        }
        //printTestResult();
        return LabValues;
    }

    private void printTestResult() {
        for (LabColor labColor : testResultLab) {
            System.out.println(labColor.getL()+" "+labColor.getA()+" "+labColor.getB());
        }
    }

    /*
    public void getRGBArray(Bitmap bufferedImage, int x, int y, int width, int height) {
        int[] pixelRGB = bufferedImage.getRGB(x,y,width, height, null,0,width);
        //Color color = new Color(pixelRGB[100]);
        RgbColor color = new RgbColor(pixelRGB[100]);

        System.out.println("Red : "+color.getR());
        System.out.println("Red : "+color.getG());
        System.out.println("Red : "+color.getB());
    }
    */
    private void similarityMeasure() {
        //fungsi mencari kemiripan dengan menghitung distance data uji terhadap semua warna standar

//	   for(int i=0;i<referenceLab.size();i++) {
//		   ArrayList<Double> colorDistance = new ArrayList<Double>();
//		   for(int j=0;j<referenceLab.get(i).size();j++) {
//			   LabColor reference = new LabColor();
//			   LabColor test = new LabColor();
//
//			   reference = referenceLab.get(i).get(j);
//			   test = testResultLab.get(j);
//
//			   double distance = getEuclidianDistance(reference, test);
//			   System.out.println("distance "+distance);
//			   colorDistance.add(distance);
//		   }
//		   distance.add(colorDistance);
//	   }
        ArrayList<ArrayList<Double> > dataTraining = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double> > dataTesting = new ArrayList<ArrayList<Double>>();

        double classData = 1;
        double numberOfData = this.imageHeight*this.imageWidth;
        for (ArrayList<LabColor> listLab : this.referenceLab) {
            for (LabColor labColor : listLab) {
                ArrayList<Double> data = new ArrayList<Double>();
                data.add(labColor.L);
                data.add(labColor.a);
                data.add(labColor.b);
                data.add(classData);
                dataTraining.add(data);
            }
            classData = classData+1.0;
        }
        System.out.println("data Training");
        // printArray2dDouble(dataTraining);
        for (LabColor labColor : testResultLab) {
            ArrayList<Double> data = new ArrayList<Double>();
            data.add(labColor.L);
            data.add(labColor.a);
            data.add(labColor.b);
            dataTesting.add(data);
        }
        System.out.println("data testting");
        // printArray2dDouble(dataTesting);
        KNNClassifier classifier = new KNNClassifier(dataTraining,dataTesting);
        System.out.println("num of data train : "+ this.referenceLab.size());
        classifier.setNumberOFClass(this.referenceLab.size());
        this.classResult = classifier.getPrediction();
    }

    public void printArray2dDouble(ArrayList<ArrayList<Double>> array) {
        System.out.println("array data : ");
        for (ArrayList<Double> arrayList : array) {
            for (Double double1 : arrayList) {
                System.out.print(double1+" ");
            }
            System.out.println();

        }
    }

    private void voteSimilarity() {
        int size = this.referenceLab.size();

        for(int i=0;i<= size;i++) {
            votes.add(0);
        }

        for(int i=0; i<this.classResult.size();i++) {
            int classIndex = this.classResult.get(i);
            System.out.println(classIndex);
            votes.set(classIndex, votes.get(classIndex) + 1);
        }
//
//	   for(int i=0;i<columnSize;i++){
//		   int index = 0;
//		   double distanceTemp = 0;
//		   for(int j=0;j<rowSize;j++) {
//			   if(j == 0) {
//				   distanceTemp = distance.get(j).get(i);
//				   index = j;
//			   }
//			   else {
//				   if(distance.get(j-1).get(i)> distance.get(j).get(i)) {
//					   distanceTemp = distance.get(j).get(i);
//					   index = j;
//				   }
//			   }
//		   }
//		   votes.set(index, votes.get(index) + 1);
//	   }
//	   printVotes();
    }

    private Integer voteCount() {
        int top = 0;
        int count = 0;
        for(int i = 0; i< this.votes.size(); i++) {
            if(i == 0) {
                top = 0;
                count = this.votes.get(i);
            }
            else if (count < this.votes.get(i)) {
                top = i;
                count = this.votes.get(i);
            }
        }
        return top;
    }

    public void printVotes() {
        for (int i = 0;i<votes.size();i++) {
            System.out.println(votes.get(i));
        }
    }
}
