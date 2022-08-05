package com.curiositas.apps.zephirmediaplayer.activities.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curiositas.apps.zephirmediaplayer.Constants;
import com.curiositas.apps.zephirmediaplayer.R;
import com.curiositas.apps.zephirmediaplayer.activities.MusicPlayerActivity;
import com.curiositas.apps.zephirmediaplayer.models.Song;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder>{

    private final String TAG = SongListAdapter.class.getSimpleName();
    private final LayoutInflater inflater;
    private List<Song> songs; // cached copy of songs

    public SongListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SongListAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.song_recyclerview_item, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListAdapter.SongViewHolder holder, int position) {
        if (songs != null) {
            Song current = songs.get(position);
            holder.getView().setText(current.getTitle());
        } else {
            // in case data isn't ready yet
            holder.getView().setText("No Song");
        }
        holder.getView().setOnClickListener(view -> {
            // retrieve the position of the item clicked
            int pos = holder.getBindingAdapterPosition();
            // get the song corresponding to that adapter position
            Song currSong = songs.get(pos);
            if (currSong != null) {
                // if valid, open song in music player activity
                //Log.d(TAG, "Retrieved song: " + currSong.getID().toString());
                Intent intent = new Intent(inflater.getContext(), MusicPlayerActivity.class);
                intent.putExtra(Constants.URI_EXTRA, currSong.get_id());
                inflater.getContext().startActivity(intent);
            } else {
                Log.d(TAG, "Position " + pos + " did not return a song");
            }
        });
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

    class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView songItem;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songItem = (TextView) itemView.findViewById(R.id.song_list_title);
        }

        public TextView getView() {
            return songItem;
        }

        @Override
        public void onClick(View view) {

        }
    }
}
