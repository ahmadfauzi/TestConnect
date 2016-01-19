package com.example.ahmadfauzi.testconnect.data_mining;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Ahmad Fauzi on 5/21/2015.
 */
public class KNNClassifier {
    private ArrayList<ArrayList<Double>> dataTraining;
    private ArrayList<ArrayList<Double>> dataTesting;
    private ArrayList<ArrayList<Pair>> distances;
    private ArrayList<ArrayList<Double>> centroid;

    private ArrayList<Integer> classResult;
    private ArrayList<Integer> vote;
    private int numberOfClass;
    private int numberOfDataEachClass;
    private int K=1;
    public int getNumberOFClass() {
        return numberOfClass;
    }

    public void setNumberOFClass(int numberOFClass) {
        this.numberOfClass = numberOFClass;
        this.numberOfDataEachClass = this.dataTraining.size()/this.numberOfClass;
//        System.out.println("n data train : " +this.dataTraining.size()+" dataperclass : "+this.numberOfDataEachClass);
        Log.d("SetNumberOfClass","n data train = " + String.valueOf(this.dataTraining.size()) + "; dataperclass = " + String.valueOf(this.numberOfDataEachClass));
    }

    public KNNClassifier(ArrayList<ArrayList<Double>> dataTraining,ArrayList<ArrayList<Double>> dataTesting) {
        this.dataTraining = dataTraining;
        this.dataTesting = dataTesting;
        this.classResult = new ArrayList<>();
    }

    public void printArray2dPair(ArrayList<ArrayList<Pair>> array) {
        System.out.println("array data : ");
        for (ArrayList<Pair> arrayList : array) {
            for (Pair double1 : arrayList) {
                System.out.print(double1.distance+","+double1.toIndex+" ");
            }
            System.out.println();
        }
    }

    public ArrayList<Integer> getPrediction(){
        centroid = new ArrayList<ArrayList<Double>>();
        this.centroid = this.obtainCentroid(dataTraining);
        this.checkOutlier(this.dataTesting);

        this.measureDistance(this.dataTraining, this.dataTesting);
        //printArray2dPair(this.distances);
//        Log.d("GetPrediction","in voting");
        this.voting();
//        Log.d("GetPrediction","out voting");
        for (Integer integer : classResult) {
//            Log.d("GetPrediction","result = " + integer);
        }
        return this.classResult;
    }

    public Double getEuclidianDistance(ArrayList<Double> test,ArrayList<Double> train){
        double distance = 0.0;
        double sum = 0.0;

        ArrayList<Double> delta = new ArrayList<Double>();
        for(int i=0;i<test.size();i++) {
            delta.add(test.get(i)-train.get(i));
        }

        ArrayList<Double> deltaPow = new ArrayList<Double>();
        for(int i=0;i<delta.size();i++) {
            deltaPow.add(Math.pow(delta.get(i), 2));
        }

        for(int i=0;i<deltaPow.size();i++) {
            sum+= deltaPow.get(i);
        }
        distance = Math.sqrt(sum);

        return distance;
    }

    public void measureDistance(ArrayList<ArrayList<Double>> dataTrain,ArrayList<ArrayList<Double>> dataTest) {
        this.distances = new ArrayList<ArrayList<Pair> >();
        for (ArrayList<Double> listTest : dataTest) {
            //System.out.println("size test : "+listTest.size());
            ArrayList<Pair> distance = new ArrayList<Pair>();
            for (ArrayList<Double> listTrain : dataTrain) {
                double euclidDist = getEuclidianDistance(listTest,listTrain);
                if(listTest.get(listTest.size()-1) == 1.0) {
                    distance.add(new Pair(euclidDist, 0.0) );
                }
                else {
                    distance.add(new Pair(euclidDist, listTrain.get(listTrain.size()-1)) );
                }
//                Log.d("MeasureDistance","Euclid distance = " + String.valueOf(euclidDist));
            }
            Collections.sort(distance, new Comparator<Pair>() {
                @Override
                public int compare(Pair pair1, Pair pair2) {
                    return pair1.getDistance().compareTo(pair2.getDistance());
                }
            });
            this.distances.add(distance);
        }
    }

