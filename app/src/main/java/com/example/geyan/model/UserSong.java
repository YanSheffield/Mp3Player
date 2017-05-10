package com.example.geyan.model;

import java.io.Serializable;

/**
 * Created by geyan on 10/05/2017.
 */

public class UserSong implements Serializable {

    private String owner;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    private String songName;

}
