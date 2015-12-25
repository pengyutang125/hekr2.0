package com.hekr.android.app.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import com.lambdatm.runtime.lang.Cell;
import com.lambdatm.runtime.lib.Base;
import com.lambdatm.runtime.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hekr_xm on 2015/9/9.
 */
public class DetailCut {
    private static DetailCut detailCut = null;
    private Context mContext;
    private AssetsDatabaseManager mg;
    private SQLiteDatabase db;

    public DetailCut() {
    }

    public DetailCut(Context mContext) {
        this.mContext = mContext;
        mg = AssetsDatabaseManager.getManager();
        db = mg.getDatabase("db");
    }

    public static synchronized DetailCut getInstance(Context mContext) {
        if (detailCut == null) {
            detailCut = new DetailCut(mContext);
        }
        return detailCut;
    }

    public static List<Object> getDetailList(String detail) {
        List<Object> stateList=null;
        try{
            if (!TextUtils.isEmpty(detail)) {
                stateList = Util.tolist((Cell) Base.read.pc(detail, null));
                //Log.i("MyLog", "stateList:" + stateList);
                return stateList;
            }
        }catch (Exception e){
            //Log.i("MyLog","解析不了为list："+e.getMessage());
            //return Util.tolist((Cell) Base.read.pc("(\"mid\" 0 \"pid\" 0 \"cid\" 0 )", null));
            return null;
        }
        //return Util.tolist((Cell) Base.read.pc("(\"mid\" 0 \"pid\" 0 \"cid\" 0 )", null));
        return stateList;
    }

    //例如：将 "detail": "(\"mid\" 3 \"pid\" 9 \"cid\" 13 \"mname\" \"__XSJ_450__\" )"
    //切割成list然后变成map
    public static Map<Object, Object> getDetailMap(String detail) {
        if(!TextUtils.isEmpty(detail)){
            //Log.i("MyLog","detail:"+detail);
            List stateList = getDetailList(detail);
            if(!stateList.isEmpty()&&stateList.size()>=2){
                Map<Object, Object> detailMap=new HashMap<Object, Object>();
                try {
                    for (int i = 0; i < stateList.size(); i = i + 2) {
                        detailMap.put(stateList.get(i), stateList.get(i + 1));
                    }
                }catch(Exception ex){
                }
                return detailMap;
            }
            return null;
        }
        return null;
    }

    //解析前面有一个值的命令例如(mcastFormatMsgs "upgradeprogress" 10 "upgradestate" 1)
    public static Map<Object, Object> getDetailMap(String detail,String order) {
        if(!TextUtils.isEmpty(detail)){
            //Log.i("MyLog","detail:"+detail);
            List stateList = getDetailList(detail);
            if(!stateList.isEmpty()&&stateList.size()>=3){
                Map<Object, Object> detailMap=new HashMap<Object, Object>();
                try {
                    for (int i = 1; i < stateList.size(); i = i + 2) {
                        detailMap.put(stateList.get(i), stateList.get(i + 1));
                    }
                }catch(Exception ex){
                }
                return detailMap;
            }
            return null;
        }
        return null;
    }

    //设备大范围的种类(例：插座、冰箱)
    public String getName(String detail) {
        String cid="";
        if(!TextUtils.isEmpty(detail)){
            cid=getDetailMap(detail).get("cid")+"";
        }

        if("".equals(cid))
        {
            return "unknow";
        }
        if("null".equals(cid))
        {
            return "unknow";
        }
        String countryCategory=mContext.getResources().getConfiguration().locale.getCountry();
        Cursor cursor = null;
        if(countryCategory.equals("CN")){
            try {
                cursor = db.rawQuery("select name from category where id=?",
                        new String[]{cid});
                if (cursor.moveToNext()) {
                    return cursor.getString(0);
                }
                return "unknow";
            }catch (Exception e){
                return "unknow";

            }finally {
                if(cursor!=null) {
                    cursor.close();
                }
            }
        }
        else{
            try {
                cursor = db.rawQuery("select ename from category where id=?",
                        new String[]{cid});
                if (cursor.moveToNext()) {
                    return cursor.getString(0);
                }
                return "unknow";
            }catch (Exception e){
                return "unknow";

            }finally {
                if(cursor!=null) {
                    cursor.close();
                }
            }
        }

    }

