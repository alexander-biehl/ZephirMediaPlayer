package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.adapters;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderbiehl.apps.zephirmediaplayer.databinding.FragmentMediaItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MediaItem}.
 */
public class MediaListRecyclerViewAdapter extends RecyclerView.Adapter<MediaListRecyclerViewAdapter.ViewHolder> {

    private final List<MediaItem> mValues;
    private final OnClickHandler clickListener;

    private int longPresPosition;

    public MediaListRecyclerViewAdapter(List<MediaItem> items, final OnClickHandler listener) {
        mValues = items;
        clickListener = listener;
    }

    public void setLongPresPosition(int pos) {
        longPresPosition = pos;
    }

    public int getLongPresPosition() {
        return longPresPosition;
    }

    public MediaItem getContextMenuItem() {
        return mValues.get(longPresPosition);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                FragmentMediaItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        // holder.mIdView.setText(mValues.get(position).mediaId);
        holder.mContentView.setText(mValues.get(position).mediaMetadata.title);

        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onClick(position, mValues.get(position));
            }
        });
//        holder.itemView.setOnLongClickListener(l -> {
//            if (clickListener != null) {
//                clickListener.onLongClick(position, mValues.get(position));
//            }
//            return true;
//        });
//        holder.itemView.setOnLongClickListener(v -> {
//            setLongPresPosition(holder.getBindingAdapterPosition());
//
//        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface OnClickHandler {
        void onClick(int position, MediaItem item);

        // void onLongClick(int position, MediaItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // public final TextView mIdView;
        public final TextView mContentView;
        public MediaItem mItem;

        public ViewHolder(FragmentMediaItemBinding binding) {
            super(binding.getRoot());
            // mIdView = binding.itemNumber;
            mContentView = binding.content;

            itemView.setOnClickListener(view -> {
                if (clickListener != null) {
                    int pos = getBindingAdapterPosition();
                    clickListener.onClick(pos, mValues.get(pos));
                }
            });
//            itemView.setOnLongClickListener(l -> {
//                if (clickListener != null) {
//                    int pos = getBindingAdapterPosition();
//                    clickListener.onLongClick(pos, mValues.get(pos));
//                }
//                return true;
//            });
            itemView.setOnLongClickListener(l -> {
                setLongPresPosition(getBindingAdapterPosition());
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