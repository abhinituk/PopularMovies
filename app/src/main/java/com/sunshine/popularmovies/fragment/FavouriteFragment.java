package com.sunshine.popularmovies.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sunshine.popularmovies.R;
import com.sunshine.popularmovies.activity.DetailActivity;
import com.sunshine.popularmovies.adapter.CustomMovieAdapter;
import com.sunshine.popularmovies.data.MovieContract;

/**
 * Created by Abhishek on 03-07-2016.
 */
public class FavouriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private CustomMovieAdapter mCustomMovieAdapter;
    private static final int LOADER_ID = 0;
    private static RecyclerView mRecycledGridView;
    private RecyclerView.LayoutManager mLayoutManager;

    private static final String[] MOVIE_COLUMN = {MovieContract.MovieEntry._ID
            , MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH
            , MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_FLAG,
            MovieContract.MovieEntry.COLUMN_FAVOURITE,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE};


    //onCreate is used to create the fragment. In this put components which has to be retained when fragment is paused or stopped & then resumed.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh) {
            movieDataUpdate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }


    @Override
    public void onStart() {
        super.onStart();
        movieDataUpdate();

    }

    private void movieDataUpdate() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);

    }


    //In this fragment draws its UI for the first time
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //gridView = (GridView) rootView.findViewById(R.id.grid_view_fragment);

        mRecycledGridView = (RecyclerView) rootView.findViewById(R.id.recycled_grid_view);
        mRecycledGridView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecycledGridView.setLayoutManager(mLayoutManager);

        mCustomMovieAdapter = new CustomMovieAdapter(getActivity(), new CustomMovieAdapter.CustomMovieAdapterOnClickHandler() {
            @Override
            public void onClick(int movieId, CustomMovieAdapter.ViewHolder vh) {
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .setData(MovieContract.MovieEntry.buildMovieWithMovieIdUri(movieId));

                startActivity(intent);
            }
        });
        mRecycledGridView.setAdapter(mCustomMovieAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //movie.favourite
        final String movieMarkedFavourite = MovieContract.MovieEntry.COLUMN_FAVOURITE + "=?";

        return new CursorLoader(getActivity(), MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMN,
                movieMarkedFavourite,
                new String[]{"1"},
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null)
            //noinspection ConstantConditions
            Snackbar.make(getView(),"No Favourite Movie",Snackbar.LENGTH_LONG).show();
        mCustomMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCustomMovieAdapter.swapCursor(null);
    }
}
