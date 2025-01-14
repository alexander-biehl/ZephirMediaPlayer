package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.placeholder.PlaceholderContent.MediaItem;
import com.alexanderbiehl.apps.zephirmediaplayer.databinding.FragmentMediaItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MediaItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MediaListRecyclerViewAdapter extends RecyclerView.Adapter<MediaListRecyclerViewAdapter.ViewHolder> {

    private final List<MediaItem> mValues;

    public MediaListRecyclerViewAdapter(List<MediaItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentMediaItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public MediaItem mItem;

        public ViewHolder(FragmentMediaItemBinding binding) {
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