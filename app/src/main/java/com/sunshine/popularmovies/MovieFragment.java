package com.sunshine.popularmovies;


import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.sunshine.popularmovies.data.MovieContract;


public class MovieFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private CustomMovieAdapter mCustomMovieAdapter;
    private static final int LOADER_ID = 0;
    static GridView gridView;


    //onCreate is used to create the fragment. In this put components which has to be retained when fragment is paused or stopped & then resumed.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState == null || !savedInstanceState.containsKey("Movie Data")) {
//            mMovieDataArrayList = new ArrayList<>(Collections.singletonList(FetchMovieTask.mMovieData));
//        } else {
//            mMovieDataArrayList = savedInstanceState.getParcelableArrayList("Movie com.sunshine.popularmovies.data");
//        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        outState.putParcelableArrayList("Movie Data", mMovieDataArrayList);
//        super.onSaveInstanceState(outState);
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
    public void onStart() {
        super.onStart();
        movieDataUpdate();

    }

    public void movieDataUpdate() {


        FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity());
        fetchMovieTask.execute(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("sort_by", "popular"));


    }


    //In this fragment draws its UI for the first time
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.grid_view_fragment);


        mCustomMovieAdapter = new CustomMovieAdapter(getActivity(), null, 0);
        gridView.setAdapter(mCustomMovieAdapter);
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


        return new CursorLoader(getActivity(), MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
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