    public void voting() {
        for (ArrayList<Pair> distance : this.distances) {
            ArrayList<Integer> voteClass = new ArrayList<Integer>();

            for(int i=0;i<=this.numberOfClass;i++) {
                voteClass.add(0);
            }
            //System.out.println("size voteclass : "+voteClass.size());

            for(int i=0;i<this.K;i++) {
                //System.out.println(distance.get(i).getToIndex());
                double index = distance.get(i).getToIndex();
                //System.out.println("index"+index);
                voteClass.set((int) index, voteClass.get((int) index)+1);
            }

            int top = 0;
            int topVotes= 0 ;
            for(int i=0;i<=this.numberOfClass;i++) {
                if(i == 0) {
                    top = i;
                    topVotes = voteClass.get(i);
                }
                else if(topVotes < voteClass.get(i)) {
                    top = i;
                    topVotes = voteClass.get(i);
                }
                //System.out.println("top "+top);
            }
            this.classResult.add(top);
        }
    }

    public ArrayList<ArrayList<Double>> obtainCentroid(ArrayList<ArrayList<Double>> dataTraining){
        ArrayList<ArrayList<Double>> centroid = new ArrayList<ArrayList<Double>>();
        for (int i=1;i<=this.numberOfClass;i++) {
            ArrayList<Double> dataPerClass = new ArrayList<Double>();
            for (int j=0;j<dataTraining.size();j++) {
                double classTrain = dataTraining.get(j).get(dataTraining.get(j).size()-1);
                if(classTrain == (double)i) {
                    if(dataPerClass.isEmpty()) {
                        dataPerClass.addAll(dataTraining.get(j));
                    }
                    else {
                        for (int k=0;k<dataTraining.get(j).size();k++) {
                            //System.out.println("loading ...");
                            double value = dataPerClass.get(k);
                            double valueTrain = dataTraining.get(j).get(k);
                            dataPerClass.set(k,value+valueTrain );
                        }
                    }
                }
            }
            for (int j=0;j<dataPerClass.size();j++) {
                double value = dataPerClass.get(j);
                dataPerClass.set(j,value/this.numberOfDataEachClass);
            }
            centroid.add(dataPerClass);
        }
        for (ArrayList<Double> double1 : centroid) {
//            System.out.println("centroid : "+double1);
            Log.d("ObtainCentroid","centroid = " + double1.toString());
        }
        return centroid;
    }

    public Double getTresHold() {
        double treshold = 0.0;
        for (int i=0;i<centroid.size();i++) {
            if(i != centroid.size()-1){
                if(treshold<this.getEuclidianDistance(centroid.get(i), centroid.get(i+1))) {
                    treshold = this.getEuclidianDistance(centroid.get(i), centroid.get(i+1));
                    //System.out.println("distance centroid : "+i+" "+(i+1)+" "+this.getEuclidianDistance(centroid.get(i), centroid.get(i+1)));
                }
            }

//				if(treshold<this.getEuclidianDistance(centroid.get(i), centroid.get(0))) {
//					treshold = this.getEuclidianDistance(centroid.get(i), centroid.get(0));
//					System.out.println("distance centroid : "+this.getEuclidianDistance(centroid.get(i), centroid.get(0)));
//				}
//			}
//			else {
//				System.out.println("distance centroid : "+this.getEuclidianDistance(centroid.get(i), centroid.get(i+1)));
//				if(treshold<this.getEuclidianDistance(centroid.get(i), centroid.get(i+1))) {
//					treshold = this.getEuclidianDistance(centroid.get(i), centroid.get(i+1));
//				}
//			}
        }
        Log.d("GetTreshold","treshold = " + String.valueOf(treshold));
        return treshold;
    }

    public void checkOutlier(ArrayList<ArrayList<Double>> dataTest) {
        boolean isOutlier = true;
        double treshold = this.getTresHold();
        for (ArrayList<Double> list : dataTest) {
            //System.out.println("treshold : "+treshold);
            for (ArrayList<Double> listCentroid : this.centroid) {
                double distance = this.getEuclidianDistance(list, listCentroid);
                //System.out.println("dist : "+distance);
                if(distance < (double) treshold) {
                    isOutlier = false;
                    break;
                }
            }
            if(isOutlier == true) {
                list.add(1.0);
            }
            else {
                list.add(0.0);
            }
            //System.out.println("outlier : "+list.get(list.size()-1));
        }
    }
}
