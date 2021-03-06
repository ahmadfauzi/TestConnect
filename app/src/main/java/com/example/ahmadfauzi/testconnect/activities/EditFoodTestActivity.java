package com.example.ahmadfauzi.testconnect.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ahmadfauzi.testconnect.network.AsyncResponse;
import com.example.ahmadfauzi.testconnect.network.ClientConnect;
import com.example.ahmadfauzi.testconnect.R;
import com.example.ahmadfauzi.testconnect.network.IPConnect;
import com.example.ahmadfauzi.testconnect.network.MySQLiteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


public class EditFoodTestActivity extends ActionBarActivity implements AsyncResponse {
    EditText editTextName;
    EditText editTextTestType;
    EditText editTextResult;
    ImageView imageViewPhoto;

    String id;
    String flagMenu;

//    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogSave;
    private ProgressDialog progressDialogDelete;
    private ProgressDialog progressDialogDetail;

//    private static final String url_foodtest= "http://10.151.12.2/foodtest";
//    ClientConnect clientConnectDetail = new ClientConnect(this, url_foodtest);
//    ClientConnect clientConnectUpdate = new ClientConnect(this, url_foodtest);
//    ClientConnect clientConnectDelete = new ClientConnect(this, url_foodtest);

    ClientConnect clientConnectDetail;
    ClientConnect clientConnectUpdate;
    ClientConnect clientConnectDelete;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FOODTEST = "foodtest";
    private static final String TAG_ID_FOODTEST = "id_FoodTest";
    private static final String TAG_NAME = "name_FoodTest";
    private static final String TAG_TESTTYPE = "testType_FoodTest";
    private static final String TAG_RESULT = "result_FoodTest";
    private static final String TAG_PHOTO = "photo_FoodTest";

