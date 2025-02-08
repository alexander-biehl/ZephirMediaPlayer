package com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.m2m.PlaylistSongM2M;

import java.util.List;

public class PlaylistSongs {
    @Embedded
    public PlaylistEntity playlistEntity;
    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = PlaylistSongM2M.class,
                    parentColumn = "playlistId",
                    entityColumn = "songId"
            )
    )
    public List<SongEntity> songEntities;
}
