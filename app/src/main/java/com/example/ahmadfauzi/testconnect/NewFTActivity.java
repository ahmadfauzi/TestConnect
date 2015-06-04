package com.example.ahmadfauzi.testconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ahmadfauzi.testconnect.dashboard.DashboardActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NewFTActivity extends ActionBarActivity implements AsyncResponse{
    public int GALLERY_REQUEST_CODE = 1;

    private ProgressDialog progressDialog;

    EditText editTextName;
    EditText editTextReagent;
    EditText editTextResult;
    Button buttonCreate;
    ImageView imageViewPhoto;

    private static String url_create_foodtest = "http://10.151.12.222/foodtest";
    ClientSocket clientSocket = new ClientSocket(this, url_create_foodtest);

    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ft);

        editTextName = (EditText) findViewById(R.id.etName);
        editTextReagent = (EditText) findViewById(R.id.etReagent);
        editTextResult = (EditText) findViewById(R.id.etResult);
        buttonCreate = (Button) findViewById(R.id.btnCreate);
        imageViewPhoto = (ImageView) findViewById(R.id.ivPhoto);

        clientSocket.delegate = this;

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(NewFTActivity.this);
                progressDialog.setMessage("Creating FoodTest....");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(true);
                progressDialog.show();

                NewFTFunction(view);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_ft, menu);
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

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == GALLERY_REQUEST_CODE){
                Uri selectedImageUri = data.getData();
                try{
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri, "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    parcelFileDescriptor.close();

                    String filePhoto = selectedImageUri.toString();
                    ImageView mIvPhoto = (ImageView) findViewById(R.id.ivPhoto);
                    mIvPhoto.setImageBitmap(image);
                    Toast.makeText(this,"Photo taken from: " + filePhoto.toString(), Toast.LENGTH_SHORT).show();
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void NewFTFunction(View view) {
        String name = editTextName.getText().toString();
        String reagent = editTextReagent.getText().toString();
        String result = editTextResult.getText().toString();

        Bitmap bitmap = ((BitmapDrawable) imageViewPhoto.getDrawable()).getBitmap();
        String photo = encodeToBase64(bitmap);

        Toast.makeText(this, name + " " + reagent, Toast.LENGTH_LONG).show();
        if(!name.isEmpty() || !reagent.isEmpty() || !result.isEmpty()){
            String urlParameter = "/create_foodtest.php?name_FoodTest=" + name + "&reagent_FoodTest=" + reagent + "&result_FoodTest=" + result + "&photo_FoodTest=" + photo;
            clientSocket.execute(urlParameter);

            //Toast.makeText(this, urlParameter, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Please fill the blank and insert photo", Toast.LENGTH_SHORT).show();
        }
    }

    public static String encodeToBase64(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        //Log.d("NewFTActivity", imageEncoded);
        return imageEncoded;
    }
/*
    class CreateNewFoodTest extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(NewFTActivity.this);
            progressDialog.setMessage("Creating FoodTest....");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        protected String doInBackground(String... args){
            String name = editTextName.getText().toString();
            String reagent = editTextReagent.getText().toString();
            String result = editTextResult.getText().toString();

            String urlParameter = "/create_foodtest?name=" + name + "&reagent=" + reagent + "&result=" + result + "&photo=photo" + "&id_Account=1";

            clientSocket.execute(urlParameter);

            processFinish(urlParameter);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name_FoodTest", name));
            params.add(new BasicNameValuePair("reagent_FoodTest", reagent));
            params.add(new BasicNameValuePair("result_FoodTest", result));

            JSONObject json = jsonParser.makeHttpRequest(url_create_foodtest, "POST", params);


            Log.d("Create Response", json.toString());
            try{
                int success = json.getInt(TAG_SUCCESS);
                if(success == 1){
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);

                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Failed to create FoodTest", Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String fileUrl){

        }
    }
*/
    @Override
    public void processFinish(String output) {
        JSONObject json = new JSONObject();
        try{
            json = new JSONObject(output);
        }catch (JSONException e){
            e.printStackTrace();
        }

        progressDialog.dismiss();
        Log.d("NewActvity", "finish = " + String.valueOf(output));
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
        try{
            int success = json.getInt(TAG_SUCCESS);
            if(success == 1){
//                Toast.makeText(this, "Create FoodTest is success", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(this, DashboardActivity.class);
//                startActivity(intent);
//                finish();
            }else {
                Toast.makeText(this, "Failed to create FoodTest", Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void imageGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose Image"), GALLERY_REQUEST_CODE);
    }
}
