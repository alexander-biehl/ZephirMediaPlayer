package com.alexanderbiehl.apps.zephirmediaplayer.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.base.EntityBase;

@Entity(tableName = "songs")
public class SongEntity extends EntityBase {

    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "track_number")
    public String trackNumber;
    @ColumnInfo(name = "source_uri")
    public String sourceUri;

    public long songArtistId;
    public long songAlbumId;

    public SongEntity(String title, String trackNumber, String sourceUri, Long artistId, Long albumId) {
        this.title = title;
        this.trackNumber = trackNumber;
        this.sourceUri = sourceUri;
        this.songArtistId = artistId;
        this.songAlbumId = albumId;
    }
}
