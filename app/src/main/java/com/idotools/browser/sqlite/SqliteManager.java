package com.idotools.browser.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.idotools.browser.bean.CartoonDetailsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxiaojun on 16-10-9.
 */
public class SqliteManager {

    public static final String INSERT = "insert into " + SqliteOpenHelper.TABLE_NAME + " (title,img,url) values (?,?,?)";
    public static final String SELECT_ALL = "select * from " + SqliteOpenHelper.TABLE_NAME;
    public static final String SELECT_BY_TITLE = "select * from " + SqliteOpenHelper.TABLE_NAME + " where url = ?";
//    public static final String UPDATE = "update " + SqliteOpenHelper.TABLE_NAME + " set lasterChapter=? where title=?";
    public static final String DELETE = "delete from "+SqliteOpenHelper.TABLE_NAME + " where url=?";
    public static final String DELETE_ALL = "delete from "+SqliteOpenHelper.TABLE_NAME;

    private Context mContext;
    private SqliteOpenHelper sqliteOpenHelper;
    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writeableDatabase;

    public SqliteManager(Context context) {
        this.mContext = context;
        sqliteOpenHelper = new SqliteOpenHelper(context);
        readableDatabase = sqliteOpenHelper.getReadableDatabase();
        writeableDatabase = sqliteOpenHelper.getWritableDatabase();
    }

    /***
     * 插入一条数据
     *
     * @param bean
     */
    public void insert(CartoonDetailsBean bean) {
        if (bean != null) {
            writeableDatabase.execSQL(INSERT, new Object[]{bean.title, bean.img, bean.url});
        }
    }

    /***
     * 查找所有数据
     *
     * @return
     */
    public List<CartoonDetailsBean> selectAll() {
        List<CartoonDetailsBean> list = new ArrayList<>();
        Cursor cursor = readableDatabase.rawQuery(SELECT_ALL, null);
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("title"));
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex("img"));
            String url = cursor.getString(cursor.getColumnIndex("url"));
            list.add(new CartoonDetailsBean(title, bytes, url));
        }
        cursor.close();
        return list;
    }

    /***
     * 根据title查找数据库是否存在这条数据
     *
     * @param url
     * @return 数据库中最新章节, 判断是否需要更新数据
     */
    public boolean selectByTitle(String url) {
        if (!TextUtils.isEmpty(url)) {
            Cursor cursor = readableDatabase.rawQuery(SELECT_BY_TITLE, new String[]{url});
            if (cursor.moveToNext()) {
                return true;
            }
        }
        return false;
    }

    /***
     * 更新数据
     *
     * @param lasterChapter
     */
    /*public void update(String title,String lasterChapter) {
        if (!TextUtils.isEmpty(lasterChapter) && !TextUtils.isEmpty(title)) {
            writeableDatabase.execSQL(UPDATE, new Object[]{lasterChapter, title});
        }
    }*/

    /***
     * 删除记录
     * @param url
     */
    public void delete(String url){
        if(!TextUtils.isEmpty(url)){
            writeableDatabase.execSQL(DELETE,new Object[]{url});
        }
    }

    /***
     * 删除所有
     */
    public void deleteAll(){
        writeableDatabase.execSQL(DELETE_ALL,new Object[]{});
    }

    /**
     * 释放资源
     */
    public void closeData(){
        mContext = null;
        readableDatabase.close();
        writeableDatabase.close();
    }

}
