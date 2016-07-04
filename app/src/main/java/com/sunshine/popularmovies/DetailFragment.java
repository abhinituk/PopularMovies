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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sunshine.popularmovies.data.MovieContract;

import java.util.ArrayList;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int LOADER_ID = 100;
    private final int REVIEW_LOADER_ID = 200;
    private final int TRAILER_LOADER_ID = 300;


    static ArrayAdapter<String> mArrayAdapterReview;
    static ArrayList<String> mArrayListReview;

    //CustomReviewAdapter
    CustomReviewAdapter mCustomReviewAdapter;
    RecyclerView mRecyclerViewReview;
    RecyclerView.LayoutManager mLayoutManagerReview;

    //CustomTrailerAdapter
    CustomTrailerAdapter mCustomTrailerAdapter;
    RecyclerView mRecyclerViewTrailer;
    RecyclerView.LayoutManager mLayoutManagerTrailer;

    private static String[] DETAIL_COLUMN = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG,
            MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_FAVOURITE};

    private static int COL_ID = 0;
    private static int COL_MOVIE_ID = 1;
    private static int COL_POSTER_PATH = 2;
    private static int COL_OVERVIEW = 3;
    private static int COL_POPULARITY = 4;
    private static int COL_RELEASE_DATE = 5;
    private static int COL_MOVIE_TITLE = 6;
    private static int COL_VOTE_AVG = 7;
    private static int COL_VOTE_COUNT = 8;
    private static int COL_FAVOURITE = 9;

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
            MovieContract.TrailerEntry.COL_TRAILER_SOURCE,
            MovieContract.TrailerEntry.COL_TRAILER_THUMBNAIL
    };

    private static int COL_TRAILER_ID = 0;
    private static int COL_TRAILER_MOVIE_ID = 1;
    private static int COL_TRAILER_NAME = 2;
    private static int COL_TRAILER_SOURCE = 3;
    private static int COL_TRAILER_THUMBNAIL = 4;


    Uri mUri;
    private ShareActionProvider mShareActionProvider;
    int mMovieId, favourite;

    //movie.movie_id=?
    final String movieWithMovieId = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";

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
        inflater.inflate(R.menu.detailfragment, menu);
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mShareActionProvider!=null)
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Creating the ShareActionProvider
    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Movie Data");
        return shareIntent;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUri = getActivity().getIntent().getData();

        mMovieId = MovieContract.MovieEntry.getMovieId(mUri);
        View view = inflater.inflate(R.layout.fragment_detail, container, false);





        //CustomTrailerAdapter
        mRecyclerViewTrailer = (RecyclerView) view.findViewById(R.id.recycled_trailer_list_view);
        mRecyclerViewTrailer.setHasFixedSize(true);

        mLayoutManagerTrailer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewTrailer.setLayoutManager(mLayoutManagerTrailer);

        mCustomTrailerAdapter = new CustomTrailerAdapter(getContext(), new CustomTrailerAdapter.CustomTrailerAdapterOnClickHandler() {
            @Override
            public void onClick(String videoSource, CustomTrailerAdapter.ViewHolder vh) {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(videoSource));
                startActivity(intent);
            }
        });
        mRecyclerViewTrailer.setAdapter(mCustomTrailerAdapter);

        //CustomReviewAdapter
        mRecyclerViewReview = (RecyclerView) view.findViewById(R.id.recycled_review_list_view);
        mRecyclerViewReview.setHasFixedSize(true);

        mLayoutManagerReview = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewReview.setLayoutManager(mLayoutManagerReview);

        mCustomReviewAdapter = new CustomReviewAdapter(getContext());
        mRecyclerViewReview.setAdapter(mCustomReviewAdapter);



//        final Button favouriteButton = (Button) view.findViewById(R.id.button);
//        favouriteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
//                        DETAIL_COLUMN,
//                        movieWithMovieId,
//                        new String[]{String.valueOf(mMovieId)}, null);
//
//                assert cursor != null;
//                if (cursor.moveToFirst()) {
//                    favourite = cursor.getInt(COL_FAVOURITE);
//                }
//
//                if (favourite == 0) {
//                    ContentValues cv = new ContentValues();
//                    cv.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, 1);
//                    int markedFavourite = getContext().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
//                            cv,
//                            movieWithMovieId,
//                            new String[]{String.valueOf(mMovieId)});
//
//
//                    if (markedFavourite != 0)
//                        Snackbar.make(v, "Movie Marked As Favourite " + markedFavourite, Snackbar.LENGTH_LONG).show();
//
//                } else {
//                    ContentValues cv = new ContentValues();
//                    cv.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, 0);
//                    int unmarkedFavourite = getContext().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
//                            cv,
//                            movieWithMovieId,
//                            new String[]{String.valueOf(mMovieId)});
//                    if (unmarkedFavourite != 0)
//                        Snackbar.make(v, "Movie Not Marked As Favourite " + unmarkedFavourite, Snackbar.LENGTH_LONG).show();
//
//                }
//
//                cursor.close();
//
//            }
//
//        });
//

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
                // If onCreateOptionsMenu has already happened, we need to update the share intent now.
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareMovieIntent());
                }


                break;
            case REVIEW_LOADER_ID:
                mCustomReviewAdapter.swapCursor(data);
                // If onCreateOptionsMenu has already happened, we need to update the share intent now.
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareMovieIntent());
                }
                break;

            case TRAILER_LOADER_ID:
                mCustomTrailerAdapter.swapCursor(data);
                // If onCreateOptionsMenu has already happened, we need to update the share intent now.
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareMovieIntent());
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Loader" + loader.getId());


        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
