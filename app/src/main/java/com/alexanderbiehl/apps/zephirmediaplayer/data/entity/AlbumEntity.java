package com.alexanderbiehl.apps.zephirmediaplayer.data.entity;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.base.EntityBase;

@Entity(tableName = "albums")
public class AlbumEntity extends EntityBase {

    @ColumnInfo(name = "title")
    public String title;

    public long albumArtistId;

    @Ignore
    public AlbumEntity(String title, String mediaId, Long artistId) {
        super();
        this.title = title;
        this.mediaId = mediaId;
        this.albumArtistId = artistId;
    }

    @Ignore
    public AlbumEntity(String title) {
        super();
        this.title = title;
    }

    public AlbumEntity() {
        super();
    }

    public static MediaItem asItem(AlbumEntity entity) {
        return new MediaItem.Builder()
                .setMediaId(entity.mediaId)
                .setMediaMetadata(
                        new MediaMetadata.Builder()
                                .setTitle(entity.title)
                                .setIsBrowsable(true)
                                .setIsPlayable(true)
                                .setMediaType(MediaMetadata.MEDIA_TYPE_ALBUM)
                                .build())
                .build();
    }
}
