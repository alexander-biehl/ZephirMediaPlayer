package com.alexanderbiehl.apps.zephirmediaplayer.database.entity;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.base.EntityBase;

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

    public SongEntity() {
        super();
    }

    @Ignore
    public SongEntity(String title, String mediaId, String trackNumber, String sourceUri, Long artistId, Long albumId) {
        super();
        this.title = title;
        this.trackNumber = trackNumber;
        this.sourceUri = sourceUri;
        this.songArtistId = artistId;
        this.songAlbumId = albumId;
        this.mediaId = mediaId;
    }

    public static MediaItem toItem(SongEntity entity) {
        if (entity == null) {
            return null;
        }
        Integer track = null;
        try {
            track = Integer.valueOf(entity.trackNumber);
        } catch (Exception ignored) {
        }
        return new MediaItem.Builder()
                .setMediaId(entity.mediaId)
                .setUri(entity.sourceUri)
                .setMediaMetadata(
                        new MediaMetadata.Builder()
                                .setTitle(entity.title)
                                .setTrackNumber(track)
                                .setIsBrowsable(false)
                                .setIsPlayable(true)
                                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                                .build())
                .build();
    }
}
