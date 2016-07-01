package com.sunshine.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.sunshine.popularmovies.data.MovieContract;

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
        this.mCustomMovieAdapterOnClickHandler= ch;
    }


    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView poster;
        View mView;

        ViewHolder(View view) {
            super(view);
            mView=view;
            poster = (ImageView) view.findViewById(R.id.grid_item_imageview);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int movieId= mCursor.getInt(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            mCustomMovieAdapterOnClickHandler.onClick(movieId,this);

        }
    }

    public static interface  CustomMovieAdapterOnClickHandler
    {
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

        Picasso.with(mContext)
                .load(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH)))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(holder.poster);
//        holder.mView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                if(mCursor!=null)
//                {
//                    Intent intent= new Intent(mContext,DetailActivity.class)
//                            .setData(MovieContract.MovieEntry.buildMovieWithMovieIdUri
//                                    (mCursor.getInt(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID))));
//                    mContext.startActivity(intent);
//                }
//            }
//        });


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

//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//
//        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item_movies, parent, false);
//        ViewHolder holder = new ViewHolder(view);
//        view.setTag(holder);
//
//        return view;
//    }

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
