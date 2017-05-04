package com.example.geyan.mp3player;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

public class RemoteActivity extends ListActivity{

    private static final int UPDATE = 1;
    private static final int ABOUT = 2;
    private Handler handler;
    private String XMLHttp = "https://raw.githubusercontent.com/YanSheffield/mp3Service/master/resources.xml";
    private HttpDownloader httpDownloader = null;
    private List<Mp3Info> infos = new ArrayList<>();
    private static final int PERMISSION_REQUEST_CODE = 1;
    private int temp = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote);
        handler = new MyHandler();
        httpDownloader = new HttpDownloader(handler,XMLHttp);
    }

    /**
     * 用户点击menu之后会调用该方法，
     * 我们可以在这个方法中加入自己的按钮空间
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,UPDATE,1,R.string.mp3List_upadte);
        menu.add(0,ABOUT,2,R.string.mp3List_about);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 点击菜单中任意一个选项，都会触发此函数
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== UPDATE){
            //点击更新按钮
            httpDownloader = new HttpDownloader(handler,XMLHttp);
        }else if (item.getItemId()==ABOUT){

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        System.out.println("position " + position + " id " + id);
        //把mp3info对象传到service中，类似于activity之间传输数据
        //TO DO 为什么service？他的优先级较高，即使这个程序被关闭，下载仍可以继续，这个符合要求的
        //根据用户点击，得到这个mp3对象
        Mp3Info singleMp3info = infos.get(position);
            //创建一个intent对象
            Intent intent = new Intent();
            //将mp3info对象存入到intent中
            intent.putExtra("mp3Info", singleMp3info);
            intent.setClass(RemoteActivity.this, DownloadService.class);
            //启动service
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermisson()) {
                    startService(intent);
                } else {
                    requestPermission();
                }
            }

    }
    private boolean checkPermisson(){
        int result = ContextCompat.checkSelfPermission(RemoteActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else
            return false;
    }
    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(RemoteActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(RemoteActivity.this,"Write External Storage permission allows us to do store images",Toast.LENGTH_LONG).show();
        }else {
            ActivityCompat.requestPermissions(RemoteActivity.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    //当从loop中取值的时候调用
     class MyHandler extends Handler{
        private String StrXMl = null;

        private ArrayList<HashMap<String,String>> list = new ArrayList<>();
        private HashMap<String,String> map;

        @Override
        public void handleMessage(Message msg){
            this.StrXMl = (String) msg.obj;
            System.out.println(StrXMl);
            analyzeXML();
            updateList(list);
        }

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

        private void updateList(ArrayList<HashMap<String, String>> list) {
//            for(Iterator iterator = infos.iterator(); iterator.hasNext();){
//                Mp3Info mp3Info = (Mp3Info) iterator.next();
//                map = new HashMap<>();
//                map.put("song_name",mp3Info.getMp3Name());
//                map.put("song_size",mp3Info.getMp3Size());
//                list.add(map);
//            }
            SimpleAdapter listAdapter = getSimpleAdapter(list,infos);
            setListAdapter(listAdapter);
        }

        public SimpleAdapter getSimpleAdapter(ArrayList<HashMap<String, String>> list,List<Mp3Info> infos) {
            for(Mp3Info mp3Info:infos) {
                map = new HashMap<>();
                map.put("song_name", mp3Info.getMp3Name());
                map.put("song_size", mp3Info.getMp3Size());
                list.add(map);
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
            return new SimpleAdapter(RemoteActivity.this,list, R.layout.mp3info, new String[]{"song_name","song_size"},
                    new int[]{R.id.mp3_Name,R.id.mp3_Size});
        }
    }
}
