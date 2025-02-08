package com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.ArtistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;

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
