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
import com.example.geyan.model.Mp3Info;
import com.example.geyan.service.PlayerService;


import java.io.File;
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
    private LyricHandler lyricHandler = new LyricHandler();
    private long begin = 0;
    private boolean ispause = false;
    private long pauseTime = 0;
    private long pausesartTime = 0;
    private long diff = 0;
    private List<Mp3Info> mp3FilesList;
    private boolean isDeleted = false;
    private SeekBar seekBar;
    private PlayerService playerService;


    private MediaPlayer mediaPlayer = null;
    private String SDCARD = null;
    private boolean isplaying = false;
    private boolean isPause = false;
    private boolean isRelease = false;
    private Mp3Info singleInfo;
    private Handler handler = new Handler();
    private Runnable runnable;
    private SeekBarEdition seekBarEdition;
    private boolean isFirstStart = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        final Intent intent = getIntent();
        singleInfo = (Mp3Info) intent.getSerializableExtra("mp3Info");
//        beginButton = (ImageButton) findViewById(R.id.startbtn);
//        endButton = (ImageButton) findViewById(R.id.stoptbtn);
        pauseButton = (ImageButton) findViewById(R.id.pausebtn);
        lyricTextview = (TextView) findViewById(R.id.lyricText);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        pauseButton.setBackgroundResource(R.drawable.start);

        System.out.println("oncreate====");

//        beginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse("file://"+getMp3Path()));
//                updateTimeCallback = new UpdateTimeCallback(lyricHandler);
//                begin = System.currentTimeMillis();
//                startPlay();
//                seekBarEdition = new SeekBarEdition();
//            }
//        });

//        endButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopPlay();
//            }
//        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isplaying==true){
                    pauseButton.setBackgroundResource(R.drawable.start);
                    pausePlay();
                    pauseTime = System.currentTimeMillis();
                }else {
                    if (isFirstStart){
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse("file://"+getMp3Path()));
                        updateTimeCallback = new UpdateTimeCallback(lyricHandler);
                        begin = System.currentTimeMillis();
                        seekBarEdition = new SeekBarEdition();
                        isFirstStart = false;
                    }
                    pauseButton.setBackgroundResource(R.drawable.pause);
                    pausePlay();
                    ispause = false;
                    isplaying = true;
                    pausesartTime = System.currentTimeMillis();
                    diff = pausesartTime-pauseTime;
                }
            }
        });
    }
//    @Override
//    public void onBackPressed() {
//        this.moveTaskToBack(true);
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==2){
            finish();
        }
        if (item.getItemId()==1){
            PlayerService playerService = new PlayerService();
            playerService.stopPlay();
            //TODO: the first mp3 will be deleted whatever you clicked
            File file = new File(getMp3Path());
            file.delete();
            Intent intent = new Intent();
            intent.putExtra("isDeleted",true);
            intent.putExtra("islogin",true);
            Toast.makeText(this,"finish delete",Toast.LENGTH_SHORT).show();
            intent.setClass(this,MainActivity.class);
            startActivity(intent);
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



    public void startPlay() {
        if(!isplaying){
            mediaPlayer.setLooping(false);
            isplaying = true;
            isRelease = false;
            isPause = false;
            mediaPlayer.start();
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
        SDCARD = Environment.getExternalStorageDirectory()+"/";
        String filePath = SDCARD + "mp3Folder" +"/"+ singleInfo.getMp3Name();
        return filePath;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    class SeekBarEdition{

        public SeekBarEdition(){
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
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        public void playCycle(){
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            if (isplaying){
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        playCycle();
                    }
                };
                handler.postDelayed(runnable,1000);
            }
        }
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

        public void updateLyricView(ArrayList<Queue> lyricQueues){
            Queue timeQueue = lyricQueues.get(0);
            Queue lyricQueue = lyricQueues.get(1);

            int i = 0;
            /**
             * This is because the element in the queue won't
             * be removed unless the next element is selected.
             * It causes that no element is available although this queue isn't null.
             */
//            while (timeQueue != null && lyricQueue!= null){
            while (timeQueue.size()>= 1 && lyricQueue.size()>= 1){
                offset = System.currentTimeMillis() - begin - diff;
                ispause = false;
                if (currentTimeMill == 0&& isplaying==true){
                    nextTimeMill = (long) timeQueue.poll();
                    lyricPoll = (String) lyricQueue.poll();
                    Message lyricMessage = lyricHandler.obtainMessage();
                    lyricMessage.obj = lyricPoll;
                    lyricHandler.sendMessage(lyricMessage);
                }
                if (offset >= nextTimeMill&& isplaying==true){
                    Message lyricMessage = lyricHandler.obtainMessage();
                    lyricMessage.obj = lyricPoll;
                    lyricHandler.sendMessage(lyricMessage);
                    nextTimeMill = (long) timeQueue.poll();
                    lyricPoll = (String) lyricQueue.poll();
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


