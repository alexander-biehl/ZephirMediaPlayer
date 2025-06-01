package com.alexanderbiehl.apps.zephirmediaplayer.database.entity.rel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;

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
