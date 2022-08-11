package com.alexanderbiehl.apps.zephirmediaplayer.models;

import java.util.Date;

public class Song {

    private long _id;
    private String title;
    private int trackNumber;
    private int year;
    private long duration;
    private String data;
    private Date dateAdded;
    private Date dateModified;
    private long albumId;
    private String albumName;
    private long artistId;
    private String artistName;

    public Song(long _id, String title, int trackNumber, int year, long duration, String data, Date dateAdded, Date dateModified, long albumId, String albumName, long artistId, String artistName) {
        this._id = _id;
        this.title = title;
        this.trackNumber = trackNumber;
        this.year = year;
        this.duration = duration;
        this.data = data;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
        this.albumId = albumId;
        this.albumName = albumName;
        this.artistId = artistId;
        this.artistName = artistName;
    }

    public long get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public int getYear() {
        return year;
    }

    public long getDuration() {
        return duration;
    }

    public String getData() {
        return data;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public long getAlbumId() {
        return albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public long getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }
}
