package com.sunshine.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.sunshine.popularmovies.R;
import com.sunshine.popularmovies.data.MovieContract;
import com.sunshine.popularmovies.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Abhishek on 11-07-2016.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60*180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");
        String pref= Utility.getPref(getContext());
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;
        String appid = "89d7f04f6c9ed1a948da0d08299b5afe";


        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + pref + "?";
            final String APPID = "api_key";
            Uri builtUri = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(APPID, appid)
                    .build();
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return ;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return ;

            }
            movieJsonStr = buffer.toString();
            //Log.v("Movie Data",movieJsonStr);

        } catch (IOException e) {
            Log.e("MovieData Fragment", "Error ", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("MovieData Fragment", "Error closing stream", e);
                }
            }
        }
        try {
            getMovieDatafromJson(movieJsonStr,pref);
        } catch (Exception e) {
            Log.e("Error", e.getMessage(), e);
            e.printStackTrace();
        }
    }
    private void getMovieDatafromJson(String movieJsonStr,String pref) {

        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String MOVIE_ID = "id";
        final String TMDB_TITLE = "original_title";
        final String BASE_URL = "http://image.tmdb.org/t/p/w185/";
        final String BASE_BACKDROP_URL="http://image.tmdb.org/t/p/w500/";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_VOTE_COUNT = "vote_count";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_BACKDROP_PATH="backdrop_path";


        if (movieJsonStr != null) {
            try {
                JSONObject json = new JSONObject(movieJsonStr);
                JSONArray array = json.getJSONArray("results");
                Vector<ContentValues> cVector = new Vector<>(array.length());

                for (int i = 0; i < array.length(); i++) {
                    JSONObject movieData = array.getJSONObject(i);
                    String path = BASE_URL + movieData.getString(TMDB_POSTER_PATH);
                    String backdrop_path= BASE_BACKDROP_URL+movieData.getString(TMDB_BACKDROP_PATH);
                    int movieId = movieData.getInt(MOVIE_ID);

                    String poster_path= Utility.storeImages(movieId,path);
                    Log.v("Path",poster_path);


                    String overview = movieData.getString(TMDB_OVERVIEW);
                    String title = movieData.getString(TMDB_TITLE);
                    double vote_average = movieData.getDouble(TMDB_VOTE_AVERAGE);
                    String release_date = movieData.getString(TMDB_RELEASE_DATE);

                    double popularity = movieData.getDouble(TMDB_POPULARITY);
                    double vote_count = movieData.getDouble(TMDB_VOTE_COUNT);


                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, poster_path);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH,backdrop_path);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, overview);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, title);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG, vote_average);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, release_date);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY, popularity);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_COUNT, vote_count);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_FAVOURITE,0);
                    if(pref.equals("popular"))
                        movieValues.put(MovieContract.MovieEntry.COLUMN_FLAG,"popular");
                    else if (pref.equals("top_rated"))
                        movieValues.put(MovieContract.MovieEntry.COLUMN_FLAG,"top_rated");

                    cVector.add(movieValues);
                }
                if (cVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVector.size()];
                    cVector.toArray(cvArray);
                    getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
                }

                Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                assert cursor != null;
                cVector = new Vector<>(cursor.getCount());
                if (cursor.moveToFirst()) {
                    do {
                        ContentValues cv = new ContentValues();
                        DatabaseUtils.cursorRowToContentValues(cursor, cv);
                        cVector.add(cv);
                    } while (cursor.moveToNext());
                }
                //Log.d(LOG_TAG, "FetchWeatherTask Complete. " + cVector.size() + " Inserted");



            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Internet Connectivity", "Enable Data Connection");
            }
        }
    }


    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
