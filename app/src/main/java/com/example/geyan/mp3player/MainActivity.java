package com.example.geyan.mp3player;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.TabHost;

/**
 * Created by geyan on 30/04/2017.
 */

public class MainActivity extends TabActivity {

    /**
     *一个大的activity包含了两个activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置布局文件
        setContentView(R.layout.main);
        //设置remote的标签页
        TabHost tabHost = getTabHost();
        //生成一个Intent对象，指向另一个activity
        Intent remoteIntent = new Intent();
        remoteIntent.setClass(this,RemoteActivity.class);
        //生成一个tabspec对象，这个对象代表一页
        TabHost.TabSpec remoteSpec = tabHost.newTabSpec("remote");
        //设置图标
        Resources resources = getResources();
        remoteSpec.setIndicator("Remote1", resources.getDrawable(android.R.drawable.stat_sys_download));
        //设置该页的内容
        remoteSpec.setContent(remoteIntent);
        //添加tab
        tabHost.addTab(remoteSpec);

        //另一个页面的intent对象
        Intent localIntent = new Intent();
        localIntent.setClass(this,LocalMp3ListActivity.class);//activity之间的跳转
        //创建一个新的页面，图标和字
        TabHost.TabSpec localSpec = tabHost.newTabSpec("local");
        localSpec.setIndicator("Local1",resources.getDrawable(android.R.drawable.stat_sys_download));
        //设置该页的内容
        localSpec.setContent(localIntent);
        tabHost.addTab(localSpec);
    }
}
