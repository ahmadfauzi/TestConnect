package com.example.ahmadfauzi.testconnect.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.ahmadfauzi.testconnect.dashboard.FoodTest;
import com.example.ahmadfauzi.testconnect.dashboard.FoodTestArrayAdapter;
import com.example.ahmadfauzi.testconnect.network.AsyncResponse;
import com.example.ahmadfauzi.testconnect.network.ClientConnect;
import com.example.ahmadfauzi.testconnect.R;
import com.example.ahmadfauzi.testconnect.network.IPConnect;
import com.example.ahmadfauzi.testconnect.network.MySQLiteHelper;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardActivity extends ActionBarActivity implements AsyncResponse, View.OnClickListener {
    public static String IP_CONNECT = "";

    // url to get all foodtest list
//    private static String url_all_foodtest = "http://10.151.12.79/foodtest";
//    ClientConnect clientConnect = new ClientConnect(this, url_all_foodtest);
    ClientConnect clientConnect;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FOODTEST = "foodtest";
    private static final String TAG_ID = "id_FoodTest";
    private static final String TAG_NAME = "name_FoodTest";
    private static final String TAG_TESTTYPE = "testType_FoodTest";
    private static final String TAG_RESULT = "result_FoodTest";
    private static final String TAG_PHOTO = "photo_FoodTest";

    JSONArray foodtestJSONArray = null;

    ArrayList<FoodTest> foodtestLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Toast.makeText(this, "URL = " + url_all_foodtest, Toast.LENGTH_SHORT).show();
//
//        clientConnect.delegate = this;
//
//        AllFoodTestFunction();
//        buildFloatingActionButton();

        LoadActivity();
    }

    private void LoadActivity(){
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(this);
        int rowIP = sqLiteHelper.getCountRowIP();
        IPConnect ipConnect = sqLiteHelper.getIpConnect();

        if(rowIP != 0) {
            String url_all_foodtest = "http://" + ipConnect.getIp() + "/foodtest";
            clientConnect = new ClientConnect(this, url_all_foodtest);
            clientConnect.delegate = this;
//            Toast.makeText(this,"countRow = " + String.valueOf(rowIP) + ", URL = " + url_all_foodtest, Toast.LENGTH_SHORT).show();
        }

        AllFoodTestFunction();
        buildFloatingActionButton();
    }

    private void AllFoodTestFunction() {
        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(this);
        int rowIP = sqLiteHelper.getCountRowIP();

        if(rowIP != 0) {
            String urlParameter = "/get_all_foodtest.php";
            clientConnect.execute(urlParameter);
        }
    }

    private void buildFloatingActionButton(){
        int size = getApplication().getResources().getDimensionPixelSize(R.dimen.floating_action_button_size);
        int margin = getApplication().getResources().getDimensionPixelOffset(R.dimen.floating_action_button_margin);
        FloatingActionButton.LayoutParams layoutParams = new FloatingActionButton.LayoutParams(size, size, Gravity.BOTTOM | Gravity.RIGHT);
        layoutParams.setMargins(margin, margin, margin, margin);

        ImageView iconActionButton = new ImageView(this);
        iconActionButton.setImageResource(R.drawable.ic_action_new);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(iconActionButton)
                .setBackgroundDrawable(R.drawable.selector_button_orange)
                .setLayoutParams(layoutParams)
                .build();
        actionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        addNewFT();
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

        if (id == R.id.action_settings) {
            inputTextDialog();
            return true;
        }

//        if(id == R.id.action_manage_testType){
//            Intent intent = new Intent(this, NewtestTypeActivity.class);
//            startActivity(intent);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void inputTextDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pengaturan IP");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        input.setTextColor(Color.BLACK);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                IP_CONNECT = input.getText().toString();
                MySQLiteHelper sqLiteHelper = new MySQLiteHelper(getApplicationContext());
                int rowIP = sqLiteHelper.getCountRowIP();

                if(rowIP == 0){
                    newIP(IP_CONNECT);
                } else {
                    updateIP(IP_CONNECT);
                }
                LoadActivity();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void newIP(String setIp){
        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(this);

        IPConnect ipConnect = new IPConnect(setIp);

        sqLiteHelper.addIP(ipConnect);
    }

    private void updateIP(String ip) {
        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(this);

//        IPConnect ip_connect = new IPConnect(setIp);

        sqLiteHelper.updateIP(ip);
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
            startActivity(intent);
            finish();
        }
    }

    private void addNewFT() {
        Intent intent = new Intent(this, NewFoodTestActivity.class);
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
                    int id = jsonObject.getInt(TAG_ID);
                    String name = jsonObject.getString(TAG_NAME);
                    String testType = jsonObject.getString(TAG_TESTTYPE);
                    String result = jsonObject.getString(TAG_RESULT);
                    String photo = jsonObject.getString(TAG_PHOTO);
                    String name_replaced = name.replaceAll("_"," ");
                    String testType_replaced = testType.replaceAll("_"," ");
                    String result_replaced = result.replaceAll("_"," ");
                    Log.d("DashboardActivity", "id : " + id + ", photo : " + photo);

                    //decode from String Base64 to Bitmap
                    byte[] decodedString = Base64.decode(photo, Base64.NO_PADDING);
                    Bitmap photoBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    Log.d("DashboardActivity",id + "//" + name + "//" + testType + "//" + result + "//" + photoBitmap);
                    FoodTest foodTest = new FoodTest();
                    foodTest.setIdFT(id);
                    foodTest.setNameFT(name_replaced);
                    foodTest.setTestTypeFT(testType_replaced);
                    foodTest.setResultFT(result_replaced);
                    foodTest.setPhotoFT(photoBitmap);

                    foodtestLists.add(foodTest);

                    Log.e("DashboardActivity", "foodtestLists = " + foodtestLists.toString());
                }
            }else{
                // no foodtest found
                // Launch Add New foodtest Activity
                Intent intent = new Intent(getApplicationContext(), NewFoodTestActivity.class);

                // Closing all previous activities
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                Toast.makeText(this, "FoodTest not found", Toast.LENGTH_SHORT).show();
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        populateFoodTestList();

//        progressDialog.dismiss();

        Log.d("DashboardActivity", "inside arraylist = " + foodtestLists.toString());
    }

    private void populateFoodTestList() {
        // Create the adapter to convert the array to views
        FoodTestArrayAdapter adapter = new FoodTestArrayAdapter(this, foodtestLists);
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

                Intent intent = new Intent(getApplicationContext(), EditFoodTestActivity.class);
                intent.putExtra(TAG_ID, foodtest_id);
                startActivityForResult(intent, 100);
            }
        });
    }
}
