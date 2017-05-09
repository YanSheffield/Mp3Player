package com.example.geyan.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.widget.Toast;

import com.example.geyan.download.HttpDownloader;
import com.example.geyan.download.Mp3Downloader;
import com.example.geyan.model.Mp3Info;
import com.example.geyan.mp3player.R;
import com.example.geyan.util.FileUtil;

import java.io.File;


/**
 * Created by geyan on 29/04/2017.
 */

public class DownloadService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //根据存入的id取出mp3对象
        Mp3Info singleMp3info = (Mp3Info) intent.getSerializableExtra("mp3Info");
        DownloadThread downloadThread = new DownloadThread(singleMp3info);
        Thread thread = new Thread(downloadThread);
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    class DownloadThread implements Runnable{
        private Mp3Info singlemp3Info;

        DownloadThread(Mp3Info singlemp3Info){
            this.singlemp3Info = singlemp3Info;
        }
        @Override
        public void run() {
            Mp3Downloader mp3Downloader = new Mp3Downloader();
            for(int i = 0;i<2;i++){
                if (i == 0){
                    System.out.println("downMp3");
                    String mp3Url = singlemp3Info.getMp3Link();
                    mp3Downloader.downloadMp3File(mp3Url,"mp3Folder",singlemp3Info.getMp3Name());
                }else {
                    System.out.println("downLrc");
                    String mp3Url = singlemp3Info.getLrcLink();
                    System.out.println("downLrc"+singlemp3Info.getLrcLink());
                    mp3Downloader.downloadMp3File(mp3Url,"mp3Folder",singlemp3Info.getIrcName());
                }
            }

//            if (downloadStatus == -1){
//                Toast.makeText(getApplicationContext(),R.string.downloadStatus_failure,Toast.LENGTH_SHORT).show();
//            }else if (downloadStatus == 1){
//                Toast.makeText(getApplicationContext(),R.string.downloadStatus_success,Toast.LENGTH_SHORT).show();
//            }else if (downloadStatus == 0){
//                Toast.makeText(getApplicationContext(),R.string.downloadStatus_depulicated,Toast.LENGTH_SHORT).show();
//            }
        }
    }
}
