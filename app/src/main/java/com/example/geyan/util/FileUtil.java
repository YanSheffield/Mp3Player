package com.example.geyan.util;

import android.os.Environment;

import com.example.geyan.model.Mp3Info;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by geyan on 29/04/2017.
 */


public class FileUtil {
    private String SDCARD;
    private String path;
    //得到SD 卡的路径
    public FileUtil(){
        SDCARD = Environment.getExternalStorageDirectory()+"/";
        System.out.println("***"+SDCARD);
    }

    //create file
    public File createFileInSDCard(String path, String fileName) throws IOException {
        File file = new File(SDCARD + path+"/"+fileName);
        file.createNewFile();
        return file;
    }

    //create path(folder)
    public File createSDDir(String dirName){
        File dir = new File(SDCARD + dirName);
        System.out.println("&&&"+SDCARD + dirName);
        dir.mkdir();
        System.out.println("++"+dir.mkdir());
        return dir;
    }
    //判断文件夹是否存在
    public boolean isFileExist(String fileName){
        File file = new File(SDCARD+fileName);
        return file.exists();
    }

    //把一个inputStream里面的数据写入SD卡中
    //outputStream写数据
    public File write2SDFromInput(String path, String fileName, InputStream inputStream){
        File file = null;
        OutputStream outputStream = null;
        this.path = path;
        System.out.println("2D"+path);
        //首先创建路径和文件
        try {
            createSDDir(path);
            file = createFileInSDCard(path,fileName);
            //将outputStream的内容写到SD卡中（file）
            outputStream = new FileOutputStream(file);
            byte buffer[] = new byte[4*1024];
            while ((inputStream.read(buffer))!=-1){
                outputStream.write(buffer);
            }
            outputStream.flush();//清空缓存
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    //得到SDcard里面mp3的信息
    public List<Mp3Info> getDownloadedMp3Files(String path){
        List<Mp3Info> mp3InfoList = new ArrayList<>();
        System.out.println("***=="+SDCARD + path);
        System.out.println("&&&(("+path);
        File file = new File(SDCARD + path);
        File[] files = file.listFiles();
        for (File mp3File:files){
            if (mp3File.getName().endsWith("mp3")){
                Mp3Info mp3Info = new Mp3Info();
                mp3Info.setMp3Name(mp3File.getName());
                mp3Info.setMp3Size(mp3File.length()+"");
                mp3InfoList.add(mp3Info);
            }
        }
        return mp3InfoList;
    }
}
