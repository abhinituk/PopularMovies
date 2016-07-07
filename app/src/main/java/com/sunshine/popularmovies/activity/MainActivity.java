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

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback,FavouriteFragment.Callback{
    private boolean mTwoPane;

    private static final String MOVIEFRAGMENT_TAG = "MF";
    private static final String DETAILFRAGMENT_TAG = "DF";
    private static final String FAVOURITEFRAGMENT_TAG = "FF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.detail_container);
        mTwoPane = view != null && view.getVisibility() == View.VISIBLE;
        Log.v("Two Pane", String.valueOf(mTwoPane));

        if (mTwoPane) {

            if (savedInstanceState == null) {
                String pref = PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");

                if (!pref.equals("favourite"))
                    getSupportFragmentManager().beginTransaction().add(R.id.container, new MovieFragment(), MOVIEFRAGMENT_TAG).commit();
                else
                    getSupportFragmentManager().beginTransaction().add(R.id.container, new FavouriteFragment(), FAVOURITEFRAGMENT_TAG).commit();

                getSupportFragmentManager().beginTransaction().add(R.id.detail_container, new DetailFragment(), DETAILFRAGMENT_TAG).commit();


            }
        } else {

            if (savedInstanceState == null) {
                String pref = PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");

                if (!pref.equals("favourite"))
                    getSupportFragmentManager().beginTransaction().add(R.id.container, new MovieFragment()).commit();
                else
                    getSupportFragmentManager().beginTransaction().add(R.id.container, new FavouriteFragment()).commit();


            }
        }


        Stetho.initializeWithDefaults(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        String pref = PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");

        if (!pref.equals("favourite"))
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new MovieFragment()).commit();
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new FavouriteFragment()).commit();
        getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("Main Activity", "onResume Called");
        String pref = PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");

        if (!pref.equals("favourite"))
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new MovieFragment()).commit();
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new FavouriteFragment()).commit();

        getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("Main Activity", "On Pause Called");
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
            args.putParcelable("DETAIL URI",movieUri);
            Log.v("Uri Sent", String.valueOf(movieUri));

            DetailFragment detailFragment= new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container,detailFragment,DETAILFRAGMENT_TAG).commit();
        }
        else
        {
            Intent intent = new Intent(this, DetailActivity.class)
                        .setData(movieUri);
            startActivity(intent);
        }

    }
}