    //detail里的cid
    public String getCid(String detail) {
        String cid="";
        if(!TextUtils.isEmpty(detail)){
            cid=""+ getDetailMap(detail).get("cid");
        }
        if("null".equals(cid)){
            return null;
        }
        return cid;
    }

    //detail里的pid
    public static String getPid(String detail) {
        String pid="";
        if(!TextUtils.isEmpty(detail)){
            pid=getDetailMap(detail).get("pid")+"";
        }
        if("null".equals(pid)){
            return null;
        }
        return pid;
    }

    //detail里的mid
    public static String getMid(String detail) {
        String mid="";
        if(!TextUtils.isEmpty(detail)){
            mid=getDetailMap(detail).get("mid")+"";
        }
        if("null".equals(mid)){
            return null;
        }
        return mid;
    }

    //2.0固件版本信息
    public String getVerAndType(String detail) {
        String ver="";
        if(!TextUtils.isEmpty(detail)){
            ver=getDetailMap(detail).get("ver")+"";
        }
        if("null".equals(ver)){
            return null;
        }
        return ver;
    }

    //3.0版本号（例：3.0.28.2）
    public String getBinver(String detail) {
        String binver="";
        if(!TextUtils.isEmpty(detail)){
            binver=getDetailMap(detail).get("binver")+"";
        }
        Log.i("SetActivity","binver:"+binver);
        if("null".equals(binver)){
            return "";
        }
        return binver;
    }
    //3.0版本类型(类型)
    public String getBintype(String detail) {
        String bintype="";
        if(!TextUtils.isEmpty(detail)){
            bintype=getDetailMap(detail).get("bintype")+"";
        }
        if("null".equals(bintype)){
            return "";
        }
        return bintype;
    }

    //固件升级更新进度值(里面调用的map是从list第二个开始元素开始解析的)
    public int getUpgradeprogress(String detail) {
        int upgradeprogress=0;
        if(!TextUtils.isEmpty(detail)){
            upgradeprogress=Integer.parseInt(getDetailMap(detail,"order").get("upgradeprogress").toString());
        }
        if("null".equals(upgradeprogress)){
            return 0;
        }
        return upgradeprogress;
    }

    //固件升级状态
    public int getUpgradestate(String detail) {
        int upgradestate=404;
        if(!TextUtils.isEmpty(detail)){
            upgradestate=Integer.parseInt(getDetailMap(detail,"order").get("upgradestate").toString());
        }
        if("null".equals(upgradestate)){
            return 404;
        }
        return upgradestate;
    }

    //设备类型Icon
    public Bitmap getIcon(String detail,Context mContext) {
        String iconUrl="";
        //Log.i(ListDeviceActivity.class.getSimpleName(),"detatil===:"+detail);
        if(!TextUtils.isEmpty(detail)){
            iconUrl = mg.getIconUrlByCid(""+ getDetailMap(detail).get("cid"));
        }
        InputStream is=null;
        InputStream iis=null;
        InputStream iiis=null;
        try
        {
            is=mContext.getAssets().open(iconUrl);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            //is.close();
            return bitmap;

        } catch (Exception e)
        {
            try{
                //Log.i("MyLog","有进入执行e下！");
                Bitmap bitmap = BitmapFactory.decodeFile(iconUrl);
                //Log.i("MyLog","bitmap："+bitmap);
                if(bitmap==null){
                    iis=mContext.getAssets().open("product/weizhi.png");
                    Bitmap citmap = BitmapFactory.decodeStream(iis);
                    //iis.close();
                    return citmap;
                }else{
                    return bitmap;
                }
            }catch (Exception ee){
                return null;
            }
        }
        finally {
            try {
                if(is!=null){
                    is.close();
                }
                if(iis!=null){
                    iis.close();
                }
                if(iiis!=null){
                    iiis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //控制指令里的tid
    public String tidFromMsg(String msg){
        String tid="";
        if(!TextUtils.isEmpty(msg)){
            tid=getDetailMap(msg).get("tid")+"";
        }
        if("null".equals(tid)){
            return null;
        }
        return tid;
    }

    //控制指令里的msg


}
