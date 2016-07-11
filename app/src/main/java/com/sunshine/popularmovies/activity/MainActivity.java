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

import com.facebook.stetho.Stetho;
import com.sunshine.popularmovies.R;
import com.sunshine.popularmovies.fragment.DetailFragment;
import com.sunshine.popularmovies.fragment.FavouriteFragment;
import com.sunshine.popularmovies.fragment.MovieFragment;
import com.sunshine.popularmovies.sync.MovieSyncAdapter;

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback, FavouriteFragment.Callback {

    private final String LOG_TAG = getClass().getSimpleName();
    private boolean mTwoPane;
    private String pref;
    private String newPref;
    private String PREFERENCE = "preference";
    private static final String MOVIEFRAGMENT_TAG = "MF";
    private static final String DETAILFRAGMENT_TAG = "DF";
    private static final String FAVOURITEFRAGMENT_TAG = "FF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "On Create Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.detail_container);
        mTwoPane = view != null && view.getVisibility() == View.VISIBLE;

        if (savedInstanceState == null) {
            Log.v(LOG_TAG, "On Saved Instance Called");
            pref = PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");

            if (!pref.equals("favourite"))
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new MovieFragment(), MOVIEFRAGMENT_TAG).commit();
            else
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new FavouriteFragment(), FAVOURITEFRAGMENT_TAG).commit();
        }

        if (savedInstanceState != null) {
            pref = savedInstanceState.getString(PREFERENCE);
        }
        Stetho.initializeWithDefaults(this);
        MovieSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        newPref = PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");
        Log.v(LOG_TAG, "On Start Called");
        Log.v(LOG_TAG, "New Preference: " + newPref);
        Log.v(LOG_TAG, "Old Preference: " + pref);
        //This case is only applicable when user installs the app for the first time
        if (pref.equals("popular") && newPref.equals("popular"))
        {
            MovieFragment mf= (MovieFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
            mf.preferenceChanged();

        }
        if (!pref.equals(newPref)) {
            Log.v(LOG_TAG, "Preference changed");
            if (!newPref.equals("favourite")) {
                Log.v(LOG_TAG, "Preference not changed to fav");
                getSupportFragmentManager().beginTransaction().replace(R.id.container,new MovieFragment(),MOVIEFRAGMENT_TAG).commit();
            }
            else {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new FavouriteFragment(), FAVOURITEFRAGMENT_TAG).commit();
                Log.v(LOG_TAG, "Preference changed to fav");
            }
        } else {
            Log.v(LOG_TAG, "Preference not changed");
            if (!pref.equals("favourite"))
                getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
            else
                getSupportFragmentManager().findFragmentByTag(FAVOURITEFRAGMENT_TAG);
        }


    }

    @Override
    protected void onResume() {
        Log.v(LOG_TAG,"On Resume Called");
        super.onResume();
        newPref = PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");

        if (!pref.equals(newPref)) {
            if (!newPref.equals("favourite")) {
                MovieFragment mf= (MovieFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mf).commit();
                mf.preferenceChanged();
            } else {
                FavouriteFragment ff= (FavouriteFragment) getSupportFragmentManager().findFragmentByTag(FAVOURITEFRAGMENT_TAG);
                getSupportFragmentManager().beginTransaction().replace(R.id.container,ff).commit();
            }
        } else {
            if (!pref.equals("favourite"))
                getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
            else
                getSupportFragmentManager().findFragmentByTag(FAVOURITEFRAGMENT_TAG);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pref = PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String pref = PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");
        outState.putString(PREFERENCE, pref);
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
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable("DETAIL", movieUri);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, detailFragment, DETAILFRAGMENT_TAG).commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(movieUri);
            startActivity(intent);
        }

    }
}
