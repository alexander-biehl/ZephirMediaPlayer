package com.alexanderbiehl.apps.zephirmediaplayer.data.model;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;

public class Song {

    private Long id;
    private String mediaId;
    private String title;
    private Integer trackNumber;
    private String sourceUri;

    public Song(
            Long id,
            String mediaId,
            String title,
            Integer trackNumber,
            String sourceUri
    ) {
        this.id = id;
        this.mediaId = mediaId;
        this.title = title;
        this.trackNumber = trackNumber;
        this.sourceUri = sourceUri;
    }

    private Song() {
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

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getSourceUri() {
        return sourceUri;
    }

    public void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri;
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
