package com.example.ahmadfauzi.testconnect.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Ahmad Fauzi on 12/14/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "IP_UjiMakanan.db";
    public static final String TABLE_NAME_UM = "ujimakanan";
    public static String COLUMN_ID_TABLE_UM = "idUM";
    public static final String COLUMN_IP_TABLE = "ipUM";

    public MySQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME_UM + " (" + COLUMN_ID_TABLE_UM + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_IP_TABLE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(getClass().getName(), "Upgrade db from version " + oldVersion + " to " + newVersion + " that erase all data");
        db.execSQL("DROP TABLE IF EXIST " + TABLE_NAME_UM);
    }

    public void addIP(IPConnect ipConnect){
        ContentValues values = new ContentValues();
        values.put(COLUMN_IP_TABLE, ipConnect.getIp());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME_UM, null, values);
        db.close();
    }

    public void updateIP(String newIPConnect){
        String updateQuery = "UPDATE " + TABLE_NAME_UM + " SET " + COLUMN_IP_TABLE  + " = '" + newIPConnect + "' WHERE "+ COLUMN_ID_TABLE_UM + " = " + "(SELECT MAX(" + COLUMN_ID_TABLE_UM + ") FROM " + TABLE_NAME_UM + ")";
        SQLiteDatabase db1 = this.getWritableDatabase();
        Cursor cursor = db1.rawQuery(updateQuery, null);
        cursor.moveToFirst();
        cursor.close();
    }

    public IPConnect getIpConnect(){
        String query = "SELECT * FROM " + TABLE_NAME_UM;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        IPConnect ipConnect = new IPConnect();

        if(cursor.moveToFirst()){
            cursor.moveToFirst();
            ipConnect.setId(Integer.parseInt(cursor.getString(0)));
            ipConnect.setIp(cursor.getString(1));
            cursor.close();
        } else {
            ipConnect = null;
        }
        db.close();
        return ipConnect;
    }

    public int getCountRowIP(){
        String countQuery = "SELECT * FROM " + TABLE_NAME_UM;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
