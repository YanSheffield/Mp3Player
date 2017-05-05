package com.example.geyan.mp3player;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.geyan.model.Mp3Info;
import com.example.geyan.util.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by geyan on 05/05/2017.
 */

public class LocalTab extends Fragment {
    private ArrayList<HashMap<String,String>> totalList = new ArrayList<>();
    private HashMap<String,String> inidividualList;
    private List<Mp3Info> mp3FilesList;
    private ArrayList finalResult;
    private int temp = 0;
    private boolean isDeleted = false;
    private View view;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.local, container, false);
        System.out.println("onCreateview");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("ontart");
        listView = (ListView) view.findViewById(R.id.listViewlocal);
        finalResult = localMp3List();
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),finalResult,R.layout.mp3info,new String[]{"song_name","song_size"},
                new int[]{R.id.mp3_Name,R.id.mp3_Size});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mp3FilesList !=null){
                    Mp3Info mp3Info = mp3FilesList.get(position);
                    Intent intent = new Intent();
                    //传递一个对象，因为实现了sizalizable
                    intent.putExtra("mp3Info",mp3Info);
                    intent.setClass(getActivity(),PlayerActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onresume");

    }

    public ArrayList localMp3List(){
        FileUtil fileUtil = new FileUtil();
        mp3FilesList = fileUtil.getDownloadedMp3Files("mp3Folder/");
        Intent intent = getActivity().getIntent();
        isDeleted = intent.getBooleanExtra("isDeleted",false);
        if (isDeleted == false) {
            for (Mp3Info mp3Info : mp3FilesList) {
                inidividualList = new HashMap<>();
                inidividualList.put("song_name", mp3Info.getMp3Name());
                inidividualList.put("song_size", mp3Info.getMp3Size());
                totalList.add(inidividualList);
                System.out.println("totoal " + totalList);
                if (temp != 0) {
                    for (int i = 0; i < totalList.size(); i++) {
                        if (inidividualList.equals(totalList.get(i))) {
                            totalList.remove(i);
                        }
                    }
                }
                temp = temp + 1;
            }
        }
        return totalList;
    }
}
