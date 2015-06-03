package com.example.ahmadfauzi.testconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class EditFTActivity extends ActionBarActivity {
    EditText editTextName;
    EditText editTextReagent;
    EditText editTextResult;
    Button buttonSave;
    Button buttonDelete;

    String id;

    private ProgressDialog progressDialog;

    // JSON parser class

    private static final String url_foodtest_details = "/localhost/foodtest/get_foodtest_details.php";
    private static final String url_update_foodtest = "/localhost/foodtest/update_foodtest.php";
    private static final String url_delete_foodtest = "/localhost/foodtest/delete_foodtest.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FOODTEST = "foodtest";
    private static final String TAG_ID_FOODTEST = "id_FoodTest";
    private static final String TAG_NAME = "name_FoodTest";
    private static final String TAG_REAGENT = "reagent_FoodTest";
    private static final String TAG_RESULT = "result_FoodTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ft);

        buttonSave = (Button) findViewById(R.id.btnSave);
        buttonDelete = (Button) findViewById(R.id.btnDelete);

        Intent intent = getIntent();

        // getting foodtest id) from intent
        id = intent.getStringExtra(TAG_ID_FOODTEST);

        //Getting complete foodtest details in background thread
        //new GetFTDetails().execute();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          //      new SaveFTDetails().execute();
            }
        });

        // Delete button click event
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // deleting foodtest in background thread
            //    new DeleteFT().execute();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_ft, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
      * Background Async Task to Get complete foodtest details
      **/
//    class GetFTDetails extends AsyncTask<String, String, String>{
//
//        @Override
//        protected void onPreExecute(){
//            /**
//             * Before starting background thread Show Progress Dialog
//             * */
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(EditFTActivity.this);
//            progressDialog.setMessage("Loading FoodTest details. Please wait...");
//            progressDialog.setIndeterminate(false);
//            progressDialog.setCancelable(true);
//            progressDialog.show();
//        }
//        /**
//         * Getting foodtest details in background thread
//         * */
//        protected String doInBackground(String... params){
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    int success;
//                    try{
//                        // Building Parameters
//                        List<NameValuePair> params = new ArrayList<NameValuePair>();
//                        params.add(new BasicNameValuePair("id_FoodTest", id));
//
//                        // getting foodtest details by making HTTP request
//                        // Note that foodtest details url will use GET request
//                        JSONObject json = new JSONParser().makeHttpRequest(url_update_foodtest,"GET", params);
//
//                        Log.d("Single FoodTest Details", json.toString());
//
//                        success = json.getInt(TAG_SUCCESS);
//                        if (success == 1){
//                            JSONArray foodtestObj = json.getJSONArray(TAG_FOODTEST);
//                            // get first foodtest object from JSON Array
//                            JSONObject foodtest = foodtestObj.getJSONObject(0);
//
//                            editTextName = (EditText) findViewById(R.id.etName);
//                            editTextReagent = (EditText) findViewById(R.id.etReagent);
//                            editTextResult = (EditText) findViewById(R.id.etResult);
//
//                            // display foodtest data in EditText
//                            editTextName.setText(foodtest.getString(TAG_NAME));
//                            editTextReagent.setText(foodtest.getString(TAG_REAGENT));
//                            editTextResult.setText(foodtest.getString(TAG_RESULT));
//                        }
//                    }catch (JSONException e){
//                        e.printStackTrace();
//                    }
//                }
//            });
//            return null;
//        }
//
//        /**
//         * After completing background task Dismiss the progress dialog
//         * **/
//        protected  void onPostExecute(String file_url){
//            progressDialog.dismiss();
//        }
//    }
//
//    /**
//     * Background Async Task to  Save foodtest Details
//     * */
//    class SaveFTDetails extends AsyncTask<String, String, String>{
//        /**
//         * Before starting background thread Show Progress Dialog
//         * */
//        @Override
//        protected void onPreExecute(){
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(EditFTActivity.this);
//            progressDialog.setMessage("Saving Foodtest...");
//            progressDialog.setIndeterminate(false);
//            progressDialog.setCancelable(true);
//            progressDialog.show();
//        }
//        /**
//         * Saving foodtest
//         * */
//        protected String doInBackground(String... args){
//            // getting updated data from EditTexts
//            String name = editTextName.getText().toString();
//            String reagent = editTextReagent.getText().toString();
//            String result = editTextResult.getText().toString();
//
//            // Building Parameters
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair(TAG_ID_FOODTEST, id));
//            params.add(new BasicNameValuePair(TAG_NAME, name));
//            params.add(new BasicNameValuePair(TAG_REAGENT, reagent));
//            params.add(new BasicNameValuePair(TAG_RESULT, result));
//
//            // sending modified data through http request
//            // Notice that update foodtest url accepts POST method
//            JSONObject json = jsonParser.makeHttpRequest(url_update_foodtest, "POST", params);
//            try {
//                int success = json.getInt(TAG_SUCCESS);
//                if(success == 1){
//                    Intent intent = getIntent();
//                    // send result code 100 to notify about foodtest update
//                    setResult(100, intent);
//                    finish();
//                }else {
//                    Toast.makeText(getApplicationContext(), "Saving failed", Toast.LENGTH_SHORT).show();
//                }
//            }catch (JSONException e){
//                e.printStackTrace();
//            }
//            return null;
//        }
//        /**
//         * After completing background task Dismiss the progress dialog
//         * **/
//        protected void onPostExecute(String file_url){
//            progressDialog.dismiss();
//        }
//    }
//    /**
//      * Background Async Task to Delete FoodTest
//      * */
//    class DeleteFT extends AsyncTask<String, String, String> {
//        /**
//         * Before starting background thread Show Progress Dialog
//         * */
//        @Override
//        protected void onPreExecute(){
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(EditFTActivity.this);
//            progressDialog.setMessage("Deleting FoodTest...");
//            progressDialog.setIndeterminate(false);
//            progressDialog.setCancelable(true);
//            progressDialog.show();
//        }
//        /**
//         * Deleting foodtest
//         * */
//        protected String doInBackground(String... args){
//            int success;
//            try{
//                // Building Parameters
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("id_FoodTest",id));
//
//                // getting foodtest details by making HTTP request
//                JSONObject json = jsonParser.makeHttpRequest(url_delete_foodtest,"POST",params);
//
//                Log.d("Delete FoodTest", json.toString());
//
//                success = json.getInt(TAG_SUCCESS);
//                if(success == 1){
//                    // foodtest successfully deleted
//                    // notify previous activity by sending code 100
//                    Intent intent = getIntent();
//                    // send result code 100 to notify about foodtest deletion
//                    setResult(100, intent);
//                    finish();
//                }
//            }catch (JSONException e){
//                e.printStackTrace();
//            }
//            return null;
//        }
//        /**
//         * After completing background task Dismiss the progress dialog
//         * **/
//        protected void onPostExecute(String file_url){
//            progressDialog.dismiss();
//        }
//    }
}
