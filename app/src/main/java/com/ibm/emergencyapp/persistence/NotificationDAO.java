package com.ibm.emergencyapp.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by tara on 10/19/2017.
 */

public class NotificationDAO {

    private SQLiteDatabase db;

    SimpleDateFormat iso8601Format = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    SimpleDateFormat timeFormat = new SimpleDateFormat(
            "h:mm a");

    SimpleDateFormat dayFormat = new SimpleDateFormat(
            "yyyy-MM-dd");


    private static final String TABLE_NAME = "notifications";

    private static final String LOG_TAG =
            NotificationDAO.class.getSimpleName();

    private static NotificationDAO instance = null;

    public synchronized static NotificationDAO getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationDAO(context);
        }

        return instance;
    }

    /**
     * Constructor.
     * @param context
     */
    private NotificationDAO(Context context) {

        DbHelper helper = new DbHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * Get a notification by its id.
     * @param notId
     * @return
     */
    public Notification get(int notId) {

        Cursor c = db.rawQuery("SELECT * from " + TABLE_NAME + " where id=" + notId + " ORDER BY date;", null);

        c.moveToFirst();


        if (c.getCount() == 0) {
            return null;
        }

        int id = c.getInt(0);
        String date = formatDateDisplay(c.getString(1));
        String content = c.getString(2);

        Notification n = new Notification(id, date, content);

        c.close();
        return n;
    }



    /**
     * Get all notifications.
     * @return
     */
    public ArrayList<Notification> getAll() {

        ArrayList<Notification> list = new ArrayList<Notification>();

        Cursor cursor = db.rawQuery("SELECT * from " + TABLE_NAME, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String date = formatDateDisplay(cursor.getString(1));
            String content = cursor.getString(2);
            list.add(new Notification(id, date, content));
        }
        cursor.close();
        return list;
    }

    /**
     * Delete a notification
     * @param id
     */
    public void delete(int id) {
        Log.d("Deleting where ID=" + id, LOG_TAG);


        int result = db.delete(TABLE_NAME, "id=?", new String[]{Integer.toString(id)});

        Log.d("Result=" + result, LOG_TAG);
    }

    /**
     * Delete all notifications
     */
    public void deleteAll() {
        Log.d("*** Deleting all...", LOG_TAG);


        ArrayList<Notification> all = getAll();
        for (Notification n : all) {
            delete(n.id);
        }

        // Seems this is not executing before the insert.....
       // db.delete(TABLE_NAME, "1=1", null);


    }

    /**
     * Insert a notification
     * @param date
     * @param content
     * @return
     */
    public Notification insert(Date date, String content) {

        String formattedDate = iso8601Format.format(date);

        try {

            ContentValues values = new ContentValues();
            values.put("date", formattedDate);
            values.put("content", content);

            int newRowId = (int) db.insert(TABLE_NAME, null, values);

            Log.d("*** inserted row " + newRowId, LOG_TAG);

            return this.get(newRowId);

        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private String formatDateDisplay(String date)  {

        try {
            Date dateObj = iso8601Format.parse(date);

            // if today only display time
            if (DateUtils.isToday(dateObj.getTime())) {
                return timeFormat.format(dateObj);
            }

            return dayFormat.format(dateObj);

        } catch (ParseException e) {

            Log.d("Parse exception: " + e.getMessage(), LOG_TAG);
            return null;
        }
    }


    private class DbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "notifications";

        private final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME + ";" ;

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                    + " (id INTEGER NOT NULL PRIMARY KEY, date DATE, content VARCHAR);");
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            //db.execSQL(SQL_DELETE_ENTRIES);
            //onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
