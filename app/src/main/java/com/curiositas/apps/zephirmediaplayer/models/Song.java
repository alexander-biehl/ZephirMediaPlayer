package com.curiositas.apps.zephirmediaplayer.models;

public class Song {

    private long contentID;
    private String title;
    //private Artist artist;
    private String artist;
    //private Album album;
    private String album;

    /* Constructor */
    /*public Song(long songID, String songTitle, Artist songArtist, Album songAlbum) {
        this.id = songID;
        this.title = songTitle;
        this.artist = songArtist;
        this.album = songAlbum;
    }*/
    public Song(long contentID, String songTitle, String songArtist, String songAlbum) {
        this.contentID = contentID;
        this.title = songTitle;
        this.artist = songArtist;
        this.album = songAlbum;
    }

    public long getID() {
        return this.contentID;
    }

    public String getTitle() {
        return this.title;
    }

    public /*Artist*/String getArtist() {
        return this.artist;
    }

    public /*Album*/String getAlbum() {
        return this.album;
    }
}
