package com.teams4.blog.Databases;

import com.google.firebase.database.Exclude;
import com.teams4.blog.UserDashboard;


public class Upload {
    private String mName;
    private String mImageUrl;
    private String mNotes;
    private String mKey;
    private String mName1;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String name, String imageUrl, String notes, String name1) {
        if (notes.trim().equals("")) {
            name = "Posted " + " -- By " + UserDashboard.NAME;
        }

        mName = name + " -- By " + UserDashboard.NAME;
        mImageUrl = imageUrl;
        mNotes = notes;
        mName1 = name1;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName1() {
        return mName1;
    }

    public void setName1(String name1) {
        mName1 = name1;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }


    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }

}

