package com.example.geyan.mp3player;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.geyan.download.HttpDownloader;
import com.example.geyan.model.Mp3Info;
import com.example.geyan.service.DownloadService;
import com.example.geyan.xml.Mp3ListContentHandler;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by geyan on 05/05/2017.
 */

public class RemoteTab extends Fragment {

    private static final int UPDATE = 1;
    private static final int ABOUT = 2;
    private Handler handler;
    private String XMLHttp = "https://raw.githubusercontent.com/YanSheffield/mp3Service/master/resources.xml";
    private HttpDownloader httpDownloader = null;
    private List<Mp3Info> infos = new ArrayList<>();
    private static final int PERMISSION_REQUEST_CODE = 1;
    private int temp = 1;
    private ListView listView = null;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.remote, container, false);
        listView = (ListView) view.findViewById(R.id.listViewRemote);
        handler = new MyHandler();
        httpDownloader = new HttpDownloader(handler,XMLHttp);
        return view;
    }

        private boolean checkPermisson(){
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else
            return false;
        }
        private void requestPermission(){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(getActivity(),"Write External Storage permission allows us to do store images",Toast.LENGTH_LONG).show();
            }else {
                ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }

    //当从loop中取值的时候调用
     class MyHandler extends Handler{
        private String StrXMl = null;

        private ArrayList<HashMap<String,String>> list = new ArrayList<>();
        private HashMap<String,String> map;
        private ArrayList finalResult = null;
//
        public ArrayList getFinalResult() {
            return finalResult;
        }

        public void setFinalResult(ArrayList finalResult) {
            this.finalResult = finalResult;
        }
//
        @Override
        public void handleMessage(Message msg){
            Intent intent = getActivity().getIntent();
            final boolean isLogin = intent.getBooleanExtra("islogin",false);
            this.StrXMl = (String) msg.obj;
            analyzeXML();
            finalResult = getListViewContent(list,infos);
            setFinalResult(finalResult);
            SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),finalResult,R.layout.mp3info,new String[]{"song_name","song_size"},
                    new int[]{R.id.mp3_Name,R.id.mp3_Size});
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    if (isLogin==true){
                        startDownload(position, id);
//                    }else {
//                        Toast.makeText(getActivity(),"Please login first",Toast.LENGTH_LONG).show();
//                    }
                }
            });
        }

        public void startDownload(int position, long id) {
            //把mp3info对象传到service中，类似于activity之间传输数据
            //TO DO 为什么service？他的优先级较高，即使这个程序被关闭，下载仍可以继续，这个符合要求的
            //根据用户点击，得到这个mp3对象
            Mp3Info singleMp3info = infos.get(position);
            //创建一个intent对象
            Intent intent = new Intent(getActivity(),DownloadService.class);
            //将mp3info对象存入到intent中
            intent.putExtra("mp3Info", singleMp3info);
            intent.setClass(getActivity(), DownloadService.class);
            //启动service
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermisson()) {
                    getActivity().startService(intent);
                } else {
                    requestPermission();
                }
            }
        }

        //
        private void analyzeXML() {
            //创建SAXParserFactory
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            try {
                XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
                Mp3ListContentHandler mp3ListContentHandler = new Mp3ListContentHandler(infos);
                //相当于是自定义一个处理xml文件的类
                xmlReader.setContentHandler(mp3ListContentHandler);
                xmlReader.parse(new InputSource(new StringReader(StrXMl)));
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public ArrayList getListViewContent(ArrayList<HashMap<String, String>> list,List<Mp3Info> infos){
            for(Mp3Info mp3Info:infos) {
                map = new HashMap<>();
                map.put("song_name", mp3Info.getMp3Name());
                map.put("song_size", mp3Info.getMp3Size());
                list.add(map);
                //do not add the same song name to remote list
                if (temp != 1) {
                    for (int i = 0; i < list.size()-1; i++) {
                        if (map.equals(list.get(i))) {
                            list.remove(i);
                        }
                    }
                }
                temp = temp+1;
            }
            this.list = list;
            return list;
        }
    }
}

