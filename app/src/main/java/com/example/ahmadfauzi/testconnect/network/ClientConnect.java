package com.example.ahmadfauzi.testconnect.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Created by Ahmad Fauzi on 5/23/2015.
 */
public class ClientConnect extends AsyncTask<String, Void, String>{
    public AsyncResponse delegate = null;
    private static String baseUrl;
    private Context context;

    public ClientConnect(Context context, String url)
    {
        baseUrl = url;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... args) {
            String resp = "";
            try {
                String urlParameter = args[0].toString();
                String url_t = this.baseUrl + urlParameter;
                URL url = new URL(url_t);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("GET");
                int statusCode = conn.getResponseCode();
                InputStream is = null;
                Log.d("ClientSocket", "URL = " + url_t);
                Log.d("ClientSocket", "Status Code = " + String.valueOf(statusCode));
                if (statusCode == 200) {
                    is = new BufferedInputStream(conn.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        resp += line;
                    }
                    is.close();
                    return  resp;
                } else {
                    resp = "{RESP : 'ERROR' }";
                    return  resp;
                }
            }catch (Exception e)
            {
                return new String("Exc : " + e.getMessage());
            }
    }


    @Override
    protected void onPostExecute(String s) {
        Log.d("client socket LOG", s);
        delegate.processFinish(s);
    }
}
