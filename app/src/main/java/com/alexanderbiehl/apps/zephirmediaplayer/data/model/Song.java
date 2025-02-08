package com.alexanderbiehl.apps.zephirmediaplayer.data.model;

import android.net.Uri;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;

public class Song {

    private Long id;
    private String mediaId;
    private String title;
    private String artist;
    private String album;
    private Integer trackNumber;
    private String sourceUri;

    public Long getId() {
        return id;
    }

    public String getMediaId() {
        return mediaId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public String getSourceUri() {
        return sourceUri;
    }

    public static Song from(SongEntity entity) {
        Song s = new Song();
        s.id = entity.id;
        s.mediaId = entity.mediaId;
        s.title = entity.title;
        s.sourceUri = entity.sourceUri;
        s.trackNumber = Integer.valueOf(entity.trackNumber);
        return s;
    }
}
