package com.alexanderbiehl.apps.zephirmediaplayer.data.models;

public class Song {

    public Long id;
    public String mediaId;
    public String title;
    public Album album;
    public Artist Artist;
    public String trackNumber;
    public String sourceUri;

    public Song() {
    }

    public Song(String mediaId, String title) {
        this.mediaId = mediaId;
        this.title = title;
    }

    public Song(Long id, String mediaId, String title) {
        this.id = id;
        this.mediaId = mediaId;
        this.title = title;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Artist getArtist() {
        return Artist;
    }

    public void setArtist(Artist artist) {
        Artist = artist;
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

    public String getSourceUri() {
        return sourceUri;
    }

    public void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
    }
}
