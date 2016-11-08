package com.buku001.tenyuan.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wuxiaojun on 16-10-9.
 */
public class SqliteOpenHelper extends SQLiteOpenHelper {

    public static final String DATA_NAME = "brawser";
    public static final int VERSION = 1;
    public static final String TABLE_NAME = "history";
    public static final String CREATE_TABLE_HISTORY = "create table "+TABLE_NAME+" (_id integer primary key autoincrement " +
            ",title varchar(10),img varchar(30),url varchar(20))";

    public SqliteOpenHelper(Context context) {
        super(context, DATA_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建一张记录详情页面的表
        db.execSQL(CREATE_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
