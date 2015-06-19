package com.example.ahmadfauzi.testconnect.dashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
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
public class FTArrayAdapter extends ArrayAdapter<FoodTest> {
    public FTArrayAdapter(Context context, ArrayList<FoodTest> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FoodTest foodTest = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_foodtest, parent, false);
        }
        // Lookup view for data population
        TextView textViewId = (TextView) convertView.findViewById(R.id.tvId);
        TextView textViewName = (TextView) convertView.findViewById(R.id.tvName);
        TextView textViewReagent = (TextView) convertView.findViewById(R.id.tvReagent);
        TextView textViewResult = (TextView) convertView.findViewById(R.id.tvResult);
        ImageView imageViewPhoto = (ImageView) convertView.findViewById(R.id.ivShowPhoto);

        // Populate the data into the template view using the data object
        textViewId.setText(String.valueOf(foodTest.idFT));
        if(!foodTest.getNameFT().isEmpty()){
            textViewName.setText("Makanan : " + foodTest.nameFT);
        }

        if(!foodTest.getReagentFT().isEmpty()){
            textViewReagent.setText("Reagen : " + foodTest.reagentFT);
        }

        if(!foodTest.getResultFT().isEmpty()){
            textViewResult.setText("Hasil : " + foodTest.resultFT);
        }

        imageViewPhoto.setImageBitmap(foodTest.photoFT);
        // Return the completed view to render on screen
        return convertView;
    }

}
