package com.example.ahmadfauzi.testconnect.data_mining;

/**
 * Created by Ahmad Fauzi on 5/21/2015.
 */
public class Pair {
    Double distance;
    Double toIndex;

    public Pair(Double distance2,Double index){
        this.distance =distance2;
        this.toIndex = index;
    }
    public Double getDistance() {
        return distance;
    }
    public void setDistance(Double distance) {
        this.distance = distance;
    }
    public Double getToIndex() {
        return toIndex;
    }
    public void setToIndex(double toIndex) {
        this.toIndex = toIndex;
    }

    //@Override
    public int compareTo(Pair pair) {
        Double compareQuantity = ((Pair) pair).getDistance();
        //System.out.println(compareQuantity);
        if(this.distance >  compareQuantity) {
            return 1;
        }
        if(this.distance <  compareQuantity) {
            return -1;
        }
//	    if(this.distance == compareQuantity) {
//	    	return 0;
//	    }
//	    //return 1;
        return (int) (this.distance - compareQuantity);

    }
}
