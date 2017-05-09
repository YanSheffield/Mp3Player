package com.example.geyan.mp3player;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geyan.lrc.LrcProcessor;
import com.example.geyan.model.LyricInfo;
import com.example.geyan.model.Mp3Info;
import com.example.geyan.service.PlayerService;
import com.example.geyan.util.FileUtil;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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
    private long begin = 0;
    private boolean ispause = false;
    private long pauseTime = 0;
    private long pausesartTime = 0;
    private long diff = 0;
    private List<Mp3Info> mp3FilesList;
    private List<LyricInfo> lyricInfoList;
    private boolean isDeleted = false;
    private SeekBar seekBar;
    private PlayerService playerService;


    private MediaPlayer mediaPlayer = null;
    private String SDCARD = null;
    private boolean isplaying = false;
    private boolean isPause = false;
    private boolean isRelease = false;
    private Mp3Info singleInfo;
    private LyricInfo singleLyric;
    private Handler handler = new Handler();
    private Runnable runnable;
    private SeekBarEdition seekBarEdition;
    private boolean isFirstStart = true;
    private TextView songName;
    private boolean isStopThread = false;
    private TextView processTime;

    static PlayerActivity activityPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        final Intent intent = getIntent();
        SDCARD = Environment.getExternalStorageDirectory()+"/";
        singleInfo = (Mp3Info) intent.getSerializableExtra("mp3Info");
        singleLyric = (LyricInfo) intent.getSerializableExtra("lyric");
        pauseButton = (ImageButton) findViewById(R.id.pausebtn);
        lyricTextview = (TextView) findViewById(R.id.lyricText);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        pauseButton.setBackgroundResource(R.drawable.start);
        processTime = (TextView) findViewById(R.id.processTime);
        songName = (TextView) findViewById(R.id.songName);
        songName.setText(singleInfo.getMp3Name());
        activityPlayer = this;

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isplaying==true){
                    pauseButton.setBackgroundResource(R.drawable.start);
                    pausePlay();
                    pauseTime = System.currentTimeMillis();
                }else {
                    if (!isFirstStart){
                        diff = System.currentTimeMillis() - pauseTime;
                    }
                    if (isFirstStart){
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse("file://"+getMp3Path()));
                        begin = System.currentTimeMillis();
                        seekBarEdition = new SeekBarEdition();
//                        updateTimeCallback = new UpdateTimeCallback();
                        isFirstStart = false;
                    }
                    pauseButton.setBackgroundResource(R.drawable.pause);
                    pausePlay();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==2){
            finish();
        }
        if (item.getItemId()==1){
//            PlayerService playerService = new PlayerService();
//            playerService.stopPlay();
            //TODO: the first mp3 will be deleted whatever you clicked
            File file = new File(getMp3Path());
            File lyricFile = new File(getMp3Path());
            file.delete();
            lyricFile.delete();
            isplaying = false;
            Intent intent = new Intent();
            intent.putExtra("isDeleted",true);
            intent.putExtra("islogin",true);
            intent.putExtra("user_name",singleInfo.getMp3Name());

            Toast.makeText(this,"finish delete",Toast.LENGTH_SHORT).show();
            intent.setClass(this,MainActivity.class);
            startActivity(intent);
            mediaPlayer.stop();
           mediaPlayer.release();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //添加menu，不需要操作界面
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0,1,1,R.string.mp3List_delete);
        menu.add(0,2,2, R.string.mp3List_about);
        return super.onCreateOptionsMenu(menu);
    }

    public static PlayerActivity getInstance(){
        return activityPlayer;
    }

    public void pausePlay() {
        if (mediaPlayer!=null){
            if (!isRelease){
                if (isplaying){
                    if (!isPause){
                        isPause = true;
                        isplaying = false;
                        mediaPlayer.pause();
                    }
                }else {
                    isPause = false;
                    isplaying = true;
                    mediaPlayer.start();
                    seekBarEdition.playCycle();
                }
            }
        }
    }


    public String getMp3Path(){
        String filePath = SDCARD + "mp3Folder" +"/"+ singleInfo.getMp3Name();
        return filePath;
    }
    public String getLyricPath(){
        String lyricPath = SDCARD+ "lyricFolder"+"/"+singleLyric.getLycName();
        return lyricPath;
    }
//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("isFirstPLayer",false);
//        intent.putExtra("isPlayingMp3",singleInfo);
//        startActivity(intent);
//    }

    class SeekBarEdition{

        public SeekBarEdition(){

            updateTimeCallback = new UpdateTimeCallback();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    if (isplaying)
                    playCycle();
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser){
                        mediaPlayer.seekTo(progress);
                    }
                    fromMill2Sec(progress);
                    updateTimeCallback.setProcess(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        public void fromMill2Sec(int progress) {
            int minites = (progress/1000)/60;
            int seconds = (progress/1000);
            if (seconds>=60){
                seconds = seconds - (minites*60);
            }
            if (seconds<10){
                processTime.setText(minites+":0"+seconds);
            }else {
                processTime.setText(minites+":"+seconds);
            }
        }

        public void playCycle(){
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            if (isplaying){
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (isplaying){
                            playCycle();
                        }
                    }
                };
                handler.postDelayed(runnable,1000);
            }
        }
    }

    class UpdateTimeCallback implements Runnable{
        private LyricHandler lyricHandler = new LyricHandler();
        private long currentTimeMill = 0;
        private long nextTimeMill = 0;
        private String lyricPoll;
        private LrcProcessor lrcProcessor = new LrcProcessor();
        private ArrayList<Queue> lyricQueues;
//        long offset;
        private boolean firstLyric = true;

        private int process;

        UpdateTimeCallback(){

        }

        public int getProcess() {
            return process;
        }

        public void setProcess(int process) {
            this.process = process;
            if (firstLyric){
                Thread thread = new Thread(this);
                thread.start();
                firstLyric = false;
            }
        }

        public void updateLyricView(ArrayList<Queue> lyricQueues){
            Queue timeQueue = lyricQueues.get(0);
            Queue lyricQueue = lyricQueues.get(1);
            /**
             * This is because the element in the queue won't
             * be removed unless the next element is selected.
             * It causes that no element is available although this queue isn't null.
             */
//            while (timeQueue != null && lyricQueue!= null){
            while (timeQueue.size()>= 1 && lyricQueue.size()>= 1){

//                offset = System.currentTimeMillis() - begin - diff;
//                ispause = false;
                if (currentTimeMill == 0 && isplaying==true){
                    nextTimeMill = (long) timeQueue.poll();
                    lyricPoll = (String) lyricQueue.poll();
                    Message lyricMessage = lyricHandler.obtainMessage();
                    lyricMessage.obj = lyricPoll;
                    lyricHandler.sendMessage(lyricMessage);
                    currentTimeMill = currentTimeMill+10;
                }
                if (process >= nextTimeMill&& isplaying==true){
                    Message lyricMessage = lyricHandler.obtainMessage();
                    lyricMessage.obj = lyricPoll;
                    lyricHandler.sendMessage(lyricMessage);
                    nextTimeMill = (long) timeQueue.poll();
                    lyricPoll = (String) lyricQueue.poll();
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            try {
                File lyric = new File(getLyricPath());
                InputStream stream = new FileInputStream(lyric);
                lyricQueues = lrcProcessor.processLrc(stream);
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


