package com.sunshine.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.sunshine.popularmovies.data.MovieContract;

public class CustomMovieAdapter extends CursorAdapter {

    public CustomMovieAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        MovieData movieData= (MovieData) getItem(position);
//        PosterImageViewHolder holder;
//        if(convertView == null) {
//            holder=new PosterImageViewHolder();
//            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_movies,parent,false);
//            holder.poster = (ImageView) convertView.findViewById(R.id.grid_item_imageview);
//            convertView.setTag(holder);
//        }
//        else {
//            holder = (PosterImageViewHolder) convertView.getTag();
//        }
//
//        Picasso.with(mContext)
//                .load(movieData.ImageUrl)
//                .placeholder(R.drawable.placeholder)
//                .error(R.drawable.placeholder_error)
//                .into(holder.poster);
//
//        return convertView;
//    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        //        PosterImageViewHolder viewHolder = new PosterImageViewHolder();
//        viewHolder.poster = (ImageView) view.findViewById(R.id.grid_item_imageview);
//        view.setTag(viewHolder);
        return LayoutInflater.from(mContext).inflate(R.layout.grid_item_movies, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView poster= (ImageView) view.findViewById(R.id.grid_item_imageview);
//        PosterImageViewHolder viewHolder = (PosterImageViewHolder) view.getTag();
        String posterPath= cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH));


//         Target target = new Target() {
//            @Override
//            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        File file = new File(
//                                Environment.getExternalStorageDirectory().getPath()
//                                        + "/saved.jpg");
//                        try {
//                            file.createNewFile();
//                            FileOutputStream ostream = new FileOutputStream(file);
//                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,ostream);
//                            ostream.close();
//                        }
//                        catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {}
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {}
//        };
        Picasso.with(mContext)
                .load(posterPath)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(poster);

    }



}
