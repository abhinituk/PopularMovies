package com.sunshine.popularmovies.utility;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class Utility {

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
            directory.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(directory);
            outputStream.write(response);
            outputStream.close();

            return returnPath;

        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }
}
