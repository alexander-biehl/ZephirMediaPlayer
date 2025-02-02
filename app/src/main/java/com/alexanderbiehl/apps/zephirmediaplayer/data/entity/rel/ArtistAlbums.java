package com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Album;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Artist;

import java.util.List;

public class ArtistAlbums {
    @Embedded
    public Artist artist;
    @Relation(
            parentColumn = "id",
            entityColumn = "albumArtistId"
    )
    public List<Album> albums;
}
