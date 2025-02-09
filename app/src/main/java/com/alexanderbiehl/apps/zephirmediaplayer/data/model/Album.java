package com.alexanderbiehl.apps.zephirmediaplayer.data.model;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.ArtistEntity;

public class Album {

    private Long id;
    private String mediaId;
    private String title;
    private String artist;

    public Album(Long id, String mediaId, String title) {
        this.id = id;
        this.mediaId = mediaId;
        this.title = title;
    }

    public Album(Long id, String mediaId, String title, String artist) {
        this.id = id;
        this.mediaId = mediaId;
        this.title = title;
        this.artist = artist;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public static Album from(ArtistEntity entity) {
        return new Album(
                entity.id,
                entity.mediaId,
                entity.title
        );
    }
}
