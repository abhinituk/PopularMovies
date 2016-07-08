package com.sunshine.popularmovies.fragment;


import android.annotation.TargetApi;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sunshine.popularmovies.R;
import com.sunshine.popularmovies.adapter.CustomMovieAdapter;
import com.sunshine.popularmovies.data.MovieContract;
import com.sunshine.popularmovies.network.FetchMovieTask;


public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG= getClass().getSimpleName();
    private CustomMovieAdapter mCustomMovieAdapter;
    private static final int LOADER_ID = 0;
    private static RecyclerView mRecycledGridView;
    private int mPosition= RecyclerView.NO_POSITION;
    private RecyclerView.LayoutManager mLayoutManager;

    private static final String[] MOVIE_COLUMN = {MovieContract.MovieEntry._ID
            , MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH
            , MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_FLAG,
            MovieContract.MovieEntry.COLUMN_FAVOURITE,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE};

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
    }

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onResume() {
        Log.v(LOG_TAG,"On Resume Called");
        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }


    private void movieDataUpdate() {
        Log.v(LOG_TAG,"Update Movie data");
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity());
        String pref = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("sort_by", "popular");
        if (!pref.equals("favourite"))
            fetchMovieTask.execute(pref);
    }


    //In this fragment draws its UI for the first time
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG,"On CreateView Called");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecycledGridView = (RecyclerView) rootView.findViewById(R.id.recycled_grid_view);
        mRecycledGridView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getContext(),2);
        mRecycledGridView.setLayoutManager(mLayoutManager);

        mCustomMovieAdapter = new CustomMovieAdapter(getActivity(), new CustomMovieAdapter.CustomMovieAdapterOnClickHandler() {
            @Override
            public void onClick(int movieId, CustomMovieAdapter.ViewHolder vh) {
                ( (Callback)getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieWithMovieIdUri(movieId));
                mPosition= vh.getAdapterPosition();
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

        //movie.flag=?
        final String movieWithFlag = MovieContract.MovieEntry.COLUMN_FLAG + "=?";

        String pref = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("sort_by", "popular");
        if (!pref.equals("favourite"))

            return new CursorLoader(getActivity(), MovieContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMN,
                    movieWithFlag,
                    new String[]{pref},
                    null);
        else return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCustomMovieAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCustomMovieAdapter.swapCursor(null);
    }
}
