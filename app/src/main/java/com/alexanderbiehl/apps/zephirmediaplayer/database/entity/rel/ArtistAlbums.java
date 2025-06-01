package com.alexanderbiehl.apps.zephirmediaplayer.database.entity.rel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.ArtistEntity;

import java.util.List;

public class ArtistAlbums {
    @Embedded
    public ArtistEntity artistEntity;
    @Relation(
            parentColumn = "id",
            entityColumn = "albumArtistId"
    )
    public List<AlbumEntity> albumEntities;
}
