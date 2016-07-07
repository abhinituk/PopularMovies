package com.sunshine.popularmovies.fragment;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sunshine.popularmovies.R;
import com.sunshine.popularmovies.adapter.CustomReviewAdapter;
import com.sunshine.popularmovies.adapter.CustomTrailerAdapter;
import com.sunshine.popularmovies.data.MovieContract;
import com.sunshine.popularmovies.network.FetchReviewTask;
import com.sunshine.popularmovies.network.FetchTrailerTask;

import java.io.File;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int LOADER_ID = 100;
    private final int REVIEW_LOADER_ID = 200;
    private final int TRAILER_LOADER_ID = 300;


    //CustomReviewAdapter
    private CustomReviewAdapter mCustomReviewAdapter;
    private RecyclerView mRecyclerViewReview;
    private RecyclerView.LayoutManager mLayoutManagerReview;

    //CustomTrailerAdapter
    private CustomTrailerAdapter mCustomTrailerAdapter;
    private RecyclerView mRecyclerViewTrailer;
    private RecyclerView.LayoutManager mLayoutManagerTrailer;

    private static final String[] DETAIL_COLUMN = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG,
            MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_FAVOURITE,
            MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH};

    private static int COL_ID = 0;
    private static int COL_MOVIE_ID = 1;
    private static final int COL_POSTER_PATH = 2;
    private static final int COL_OVERVIEW = 3;
    private static final int COL_POPULARITY = 4;
    private static final int COL_RELEASE_DATE = 5;
    private static final int COL_MOVIE_TITLE = 6;
    private static final int COL_VOTE_AVG = 7;
    private static final int COL_VOTE_COUNT = 8;
    private static final int COL_FAVOURITE = 9;
    private static final int COL_BACKDROP_PATH = 10;

    private static final String[] REVIEW_COLUMN = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COL_REVIEW_ID,
            MovieContract.ReviewEntry.COL_REVIEW_AUTHOR,
            MovieContract.ReviewEntry.COL_REVIEW_CONTENT
    };

    private static int COL_REVIEW_ID = 0;
    private static int COL_REVIEW_MOVIE_ID = 1;
    private static int COL_REVIEW_AUTHOR = 2;
    private static int COL_REVIEW_CONTENT = 3;

    private static final String[] TRAILER_COLUMN = {
            MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COL_TRAILER_ID,
            MovieContract.TrailerEntry.COL_TRAILER_NAME,
            MovieContract.TrailerEntry.COL_TRAILER_SOURCE,
            MovieContract.TrailerEntry.COL_TRAILER_THUMBNAIL
    };


    private Uri mUri;
    private ShareActionProvider mShareActionProvider;
    private int mMovieId;
    private int favourite;
    private String shareIntentText;

    private FloatingActionButton fabFavourite;

    //movie.movie_id=?
    private final String movieWithMovieId = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";

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


    private void updateTrailer() {
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(getContext());
        if (mUri!=null)
            fetchTrailerTask.execute(MovieContract.MovieEntry.getMovieId(mUri));
    }

    private void updateReview() {
        FetchReviewTask fetchReviewTask = new FetchReviewTask(getContext());
        if (mUri!=null)
            fetchReviewTask.execute(MovieContract.MovieEntry.getMovieId(mUri));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
//         Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.

        super.onCreateOptionsMenu(menu, inflater);
    }

    //Creating the ShareActionProvider
    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareIntentText);
        return shareIntent;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args= getArguments();
        if (args!=null)
            mUri= args.getParcelable("DETAIL URI");
        if (mUri!=null)
            mMovieId = MovieContract.MovieEntry.getMovieId(mUri);
        View view = inflater.inflate(R.layout.fragment_detail, container, false);


        //Implementing the Collapsing toolbar layout
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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


        fabFavourite = (FloatingActionButton) view.findViewById(R.id.favorite);

        fabFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                        DETAIL_COLUMN,
                        movieWithMovieId,
                        new String[]{String.valueOf(mMovieId)}, null);

                assert cursor != null;
                if (cursor.moveToFirst()) {
                    favourite = cursor.getInt(COL_FAVOURITE);
                }

                if (favourite == 0) {
                    ContentValues cv = new ContentValues();
                    cv.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, 1);
                    getContext().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                            cv,
                            movieWithMovieId,
                            new String[]{String.valueOf(mMovieId)});
                    Snackbar.make(view, "Movie Added As Favourite", Snackbar.LENGTH_LONG).show();

                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, 0);
                    getContext().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                            cv,
                            movieWithMovieId,
                            new String[]{String.valueOf(mMovieId)});
                    Snackbar.make(view, "Movie Removed From Favorite", Snackbar.LENGTH_LONG).show();

                }

                cursor.close();

            }
        });


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
        switch (loader.getId()) {
            case LOADER_ID:
                if (!data.moveToFirst()) {
                    return;
                }

                String title = data.getString(COL_MOVIE_TITLE);
                String overview = data.getString(COL_OVERVIEW);
                String vote_average = data.getString(COL_VOTE_AVG);
                String release_date = data.getString(COL_RELEASE_DATE);
                String backdrop_path = data.getString(COL_BACKDROP_PATH);
                String poster_path = data.getString(COL_POSTER_PATH);
                String vote_count = data.getString(COL_VOTE_COUNT);
                String popularity = data.getString(COL_POPULARITY);
                int favorite = data.getInt(COL_FAVOURITE);
                shareIntentText = title + "\n" + release_date + "\n" + overview;
                if (mShareActionProvider != null)
                    mShareActionProvider.setShareIntent(createShareMovieIntent());

                assert getView() != null;
                CollapsingToolbarLayout collapsingToolbar =
                        (CollapsingToolbarLayout) getView().findViewById(R.id.collapsing_toolbar);
                ImageView mBackdrop = (ImageView) getView().findViewById(R.id.backdrop);

                ImageView poster = (ImageView) getView().findViewById(R.id.poster);
                TextView releaseDate = (TextView) getView().findViewById(R.id.release_date);
                TextView voteAvg = (TextView) getView().findViewById(R.id.vote_avg);
                TextView voteCount = (TextView) getView().findViewById(R.id.vote_count);
                TextView popularityText = (TextView) getView().findViewById(R.id.popularity);
                TextView overView = (TextView) getView().findViewById(R.id.overview_textView);

                collapsingToolbar.setTitle(title);

                if (favorite == 0)
                    fabFavourite.setImageResource(R.drawable.ic_favorite);
                else fabFavourite.setImageResource(R.drawable.ic_favorite_marked);

                Picasso.with(getContext()).load(backdrop_path)
                        .error(R.drawable.placeholder_error)
                        .placeholder(R.drawable.placeholder)
                        .into(mBackdrop);


                Picasso.with(getContext())
                        .load(new File(poster_path))
                        .error(R.drawable.placeholder_error)
                        .placeholder(R.drawable.placeholder)
                        .into(poster);
                String overviewTitle = "Overview: " + "\n" + overview;
                String releaseDateText="Release Date: " + release_date;
                String voteAvgText="Vote Average: " + vote_average;
                String voteCountText="Vote Count: " + vote_count;
                String popularityTextContent="Popularity: " + popularity;

                releaseDate.setText(releaseDateText);
                voteAvg.setText(voteAvgText);
                voteCount.setText(voteCountText);
                popularityText.setText(popularityTextContent);
                overView.setText(overviewTitle);


                break;
            case REVIEW_LOADER_ID:
                mCustomReviewAdapter.swapCursor(data);
                break;

            case TRAILER_LOADER_ID:
                mCustomTrailerAdapter.swapCursor(data);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Loader" + loader.getId());


        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
