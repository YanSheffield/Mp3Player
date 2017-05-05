package com.example.geyan.xml;

import com.example.geyan.model.Mp3Info;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

/**
 * Created by geyan on 29/04/2017.
 */

public class Mp3ListContentHandler extends DefaultHandler {

    private List<Mp3Info> infos = null;
    private Mp3Info mp3Info = null;

    public Mp3ListContentHandler(List<Mp3Info> infos) {
        this.infos = infos;
    }

    private String tagName = null;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    /**每解析一个标签的内容都要调用一次，
     * 解析完成之后要把resource标签里面的内容加到List当中去
     * @throws SAXException
     */
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();

    }
    //每解析一个标签的内容都要调用一次，
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        //每一个resource里面的指都是mp3Info对象，tagName就是xml里面的一个tag
        this.tagName = localName;
        if (tagName.equals("resource")){
            //生成一个mp3对象
            mp3Info = new Mp3Info();
        }
    }
    // TODO: 29/04/2017  为什么要用qname
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (qName.equals("resource")){
            infos.add(mp3Info);
        }
        tagName = "";
    }

    /**
     * 解析xml就是先把xml看成是一个字符串，然后根据标签内容来提取有效信息
     * 再给那个类的属性
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     */
    @Override
    //每解析一个标签的内容都要调用一次，都是由xml的格式来定的
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        String temp = new String(ch,start,length);
        if (tagName.equals("id")){
            mp3Info.setId(temp);
        }else if (tagName.equals("mp3.name")){
            mp3Info.setMp3Name(temp);
        }else if (tagName.equals("mp3.size")){
            mp3Info.setMp3Size(temp);
        }else if (tagName.equals("lrc.name")){
            mp3Info.setIrcName(temp);
        }else if (tagName.equals("lrc.size")){
            mp3Info.setIrcSize(temp);
        }
    }
//    public void setInfos(List<Mp3Info> infos) {
//        this.infos = infos;
//    }
//
//    public List<Mp3Info> getInfos() {
//        return infos;
//    }
}
