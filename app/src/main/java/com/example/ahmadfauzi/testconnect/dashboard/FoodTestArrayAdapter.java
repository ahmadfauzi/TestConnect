package com.example.ahmadfauzi.testconnect.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ahmadfauzi.testconnect.R;

import java.util.ArrayList;

/**
 * Created by Ahmad Fauzi on 5/26/2015.
 */
public class FoodTestArrayAdapter extends ArrayAdapter<FoodTest> {
    public FoodTestArrayAdapter(Context context, ArrayList<FoodTest> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FoodTest foodTest = getItem(position);
        // Check if an existing view is being reused,    otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_foodtest, parent, false);
        }
        // Lookup view for data population
        TextView textViewId = (TextView) convertView.findViewById(R.id.tvId);
        TextView textViewName = (TextView) convertView.findViewById(R.id.tvName);
        TextView textViewTestType = (TextView) convertView.findViewById(R.id.tvTestType);
        TextView textViewResult = (TextView) convertView.findViewById(R.id.tvResult);
        ImageView imageViewPhoto = (ImageView) convertView.findViewById(R.id.ivShowPhoto);

        // Populate the data into the template view using the data object
        textViewId.setText(String.valueOf(foodTest.idFT));
        if(!foodTest.getNameFT().isEmpty()){
            textViewName.setText("Makanan : " + foodTest.nameFT);
        }

        if(!foodTest.getTestTypeFT().isEmpty()){
            textViewTestType.setText("Jenis Uji : " + foodTest.testTypeFT);
        }

        if(!foodTest.getResultFT().isEmpty()){
            textViewResult.setText("Hasil : " + foodTest.resultFT);
        }

        imageViewPhoto.setImageBitmap(foodTest.photoFT);
        // Return the completed view to render on screen
        return convertView;
    }
}
