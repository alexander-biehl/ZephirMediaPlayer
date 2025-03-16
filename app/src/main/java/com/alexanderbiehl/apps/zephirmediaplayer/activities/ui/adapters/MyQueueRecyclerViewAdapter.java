package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderbiehl.apps.zephirmediaplayer.common.OnClickHandler;
import com.alexanderbiehl.apps.zephirmediaplayer.databinding.FragmentQueueItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MediaItem}.
 */
public class MyQueueRecyclerViewAdapter extends RecyclerView.Adapter<MyQueueRecyclerViewAdapter.ViewHolder> {

    private final List<MediaItem> mValues;
    private final OnClickHandler clickHandler;
    private int longPressPosition;

    public MyQueueRecyclerViewAdapter(List<MediaItem> items, final OnClickHandler onClickHandler) {
        mValues = items;
        this.clickHandler = onClickHandler;
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

        holder.itemView.setOnClickListener(v -> {
            if (clickHandler != null) {
                clickHandler.onClick(position, mValues.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public int getLongPressPosition() {
        return longPressPosition;
    }

    public void setLongPressPosition(int longPressPosition) {
        this.longPressPosition = longPressPosition;
    }

    public MediaItem getContextMenuItem() {
        return mValues.get(longPressPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public MediaItem mItem;

        public ViewHolder(FragmentQueueItemBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;

            itemView.setOnClickListener(v -> {
                if (clickHandler != null) {
                    int pos = getBindingAdapterPosition();
                    clickHandler.onClick(pos, mValues.get(pos));
                }
            });

            itemView.setOnLongClickListener(l -> {
                setLongPressPosition(getBindingAdapterPosition());
                itemView.showContextMenu();
                return true;
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}