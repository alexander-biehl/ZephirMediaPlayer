package com.alexanderbiehl.apps.zephirmediaplayer.database.entity.rel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.ArtistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;

import java.util.List;

public class ArtistSongs {

    @Embedded
    public ArtistEntity artistEntity;
    @Relation(
            parentColumn = "id",
            entityColumn = "songArtistId"
    )
    public List<SongEntity> songEntities;
}
