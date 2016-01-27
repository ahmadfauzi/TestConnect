package com.example.ahmadfauzi.testconnect.activities;

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
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmadfauzi.testconnect.network.AsyncResponse;
import com.example.ahmadfauzi.testconnect.network.ClientConnect;
import com.example.ahmadfauzi.testconnect.R;
import com.example.ahmadfauzi.testconnect.data_mining.ColorComparators;
import com.example.ahmadfauzi.testconnect.data_mining.Sample;
import com.example.ahmadfauzi.testconnect.data_mining.ColorBars;
import com.example.ahmadfauzi.testconnect.data_mining.TestType;
import com.example.ahmadfauzi.testconnect.network.IPConnect;
import com.example.ahmadfauzi.testconnect.network.MySQLiteHelper;

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
public class NewFoodTestActivity extends ActionBarActivity implements AsyncResponse, AdapterView.OnItemSelectedListener {
    ProgressDialog progressDialogResult;
    private ProgressDialog progressDialog;

    EditText editTextName;
    Spinner spinnerTestType;
    ImageView imageViewPhoto;
    TextView textViewResult;

//    private static String url_create_foodtest = "http://10.151.12.2/foodtest";
//    ClientConnect clientConnect = new ClientConnect(this, url_create_foodtest);
    ClientConnect clientConnect;

    private static final String TAG_SUCCESS = "success";

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2 ;

    private Sample testSample;
    private TestType testType;
    private ColorBars colorBars;

    private String dateTime;
    private String resultFoodTest;
    private String selectedTestType = "";

    Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
//            if (progressDialogResult.getProgress() >= 100) {
//                progressDialogResult.dismiss();
//                textViewResult.setText(resultMgl.getResult());
//            } else {
//                progressDialogResult.incrementProgressBy(10);
//            }
            progressDialogResult.dismiss();
            textViewResult.setText(resultFoodTest);
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ft);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextName = (EditText) findViewById(R.id.etName);
        spinnerTestType = (Spinner) findViewById(R.id.spinnerTestType);
        textViewResult = (TextView) findViewById(R.id.tvResult);
        imageViewPhoto = (ImageView) findViewById(R.id.ivPhoto);

        ArrayAdapter adapterSpinner = ArrayAdapter.createFromResource(this, R.array.testType, R.layout.spinner_item_testtype);
        spinnerTestType.setAdapter(adapterSpinner);
        spinnerTestType.setOnItemSelectedListener(this);

        testType = new TestType(getApplicationContext());
        colorBars = new ColorBars(getApplicationContext());

        //get IP
        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(this);
        int rowIP = sqLiteHelper.getCountRowIP();
        IPConnect ipConnect = sqLiteHelper.getIpConnect();

        if(rowIP != 0) {
            String url_foodtest= "http://" + ipConnect.getIp() + "/foodtest";
            clientConnect = new ClientConnect(this, url_foodtest);
            clientConnect.delegate = this;
        }

//        final String [] items = new String [] {"Camera", "Gallery"};
        final String [] items = new String [] {"Camera"};
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
                resultFoodTest = "";
                textViewResult.setText(resultFoodTest);
                dialog.show();
            }
        });

        Button buttonCreate = (Button) findViewById(R.id.btnCreate);
        buttonCreate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
