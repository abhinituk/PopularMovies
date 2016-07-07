package com.sunshine.popularmovies.network;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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


public class FetchMovieTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;

    public FetchMovieTask(Activity activity) {
        mContext = activity;
    }

    @Override
    protected Void doInBackground(String... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;
        String appid = "89d7f04f6c9ed1a948da0d08299b5afe";


        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "?";
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
                return null;
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
                return null;

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
            getMovieDatafromJson(movieJsonStr,params[0]);
        } catch (Exception e) {
            Log.e("Error", e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
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
                    //String path= Utility.downloadImagesToIntenalStorage(mContext,poster_path,movieId)+"";

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
                    mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
                }

                Cursor cursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
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

    
}

