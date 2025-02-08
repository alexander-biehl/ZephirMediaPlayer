package com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;

import java.util.List;

public class AlbumSongs {

    @Embedded
    public AlbumEntity albumEntity;
    @Relation(
            parentColumn = "id",
            entityColumn = "songAlbumId"
    )
    public List<SongEntity> songEntities;
}
