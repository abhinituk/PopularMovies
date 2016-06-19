package com.sunshine.popularmovies;


import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Collections;


public class MovieFragment extends Fragment {
    private CustomMovieAdapter mCustomMovieAdapter;


    static GridView gridView;
    ArrayList<MovieData> mMovieDataArrayList;


    //onCreate is used to create the fragment. In this put components which has to be retained when fragment is paused or stopped & then resumed.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey("Movie Data")) {
            mMovieDataArrayList = new ArrayList<>(Collections.singletonList(FetchMovieTask.mMovieData));
        } else {
            mMovieDataArrayList = savedInstanceState.getParcelableArrayList("Movie com.sunshine.popularmovies.data");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("Movie Data", mMovieDataArrayList);
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

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity());
                fetchMovieTask.execute(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("sort_by", "popular"));

            }
        });

    }


    //In this fragment draws its UI for the first time
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.grid_view_fragment);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                String title = mCustomMovieAdapter.getItem(position).title;
//                String overview = mCustomMovieAdapter.getItem(position).overview;
//                String release_date=mCustomMovieAdapter.getItem(position).release_date;
//                String vote_average = String.valueOf(mCustomMovieAdapter.getItem(position).vote_average);
//                String poster_path = mCustomMovieAdapter.getItem(position).ImageUrl;
//
//                MovieData movieData=new MovieData(title,overview,release_date,vote_average,poster_path);
//                Intent intent = new Intent(getActivity(),DetailActivity.class)
//                        .putExtra("Movie Data",movieData);
//                startActivity(intent);
//
//
//            }
//        });
        return rootView;
    }


}
