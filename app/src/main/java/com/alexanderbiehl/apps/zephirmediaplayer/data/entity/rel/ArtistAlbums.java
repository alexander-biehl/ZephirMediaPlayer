package com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.ArtistEntity;

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
