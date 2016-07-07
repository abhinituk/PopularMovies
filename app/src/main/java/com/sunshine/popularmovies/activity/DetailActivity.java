package com.sunshine.popularmovies.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sunshine.popularmovies.R;
import com.sunshine.popularmovies.fragment.DetailFragment;

public class DetailActivity extends AppCompatActivity  {



    Uri mUri;
    int movieId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            Bundle args = new Bundle();
            args.putParcelable("DETAIL URI",getIntent().getData());

            DetailFragment detailFragment= new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().add(R.id.detail_container, detailFragment).commit();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Log.v("Settings clicked ", String.valueOf(id));

        if (id == R.id.action_settings_detail) {
            startActivity(new Intent(this, SettingsActivity.class));
            Log.v("Entered","true");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}

