package com.example.geyan.lrc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by geyan on 02/05/2017.
 */

public class LrcProcessor {

    private StringBuffer lineContent = new StringBuffer();

    public ArrayList<Queue> processLrc(InputStream inputStream) throws IOException {
        //store the start time
        Queue<Long> timeMills = new LinkedList<>();
        //store the corresponding lyric
        Queue<String> lyrics = new LinkedList<>();
        //store above both Queues into Arraylist
        ArrayList<Queue> queues = new ArrayList<>();

        InputStream bufferedInputStream = new BufferedInputStream(inputStream);
        Reader reader = new InputStreamReader(bufferedInputStream);
        BufferedReader bufferedReadLine = new BufferedReader(reader);
//        String line;
//        while ((line = bufferedReadLine.readLine())!=null){
//            lineContent.append(line);
//        }
//        bufferedReadLine.close();
        String temp = null;
        int i = 0;
        //craete a Regulation express to seek "[]"
        Pattern regExp = Pattern.compile("\\[([^\\]]+)\\]");
        String result = null;
        boolean b = true;
        //TODO:the last lyric cannot be accessed
        while ((temp = bufferedReadLine.readLine())!=null){
            i++;
            Matcher matcher = regExp.matcher(temp);
            if (matcher.find()){
                if (result!=null){
                    lyrics.add(result);
                }
                //return the result matched regulation express
                String timeStr = matcher.group();
                Long timeMill = time2Long(timeStr.substring(1,timeStr.length()-1));
                if (b){
                    //add time to Queue
                    timeMills.offer(timeMill);
                }
                //obtain lyric
                String msg = temp.substring(10);
                result = "" +msg + "\n";
            }else {
                result = result + temp + "\n";
                lyrics.add(result);
            }
//            lyrics.add(result);
            //add two queues to an arraylist.one for time,and one for lyric
            queues.add(timeMills);
            queues.add(lyrics);
        }
        return queues;
    }

    public long time2Long(String timeStr){
        String[] s = timeStr.split(":");
        int min = Integer.parseInt(s[0]);
        String ss[] = s[1].split("\\.");
        int sec = Integer.parseInt(ss[0]);
        int mill = Integer.parseInt(ss[1]);
        return min * 60 * 1000 + sec * 1000 + mill * 10l;
    }
}
