package com.example.geyan.model;

import java.io.Serializable;

/**
 * Created by geyan on 08/05/2017.
 */

public class LyricInfo implements Serializable {
    private String lycName;
    private String lycSize;
    private String lycLike;

    public String getLycName() {
        return lycName;
    }

    public void setLycName(String lycName) {
        this.lycName = lycName;
    }

    public String getLycSize() {
        return lycSize;
    }

    public void setLycSize(String lycSize) {
        this.lycSize = lycSize;
    }

    public String getLycLike() {
        return lycLike;
    }

    public void setLycLike(String lycLike) {
        this.lycLike = lycLike;
    }
}
