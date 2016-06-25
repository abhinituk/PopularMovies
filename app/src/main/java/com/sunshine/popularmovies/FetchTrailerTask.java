package com.sunshine.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sunshine.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Abhishek on 23-06-2016.
 */
public class FetchTrailerTask extends AsyncTask<Integer, Void, ArrayList<String>> {

    ArrayList<String> mArrayListTrailer = new ArrayList<>();
    Context mContext;

    public FetchTrailerTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected ArrayList<String> doInBackground(Integer... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonString = null;
        String api_key = "89d7f04f6c9ed1a948da0d08299b5afe";

        try {

            String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos?";
            String APP_ID = "api_key";
            Uri uri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(APP_ID, api_key)
                    .build();

            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null)
                return null;
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder buffer = new StringBuilder();
            String line;
            if ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            if (buffer.length() == 0)
                return null;
            jsonString = buffer.toString();
            Log.v("Trailer Json Data", jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            getTrailerDataFromJson(jsonString, params[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getTrailerDataFromJson(String jsonString, int movieId) throws JSONException {
        final String TMDB_TRAILER_RESULTS = "results";
        final String TMDB_TRAILER_KEY = "key";
        final String TMDB_TRAILER_NAME = "name";

        if (jsonString != null) {

            JSONObject object = new JSONObject(jsonString);
            JSONArray result = object.getJSONArray(TMDB_TRAILER_RESULTS);

            Vector<ContentValues> valuesVector = new Vector<>(result.length());

            for (int i = 0; i < result.length(); i++) {
                JSONObject trailer = result.getJSONObject(i);
                String key = "https://www.youtube.com/watch?v=" + trailer.getString(TMDB_TRAILER_KEY);
                String name = trailer.getString(TMDB_TRAILER_NAME);

                ContentValues values = new ContentValues();
                values.put(MovieContract.TrailerEntry.COL_TRAILER_ID, movieId);
                values.put(MovieContract.TrailerEntry.COL_TRAILER_NAME, name);
                values.put(MovieContract.TrailerEntry.COL_TRAILER_SOURCE, key);

                valuesVector.add(values);

                String trailerData = key + "\n" + name;

                mArrayListTrailer.add(trailerData);
            }
            if (valuesVector.size() > 0) {
                ContentValues contentValues[] = new ContentValues[]{};
                valuesVector.toArray(contentValues);
                int returnCount = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, contentValues);
                Log.v("Return Count", String.valueOf(returnCount));
            }

            Cursor cursor = mContext.getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            assert cursor != null;
            Log.v("Trailer Cursor size", String.valueOf(cursor.getCount()));

            valuesVector = new Vector<>(cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cursor, cv);
                    valuesVector.add(cv);
                } while (cursor.moveToNext());
            }
            Log.d("FetchTrailerTask", "FetchTrailerTask Complete. " + valuesVector.size() + " Inserted");


        }
    }
}
