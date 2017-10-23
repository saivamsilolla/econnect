package com.intuit.sbg.econnect.model;

import java.io.Serializable;
import java.util.Date;

public class TweetMessage implements Serializable{

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isIdFavourite() {
        return idFavourite;
    }

    public void setIdFavourite(boolean idFavourite) {
        this.idFavourite = idFavourite;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteCount(int favouriteCount) {
        this.favouriteCount = favouriteCount;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }


    private String text;
    private boolean idFavourite;
    private int favouriteCount;
    private Date created_at;


    private String userId;
    //private String id;


}
