package com.example.geyan.mp3player;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.geyan.model.Mp3Info;
import com.example.geyan.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by geyan on 30/04/2017.
 */

public class LocalMp3ListActivity extends ListActivity {

    private ArrayList<HashMap<String,String>> totalList = new ArrayList<>();
    private HashMap<String,String> inidividualList;
    private List<Mp3Info> mp3FilesList;
    private int temp = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local);

    }

    @Override
    protected void onResume() {
        super.onResume();
        localMp3List();
        System.out.println("onresume");
    }

    public void localMp3List(){
        FileUtil fileUtil = new FileUtil();
        mp3FilesList = fileUtil.getDownloadedMp3Files("mp3Folder/");
        for (Mp3Info mp3Info:mp3FilesList){
            inidividualList = new HashMap<>();
            inidividualList.put("song_name",mp3Info.getMp3Name());
            inidividualList.put("song_size",mp3Info.getMp3Size());
            System.out.println("111111");
            totalList.add(inidividualList);
            System.out.println("totoal "+totalList);
            if(temp!=0) {
                for (int i=0;i<totalList.size();i++){
                    if (inidividualList.equals(totalList.get(i))){
                        totalList.remove(i);
                    }
                }
            }
            temp = temp +1;
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(LocalMp3ListActivity.this,totalList,
                R.layout.mp3info,new String[]{"song_name","song_size"},new int[]{R.id.mp3_Name,R.id.mp3_Size} );
        setListAdapter(simpleAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (mp3FilesList !=null){
            Mp3Info mp3Info = mp3FilesList.get(position);
            Intent intent = new Intent();
            //传递一个对象，因为实现了sizalizable
            intent.putExtra("mp3Info",mp3Info);
            intent.setClass(LocalMp3ListActivity.this,PlayerActivity.class);
            startActivity(intent);
        }
    }
}
