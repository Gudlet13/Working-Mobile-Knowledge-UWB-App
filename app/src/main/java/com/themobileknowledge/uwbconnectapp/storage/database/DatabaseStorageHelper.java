package com.themobileknowledge.uwbconnectapp.storage.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.themobileknowledge.uwbconnectapp.model.Accessory;

import java.util.ArrayList;
import java.util.List;

public class DatabaseStorageHelper {

    private SQLiteDatabase mSQLiteDatabase;
    private DatabaseStorage mDatabaseStorage;

    public DatabaseStorageHelper(Context context) {
        mDatabaseStorage = new DatabaseStorage(context);
    }

    private void openWritableDatabase() {
        mSQLiteDatabase = mDatabaseStorage.getWritableDatabase();
    }

    private void openReadableDatabase() {
        mSQLiteDatabase = mDatabaseStorage.getReadableDatabase();
    }

    private void closeDatabase() {
        mDatabaseStorage.close();

        if (mSQLiteDatabase.isOpen()) {
            mSQLiteDatabase.close();
        }
    }

    public void deleteTableAccessoryAlias() {
        openWritableDatabase();

        // Delete whole accessory name table
        mSQLiteDatabase.execSQL("DELETE FROM " + DatabaseStorage.TABLE_ACCESSORY_ALIAS);

        closeDatabase();
    }

    public List<Accessory> getAccessories() {
        List<Accessory> accessories = new ArrayList<>();

        openReadableDatabase();

        Cursor cursor = mSQLiteDatabase.rawQuery(
                "SELECT * FROM " + DatabaseStorage.TABLE_ACCESSORY_ALIAS,
                null
        );

        while (cursor.moveToNext()) {
            Accessory accessory = new Accessory();
            accessory.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseStorage.COLUMN_NAME)));
            accessory.setMac(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseStorage.COLUMN_MAC)));
            accessory.setAlias(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseStorage.COLUMN_ALIAS)));

            accessories.add(accessory);
            cursor.moveToNext();
        }

        cursor.close();
        closeDatabase();

        return accessories;
    }

    public Accessory getAliasedAccessory(String mac) {
        Accessory accessory = null;

        openReadableDatabase();

        Cursor cursor = mSQLiteDatabase.rawQuery(
                "SELECT * FROM " + DatabaseStorage.TABLE_ACCESSORY_ALIAS
                        + " WHERE " + DatabaseStorage.COLUMN_MAC + " = ?",
                new String[]{mac}
        );

        if (cursor.moveToFirst()) {
            accessory = new Accessory();
            accessory.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseStorage.COLUMN_NAME)));
            accessory.setMac(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseStorage.COLUMN_MAC)));
            accessory.setAlias(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseStorage.COLUMN_ALIAS)));
        }

        cursor.close();
        closeDatabase();

        return accessory;
    }

    public void insertAccessory(Accessory accessory) {
        openWritableDatabase();

        SQLiteStatement sqLiteStatement = mSQLiteDatabase.compileStatement("INSERT INTO " + DatabaseStorage.TABLE_ACCESSORY_ALIAS
                + " ("
                + DatabaseStorage.COLUMN_NAME
                + ", "
                + DatabaseStorage.COLUMN_MAC
                + ", "
                + DatabaseStorage.COLUMN_ALIAS
                + ") "
                + "VALUES (?, ?, ?)"
        );

        sqLiteStatement.bindString(1, accessory.getName());
        sqLiteStatement.bindString(2, accessory.getMac());
        sqLiteStatement.bindString(3, accessory.getAlias());
        sqLiteStatement.executeInsert();
        sqLiteStatement.close();

        closeDatabase();
    }

    public void deleteAccessory(Accessory accessory) {
        openWritableDatabase();

        SQLiteStatement sqLiteStatement = mSQLiteDatabase.compileStatement("DELETE FROM " + DatabaseStorage.TABLE_ACCESSORY_ALIAS
                + " WHERE " + DatabaseStorage.COLUMN_MAC + " = ?"
        );

        sqLiteStatement.bindString(1, accessory.getMac());
        sqLiteStatement.execute();
        sqLiteStatement.close();

        closeDatabase();
    }

    public void updateAccessoryAlias(Accessory accessory, String alias) {
        openWritableDatabase();

        SQLiteStatement sqLiteStatement = mSQLiteDatabase.compileStatement("UPDATE " + DatabaseStorage.TABLE_ACCESSORY_ALIAS
                + " SET " + DatabaseStorage.COLUMN_ALIAS + " = ?"
                + " WHERE " + DatabaseStorage.COLUMN_MAC + " = ?"
        );

        sqLiteStatement.bindString(1, alias);
        sqLiteStatement.bindString(2, accessory.getMac());
        sqLiteStatement.execute();
        sqLiteStatement.close();

        closeDatabase();
    }

    /* public void insertUserId(String userId) {
        open();
        SQLiteStatement statement = database.compileStatement("INSERT INTO " + DataStore.TABLE_USER_CONFIDENTIAL
                + " (" + DataStore.COLUMN_USER_ID + ", " + DataStore.COLUMN_WALLET_ID + ") VALUES (?, ?)");

        statement.bindString(1, userId);
        statement.bindString(2, KEY_DEFAULT_VALUE);
        statement.executeInsert();
        statement.close();
        close();
    }

    public void updateWalletId(String walletId) {
        open();
        SQLiteStatement statement = database.compileStatement("UPDATE " + DataStore.TABLE_USER_CONFIDENTIAL
                + " SET " + DataStore.COLUMN_WALLET_ID + " = ?");

        statement.bindString(1, walletId);
        statement.executeInsert();
        statement.close();
        close();
    }

    public String getUserId() {
        open();
        Cursor cursor = database.rawQuery("SELECT " + DataStore.COLUMN_USER_ID
                        + " FROM " + DataStore.TABLE_USER_CONFIDENTIAL,
                null);

        String userId = "";
        if (cursor.moveToFirst()) {
            userId = cursor.getString(0);
        }

        cursor.close();
        close();

        return userId;
    } */
}
