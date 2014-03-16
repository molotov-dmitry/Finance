package com.finance.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class DBAdapter {
    private final Context context;
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    private static final String DATABASE_TABLE_ACC = DBHelper.TABLE_NAME_ACC;
    private static final String DATABASE_TABLE_INFLOW = DBHelper.TABLE_NAME_INFLOW;
    private static final String DATABASE_TABLE_OUTFLOW = DBHelper.TABLE_NAME_OUTFLOW;


    public DBAdapter(Context context) {
        this.context = context;
    }

    public DBAdapter open() {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public long createAccount(String acc_name, long balance) {
        ContentValues initialValues = createAccContentValues(acc_name, balance);
        return database.insert(DATABASE_TABLE_ACC, null, initialValues);
    }

    public boolean updateAccount(long rowId, String acc_name, long balance) {
        ContentValues updateValues = createAccContentValues(acc_name, balance);
        return database.update(DATABASE_TABLE_ACC, updateValues, DBHelper.KEY_ACC_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteAccount(long rowId) {
        return database.delete(DATABASE_TABLE_ACC, DBHelper.KEY_ACC_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllAccounts() {
        return database.query(DATABASE_TABLE_ACC, new String[]{DBHelper.KEY_ACC_ROWID,
                DBHelper.KEY_ACC_NAME, DBHelper.KEY_ACC_BALANCE}, null, null, null,
                null, null);
    }

    private ContentValues createAccContentValues(String acc_name, long balance) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_ACC_NAME, acc_name);
        values.put(DBHelper.KEY_ACC_BALANCE, balance);
        return values;
    }


    public long createFlow(boolean flowType, Date date, long amount, long account_id, String name) {
        String table;

        if (flowType)
            table = DATABASE_TABLE_INFLOW;
        else
            table = DATABASE_TABLE_OUTFLOW;

        ContentValues initialValues = createFlowContentValues(date, amount, account_id, name);
        return database.insert(table, null, initialValues);
    }

    public boolean updateFlow(boolean flowType, long rowId, Date date, long amount, long account_id, String name) {
        String table;

        if (flowType)
            table = DATABASE_TABLE_INFLOW;
        else
            table = DATABASE_TABLE_OUTFLOW;

        ContentValues updateValues = createFlowContentValues(date, amount, account_id, name);
        return database.update(table, updateValues, DBHelper.KEY_FLOW_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteFlow(boolean flowType, long rowId) {
        String table;

        if (flowType)
            table = DATABASE_TABLE_INFLOW;
        else
            table = DATABASE_TABLE_OUTFLOW;

        return database.delete(table, DBHelper.KEY_FLOW_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllFlows(boolean flowType) {

        String table;

        if (flowType)
            table = DATABASE_TABLE_INFLOW;
        else
            table = DATABASE_TABLE_OUTFLOW;

        return database.query(table, new String[]{DBHelper.KEY_FLOW_ROWID,
                DBHelper.KEY_FLOW_DATE, DBHelper.KEY_FLOW_AMOUNT, DBHelper.KEY_FLOW_ACCOUNT, DBHelper.KEY_FLOW_NAME}, null, null, null,
                null, null);
    }

    private ContentValues createFlowContentValues(Date date, long amount, long account_id, String name) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_FLOW_DATE, date.toString());
        values.put(DBHelper.KEY_FLOW_AMOUNT, amount);
        values.put(DBHelper.KEY_FLOW_ACCOUNT, account_id);
        values.put(DBHelper.KEY_FLOW_NAME, name);
        return values;
    }
}
