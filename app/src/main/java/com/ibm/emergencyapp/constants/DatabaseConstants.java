package com.ibm.emergencyapp.constants;

/**
 * Created by Nigel on 06/25/2018.
 */

public class DatabaseConstants {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SQLiteDatabase.db";
    public static final String TABLE_NAME_NOTIFICATIONS = "NOTIFICATIONS";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NOTIFICATION_NAME = "name";
    public static final String COLUMN_NOTIFICATION_DESCRIPTION = "description";
    public static final String COLUMN_NOTIFICATION_DATE = "date";

    public static final String SQL_CREATE_NOTIFICATIONS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_NOTIFICATIONS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NOTIFICATION_NAME + " TEXT," +
                    COLUMN_NOTIFICATION_DESCRIPTION + " TEXT," +
                    COLUMN_NOTIFICATION_DATE + " TEXT)";

    public static final String SQL_DELETE_NOTIFICATIONS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME_NOTIFICATIONS;

    public static final String SQL_SELECT_POI_ITEM =
            "SELECT * FROM  " + TABLE_NAME_NOTIFICATIONS;
}
