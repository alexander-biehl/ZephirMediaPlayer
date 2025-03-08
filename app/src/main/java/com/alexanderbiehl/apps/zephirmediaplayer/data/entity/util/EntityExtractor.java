package com.alexanderbiehl.apps.zephirmediaplayer.data.entity.util;

import android.content.ContentUris;
import android.provider.MediaStore;
import android.util.Log;

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

    private static final String TAG = EntityExtractor.class.getSimpleName();

    private static final String ALBUM_PREFIX = "[albumEntity]";
    private static final String ARTIST_PREFIX = "[artistEntity]";
    private static final String ITEM_PREFIX = "[item]";
    private static final String PLAYLIST_PREFIX = "[playlistEntity]";

    public static List<ArtistEntity> extractArtistEntities(List<MediaItem> items) {

        Map<String, ArtistEntity> entityMap = new HashMap<>();
        for (MediaItem item : items) {
            String artist = item.mediaMetadata.artist.toString();
            if (artist.equals("Actress")) {
                Log.d(TAG, "Actress");
            }
            String mediaId = ARTIST_PREFIX + artist;
            if (!entityMap.containsKey(artist)) {
                entityMap.put(artist, new ArtistEntity(artist, mediaId));
            }
        }
        return new ArrayList<>(entityMap.values());
    }

    public static List<AlbumEntity> extractAlbumEntities(List<MediaItem> items, Map<String, Long> artistIdMap) {

        Map<String, AlbumEntity> entityMap = new HashMap<>();
        for (MediaItem item : items) {
            String album = item.mediaMetadata.albumTitle.toString();
            String artist = item.mediaMetadata.artist.toString();
            // use artist-album for the map key to avoid issues with duplicate album names
            String mapKey = artist + "-" + album;

            // use ALBUM_PREFIXalbum_artist as the album mediaID to avoid issues where different
            // artists have albums with the same title
            String mediaId = ALBUM_PREFIX + album + "_" + artist;
            if (!entityMap.containsKey(mapKey)) {
                entityMap.put(mapKey, new AlbumEntity(album, mediaId, artistIdMap.get(artist)));
            }
        }
        return new ArrayList<>(entityMap.values());
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
                        ITEM_PREFIX + e.mediaId,
                        String.valueOf(e.mediaMetadata.trackNumber),
                        ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                Long.parseLong(e.mediaId)
                        ).toString(),
                        artistIdMap.get(e.mediaMetadata.artist.toString()),
                        albumIdMap.get(e.mediaMetadata.albumTitle.toString() + "-" + e.mediaMetadata.artist)
                ))
                .collect(Collectors.toList());
    }
}
