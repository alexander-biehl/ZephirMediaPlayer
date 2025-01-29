package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.media3.common.MediaItem;
import com.alexanderbiehl.apps.zephirmediaplayer.databinding.FragmentQueueItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MediaItem}.
 */
public class MyQueueRecyclerViewAdapter extends RecyclerView.Adapter<MyQueueRecyclerViewAdapter.ViewHolder> {

    private final List<MediaItem> mValues;

    public MyQueueRecyclerViewAdapter(List<MediaItem> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                FragmentQueueItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.valueOf(position));
        holder.mContentView.setText(mValues.get(position).mediaMetadata.title);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public MediaItem mItem;

        public ViewHolder(FragmentQueueItemBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}