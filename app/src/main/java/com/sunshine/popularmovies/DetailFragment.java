package com.sunshine.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Abhishek on 13-05-2016.
 */
public class DetailFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id == R.id.action_settings)
        {
            startActivity(new Intent(getActivity(),SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_detail,container,false);

        /*String title;
        String overview;
        String release_date;
        String vote_average;
        String poster;*/

        Intent intent = getActivity().getIntent();
        if(intent!=null)
        {
            MovieData movieData=intent.getParcelableExtra("Movie Data");


            ((TextView) rootView.findViewById(R.id.detail_title)).setText(movieData.title);
            ((TextView) rootView.findViewById(R.id.detail_overview)).setText(movieData.overview);
            ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(movieData.release_date);
            ((TextView) rootView.findViewById(R.id.detail_vote_average)).setText((int) movieData.vote_average);
            ImageView movie_poster= (ImageView) rootView.findViewById(R.id.detail_image_view);
            Picasso.with(getActivity()).load(movieData.ImageUrl).into(movie_poster);
        }


        return rootView;
    }
}
