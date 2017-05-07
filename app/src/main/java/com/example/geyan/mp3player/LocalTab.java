package com.example.geyan.mp3player;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    MenuItem memu;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.local, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
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

                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

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
    }

    public ArrayList localMp3List(){
        FileUtil fileUtil = new FileUtil();
        mp3FilesList = fileUtil.getDownloadedMp3Files("mp3Folder/");
        Intent intent = getActivity().getIntent();
        isDeleted = intent.getBooleanExtra("isDeleted",false);
//        if (isDeleted) {
            for (Mp3Info mp3Info : mp3FilesList) {
                inidividualList = new HashMap<>();
                inidividualList.put("song_name", mp3Info.getMp3Name());
                inidividualList.put("song_size", mp3Info.getMp3Size());
                totalList.add(inidividualList);
                //do not add the same song name to remote list,but
                //do not check the last element because it is itself
                if (temp != 0) {
                    for (int i = 0; i < totalList.size()-1; i++) {
                        if (inidividualList.equals(totalList.get(i))) {
                            totalList.remove(i);
                        }
                    }
                }
                temp = temp + 1;
            }
//        }
        return totalList;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        memu = menu.add("refresh");
        memu = menu.add("sign out");
    }

    /**
     * 点击菜单中任意一个选项，都会触发此函数
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== 0){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }else if (item.getItemId()==1){
            Intent intent = new Intent();
            intent.setClass(getActivity(),MainActivity.class);
            intent.putExtra("islogin",false);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
