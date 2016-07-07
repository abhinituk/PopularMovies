package com.sunshine.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sunshine.popularmovies.R;
import com.sunshine.popularmovies.data.MovieContract;

import java.io.File;

public class CustomMovieAdapter extends RecyclerView.Adapter<CustomMovieAdapter.ViewHolder> {

    private Cursor mCursor;
    final private Context mContext;
    final private CustomMovieAdapterOnClickHandler mCustomMovieAdapterOnClickHandler;


    @Override
    public int getItemCount() {
        if (null == mCursor)
            return 0;
        else
            return mCursor.getCount();
    }


    public CustomMovieAdapter(Context context, CustomMovieAdapterOnClickHandler ch) {
        this.mContext = context;
        this.mCustomMovieAdapterOnClickHandler = ch;
    }


    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final CardView cardView;
        final TextView titleTextView;
        final ImageView poster;

        ViewHolder(View view) {
            super(view);
            cardView= (CardView) view.findViewById(R.id.grid_item_cardView);
            poster = (ImageView) view.findViewById(R.id.grid_item_imageview);
            titleTextView= (TextView) view.findViewById(R.id.grid_item_title_textView);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int movieId = mCursor.getInt(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            mCustomMovieAdapterOnClickHandler.onClick(movieId, this);

        }
    }

    public interface CustomMovieAdapterOnClickHandler {
        void onClick(int movieId, ViewHolder vh);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_movies, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String path = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH));
        String title=mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));
        Picasso.with(mContext)
                .load(new File(path))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(holder.poster);

        holder.titleTextView.setText(title);
    }


//    @Override
//    public void bindView(View view, Context context, Cursor cursor) {
//        ViewHolder holder = (ViewHolder) view.getTag();
//
//        String posterPath = cursor.getString(MovieFragment.COL_POSTER_PATH);
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


//
//
//    }


}
