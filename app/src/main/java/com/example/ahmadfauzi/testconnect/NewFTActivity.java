package com.example.ahmadfauzi.testconnect;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ahmadfauzi.testconnect.dashboard.DashboardActivity;
import com.example.ahmadfauzi.testconnect.data_mining.ColorComparators;
import com.example.ahmadfauzi.testconnect.data_mining.Sample;
import com.example.ahmadfauzi.testconnect.data_mining.ColorBars;
import com.example.ahmadfauzi.testconnect.data_mining.Reagent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NewFTActivity extends ActionBarActivity implements AsyncResponse{
    private ProgressDialog progressDialog;

    EditText editTextName;
    EditText editTextReagent;
    EditText editTextResult;
    Button buttonCreate;
    ImageView imageViewPhoto;

    private static String url_create_foodtest = "http://10.151.44.167/foodtest";
    ClientSocket clientSocket = new ClientSocket(this, url_create_foodtest);

    private static final String TAG_SUCCESS = "success";

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2 ;

    private Sample testSample;
    private Reagent reagent;
    private ColorBars colorBars;

    private String dateTime;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ft);

        setTitle("Tambah Uji");

        editTextName = (EditText) findViewById(R.id.etName);
        editTextReagent = (EditText) findViewById(R.id.etReagent);
        editTextResult = (EditText) findViewById(R.id.etResult);
        buttonCreate = (Button) findViewById(R.id.btnCreate);
        imageViewPhoto = (ImageView) findViewById(R.id.ivPhoto);

        clientSocket.delegate = this;

        reagent = new Reagent(getApplicationContext());
        colorBars = new ColorBars(getApplicationContext());

        final String [] items = new String [] {"Camera", "Gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) {
                //pick from camera
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                } else { //pick from file
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        } );
        final AlertDialog dialog = builder.create();
        Button buttonImage = (Button) findViewById(R.id.btnCamera);
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
        finish();
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
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_new:
                NewFTFunction();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap image = null;
        if (resultCode != RESULT_OK) return;
        switch (requestCode){
            case PICK_FROM_CAMERA:
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if(bitmap != null){
                    Log.d("DaftarActivity", "Mengambil foto dari kamera berhasil");
                    image = ThumbnailUtils.extractThumbnail(bitmap, 400, 400);
                    imageViewPhoto.setImageBitmap(image);
                }else{
                    Log.d("DaftarActivity", "Mengambil foto dari kamera gagal");
                }
                break;
            case PICK_FROM_FILE:
                Uri selectedImageUri = data.getData();
                try{
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri, "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    Bitmap imageFile = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    parcelFileDescriptor.close();

                    String file = selectedImageUri.toString();
                    imageViewPhoto.setImageBitmap(imageFile);
                    Log.d("DaftarActivity","FILE from = " + file.toString());
                    Toast.makeText(this,"FILE from = " + file.toString(), Toast.LENGTH_SHORT).show();
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void NewFTFunction() {
        String name = editTextName.getText().toString();
        String reagent = editTextReagent.getText().toString();
        String result = editTextResult.getText().toString();
        String name_replaced = name.replaceAll(" ","_");
        String reagent_replaced = reagent.replaceAll(" ","_");
        String result_replaced = result.replaceAll(" ","_");

        Bitmap bitmap = ((BitmapDrawable) imageViewPhoto.getDrawable()).getBitmap();
        String photo = encodeToBase64(bitmap);

//        Toast.makeText(this, name_replaced + " " + reagent_replaced, Toast.LENGTH_LONG).show();
        if(!name.isEmpty() || !reagent.isEmpty() || !result.isEmpty()){
            progressDialog = new ProgressDialog(NewFTActivity.this);
            progressDialog.setMessage("Menambahkan....");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

            String urlParameter = "/create_foodtest.php?name_FoodTest=" + name_replaced + "&reagent_FoodTest=" + reagent_replaced + "&result_FoodTest=" + result_replaced + "&photo_FoodTest=" + photo;
            clientSocket.execute(urlParameter);

//            Toast.makeText(this, urlParameter, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Tolong isi tempat yang kosong", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Berhasil menambahkan", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(this, DashboardActivity.class);
//                startActivity(intent);
//                finish();
            }else {
                Toast.makeText(this, "Gagal menambahkan", Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createResult(View view) {
        reagent = getSelectedTest();
        processSample();
    }

    //Make empty sample
    public void processSample(){
        testSample = new Sample(getApplicationContext());
        testSample.setTestName(reagent.getTestName());
        Log.d("Main Activity",reagent.getTestName());

        String path = getPicture();
        testSample.setDateTime(dateTime);
        testSample.setSamplePath(path);
        Log.d("Main Activity", "path : " + path);

        String resultValue = getResult();
        testSample.setResult_mgl(resultValue);
        Log.d("Main Activity", "result : " + resultValue);

        //hasilUjiUi
        setTestSample(testSample);
        editTextResult.setText(testSample.getResult_mgl());
    }

    private String getPicture(){
        Bitmap bitmapSample = ((BitmapDrawable) imageViewPhoto.getDrawable()).getBitmap();
        //set filename with current datetime (GMT)
        String filenameDate = null;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy_hh-mm-ss");
        this.dateTime = dateFormat.format(calendar.getTime());
        String dateTime = this.dateTime;

        filenameDate = dateTime + "_GMT_" + this.reagent.getTestName();
        Log.d("MainActivity", "File Name = " + filenameDate);
        Toast.makeText(this, "REAGENT = " + reagent.getTestName(), Toast.LENGTH_SHORT).show();

        //Save file to SDCARD
        File myDir = new File(Environment.getExternalStorageDirectory() + "/ImageTest");
        myDir.mkdir();
        String fileName = filenameDate + ".png";
        File file = new File(myDir, fileName);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapSample.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.toString();
    }

    private String getResult(){
        ColorComparators colorComparators;
        String result = null;

        colorComparators = new ColorComparators(testSample, reagent);
        int resultIndex = colorComparators.getCompareResult();

        if(resultIndex == -1){
            resultIndex = 0;
        }
        result = reagent.getColorBarValue().get(resultIndex);
        Log.d("MainActivity", "RESULT = " + result);

        return result;
    }

    private Reagent getSelectedTest(){
        Reagent test = new Reagent(getApplicationContext());
        ArrayList<String> path = new ArrayList<String>();
        ArrayList<String> value = new ArrayList<String>();

        String selectedTest = editTextReagent.getText().toString();
        test.setTestName(selectedTest);
        for(ArrayList<String> listPath : this.colorBars.getColorBarPath()){
            if(listPath.get(0).contains(selectedTest)){
                path.add(listPath.get(1));
            }
        }
        for(ArrayList<String> listValue : this.colorBars.getColorBarValue()){
            if(listValue.get(0).contains(selectedTest)){
                value.add(listValue.get(1));
            }
        }
        test.setColorBarsPath(path);
        test.setColorBarValue(value);
        return test;
    }

    public void setTestSample(Sample testSample){
        int imageWidth = 10;
        int imageHeight = 50;

        this.testSample = testSample;
        Bitmap bitmapImage = testSample.getSample();
        int width = bitmapImage.getWidth();
        int height = bitmapImage.getHeight();
        int x = (width/2) - imageWidth/2;
        int y = (height/2) - imageHeight/2;
        Log.d("Main Activity","width : " + width + ", height : " + height + ", midX : " + x + ", midY : " + y);
    }
}
