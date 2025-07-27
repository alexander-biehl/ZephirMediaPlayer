package com.alexanderbiehl.apps.zephirmediaplayer.data.models;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.ArtistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.rel.ArtistAlbums;

import java.util.ArrayList;
import java.util.List;

public class Artist {

    public Long id;
    public String mediaId;
    public String title;
    public List<Album> albums;

    public Artist() {
        albums = new ArrayList<>();
    }

    public Artist(String title) {
        this.title = title;
        this.albums = new ArrayList<>();
    }

    public Artist(String mediaId, String title) {
        this.mediaId = mediaId;
        this.title = title;
        this.albums = new ArrayList<>();
    }

    public static Artist fromEntity(ArtistEntity artistEntity) {
        Artist artist = new Artist();
        if (artistEntity.id != null) {
            artist.id = artistEntity.id;
        }
        artist.setMediaId(artistEntity.mediaId);
        artist.setTitle(artistEntity.title);
        return artist;
    }

    public static Artist fromEntity(ArtistAlbums artistAlbums) {
        Artist artist = fromEntity(artistAlbums.artistEntity);
        List<Album> albums = new ArrayList<>();
        for (var albumEntity : artistAlbums.albumEntities) {
            albums.add(Album.fromEntity(albumEntity));
        }
        artist.setAlbums(albums);
        return artist;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public static MediaItem asItem(Artist artist) {
        if (artist == null) {
            return null;
        }
        return new MediaItem.Builder()
                .setMediaId(artist.getMediaId())
                .setMediaMetadata(new MediaMetadata.Builder()
                        .setTitle(artist.getTitle())
                        .setIsBrowsable(true)
                        .setIsPlayable(false)
                        .setMediaType(MediaMetadata.MEDIA_TYPE_ARTIST)
                        .build())
                .build();
    }
}
