package com.alexanderbiehl.apps.zephirmediaplayer.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.base.DoaBase;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.rel.PlaylistSongs;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.rel.m2m.PlaylistSongM2M;

import java.util.List;

@Dao
public interface PlaylistDao extends DoaBase<PlaylistEntity> {

    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :id")
    PlaylistSongs getPlaylistSongsById(final Long id);

    @Transaction
    @Query("SELECT * FROM playlists WHERE media_id = :mediaId")
    PlaylistSongs getPlaylistSongsByMediaId(final String mediaId);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPlaylistSongs(PlaylistSongM2M... pls);

    @Query("SELECT songs.* FROM songs " +
            "INNER JOIN playlist_song_m2m ON songs.id = playlist_song_m2m.songId " +
            "INNER JOIN playlists ON playlists.id = playlist_song_m2m.playlistId " +
            "WHERE playlists.media_id = :mediaId " +
            "ORDER BY playlist_song_m2m.`order` ASC")
    SongEntity[] getSongsByPlaylistMediaId(final String mediaId);

    @Query("SELECT * FROM playlists")
    List<PlaylistEntity> getAllPlaylists();

    @Query("SELECT * FROM playlists WHERE media_id = :mediaId")
    PlaylistEntity getByMediaId(final String mediaId);

    @Query("DELETE FROM playlist_song_m2m WHERE playlistId = :playlistId")
    void deletePlaylistSongMappings(final long playlistId);
}
