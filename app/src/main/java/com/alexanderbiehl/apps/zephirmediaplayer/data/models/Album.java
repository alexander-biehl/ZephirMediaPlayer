package com.alexanderbiehl.apps.zephirmediaplayer.data.models;

import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.AlbumEntity;

import java.util.ArrayList;
import java.util.List;

public class Album {

    public Long id;
    public String mediaId;
    public String title;
    public Artist artist;
    public List<Song> songs;

    public Album() {
        songs = new ArrayList<>();
    }

    public Album(String mediaId, String title) {
        this.mediaId = mediaId;
        this.title = title;
        this.songs = new ArrayList<>();
    }

    public Album(Long id, String mediaId, String title) {
        this.id = id;
        this.mediaId = mediaId;
        this.title = title;
        this.songs = new ArrayList<>();
    }

    public Album(String mediaId, String title, Artist artist) {
        this.mediaId = mediaId;
        this.title = title;
        this.artist = artist;
        this.songs = new ArrayList<>();
    }

    public static Album fromEntity(AlbumEntity albumEntity) {
        Album album = new Album();
        if (albumEntity.id != null) {
            album.id = albumEntity.id;
        }
        album.setMediaId(albumEntity.mediaId);
        album.setTitle(albumEntity.title);
        return album;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
