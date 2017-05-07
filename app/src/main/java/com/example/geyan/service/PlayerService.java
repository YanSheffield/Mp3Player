package com.example.geyan.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.geyan.model.Mp3Info;
import com.example.geyan.mp3player.PlayerActivity;
import com.example.geyan.util.AppConstant;

/**
 * Created by geyan on 01/05/2017.
 */

public class PlayerService extends Service{

    private MediaPlayer mediaPlayer = null;
    private String SDCARD = null;
    private boolean isplaying = false;
    private boolean isPause = false;
    private boolean isRelease = false;
    private Mp3Info singleInfo;

    private SeekBar seekBar = null;
    private Handler handler = new Handler();
    Runnable runnable;
    private int i;

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        System.out.println("----second "+i);
        //从activity中得到mp3INfo对象
        singleInfo = (Mp3Info) intent.getSerializableExtra("mp3Info");
        int MSG = intent.getIntExtra("MSG",0);

        if (MSG == AppConstant.PlayMessage.PLAY_MSG){
            startPlay();
        }else if (MSG == AppConstant.PlayMessage.PAUSE_MSG){
            pausePlay();

        }else if (MSG == AppConstant.PlayMessage.STOP_MSG){
            stopPlay();
        }
        return super.onStartCommand(intent,flags,startId);
    }

    public void startPlay() {
        if(!isplaying){
            mediaPlayer = MediaPlayer.create(PlayerService.this, Uri.parse("file://"+getMp3Path()));
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
            isplaying = true;
            isRelease = false;
            isPause = false;
        }
    }

    public void stopPlay() {
        if (mediaPlayer!=null){
            if (isplaying){
                if (!isRelease){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    isRelease = true;
                    isplaying = false;
                }
            }
        }
    }

    public void pausePlay() {
        if (mediaPlayer!=null){
            if (!isRelease){
                if (isplaying){
                    if (!isPause){
                        mediaPlayer.pause();
                        isPause = true;
                        isplaying = false;
                    }
                }else {
                    mediaPlayer.start();
                    isPause = false;
                    isplaying = true;
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String getMp3Path(){
        SDCARD = Environment.getExternalStorageDirectory()+"/";
        String filePath = SDCARD + "mp3Folder" +"/"+ singleInfo.getMp3Name();
        return filePath;
    }
}
