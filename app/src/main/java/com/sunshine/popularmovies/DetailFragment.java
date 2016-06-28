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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sunshine.popularmovies.data.MovieContract;

import java.util.ArrayList;

/**
 * Created by Abhishek on 13-05-2016.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int LOADER_ID = 100;
    private final int REVIEW_LOADER_ID = 200;
    private final int TRAILER_LOADER_ID = 300;

    static ArrayAdapter<String> mArrayAdapterTrailer;
    static ArrayAdapter<String> mArrayAdapterReview;
    static ArrayList<String> mArrayListReview;
    static ArrayList<String> mArrayListTrailer;

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

    private static String[] REVIEW_COLUMN = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COL_REVIEW_ID,
            MovieContract.ReviewEntry.COL_REVIEW_AUTHOR,
            MovieContract.ReviewEntry.COL_REVIEW_CONTENT
    };

    private static int COL_REVIEW_ID = 0;
    private static int COL_REVIEW_MOVIE_ID = 1;
    private static int COL_REVIEW_AUTHOR = 2;
    private static int COL_REVIEW_CONTENT = 3;

    private static String[] TRAILER_COLUMN = {
            MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COL_TRAILER_ID,
            MovieContract.TrailerEntry.COL_TRAILER_NAME,
            MovieContract.TrailerEntry.COL_TRAILER_SOURCE
    };

    private static int COL_TRAILER_ID = 0;
    private static int COL_TRAILER_MOVIE_ID = 1;
    private static int COL_TRAILER_NAME = 2;
    private static int COL_TRAILER_SOURCE = 3;


    Uri mUri;

    int mMovieId;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);
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
        updateTrailer();
        updateReview();
    }


    public void updateTrailer() {
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(getContext());
        fetchTrailerTask.execute(MovieContract.MovieEntry.getMovieId(mUri));
    }

    public void updateReview() {
        FetchReviewTask fetchReviewTask = new FetchReviewTask(getContext());
        fetchReviewTask.execute(MovieContract.MovieEntry.getMovieId(mUri));
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
        mUri = getActivity().getIntent().getData();
        //Log.v("Uri Received", String.valueOf(mUri));

        mMovieId = MovieContract.MovieEntry.getMovieId(mUri);
        View view = inflater.inflate(R.layout.fragment_detail, container, false);


        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == LOADER_ID) {

            return new CursorLoader(getActivity(),
                    mUri,
                    DETAIL_COLUMN,
                    null,
                    null,
                    null);
        } else if (id == REVIEW_LOADER_ID) {
            return new CursorLoader(getActivity(),
                    MovieContract.ReviewEntry.buildReviewrWithId(mMovieId),
                    REVIEW_COLUMN,
                    null,
                    null,
                    null);
        } else if (id == TRAILER_LOADER_ID) {
            return new CursorLoader(getActivity(),
                    MovieContract.TrailerEntry.buildTrailerWithId(mMovieId),
                    TRAILER_COLUMN,
                    null,
                    null,
                    null);
        } else
            return null;


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v("Loader Id", String.valueOf(loader.getId()));
        switch (loader.getId()) {
            case LOADER_ID:
                if (!data.moveToFirst()) {
                    return;
                }
                String title = data.getString(COL_MOVIE_TITLE);
                String overview = data.getString(COL_OVERVIEW);
                String vote_average = data.getString(COL_VOTE_AVG);
                String release_date = data.getString(COL_RELEASE_DATE);
                String poster_path = data.getString(COL_POSTER_PATH);

                ImageView mPoster = (ImageView) getView().findViewById(R.id.detail_image_view);
                TextView mTitle = (TextView) getView().findViewById(R.id.detail_title);
                TextView mOverview = (TextView) getView().findViewById(R.id.detail_overview);
                TextView mReleaseDate = (TextView) getView().findViewById(R.id.detail_release_date);
                TextView mVoteAvg = (TextView) getView().findViewById(R.id.detail_vote_average);

                mTitle.setText(title);
                mOverview.setText(overview);
                mVoteAvg.setText(vote_average);
                mReleaseDate.setText(release_date);

                Picasso.with(getContext())
                        .load(poster_path)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder_error)
                        .into(mPoster);
                break;
            case REVIEW_LOADER_ID:
                ListView reviewListView = (ListView) getView().findViewById(R.id.review_list_view);

                mArrayListReview = new ArrayList<>();
                if (data.moveToFirst()) {
                    do {
                        String author = data.getString(COL_REVIEW_AUTHOR);
                        String content = data.getString(COL_REVIEW_CONTENT);
                        String resultReview = author + "\n" + content;
                        //Log.v("Review",resultReview);
                        mArrayListReview.add(resultReview);
                    } while (data.moveToNext());
                }
                //Log.v("Review ArrayList", String.valueOf(mArrayListReview));
                mArrayAdapterReview = new ArrayAdapter<>(getContext(),
                        R.layout.list_item_review,
                        R.id.list_item_review_textView,
                        mArrayListReview);
                reviewListView.setAdapter(mArrayAdapterReview);

                //http://stackoverflow.com/questions/3495890/how-can-i-put-a-listview-into-a-scrollview-without-it-collapsing
                Utility.setListViewHeightBasedOnChildren(reviewListView);
                mArrayAdapterReview.notifyDataSetChanged();
                break;

            case TRAILER_LOADER_ID:
                ListView trailerListView = (ListView) getView().findViewById(R.id.trailer_list_view);
                mArrayListTrailer = new ArrayList<>();
                if (data.moveToFirst()) {
                    do {
                        String trailerName = data.getString(COL_TRAILER_NAME);
                        String trailerSource = data.getString(COL_TRAILER_SOURCE);
                        String resultTrailer = trailerName + "\n" + trailerSource;

                        mArrayListTrailer.add(resultTrailer);
                    } while (data.moveToNext());
                }
                //Log.v("Trailer ArrayList", String.valueOf(mArrayListTrailer));

                mArrayAdapterTrailer = new ArrayAdapter<>(getContext(),
                        R.layout.list_item_trailer,
                        R.id.list_item_trailer_textView,
                        mArrayListTrailer);
                trailerListView.setAdapter(mArrayAdapterTrailer);

                //http://stackoverflow.com/questions/3495890/how-can-i-put-a-listview-into-a-scrollview-without-it-collapsing
                Utility.setListViewHeightBasedOnChildren(trailerListView);
                mArrayAdapterTrailer.notifyDataSetChanged();
                break;
            default:
                throw new UnsupportedOperationException("Unknown Loader"+loader.getId());



        }
    }




    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
