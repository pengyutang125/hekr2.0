package com.hekr.android.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xubukan on 2015/3/24.
 */
public class AssetsDatabaseManager {

    // for LogCat
    private static final String TAG = "AssetsDatabase";
    // %s is packageName
    private static String databasepath = "/data/data/%s/database";

    // A mapping from assets database file to SQLiteDatabase object
    private Map<String, SQLiteDatabase> databases = new HashMap<String, SQLiteDatabase>();

    // Context of application
    private Context context = null;

    // Singleton Pattern
    private static AssetsDatabaseManager mInstance = null;

    /**
     * Initialize AssetsDatabaseManager
     * @param context, context of application
     */
    public static void initManager(Context context) {
        if(mInstance == null){
            mInstance = new AssetsDatabaseManager(context);
        }
        //Log.i(ListDeviceActivity.class.getSimpleName(),"mInstance===:"+mInstance);
    }

    /**
     * Get a AssetsDatabaseManager object
     * @return, if success return a AssetsDatabaseManager object, else return null
     */
    public static AssetsDatabaseManager getManager() {
        return mInstance;
    }

    private AssetsDatabaseManager(Context context) {
        this.context = context;
    }

    /**
     * Get a assets database, if this database is opened this method is only return a copy of the opened database
     * @param dbfile, the assets file which will be opened for a database
     * @return, if success it return a SQLiteDatabase object else return null
     */
    public SQLiteDatabase getDatabase(String dbfile) {
        if(databases.get(dbfile) != null){
            //Log.i(tag, String.format("Return a database copy of %s", dbfile));
            return (SQLiteDatabase) databases.get(dbfile);
        }
        if(context==null)
            return null;

        Log.i(TAG, String.format("Create database %s", dbfile));
        String spath = getDatabaseFilepath();
        String sfile = getDatabaseFile(dbfile);

        File file = new File(sfile);
        SharedPreferences dbs = context.getSharedPreferences(AssetsDatabaseManager.class.toString(), 0);
        boolean flag = dbs.getBoolean(dbfile, false); // Get Database file flag, if true means this database file was copied and valid
        if(!flag || !file.exists()){
            file = new File(spath);
            if(!file.exists() && !file.mkdirs()){
                Log.i(TAG, "Create \""+spath+"\" fail!");
                return null;
            }
            if(!copyAssetsToFilesystem(dbfile, sfile)){
                Log.i(TAG, String.format("Copy %s to %s fail!", dbfile, sfile));
                return null;
            }

            dbs.edit().putBoolean(dbfile, true).commit();
        }

        SQLiteDatabase db = SQLiteDatabase.openDatabase(sfile, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        if(db != null){
            databases.put(dbfile, db);
        }
        return db;
    }

    public String getIconUrlByCid(String cid) {
        if(mInstance!=null)
        {
            Cursor cursor = null;
            try {
                SQLiteDatabase db = mInstance.getDatabase("db");
                cursor = db.rawQuery("select logo_url from category where id=?",
                        new String[]{cid});
                if (cursor.moveToNext()) {
                    return cursor.getString(0);
                }
            }catch (Exception e){
                return "数据库取图片路径失败"+e.getMessage();
            }
            finally {
                if(cursor!=null) {
                    cursor.close();
                }
            }
        }
        return null;
    }

    public boolean needProductPageInsert(String pageId) {
        if(mInstance!=null)
        {
            SQLiteDatabase db = mInstance.getDatabase("db");
            Cursor cursor=null;
            try {
                cursor = db.rawQuery("select path from page where id=?",
                        new String[]{pageId});
                //数据库查出来的结果为0条数据
                if(cursor.getCount()==0)
                {
                    return true;
                }
                return false;
            }catch (Exception e){
                return false;
            }finally {
                if(cursor!=null) {
                    cursor.close();
                }
            }
        }
        return false;
    }

    public boolean needProductPageUpdate(String pageId,String version) {
        if(mInstance!=null)
        {
            SQLiteDatabase db = mInstance.getDatabase("db");
            Cursor cursor=null;
            try {
                cursor = db.rawQuery("select path from page where id=? and version=?",
                        new String[]{pageId,version});
                if(cursor.getCount()==0)
                {
                    return true;
                }
                return false;
            }catch (Exception e){
                return false;
            }finally {
                if(cursor!=null) {
                    cursor.close();
                }
            }
        }
        return false;
    }


    private String getDatabaseFilepath() {
        return String.format(databasepath, context.getApplicationInfo().packageName);
    }

    private String getDatabaseFile(String dbfile) {
        return getDatabaseFilepath()+"/"+dbfile;
    }

    private boolean copyAssetsToFilesystem(String assetsSrc, String des) {
        Log.i(TAG, "Copy "+assetsSrc+" to "+des);
        InputStream istream = null;
        OutputStream ostream = null;
        try{
            AssetManager am = context.getAssets();
            istream = am.open(assetsSrc);
            ostream = new FileOutputStream(des);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = istream.read(buffer))>0){
                ostream.write(buffer, 0, length);
            }
            istream.close();
            ostream.close();
        }
        catch(Exception e){
            //e.printStackTrace();
            try{
                if(istream!=null)
                    istream.close();
                if(ostream!=null)
                    ostream.close();
            }
            catch(Exception ee){
                ee.printStackTrace();
            }
            return false;
        }
        return true;
    }
    /**
     * Close assets database
     * @param dbfile, the assets file which will be closed soon
     * @return, the status of this operating
     */
    public boolean closeDatabase(String dbfile) {
        if(databases.get(dbfile) != null){
            SQLiteDatabase db = (SQLiteDatabase) databases.get(dbfile);
            db.close();
            databases.remove(dbfile);
            return true;
        }
        return false;
    }

    /**
     * Close all assets database
     */
    public static void closeAllDatabase() {
        Log.i(TAG, "closeAllDatabase");
        if(mInstance != null){
            for(int i=0; i<mInstance.databases.size(); ++i){
                if(mInstance.databases.get(i)!=null){
                    mInstance.databases.get(i).close();
                }
            }
            mInstance.databases.clear();
        }
    }
}