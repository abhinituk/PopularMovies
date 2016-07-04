package com.sunshine.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.sunshine.popularmovies.data.MovieContract;


public class CustomTrailerAdapter extends RecyclerView.Adapter<CustomTrailerAdapter.ViewHolder> {

    Context mContext;
    Cursor mCursor;
    CustomTrailerAdapterOnClickHandler mCustomTrailerAdapterOnClickHandler;

    public CustomTrailerAdapter(Context context,CustomTrailerAdapterOnClickHandler customTrailerAdapterOnClickHandler) {
        super();
        mContext=context;
        mCustomTrailerAdapterOnClickHandler=customTrailerAdapterOnClickHandler;

    }

    @Override
    public CustomTrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.list_item_trailer,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomTrailerAdapter.ViewHolder holder, int position) {

        mCursor.moveToPosition(position);
        String thumbnailImage= mCursor.getString(mCursor.getColumnIndex(MovieContract.TrailerEntry.COL_TRAILER_THUMBNAIL));
        String source= mCursor.getString(mCursor.getColumnIndex(MovieContract.TrailerEntry.COL_TRAILER_SOURCE));


        holder.playButton.setImageResource(R.drawable.ic_play_arrow_white_18dp);
        //Loading the thumbnail images
        Picasso.with(mContext)
                .load(thumbnailImage)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(holder.thumbnailImageView);



    }

    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        if (null == mCursor)
            return 0;
        else
            return mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        ImageView thumbnailImageView;
        ImageView playButton;


        public ViewHolder(View itemView) {
            super(itemView);
            cardView= (CardView) itemView.findViewById(R.id.grid_item_trailer_cardView);
            thumbnailImageView= (ImageView) itemView.findViewById(R.id.list_item_thumbnail_imageView);
            playButton= (ImageView) itemView.findViewById(R.id.playTrailerButtonImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position= getAdapterPosition();
            mCursor.moveToPosition(position);

            String videoSource = mCursor.getString(mCursor.getColumnIndex(MovieContract.TrailerEntry.COL_TRAILER_SOURCE));
            mCustomTrailerAdapterOnClickHandler.onClick(videoSource,this);
        }
    }
    public interface CustomTrailerAdapterOnClickHandler{
        void onClick(String videoSource, ViewHolder vh);
    }
}
