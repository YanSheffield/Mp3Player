package com.example.geyan.download;

import android.content.Intent;
import android.widget.Toast;

import com.example.geyan.mp3player.MainActivity;
import com.example.geyan.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by geyan on 29/04/2017.
 */

public class Mp3Downloader {

    private URL url;
    private int downloadStatus;

    public int downloadMp3File(String urlStr,String path,String fileName) {
        FileUtil fileUtil = new FileUtil();
        //增加程序的健壮性
        if (fileUtil.isFileExist(path+"/"+fileName)){
            System.out.println("已经存在");
            downloadStatus = 1;
            return downloadStatus;
        }else {
            try {
                url = new URL(urlStr);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //从网上拿到（读）文件存到流中
                InputStream inputStream = urlConnection.getInputStream();
                File resultFile = fileUtil.write2SDFromInput(path,fileName,inputStream);

                if (resultFile==null){
                    System.out.println("下载文件是空的");
                    downloadStatus = -1;
                    return downloadStatus;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            downloadStatus = 0;
            return downloadStatus;
        }
    }
}
