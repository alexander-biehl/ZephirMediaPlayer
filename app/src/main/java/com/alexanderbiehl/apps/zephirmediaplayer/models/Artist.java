package com.alexanderbiehl.apps.zephirmediaplayer.models;


public class Artist {

    private long id;
    private String name;

    public Artist(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
