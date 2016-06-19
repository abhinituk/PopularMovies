package com.sunshine.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
 * Created by Abhishek on 19-06-2016.
 */
public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieData>> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    ArrayList<MovieData> mMovieDataArrayList;
    static MovieData mMovieData;
    private CustomMovieAdapter mCustomMovieAdapter;
    private Context mContext;

    public FetchMovieTask(Activity activity) {
        mContext = activity;
    }

    @Override
    protected ArrayList<MovieData> doInBackground(String... params) {
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
            Log.v("Movie Data",movieJsonStr);

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
            return getMovieDatafromJson(movieJsonStr);
        } catch (Exception e) {
            Log.e("Error", e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }


    private ArrayList<MovieData> getMovieDatafromJson(String movieJsonStr) throws JSONException {

        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String MOVIE_ID = "id";
        final String TMDB_TITLE = "original_title";
        final String BASE_URL = "http://image.tmdb.org/t/p/w185/";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_VOTE_COUNT = "vote_count";
        final String TMDB_VOTE_AVERAGE = "vote_average";


        if (movieJsonStr != null) {
            try {
                JSONObject json = new JSONObject(movieJsonStr);
                JSONArray array = json.getJSONArray("results");
                Vector<ContentValues> cVector = new Vector<>(array.length());

                for (int i = 0; i < array.length(); i++) {
                    JSONObject movieData = array.getJSONObject(i);
                    String poster_path = BASE_URL + movieData.getString(TMDB_POSTER_PATH);
                    String overview = movieData.getString(TMDB_OVERVIEW);
                    String title = movieData.getString(TMDB_TITLE);
                    double vote_average = movieData.getDouble(TMDB_VOTE_AVERAGE);
                    String release_date = movieData.getString(TMDB_RELEASE_DATE);
                    int movieId = movieData.getInt(MOVIE_ID);
                    double popularity = movieData.getDouble(TMDB_POPULARITY);
                    double vote_count = movieData.getDouble(TMDB_VOTE_COUNT);

                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, poster_path);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, overview);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, title);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG, vote_average);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, release_date);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY, popularity);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_COUNT, vote_count);
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
                Log.d(LOG_TAG, "FetchWeatherTask Complete. " + cVector.size() + " Inserted");
                mMovieDataArrayList= convertValuesToMovieDataObject(cVector);



            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Internet Connectivity", "Enable Data Connection");
            }
        }
        return mMovieDataArrayList;
    }

    public static ArrayList<MovieData> convertValuesToMovieDataObject(Vector<ContentValues> cv) {
        ArrayList<MovieData> arrayList = new ArrayList<>(cv.size());

        for (int i = 0; i < cv.size(); i++) {
            ContentValues movieValue = cv.elementAt(i);

            int movieId= movieValue.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            String title= movieValue.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
            String overview= movieValue.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW);
            String posterpath = movieValue.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH);
            double popularity = movieValue.getAsDouble(MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY);
            double vote_count= movieValue.getAsDouble(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_COUNT);
            double vote_avg= movieValue.getAsDouble(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG);
            String releaseDate= movieValue.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE);

            mMovieData= new MovieData(title,overview,vote_avg,releaseDate,posterpath,movieId,popularity,vote_count);
            arrayList.add(mMovieData);


        }
        return arrayList;
    }

//    private long addMovie(int movieId,String title,String overview,String posterPath,
//                          double popularity,double voteCount,double voteAvg,String releaseDate )
//    {
//        long movieRowId;
//        Cursor cursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
//                new String[]{MovieContract.MovieEntry._ID},
//                MovieContract.MovieEntry.COLUMN_MOVIE_ID+" = ?",
//                new String[]{String.valueOf(movieId)},
//                null);
//        if(cursor!=null && cursor.moveToFirst())
//        {
//            int columnId= cursor.getColumnIndex(MovieContract.MovieEntry._ID);
//            movieRowId= cursor.getInt(columnId);
//        }
//        else
//        {
//            ContentValues values= new ContentValues();
//            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,movieId);
//            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,title);
//            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,posterPath);
//            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY,popularity);
//            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_COUNT,voteCount);
//            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG,voteAvg);
//            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,releaseDate);
//
//            Uri insertedUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,values);
//
//            movieRowId= ContentUris.parseId(insertedUri);
//        }
//        assert cursor != null;
//        cursor.close();
//        return movieRowId;
//
//    }


    @Override
    protected void onPostExecute(ArrayList<MovieData> strings) {
        if (strings != null) {
            if (mContext != null)//http://stackoverflow.com/questions/28414480/android-nullpointerexception-from-creating-an-adapter
            {
                mCustomMovieAdapter = new CustomMovieAdapter(mContext, strings);
                MovieFragment.gridView.setAdapter(mCustomMovieAdapter);
                mCustomMovieAdapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(mContext, "Something Went Wrong...Check Your Internet Connection & Try Again", Toast.LENGTH_LONG).show();
        }
    }
}

