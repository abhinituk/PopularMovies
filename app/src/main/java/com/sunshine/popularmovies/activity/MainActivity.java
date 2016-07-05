package com.sunshine.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;
import com.sunshine.popularmovies.R;
import com.sunshine.popularmovies.fragment.FavouriteFragment;
import com.sunshine.popularmovies.fragment.MovieFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            String pref= PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");

            if(!pref.equals("favourite"))
                getSupportFragmentManager().beginTransaction().add(R.id.container,new MovieFragment()).commit();
            else
                getSupportFragmentManager().beginTransaction().add(R.id.container,new FavouriteFragment()).commit();


        }
        Stetho.initializeWithDefaults(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        String pref= PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");

        if(!pref.equals("favourite"))
            getSupportFragmentManager().beginTransaction().add(R.id.container,new MovieFragment()).commit();
        else
            getSupportFragmentManager().beginTransaction().add(R.id.container,new FavouriteFragment()).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("Main Activity", "onResume Called");
        String pref= PreferenceManager.getDefaultSharedPreferences(this).getString("sort_by", "popular");
        if (!pref.equals("favourite"))
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new MovieFragment()).commit();
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new FavouriteFragment()).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("Main Activity","On Pause Called");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if(id == R.id.action_settings)
        {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
