package com.idotools.browser.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.idotools.utils.LogUtils;

/**
 * Created by wuxiaojun on 16-10-9.
 */
public class SqliteOpenHelper extends SQLiteOpenHelper {

    public static final String DATA_NAME = "dmbrawser";
    public static final int VERSION = 2;
    public static final String TABLE_NAME_HISTORY = "history";
    public static final String CREATE_TABLE_HISTORY = "create table if not exists " + TABLE_NAME_HISTORY + " (_id integer primary key autoincrement " +
            ",title varchar(10),img varchar(30),url varchar(20))";

    public static final String TABLE_NAME_RECORDS = "records";
    public static final String CREATE_TABLE_RECORDS = "create table if not exists " + TABLE_NAME_RECORDS + " (_id integer primary key autoincrement" +
            ", title varchar(20),img varchar(30),url varchar(20))";

    public SqliteOpenHelper(Context context) {
        super(context, DATA_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建一张记录详情页面的表
        db.execSQL(CREATE_TABLE_HISTORY);
        db.execSQL(CREATE_TABLE_RECORDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL(CREATE_TABLE_RECORDS);
        }
    }

}
