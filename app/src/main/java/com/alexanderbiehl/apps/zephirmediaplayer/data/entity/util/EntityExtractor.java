package com.alexanderbiehl.apps.zephirmediaplayer.data.entity.util;

import android.content.ContentUris;
import android.provider.MediaStore;

import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.ArtistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityExtractor {

    public static List<ArtistEntity> extractArtistEntities(List<MediaItem> items) {
        return items
                .stream()
                .map(e -> e.mediaMetadata.artist)
                .distinct()
                .map(artist -> new ArtistEntity(artist.toString()))
                .collect(Collectors.toList());
    }

    public static List<AlbumEntity> extractAlbumEntities(List<MediaItem> items, Map<String, Long> artistIdMap) {
        List<AlbumEntity> entities = new ArrayList<>();
        Map<String, AlbumEntity> entityMap = new HashMap<>();
        for (MediaItem item : items) {
            String album = item.mediaMetadata.albumTitle.toString();
            String artist = item.mediaMetadata.artist.toString();
            if (!entityMap.containsKey(album)) {
                entityMap.put(album, new AlbumEntity(album, artistIdMap.get(artist)));
            }
        }
        return entityMap.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
    }

    public static List<SongEntity> extractSongEntities(
            List<MediaItem> items,
            Map<String, Long> artistIdMap,
            Map<String, Long> albumIdMap
    ) {
        return items
                .stream()
                .map(e -> new SongEntity(
                        e.mediaMetadata.title.toString(),
                        String.valueOf(e.mediaMetadata.trackNumber),
                        ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                Long.parseLong(e.mediaId)
                        ).toString(),
                        artistIdMap.get(e.mediaMetadata.artist.toString()),
                        albumIdMap.get(e.mediaMetadata.albumTitle.toString())
                ))
                .collect(Collectors.toList());
    }
}
