package com.sunshine.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.sunshine.popularmovies.data.MovieContract;

/**
 * Created by Abhishek on 12-05-2016.
 */
public class CustomMovieAdapter extends CursorAdapter {

    public CustomMovieAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieData movieData= (MovieData) getItem(position);
        PosterImageViewHolder holder;
        if(convertView == null) {
            holder=new PosterImageViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_movies,parent,false);
            holder.poster = (ImageView) convertView.findViewById(R.id.grid_item_imageview);
            convertView.setTag(holder);
        }
        else {
            holder = (PosterImageViewHolder) convertView.getTag();
        }

        Picasso.with(mContext)
                .load(movieData.ImageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(holder.poster);

        return convertView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        //        PosterImageViewHolder viewHolder = new PosterImageViewHolder();
//        viewHolder.poster = (ImageView) view.findViewById(R.id.grid_item_imageview);
//        view.setTag(viewHolder);
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_main, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.v("Custom Movie Adapter",cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH)));
//        PosterImageViewHolder viewHolder = (PosterImageViewHolder) view.getTag();
//        Picasso.with(mContext)
//                .load(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH)))
//                .placeholder(R.drawable.placeholder)
//                .error(R.drawable.placeholder_error)
//                .into(viewHolder.poster);
    }
}
