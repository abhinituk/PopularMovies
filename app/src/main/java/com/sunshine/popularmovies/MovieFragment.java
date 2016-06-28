package com.sunshine.popularmovies;


import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sunshine.popularmovies.data.MovieContract;


public  class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    String DETAIL_URI="URI";
    private CustomMovieAdapter mCustomMovieAdapter;
    private static final int LOADER_ID = 0;
    static GridView gridView;

    private static final String[] MOVIE_COLUMN= {MovieContract.MovieEntry._ID
                                                    , MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH
                                                    , MovieContract.MovieEntry.COLUMN_MOVIE_ID};
    static final int COL_ID=0;
    static final int COL_POSTER_PATH=1;
    static final int COL_MOVIE_ID=2;


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
        super.onSaveInstanceState(outState);
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

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if(cursor!=null)
                {
                    Intent intent= new Intent(getActivity(),DetailActivity.class)
                            .setData(MovieContract.MovieEntry.buildMovieWithMovieIdUri(cursor.getInt(COL_MOVIE_ID)));

                    //Log.v("Movie ID", String.valueOf(cursor.getInt(COL_MOVIE_ID)));
                    startActivity(intent);
                }
            }
        });


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
                MOVIE_COLUMN,
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
