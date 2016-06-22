package com.sunshine.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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

        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item_movies, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        String posterPath = cursor.getString(MovieFragment.COL_POSTER_PATH);
//        Target target = new Target() {
//            @Override
//            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        File directory = new File(Environment.getExternalStorageDirectory() + "/Images");
//                        if (!directory.exists()) {
//                            directory.mkdir();
//                        }
//                        try {
//                            directory.createNewFile();
//                            FileOutputStream outputStream = new FileOutputStream(directory);
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                            outputStream.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }).start();
//
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//        };


        Picasso.with(mContext)
                .load(posterPath)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(holder.poster);

    }

    public static class ViewHolder {
        final ImageView poster;

        ViewHolder(View view) {
            poster = (ImageView) view.findViewById(R.id.grid_item_imageview);
        }

    }


}
