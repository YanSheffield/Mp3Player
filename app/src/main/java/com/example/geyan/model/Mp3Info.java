package com.example.geyan.model;

import java.io.Serializable;

/**
 * Created by geyan on 29/04/2017.
 */

public class Mp3Info implements Serializable{
    private static final long serialVersionUID = 11;
    private String id;
    private String mp3Name;
    private String mp3Size;
    private String ircName;
    private String ircSize;
    private String mp3Link;
    private String lrcLink;
    public Mp3Info() {
    }

    public Mp3Info(String id, String mp3Name, String mp3Size, String ircName, String ircSize) {
        this.id = id;
        this.mp3Name = mp3Name;
        this.mp3Size = mp3Size;
        this.ircName = ircName;
        this.ircSize = ircSize;
    }

    public String getLrcLink() {
        return lrcLink;
    }

    public void setLrcLink(String lrcLink) {
        this.lrcLink = lrcLink;
    }
    public String getIrcName() {
        return ircName;
    }

    public void setIrcName(String ircName) {
        this.ircName = ircName;
    }

    public String getIrcSize() {
        return ircSize;
    }

    public void setIrcSize(String ircSize) {
        this.ircSize = ircSize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMp3Name(String mp3Name) {
        this.mp3Name = mp3Name;
    }

    public void setMp3Size(String mp3Size) {
        this.mp3Size = mp3Size;
    }

    public String getMp3Name() {
        return mp3Name;
    }

    public String getMp3Size() {
        return mp3Size;
    }

    public String getMp3Link() {
        return mp3Link;
    }

    public void setMp3Link(String mp3Link) {
        this.mp3Link = mp3Link;
    }
    @Override
    public String toString() {
        return "Mp3Info{" +
                "id='" + id + '\'' +
                ", mp3Name='" + mp3Name + '\'' +
                ", mp3Size='" + mp3Size + '\'' +
                ", ircName='" + ircName + '\'' +
                ", ircSize='" + ircSize + '\'' +
                '}';
    }
}
