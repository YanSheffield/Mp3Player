package com.example.geyan.download;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by geyan on 28/04/2017.
 */

public class HttpDownloader implements Runnable{

    private String line;
    private StringBuffer lineContent = new StringBuffer();
    private String urlStr;
    private Handler handler;

    public HttpDownloader(Handler handler,String urlStr){
        this.handler = handler;
        this.urlStr = urlStr;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        String text = getLineContent().toString();
        recieveMessage(text);
    }

    public void recieveMessage(String text) {
        Message message = handler.obtainMessage();
        message.obj = text;
        handler.sendMessage(message);
    }

    public StringBuffer getLineContent() {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //其实此时已经拿到了网上的内容，但是字节流的读取效率不高
            InputStream inputStream = urlConnection.getInputStream();
            //字节流转化为字符流，需要两步
            InputStream bufferedInputStream = new BufferedInputStream(inputStream);
            Reader reader = new InputStreamReader(bufferedInputStream);
            //一个字符还是太慢，想要一次读取一行
            BufferedReader bufferedReader = new BufferedReader(reader);
            while ((line = bufferedReader.readLine())!=null){
                lineContent.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineContent;
    }
}
