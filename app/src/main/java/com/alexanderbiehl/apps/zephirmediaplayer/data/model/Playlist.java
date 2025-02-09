package com.alexanderbiehl.apps.zephirmediaplayer.data.model;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.PlaylistSongs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Playlist {

    private Long id;
    private String title;
    private List<Song> songs;

    public Playlist(Long id, String title) {
        this.id = id;
        this.title = title;
        this.songs = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public static Playlist from(PlaylistEntity entity) {
        return new Playlist(
                entity.id,
                entity.title
        );
    }

    public static Playlist from(PlaylistSongs ps) {
        Playlist p = Playlist.from(ps.playlistEntity);
        p.setSongs(ps
                .songEntities
                .stream()
                .map(Song::from)
                .collect(Collectors.toList())
        );
        return p;
    }
}
