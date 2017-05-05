package com.example.geyan.mp3player;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geyan.lrc.LrcProcessor;
import com.example.geyan.model.Mp3Info;
import com.example.geyan.service.PlayerService;
import com.example.geyan.util.AppConstant;
import com.example.geyan.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * Created by geyan on 30/04/2017.
 */

public class PlayerActivity extends AppCompatActivity {

    private ImageButton beginButton = null;
    private ImageButton endButton = null;
    private ImageButton pauseButton = null;
    private TextView lyricTextview = null;
    private String lyricHttp = "https://raw.githubusercontent.com/YanSheffield/mp3Service/master/first-text.lrc";
    private UpdateTimeCallback updateTimeCallback;
    private Mp3Info singleInfo;
    private LyricHandler lyricHandler = new LyricHandler();
    private long begin = 0;
    private boolean isplaying = false;
    private boolean ispause = false;
    private long pauseTime = 0;
    private long pausesartTime = 0;
    private long diff = 0;
    private List<Mp3Info> mp3FilesList;
    private boolean isDeleted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        Intent intent = getIntent();
        singleInfo = (Mp3Info) intent.getSerializableExtra("mp3Info");
        beginButton = (ImageButton) findViewById(R.id.startbtn);
        endButton = (ImageButton) findViewById(R.id.stoptbtn);
        pauseButton = (ImageButton) findViewById(R.id.pausebtn);
        lyricTextview = (TextView) findViewById(R.id.lyricText);
        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isplaying = true;
                Intent intentInner = new Intent();
                intentInner.putExtra("MSG",AppConstant.PlayMessage.PLAY_MSG);
                intentInner.putExtra("mp3Info",singleInfo);
                intentInner.setClass(PlayerActivity.this, PlayerService.class);
                startService(intentInner);
                updateTimeCallback = new UpdateTimeCallback(lyricHandler);
                begin = System.currentTimeMillis();
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isplaying = false;
                Intent intentInner = new Intent();
                intentInner.putExtra("MSG",AppConstant.PlayMessage.STOP_MSG);
                intentInner.setClass(PlayerActivity.this, PlayerService.class);
                startService(intentInner);
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isplaying==true){
                    isplaying = false;
                    pauseTime = System.currentTimeMillis();
                }else {
                    ispause = true;
                    isplaying = true;
                    pausesartTime = System.currentTimeMillis();
                    diff = pausesartTime-pauseTime;
                }
                Intent intentInner = new Intent();
                intentInner.putExtra("MSG",AppConstant.PlayMessage.PAUSE_MSG);
                intentInner.setClass(PlayerActivity.this, PlayerService.class);
                startService(intentInner);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==2){
            finish();
        }
        if (item.getItemId()==1){
//            FileUtil fileUtil = new FileUtil();
//            mp3FilesList = fileUtil.getDownloadedMp3Files("mp3Folder/");
//            System.out.println("DDDDD "+mp3FilesList);
//            mp3FilesList.remove(0);
//            System.out.println("TTTTTT "+mp3FilesList);
//            System.out.println("00000"+FileUtil.finalpath);
            File file = new File(Environment.getExternalStorageDirectory()+"/"+"mp3Folder"+"/first-mp3.mp3");
            file.delete();
            Intent intent = new Intent();
            intent.putExtra("isDeleted",true);
            Toast.makeText(this,"finish delete",Toast.LENGTH_SHORT).show();
            intent.setClass(this,MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        if (item.getItemId()==2){
//            finish();
//        }
//        if (item.getItemId()==1){
////            FileUtil fileUtil = new FileUtil();
////            mp3FilesList = fileUtil.getDownloadedMp3Files("mp3Folder/");
////            System.out.println("DDDDD "+mp3FilesList);
////            mp3FilesList.remove(0);
////            System.out.println("TTTTTT "+mp3FilesList);
////            System.out.println("00000"+FileUtil.finalpath);
//            File file = new File(Environment.getExternalStorageDirectory()+"/"+"mp3Folder"+"/first-mp3.mp3");
//            file.delete();
//            Intent intent = new Intent();
//            intent.putExtra("isDeleted",true);
//            Toast.makeText(this,"finish delete",Toast.LENGTH_SHORT).show();
//            intent.setClass(this,MainActivity.class);
//            startActivity(intent);
//        }
//        return super.onOptionsItemSelected(item);
//    }


    //添加menu，不需要操作界面
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0,1,1,R.string.mp3List_delete);
        menu.add(0,2,2, R.string.mp3List_about);
        return super.onCreateOptionsMenu(menu);
    }

    class UpdateTimeCallback implements Runnable{

        private long currentTimeMill = 0;
        private long nextTimeMill = 0;
        private String lyricPoll;
        private LrcProcessor lrcProcessor = new LrcProcessor();
        private ArrayList<Queue> lyricQueues;
        private LyricHandler lyricHandler;
        long offset;

        UpdateTimeCallback(LyricHandler lyricHandler){
            this.lyricHandler = lyricHandler;
            Thread thread = new Thread(this);
            thread.start();
        }

        //TODO:handle post change the interface
        public void updateLyricView(ArrayList<Queue> lyricQueues){
            Queue timeQueue = lyricQueues.get(0);
//            System.out.println("time QU "+timeQueue);
            Queue lyricQueue = lyricQueues.get(1);
//            System.out.println("initial "+lyricQueue+ "lyric size "+lyricQueue.size()+" time size "+timeQueue.size());

            int i = 0;
            /**
             * This is because the element in the queue won't
             * be removed unless the next element is selected.
             * It causes that no element is available although this queue isn't null.
             */
//            while (timeQueue != null && lyricQueue!= null){
            while (timeQueue.size()>= 1 && lyricQueue.size()>= 1){
//                if (ispause == true){
                     offset = System.currentTimeMillis() - begin - diff;
//                    System.out.println("diff "+diff);
                    ispause = false;
//                    System.out.println("-------"+offset);
//                }else {
//                     offset = System.currentTimeMillis() - begin;
//                }
                if (currentTimeMill == 0&& isplaying==true){
                    nextTimeMill = (long) timeQueue.poll();
                    lyricPoll = (String) lyricQueue.poll();
                    Message lyricMessage = lyricHandler.obtainMessage();
                    lyricMessage.obj = lyricPoll;
                    lyricHandler.sendMessage(lyricMessage);
                }
//                    System.out.println("offset " +offset+ " next Time mill  "+nextTimeMill);
                if (offset >= nextTimeMill&& isplaying==true){
                    Message lyricMessage = lyricHandler.obtainMessage();
                    lyricMessage.obj = lyricPoll;
                    lyricHandler.sendMessage(lyricMessage);
                    nextTimeMill = (long) timeQueue.poll();
                    lyricPoll = (String) lyricQueue.poll();
//                    System.out.println("2222"+lyricPoll);
                }
                currentTimeMill = currentTimeMill+10;
            }
        }

        @Override
        public void run() {
            try {
                URL lyric_url = new URL("https://raw.githubusercontent.com/YanSheffield/mp3Service/master/first-text.lrc");
                HttpURLConnection urlConnection = (HttpURLConnection) lyric_url.openConnection();
                InputStream lyric_inputStream = urlConnection.getInputStream();
                lyricQueues = lrcProcessor.processLrc(lyric_inputStream);
                if (lyricQueues!=null){
                    updateLyricView(lyricQueues);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class LyricHandler extends Handler{

        @Override
        public void handleMessage(Message msg){
            lyricTextview.setText((String)msg.obj);
        }
    }

}


