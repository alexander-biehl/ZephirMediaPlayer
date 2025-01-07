package com.alexanderbiehl.apps.zephirmediaplayer.activities.adapters;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderbiehl.apps.zephirmediaplayer.databinding.FragmentMediaBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MediaMetadataCompat}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MediaListRecyclerViewAdapter extends RecyclerView.Adapter<MediaListRecyclerViewAdapter.ViewHolder> {

    private List<MediaBrowserCompat.MediaItem> mValues;
    private final IFragmentBridge callback;
    private final View.OnClickListener onClickListener;

    public MediaListRecyclerViewAdapter(final IFragmentBridge callback) {
        this.callback = callback;
        mValues = new ArrayList<>();
        onClickListener = view -> {
            // Communicate the clicked item back up to the Fragment
            //final MediaBrowserCompat.MediaItem item = holder.mItem;
            //int position = get
            if (this.callback != null) {
                this.callback.onMediaSelected(view);
            }
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentMediaBinding binding = FragmentMediaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        MediaBrowserCompat.MediaItem item = mValues.get(position);
        if (item != null) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getDescription().getTitle());
            holder.mContentView.setText(mValues.get(position).getDescription().getSubtitle());

            holder.mIdView.setOnClickListener(onClickListener);
            holder.mContentView.setOnClickListener(onClickListener);
        } else {
            holder.mItem = null;
            holder.mIdView.setText("No Data");
            holder.mContentView.setText("No Data");
        }
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

    public void setMedia(List<MediaBrowserCompat.MediaItem> items) {
        mValues = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public MediaBrowserCompat.MediaItem mItem;

        public ViewHolder(FragmentMediaBinding binding) {
            super(binding.getRoot());
            mIdView = binding.mediaItemNumber;
            mContentView = binding.mediaItemContent;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public interface IFragmentBridge {
        void onMediaSelected(View view);
    }
}