package com.newspaper.allbangla;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = "ttttttttttttttttt";

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "archivevideos";

    // Login table name
    private static final String TABLE_LOGIN = "archive";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LINK = "link";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_VIEWS = "views";
    MainActivity mainActivity;


    private Toast toast = null;
    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mainActivity= (MainActivity) context;

        toast = Toast.makeText(mainActivity, "", Toast.LENGTH_LONG);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_LINK + " TEXT UNIQUE," + KEY_IMAGE_URL + " TEXT," + KEY_VIEWS + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String link, String image_url, String views) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c= getSingleUserDetails(link);
        if (!c.moveToNext()) {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, name); // Name
            values.put(KEY_LINK, link); // Email
            values.put(KEY_IMAGE_URL, image_url); // Email
            values.put(KEY_VIEWS, views); // Created At

            // Inserting Row
            long id = db.insert(TABLE_LOGIN, null, values);
            c.close();

            showToast("Added to Archive Folder");
//            Toast.makeText(mainActivity, "Added to Archive Folder", Toast.LENGTH_LONG).show();
        }else {

            showToast("Already Inserted");
//            Toast.makeText(mainActivity, "", Toast.LENGTH_LONG).show();
            c.close();
        }
        db.close(); // Closing database connection

//        Log.e(TAG, "New user inserted into sqlite: " +" : "+name+" : "+link);
    }


    public void deleteUser(String link) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c= getSingleUserDetails(link);
        if (!c.moveToNext()) {

            c.close();

            showToast("Error");

//            Toast.makeText(mainActivity, "Error", Toast.LENGTH_LONG).show();
        }else {
            String selectQuery = "DELETE  FROM " + TABLE_LOGIN + " WHERE " + KEY_LINK + " = '" + link + "'";


            db.execSQL(selectQuery);

            showToast("Removed from Archive");
//            Toast.makeText(mainActivity, "", Toast.LENGTH_LONG).show();
            c.close();
        }
        db.close(); // Closing database connection

//        Log.e(TAG, "New user inserted into sqlite: " +" : "+link);
    }


    /**
     * Getting user data from database
     * */
    public Cursor getUserDetails(String searchText) {
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN + " WHERE " + KEY_NAME + " LIKE '%" + searchText + "%'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row

        return cursor;
    }

    /**
     * Getting user login status return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }
    public Cursor getSingleUserDetails(String userLink) {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN + " WHERE " + KEY_LINK + " = '" + userLink + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row


        return cursor;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();

        Log.e(TAG, "Deleted all user info from sqlite");
    }



//    public void updateUserProfilePic(String email, String url) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_IMAGE_URL, url); // Created At
//
//        Log.e(TAG, "New user updated into sqlite: " +" : "+url+" : "+values.toString());
//        // Inserting Row
//        long id= db.update(TABLE_LOGIN,values,KEY_ID + "=1", null);
//
//        db.close(); // Closing database connection
//
//    }
//    public String getUserProfilePic(String email) {
//        String user = null;
//        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN +" WHERE "+KEY_EMAIL+" = "+"'"+email+"'";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        // Move to first row
//        cursor.moveToFirst();
//        if (cursor.getCount() > 0) {
//            user= cursor.getString(9);
//        }
//        if (user==null){
//            user="Nothing";
//        }
//        cursor.close();
//        db.close();
//        // return user
//        Log.e(TAG, "Fetching user from Sqlite: " + user);
//
//        return user;
//
//    }

    public void showToast(String textshow){
        if (toast!= null){
            toast.cancel();
        }
        toast = Toast.makeText(mainActivity, ""+textshow,Toast.LENGTH_LONG);
        toast.show();
    }

}