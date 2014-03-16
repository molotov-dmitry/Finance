package com.finance.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME_ACC = "account";
    public static final String TABLE_NAME_INFLOW = "inflow";
    public static final String TABLE_NAME_OUTFLOW = "outflow";

    public static final String KEY_ACC_ROWID = "_id";
    public static final String KEY_ACC_NAME = "acc_name";
    public static final String KEY_ACC_BALANCE = "balance";

    public static final String KEY_FLOW_ROWID = "_id";
    public static final String KEY_FLOW_DATE = "flow_date";
    public static final String KEY_FLOW_AMOUNT = "flow_amount";
    public static final String KEY_FLOW_ACCOUNT = "flow_account";
    public static final String KEY_FLOW_NAME = "flow_name";

    private static final String DATABASE_NAME = "financedb";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_FIELDS_ACC = KEY_ACC_ROWID + " integer primary key autoincrement, "
            + KEY_ACC_NAME + " text not null, "
            + KEY_ACC_BALANCE + " integer not null";

    private static final String TABLE_FIELDS_FLOW = KEY_FLOW_ROWID + " integer primary key autoincrement, "
            + KEY_FLOW_DATE + " text not null, "
            + KEY_FLOW_AMOUNT + " integer not null, "
            + KEY_FLOW_ACCOUNT + " integer not null, "
            + KEY_FLOW_NAME + " text not null";

    private static final String DATABASE_CREATE_ACC = "create table if not exists " + TABLE_NAME_ACC + " ("
            + TABLE_FIELDS_ACC + ");";

    private static final String DATABASE_CREATE_INFLOW = "create table if not exists " + TABLE_NAME_INFLOW + " ("
            + TABLE_FIELDS_FLOW + ");";

    private static final String DATABASE_CREATE_OUTFLOW = "create table if not exists " + TABLE_NAME_OUTFLOW + " ("
            + TABLE_FIELDS_FLOW + ");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_ACC);
        database.execSQL(DATABASE_CREATE_INFLOW);
        database.execSQL(DATABASE_CREATE_OUTFLOW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(), "DB upgrade " + oldVersion + " to " + newVersion + ", all date will be erased");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ACC);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_INFLOW);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_OUTFLOW);
        onCreate(database);
    }
}
