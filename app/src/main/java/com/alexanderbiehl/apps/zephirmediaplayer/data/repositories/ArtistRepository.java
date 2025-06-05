package com.alexanderbiehl.apps.zephirmediaplayer.data.repositories;

import androidx.media3.common.MediaItem;

import java.util.ArrayList;
import java.util.List;

public class ArtistRepository {
    public List<MediaItem> getAlbumsByArtistId(String mediaId) {
        return new ArrayList<>();
    }

    public MediaItem getById(String mediaId) {
        return null;
    }

    public List<MediaItem> getArtists() {
        return new ArrayList<>();
    }
}
