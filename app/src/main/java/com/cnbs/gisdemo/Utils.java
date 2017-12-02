package com.cnbs.gisdemo;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;



/**
 * author: zuo
 * date: 2017/11/30 14:16
 */

public class Utils {
    public final String mmpk_name = "GisTest.mmpk"; //文件名字
    public final String File_name = "GisTest.tpk"; //文件名字
    public final String Package_name = "com.cnbs.gisdemo"; //项目包路径
    public final String Save_Path = "/data"
            + Environment.getDataDirectory().getAbsolutePath()+"/"
            + Package_name
            +"/arcgis";

    public void saveRawToSD(Context context) {
        try {
            String filename = Save_Path + "/" + File_name;
            File dir = new File(Save_Path);
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (!(new File(filename)).exists()) {
                InputStream is = context.getResources().openRawResource(R.raw.gistest);
                FileOutputStream fos = new FileOutputStream(filename);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveAssetsToSD(Context context) {
        try {
            String filename = Save_Path + "/" + File_name;
            File dir = new File(Save_Path);
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (!(new File(filename)).exists()) {
                InputStream is = context.getResources().getAssets().open("GisTest.tpk");
                FileOutputStream fos = new FileOutputStream(filename);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
