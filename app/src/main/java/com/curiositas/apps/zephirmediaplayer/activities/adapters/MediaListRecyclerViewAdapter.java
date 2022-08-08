package com.curiositas.apps.zephirmediaplayer.activities.adapters;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.curiositas.apps.zephirmediaplayer.databinding.FragmentMediaBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MediaMetadataCompat}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MediaListRecyclerViewAdapter extends RecyclerView.Adapter<MediaListRecyclerViewAdapter.ViewHolder> {

    private final List<MediaBrowserCompat.MediaItem> mValues;

    public MediaListRecyclerViewAdapter(List<MediaBrowserCompat.MediaItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentMediaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getDescription().getTitle());
        holder.mContentView.setText(mValues.get(position).getDescription().getSubtitle());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public MediaBrowserCompat.MediaItem mItem;

        public ViewHolder(FragmentMediaBinding binding) {
            super(binding.getRoot());
            mIdView = binding.mediaItemNumber;
            mContentView = binding.mediaItemContent;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}