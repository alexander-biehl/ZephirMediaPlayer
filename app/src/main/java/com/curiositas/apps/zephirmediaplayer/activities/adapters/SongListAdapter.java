package com.curiositas.apps.zephirmediaplayer.activities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curiositas.apps.zephirmediaplayer.R;
import com.curiositas.apps.zephirmediaplayer.models.Song;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder>{

    private final LayoutInflater inflater;
    private List<Song> songs; // cached copy of songs

    public SongListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SongListAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.fragment_song_list, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListAdapter.SongViewHolder holder, int position) {
        if (songs != null) {
            Song current = songs.get(position);
            holder.songItem.setText(current.getTitle());
        } else {
            // in case data isn't ready yet
            holder.songItem.setText("No Song");
        }
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (this.songs != null) {
            return this.songs.size();
        }
        return 0;
    }

    class SongViewHolder extends RecyclerView.ViewHolder {

        private final TextView songItem;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songItem = (TextView) itemView.findViewById(R.id.song_list_title);
        }
    }
}