    JSONArray foodtestJSONArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ft);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextName = (EditText) findViewById(R.id.etName);
        editTextTestType = (EditText) findViewById(R.id.etTestType);
        editTextResult = (EditText) findViewById(R.id.etResult);
        imageViewPhoto = (ImageView) findViewById(R.id.ivPhoto);

        flagMenu = "DETAIL";

        Intent intent = getIntent();
        // getting foodtest id) from intent
        id = intent.getStringExtra(TAG_ID_FOODTEST);
        Log.d("EditFTActivity","id : " + id);

        //Getting complete foodtest details
        progressDialogDetail = new ProgressDialog(EditFoodTestActivity.this);
        progressDialogDetail.setMessage("Memuat...");
        progressDialogDetail.setIndeterminate(false);
        progressDialogDetail.setCancelable(true);
        progressDialogDetail.show();

        //get IP
        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(this);
        int rowIP = sqLiteHelper.getCountRowIP();
        IPConnect ipConnect = sqLiteHelper.getIpConnect();

        if(rowIP != 0) {
            String url_foodtest= "http://" + ipConnect.getIp() + "/foodtest";
            clientConnectDetail = new ClientConnect(this, url_foodtest);
            clientConnectUpdate = new ClientConnect(this, url_foodtest);
            clientConnectDelete = new ClientConnect(this, url_foodtest);

            clientConnectDetail.delegate = this;
            clientConnectUpdate.delegate = this;
            clientConnectDelete.delegate = this;
        }
        GetFTDetails();
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
        switch (id){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.action_save:
                flagMenu = "SAVE";

                progressDialogSave = new ProgressDialog(EditFoodTestActivity.this);
                progressDialogSave.setMessage("Menyimpan...");
                progressDialogSave.setIndeterminate(false);
                progressDialogSave.setCancelable(true);
                progressDialogSave.show();

                saveFT();
                break;
            case R.id.action_delete:
                flagMenu = "DELETE";
                deleteFT();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void GetFTDetails() {
        String urlParameter = "/get_foodtest_details.php?id_FoodTest=" + id;
        clientConnectDetail.execute(urlParameter);
    }


    private void saveFT(){
        String name = editTextName.getText().toString();
        String testType = editTextTestType.getText().toString();
        String result = editTextResult.getText().toString();

//        Bitmap bitmap = ((BitmapDrawable) imageViewPhoto.getDrawable()).getBitmap();
//        String photo = encodeToBase64(bitmap);

        Toast.makeText(this,id + " " +  name + " " + testType, Toast.LENGTH_SHORT).show();
        if(!name.isEmpty() || !testType.isEmpty() || !result.isEmpty()) {
            String urlParameter = "/update_foodtest.php?name_FoodTest=" + name + "&testType_FoodTest=" + testType + "&result_FoodTest=" + result + "&id_FoodTest=" + id;
            clientConnectUpdate.execute(urlParameter);
        }else {
            Toast.makeText(this, "Tolong isi pada tempat yang kosong", Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeToBase64(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        //Log.d("NewFTActivity", imageEncoded);
        return imageEncoded;
    }

    private void deleteFT(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(EditFoodTestActivity.this);

        builder.setTitle("Are you sure?");
        builder.setMessage("Data will be deleted");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialogDelete = new ProgressDialog(EditFoodTestActivity.this);
                progressDialogDelete.setMessage("Menghapus...");
                progressDialogDelete.setIndeterminate(false);
                progressDialogDelete.setCancelable(true);
                progressDialogDelete.show();

                String urlParameter = "/delete_foodtest.php?id_FoodTest=" + id;
                clientConnectDelete.execute(urlParameter);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getApplicationContext(), "Cancel delete data", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void processFinish(String output) {
        Log.e("EditFTActivity", "finish = " + String.valueOf(output));
        JSONObject json = new JSONObject();
        try{
            json = new JSONObject(output);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d("EditFTActivity","flag = " + flagMenu);
        if(flagMenu.equals("DETAIL")) {
            try {
                int success = json.getInt(TAG_SUCCESS);
//                Toast.makeText(this, "success = " + String.valueOf(success), Toast.LENGTH_SHORT).show();
                if (success == 1) {
                    // foodtest found
                    // Getting Array of FoodTest
//                    Toast.makeText(this, "Uji Ditemukan", Toast.LENGTH_SHORT).show();
                    foodtestJSONArray = json.getJSONArray(TAG_FOODTEST);

                    JSONObject jsonObject = foodtestJSONArray.getJSONObject(0);

                    // Storing each json item in variable
                    int id = jsonObject.getInt(TAG_ID_FOODTEST);
                    String name = jsonObject.getString(TAG_NAME);
                    String testType = jsonObject.getString(TAG_TESTTYPE);
                    String result = jsonObject.getString(TAG_RESULT);
                    String photo = jsonObject.getString(TAG_PHOTO);
                    Log.d("EditFTActivity", "id = " + id + ", photo = " + photo);

                    //decode from String Base64 to Bitmap
                    byte[] decodedString = Base64.decode(photo, Base64.NO_PADDING);
                    Bitmap photoBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    editTextName = (EditText) findViewById(R.id.etName);
                    editTextTestType = (EditText) findViewById(R.id.etTestType);
                    editTextResult = (EditText) findViewById(R.id.etResult);

                    editTextName.setText(name);
                    editTextTestType.setText(testType);
                    editTextResult.setText(result);

                    Log.d("EditFTActivity", "Detail = " + name + "//" + testType + "//" + result);
                } else {
                    Toast.makeText(this, "Detail tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialogDetail.dismiss();

        }else if(flagMenu.equals("SAVE")){
            progressDialogSave.dismiss();
            Toast.makeText(this, "Menyimpan Uji Berhasil", Toast.LENGTH_SHORT).show();
            try{
                int success = json.getInt(TAG_SUCCESS);
                if(success == 1){
                    Toast.makeText(this, "Update Uji Berhasil", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(this, "Update Uji Gagal", Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(flagMenu.equals("DELETE")){
            progressDialogDelete.dismiss();
            Toast.makeText(this, "Menghapus Berhasil", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
