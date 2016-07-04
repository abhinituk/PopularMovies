package com.sunshine.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().add(R.id.detail_container, new DetailFragment()).commit();
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

