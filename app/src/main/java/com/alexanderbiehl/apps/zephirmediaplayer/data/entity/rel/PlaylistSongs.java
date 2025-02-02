package com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Playlist;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Song;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.m2m.PlaylistSongM2M;

import java.util.List;

public class PlaylistSongs {
    @Embedded
    public Playlist playlist;
    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = PlaylistSongM2M.class,
                    parentColumn = "playlistId",
                    entityColumn = "songId"
            )
    )
    public List<Song> songs;
}
