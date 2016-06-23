package com.sunshine.popularmovies;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sunshine.popularmovies.data.MovieContract;

/**
 * Created by Abhishek on 13-05-2016.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int LOADER_ID = 1;

    private static String[] DETAIL_COLUMN = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG,
            MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_COUNT};

    private static int COL_ID = 0;
    private static int COL_MOVIE_ID = 1;
    private static int COL_POSTER_PATH = 2;
    private static int COL_OVERVIEW = 3;
    private static int COL_POPULARITY = 4;
    private static int COL_RELEASE_DATE = 5;
    private static int COL_MOVIE_TITLE = 6;
    private static int COL_VOTE_AVG = 7;
    private static int COL_VOTE_COUNT = 8;
    Uri mUri;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTrailersAndReviews();
    }

    public void updateTrailersAndReviews()
    {
        int movieId= MovieContract.MovieEntry.getMovieId(mUri);
        FetchVideosAndReviewsTask videosAndReviewsTask= new FetchVideosAndReviewsTask();
        videosAndReviewsTask.execute(movieId);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUri= getActivity().getIntent().getData();
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(null!=mUri)
            return new CursorLoader(getActivity(),
                mUri,
                DETAIL_COLUMN,
                null,
                null,
                null);
        else
            return null;


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (!data.moveToFirst()) {
            return;
        }
        String title = data.getString(COL_MOVIE_TITLE);
        String overview = data.getString(COL_OVERVIEW);
        double popularity = data.getDouble(COL_POPULARITY);
        String vote_average = data.getString(COL_VOTE_AVG);
        String release_date = data.getString(COL_RELEASE_DATE);
        String poster_path = data.getString(COL_POSTER_PATH);

        ImageView mPoster = (ImageView) getView().findViewById(R.id.detail_image_view);
        TextView mTitle = (TextView) getView().findViewById(R.id.detail_title);
        TextView mOverview = (TextView) getView().findViewById(R.id.detail_overview);
        TextView mReleaseDate = (TextView) getView().findViewById(R.id.detail_release_date);
        TextView mVoteAvg = (TextView) getView().findViewById(R.id.detail_vote_average);


        //DetailViewHolder holder= (DetailViewHolder) getView().getTag();
        Log.v("Title", title);

        mTitle.setText(title);
        mOverview.setText(overview);
        mVoteAvg.setText(vote_average);
        mReleaseDate.setText(release_date);

        Picasso.with(getContext())
                .load(poster_path)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(mPoster);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
