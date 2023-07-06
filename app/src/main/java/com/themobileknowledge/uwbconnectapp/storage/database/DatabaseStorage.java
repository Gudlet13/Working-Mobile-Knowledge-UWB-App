package com.themobileknowledge.uwbconnectapp.storage.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseStorage extends SQLiteOpenHelper  {

    private static final String DATABASE_NAME = "accessoryalias.db";
    private static final int DATABASE_VERSION = 1;

    public static final String CREATE_TABLE = "CREATE TABLE ";

    public static final String TABLE_ACCESSORY_ALIAS = "ACCESSORY_ALIAS";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_MAC = "MAC";
    public static final String COLUMN_ALIAS = "ALIAS";
    public static final String COLUMN_COLOR = "COLOR";

    private final String TABLE_ACCESSORY_ALIAS_CREATE = CREATE_TABLE
            + TABLE_ACCESSORY_ALIAS
            + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " VARCHAR, "
            + COLUMN_MAC + " VARCHAR, "
            + COLUMN_ALIAS + " VARCHAR, "
            + COLUMN_COLOR + " INTEGER);";

    public DatabaseStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_ACCESSORY_ALIAS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // Drop database tables. This will delete all data previously stored
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCESSORY_ALIAS);

        onCreate(database);
    }
}
