package com.sunshine.popularmovies.utility;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sunshine.popularmovies.data.MovieContract;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class Utility {

    private final String LOG_TAG = getClass().getSimpleName();
    private static int COL_REVIEW_ID = 0;
    private static int COL_REVIEW_MOVIE_ID = 1;
    private static int COL_REVIEW_AUTHOR = 2;
    private static int COL_REVIEW_CONTENT = 3;
    public static String pref;
    private static String path;

    public static String getPref(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context).getString("sort_by", "popular");
        if (!pref.equals("favourite"))
            return pref;
        else
            return null;
    }


    public static String storeImages(int movieId, String posterPath) {
        try {
            URL url = new URL(posterPath);
            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n = 0;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();

            File directory = new File(Environment.getExternalStorageDirectory() + "/Images");
            if (!directory.exists()) {
                directory.mkdir();
            }
            String path = movieId + ".jpg";
            String returnPath = Environment.getExternalStorageDirectory() + "/Images/" + path;

            directory = new File(directory, path);
            if (directory.exists()) {
                Log.v("Images", "Image Exist");
                return returnPath;

            } else {
                directory.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(directory);
                outputStream.write(response);
                outputStream.close();
                Log.v("Images Download", "Images does not exist, so download");

                return returnPath;

            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //This tells whether movie is marked as favourite or not
    public static boolean getFavouriteStatus(int movieId, Context context) {
        //movie.movie_id=?
        final String movieWithMovieId = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        boolean favouriteMarked = false;
        int favourite;

        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID, MovieContract.MovieEntry.COLUMN_FAVOURITE},
                movieWithMovieId,
                new String[]{String.valueOf(movieId)},
                null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            favourite = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVOURITE));
            favouriteMarked = favourite == 1;

        }
        cursor.close();
        return favouriteMarked;
    }

    public static boolean getNetworkStatus(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }

}
