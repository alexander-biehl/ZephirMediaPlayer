package com.alexanderbiehl.apps.zephirmediaplayer.models;

import java.util.List;

public class Album {

    private long id;
    private String name;
    private Artist artist;
    private List<Song> songList;

    public Album(long id, String name, Artist artist, List<Song> songs) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.songList = songs;
    }

    public long getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Artist getArtist() {
        return this.artist;
    }

    public List<Song> getSongList() {
        return this.songList;
    }
}
