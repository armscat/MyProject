package com.armscat.photoeditors.bean;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

public class FileCache {

    private File cacheDir;
    private File cacheLibraryDir;

    public FileCache(Context context){

        //找到保存的dir缓存图像
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

            cacheDir = new File(context.getCacheDir().getAbsolutePath() + "/PHOTO/");
            cacheLibraryDir = new File(context.getCacheDir().getAbsolutePath() + "/Library/");
        }else {
            cacheDir = context.getCacheDir();
        }
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }



    public String getLibraryPath(String photoId){

        //高清
        File hdPng = new File(cacheLibraryDir, photoId+"/hd_" +photoId+".png");
        if(hdPng.exists()){
            return cacheLibraryDir.getPath() +"/"+ photoId+"//hd_"+photoId+".png";
        }
        //普通
        File fPng = new File(cacheLibraryDir, photoId+"/" +photoId+".png");
        if(fPng.exists()){
            return cacheLibraryDir.getPath() +"/"+ photoId+"/"+photoId+".png";
        }

        File hdJpg = new File(cacheLibraryDir, photoId+"/hd_" +photoId+".jpg");
        if(hdJpg.exists()){
            return cacheLibraryDir.getPath() +"/"+ photoId+"/hd_" +photoId+".jpg";
        }

        File fJpg = new File(cacheLibraryDir, photoId+"/" +photoId+".jpg");
        if(fJpg.exists()){
            return cacheLibraryDir.getPath() +"/"+ photoId+"/" +photoId+".jpg";
        }



        return null;

    }


    public void clear(){
        File[] files=cacheDir.listFiles();
        for(File f:files)
            f.delete();
    }

}