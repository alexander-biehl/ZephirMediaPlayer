package com.curiositas.apps.zephirmediaplayer.activities.adapters;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
        FragmentMediaBinding binding = FragmentMediaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getDescription().getTitle());
        holder.mContentView.setText(mValues.get(position).getDescription().getSubtitle());
        holder.mLinearLayout.setOnClickListener(view -> {
            MediaBrowserCompat.MediaItem item = holder.mItem;
            //NavHostFragment.findNavController(holder.)
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public List<MediaBrowserCompat.MediaItem> getValues() {
        return mValues;
    }

    public MediaBrowserCompat.MediaItem getItem() {
        //return mValues.get()
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public final LinearLayout mLinearLayout;
        public MediaBrowserCompat.MediaItem mItem;

        public ViewHolder(FragmentMediaBinding binding) {
            super(binding.getRoot());
            mIdView = binding.mediaItemNumber;
            mContentView = binding.mediaItemContent;
            mLinearLayout = binding.mediaListItem;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        public MediaBrowserCompat.MediaItem getItem() {
            return mItem;
        }

    }
}