package com.ayush.newsfeed;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayush.newsfeed.common.models.Category;
import com.ayush.newsfeed.common.models.FeedItem;
import com.squareup.picasso.Picasso;

/**
 * Created by dexter on 10/05/16.
 */
public abstract class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
    }

    @NonNull
    private static String combinationFormatter(final long millis) {

        final long second = (millis / 1000) % 60;
        final long minute = (millis / (1000 * 60)) % 60;
        final long hour = (millis / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

//        holder.setPosition(position);
        final FeedItem feedItem = getItem(position);

        Picasso.with(holder.image.getContext())
                .load(feedItem.imageUrl())
                .config(Bitmap.Config.RGB_565)
                .fit()
                .stableKey(feedItem.id() + "")
                .into(holder.image);

        holder.heading.setText(feedItem.heading());
        holder.description.setText(feedItem.description());
        holder.sourceDetails.setText(
                Category.name(feedItem.category()) + " - " + combinationFormatter(feedItem.curatedOn()));
    }

    public abstract FeedItem getItem(int position);

    @Override
    public long getItemId(int position) {
        return getItem(position).id();
    }

    final class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        private final TextView heading;
        private final TextView description;
        private final TextView sourceDetails;

        public ViewHolder(View itemView) {

            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.image);
            this.heading = (TextView) itemView.findViewById(R.id.heading);
            this.description = (TextView) itemView.findViewById(R.id.description);
            this.sourceDetails = (TextView) itemView.findViewById(R.id.sourceDetails);
        }
    }
}