//                String testTypeName = editTextTestType.getText().toString();
                String testTypeName = selectedTestType;
                String name = editTextName.getText().toString();
                if(!testTypeName.isEmpty() &&!name.isEmpty()) {
                    progressDialogResult = new ProgressDialog(NewFoodTestActivity.this);
                    progressDialogResult.setMessage("Mendapatkan hasil...");
                    progressDialogResult.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialogResult.setCancelable(true);
//                    progressDialogResult.setProgress(100);
//                    progressDialogResult.setMax(100);
                    progressDialogResult.show();

                    Thread background = new Thread (new Runnable() {
                        public void run() {
                            try {
                                testType = getSelectedTest();
                                makeEmptySample();

                                Thread.sleep(1);

                                progressHandler.sendMessage(progressHandler.obtainMessage());

                                // enter the code to be run while displaying the progressbar.
                                //
                                // This example is just going to increment the progress bar:
                                // So keep running until the progress value reaches maximum value
//                                while (progressDialogResult.getProgress()<= progressDialogResult.getMax()) {
                                    // wait 500ms between each update
//                                    Thread.sleep(500);

                                    // active the update handler
//                                    progressHandler.sendMessage(progressHandler.obtainMessage());
//                                }
                            } catch (java.lang.InterruptedException e) {
                                // if something fails do something smart
                                Toast.makeText(getApplicationContext(), "Mendapatkan hasil gagal", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    // start the background thread
                    background.start();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Tolong nama jenis uji diisi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        TextView textTestType = (TextView) view;
        if(position > 0){
            selectedTestType = textTestType.getText().toString();
//            Toast.makeText(this, "Anda memilih jenis uji " + textTestType.getText(), Toast.LENGTH_SHORT).show();
        } else {
            selectedTestType = "";
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                break;
            case R.id.action_new:
                newFoodTestFunction();
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

    private void newFoodTestFunction() {
        String name = editTextName.getText().toString();
//        String testType = editTextTestType.getText().toString();
        String testType = selectedTestType;
        String result = textViewResult.getText().toString();
        String name_replaced = name.replaceAll(" ","_");
        String testType_replaced = testType.replaceAll(" ","_");
        String result_replaced = result.replaceAll(" ","_");

        Bitmap bitmap = ((BitmapDrawable) imageViewPhoto.getDrawable()).getBitmap();
        String photo = encodeToBase64(bitmap);

//        Toast.makeText(this, name_replaced + " " + testType_replaced, Toast.LENGTH_LONG).show();
        if(!name.isEmpty() && !testType.isEmpty() && !result.isEmpty()){
            progressDialog = new ProgressDialog(NewFoodTestActivity.this);
            progressDialog.setMessage("Menambahkan....");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

            String urlParameter = "/create_foodtest.php?name_FoodTest=" + name_replaced + "&testType_FoodTest=" + testType_replaced + "&result_FoodTest=" + result_replaced + "&photo_FoodTest=" + photo;
            clientConnect.execute(urlParameter);

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

    //Make empty sample
    public void makeEmptySample(){
        testSample = new Sample(getApplicationContext());
        testSample.setTestName(testType.getTestName());
        Log.d("Main Activity", testType.getTestName());

        String path = getPicture();
        testSample.setDateTime(dateTime);
        testSample.setSamplePath(path);
        Log.d("Main Activity", "path : " + path);

        String resultValue = getResult();
        testSample.setResult_mgl(resultValue);
        Log.d("Main Activity", "result : " + resultValue);

        //hasilUjiUi
        setTestSample(testSample);
        resultFoodTest = testSample.getResult_mgl();
        Log.e("NewFoodTestActivity","RESULT = " + resultFoodTest);
//        textViewResult.setText(testSample.getResult_mgl());
    }

    private String getPicture(){
        Bitmap bitmapSample = ((BitmapDrawable) imageViewPhoto.getDrawable()).getBitmap();
        //set filename with current datetime (GMT)
        String filenameDate = null;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy_hh:mm:ss");
        this.dateTime = dateFormat.format(calendar.getTime());
        String dateTime = this.dateTime;

        filenameDate = dateTime + "_GMT_" + this.testType.getTestName() + "_" + editTextName.getText().toString();
        Log.d("MainActivity", "File Name = " + filenameDate);

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

        colorComparators = new ColorComparators(testSample, testType);
        int resultIndex = colorComparators.getCompareResult();

        if(resultIndex == -1){
            resultIndex = 0;
        }
        result = testType.getColorBarValue().get(resultIndex);
        Log.d("MainActivity", "RESULT = " + result);

        return result;
    }

    private TestType getSelectedTest(){
        TestType test = new TestType(getApplicationContext());
        ArrayList<String> path = new ArrayList<String>();
        ArrayList<String> value = new ArrayList<String>();

//        String selectedTest = editTexttestType.getText().toString();
        String selectedTest = selectedTestType;
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
