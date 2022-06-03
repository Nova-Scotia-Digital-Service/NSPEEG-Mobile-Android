package com.ibm.emergencyapp.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ibm.emergencyapp.util.NotificationItems;

import java.util.ArrayList;

import static com.ibm.emergencyapp.constants.DatabaseConstants.*;

/**
 * Created by Nigel on 06/25/2018.
 */

public class SQLiteHelper extends SQLiteOpenHelper{

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_NOTIFICATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_NOTIFICATIONS_TABLE);
        onCreate(db);
    }

    public ArrayList<NotificationItems> getItinerariesItems(){
        ArrayList<NotificationItems> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_POI_ITEM, null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                NotificationItems info = new NotificationItems();
                info.id = cursor.getInt(0);
                info.name = cursor.getString(1);
                info.discription = cursor.getString(2);
                info.date = cursor.getString(3);
                items.add(info);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public void insertRecord(String name, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTIFICATION_NAME, name);
        contentValues.put(COLUMN_NOTIFICATION_DESCRIPTION, description);
        contentValues.put(COLUMN_NOTIFICATION_DATE, date);
        db.insert(TABLE_NAME_NOTIFICATIONS, null, contentValues);
    }

    /**
     * Delete a notification
     * @param id
     */
    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("Deleting where ID=" + id, "SQLHelper");


        int result = db.delete(TABLE_NAME_NOTIFICATIONS, "id=?", new String[]{Integer.toString(id)});

        Log.d("Result=" + result, "SQLHelper");
    }
}