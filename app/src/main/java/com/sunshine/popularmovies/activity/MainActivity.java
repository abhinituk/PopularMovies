package com.sunshine.popularmovies.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.facebook.stetho.Stetho;
import com.sunshine.popularmovies.R;
import com.sunshine.popularmovies.fragment.DetailFragment;
import com.sunshine.popularmovies.fragment.FavouriteFragment;
import com.sunshine.popularmovies.fragment.MovieFragment;

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback, FavouriteFragment.Callback {

    private final String LOG_TAG = getClass().getSimpleName();
    private boolean mTwoPane;

    private static final String MOVIEFRAGMENT_TAG = "MF";
    private static final String DETAILFRAGMENT_TAG = "DF";
    private static final String FAVOURITEFRAGMENT_TAG = "FF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(LOG_TAG, "On Create Called");

        View view = findViewById(R.id.detail_container);
        mTwoPane = view != null && view.getVisibility() == View.VISIBLE;
        Log.v(LOG_TAG, String.valueOf(mTwoPane));

        if (savedInstanceState == null) {
            String pref = PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");

            if (!pref.equals("favourite"))
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new MovieFragment(), MOVIEFRAGMENT_TAG).commit();
            else
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new FavouriteFragment(), FAVOURITEFRAGMENT_TAG).commit();


        }


        Stetho.initializeWithDefaults(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "OnStart Called");
        String pref = PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");

        if (!pref.equals("favourite"))
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new MovieFragment()).commit();
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new FavouriteFragment()).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume Called");
        String pref = PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");

        if (!pref.equals("favourite"))
            getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);

        else
            getSupportFragmentManager().findFragmentByTag(FAVOURITEFRAGMENT_TAG);
        getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "On Pause Called");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri movieUri) {
        if (mTwoPane)
        {
            Bundle args= new Bundle();
            args.putParcelable("DETAIL",movieUri);
            Log.v(LOG_TAG, String.valueOf(movieUri));

            DetailFragment detailFragment= new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container,detailFragment,DETAILFRAGMENT_TAG).commit();
        }
        else
        {
            Log.v(LOG_TAG, String.valueOf(movieUri));
            Intent intent = new Intent(this, DetailActivity.class)
                        .setData(movieUri);
            startActivity(intent);
        }

    }
}
