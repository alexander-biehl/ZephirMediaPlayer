package com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.m2m;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;

@Entity(primaryKeys = {"playlistId", "songId"},
        indices = {
            @Index(value = "playlistId"), @Index(value = "songId")
        },
        tableName = "playlist_song_m2m",
        foreignKeys = {
                @ForeignKey(
                        entity = PlaylistEntity.class,
                        parentColumns = "id",
                        childColumns = "playlistId"),
                @ForeignKey(
                        entity = SongEntity.class,
                        parentColumns = "id",
                        childColumns = "songId"
                )})

public class PlaylistSongM2M {
    public long playlistId;
    public long songId;
    public int order;
}
