package com.example.geyan.mp3player;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.geyan.access.DatabaseHelper;
import com.example.geyan.access.DatabaseSongs;
import com.example.geyan.model.LyricInfo;
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
    private List<Mp3Info> mp3InfoListDatabase = new ArrayList<>();
    private List<LyricInfo> lyricInfoList;
    private ArrayList<HashMap<String,String>> finalResult;
    private int temp = 0;
    private boolean isDeleted = false;
    private View view;
    private ListView listView;
    MenuItem memu;
    private LyricInfo singleLyricOne;
    private String username;
    private boolean isFirstTime = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.local, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        final Intent intent = getActivity().getIntent();
        username = intent.getStringExtra("user_name");
        listView = (ListView) view.findViewById(R.id.listViewlocal);
        finalResult = localMp3List();
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),finalResult,R.layout.mp3info,new String[]{"song_name","song_size"},
                new int[]{R.id.mp3_Name,R.id.mp3_Size});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mp3FilesList !=null){

//                    Intent intent1 = getActivity().getIntent();
                    boolean isFirst = intent.getBooleanExtra("isFirstPLayer",true);
                    Mp3Info isplaying_mp3Info = (Mp3Info) intent.getSerializableExtra("isPlayingMp3");
                    System.out.println("position "+position);
                    Mp3Info mp3Info = mp3InfoListDatabase.get(position);
                    System.out.println("INner "+mp3InfoListDatabase);
                    System.out.println("mp3Infi "+mp3Info.getMp3Name());
                    for (LyricInfo singleLyric:lyricInfoList){
                        if (mp3Info.getMp3Name().substring(0, mp3Info.getMp3Name().length()-4).
                                equals(singleLyric.getLycName().substring(0,singleLyric.getLycName().length()-4))){
                             singleLyricOne = singleLyric;
                        }
                    }
                    //传递一个对象，因为实现了sizalizable
                    intent.putExtra("mp3Info",mp3Info);
                    intent.putExtra("lyric",singleLyricOne);
                    intent.putExtra("user_name",username);
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
        lyricInfoList = fileUtil.getDownloadLrc("lyricFolder/");

        Intent intent = getActivity().getIntent();
        isDeleted = intent.getBooleanExtra("isDeleted",false);

        DatabaseSongs databaseSongs = new DatabaseSongs(getActivity());
        SQLiteDatabase db = databaseSongs.getReadableDatabase();
        Cursor cursor = db.query("songs",new String[]{"owner,song"},null,null,null,null,null);
        while (cursor.moveToNext()){
            String owner = cursor.getString(cursor.getColumnIndex("owner"));
            String songName = cursor.getString(cursor.getColumnIndex("song"));
            if (owner!=null){
            if (owner.equals(username)){
                for (Mp3Info mp3Info:mp3FilesList){
                    if (mp3Info.getMp3Name().equals(songName)){
                        mp3InfoListDatabase.add(mp3Info);
                        inidividualList = new HashMap<>();
                        inidividualList.put("song_name", mp3Info.getMp3Name());
                        inidividualList.put("song_size", mp3Info.getMp3Size());
                        totalList.add(inidividualList);
                        if (temp != 0) {
                            for (int i = 0; i < totalList.size()-1; i++) {
                                if (inidividualList.equals(totalList.get(i))) {
                                    totalList.remove(i);
                                }
                                if (mp3Info.getMp3Name().equals(mp3InfoListDatabase.get(i).getMp3Name())){
                                    mp3InfoListDatabase.remove(i);
                                }
                            }
                        }
                        temp = temp + 1;
                    }
                }
            }
        }
        }
        return totalList;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        memu = menu.add(0,1,1,R.string.refresh);
        memu = menu.add(0,2,2,R.string.sign_out);
    }

    /**
     * 点击菜单中任意一个选项，都会触发此函数
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== 1){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }else if (item.getItemId()==2){
            Intent intent = new Intent();
            intent.setClass(getActivity(),MainActivity.class);
            intent.putExtra("islogin",false);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
