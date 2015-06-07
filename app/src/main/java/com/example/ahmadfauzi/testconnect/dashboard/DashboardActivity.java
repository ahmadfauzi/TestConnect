package com.example.ahmadfauzi.testconnect.dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmadfauzi.testconnect.AsyncResponse;
import com.example.ahmadfauzi.testconnect.ClientSocket;
import com.example.ahmadfauzi.testconnect.EditFTActivity;
import com.example.ahmadfauzi.testconnect.NewFTActivity;
import com.example.ahmadfauzi.testconnect.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DashboardActivity extends ActionBarActivity implements AsyncResponse {
    private ProgressDialog progressDialog;

    // url to get all foodtest list
    private static String url_all_foodtest = "http://10.151.12.144/foodtest";
    ClientSocket clientSocket = new ClientSocket(this, url_all_foodtest);

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FOODTEST = "foodtest";
    private static final String TAG_ID_FOODTEST = "id_FoodTest";
    private static final String TAG_NAME = "name_FoodTest";
    private static final String TAG_REAGENT = "reagent_FoodTest";
    private static final String TAG_RESULT = "result_FoodTest";
    private static final String TAG_PHOTO = "photo_FoodTest";

    JSONArray foodtestJSONArray = null;

    ArrayList<FoodTest> foodtestLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        clientSocket.delegate = this;

        progressDialog = new ProgressDialog(DashboardActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.show();

        AllFTFunction();
    }

    private void AllFTFunction() {
        String urlParameter = "/get_all_foodtest.php";
        clientSocket.execute(urlParameter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_add:
                addNewFT();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 100){
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    private void addNewFT() {
        Intent intent = new Intent(this, NewFTActivity.class);
        startActivity(intent);
    }

    @Override
    public void processFinish(String output) {
        Log.e("DashboardActivity", "finish = " + String.valueOf(output));
        JSONObject json = new JSONObject();
        try{
            json = new JSONObject(output);
        }catch (JSONException e){
            e.printStackTrace();
        }

        try {
            foodtestLists = new ArrayList<FoodTest>();
            int success = json.getInt(TAG_SUCCESS);
//            Toast.makeText(this, "success = " + String.valueOf(success), Toast.LENGTH_SHORT).show();
            if(success == 1){
                // foodtest found
                // Getting Array of FoodTest
//                Toast.makeText(this, "Foodtest Found", Toast.LENGTH_SHORT).show();
                foodtestJSONArray = json.getJSONArray(TAG_FOODTEST);

                for(int i=0; i<foodtestJSONArray.length(); i++){
                    JSONObject jsonObject = foodtestJSONArray.getJSONObject(i);

                    // Storing each json item in variable
                    int id = jsonObject.getInt(TAG_ID_FOODTEST);
                    String name = jsonObject.getString(TAG_NAME);
                    String reagent = jsonObject.getString(TAG_REAGENT);
                    String result = jsonObject.getString(TAG_RESULT);
                    String photo = jsonObject.getString(TAG_PHOTO);
                    Log.d("DashboardActivity", "id : " + id + ", photo : " + photo);

                    //decode from String Base64 to Bitmap
                    byte[] decodedString = Base64.decode(photo, Base64.NO_PADDING);
                    Bitmap photoBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

//                    // creating new HashMap
//                    HashMap<String, String> map = new HashMap<String, String>();
//
//                    // adding each child node to HashMap key => value
//                    map.put(TAG_ID_FOODTEST,id);
//                    map.put(TAG_NAME, name);
//                    map.put(TAG_REAGENT, reagent);
//                    map.put(TAG_RESULT, result);
//                    //map.put(TAG_PHOTO, photo);
//
//                    // adding HashList to ArrayList
//                    foodtestList.add(map);
                    Log.d("DashboardActivity",id + "//" + name + "//" + reagent + "//" + result + "//" + photoBitmap);
                    FoodTest foodTest = new FoodTest();
                    foodTest.setIdFT(id);
                    foodTest.setNameFT(name);
                    foodTest.setReagentFT(reagent);
                    foodTest.setResultFT(result);
                    foodTest.setPhotoFT(photoBitmap);

                    foodtestLists.add(foodTest);

                    Log.e("DashboardActivity", "foodtestLists = " + foodtestLists.toString());
                }
            }else{
                // no foodtest found
                // Launch Add New foodtest Activity
                Intent intent = new Intent(getApplicationContext(), NewFTActivity.class);

                // Closing all previous activities
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                Toast.makeText(this, "FoodTest not found", Toast.LENGTH_SHORT).show();
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        populateFoodTestList();

        progressDialog.dismiss();

        Log.d("DashboardActivity", "inside arraylist = " + foodtestLists.toString());
    }

    private void populateFoodTestList() {
        // Create the adapter to convert the array to views
        FTArrayAdapter adapter = new FTArrayAdapter(this, foodtestLists);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.lvFoodTest);
        listView.setAdapter(adapter);

        // on selecting single product
        // launching Edit Product Screen
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // getting values from selected ListItem
                String foodtest_id = ((TextView) view.findViewById(R.id.tvId)).getText().toString();

                Intent intent = new Intent(getApplicationContext(), EditFTActivity.class);
                intent.putExtra(TAG_ID_FOODTEST, foodtest_id);
                startActivityForResult(intent, 100);
                finish();
            }
        });
    }
}
