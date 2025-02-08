package com.alexanderbiehl.apps.zephirmediaplayer.data.model;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.ArtistEntity;

public class Artist {

    private Long id;
    private String mediaId;
    private String title;

    public Artist(Long id, String mediaId, String title) {
        this.id = id;
        this.mediaId = mediaId;
        this.title = title;
    }

    public static Artist from(ArtistEntity entity) {
        return new Artist(
                entity.id,
                entity.mediaId,
                entity.title
        );
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
}
