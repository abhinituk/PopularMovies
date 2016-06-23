package com.sunshine.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Abhishek on 23-06-2016.
 */
public class FetchVideosAndReviewsTask extends AsyncTask<Integer,Void,Void> {

    @Override
    protected Void doInBackground(Integer... params) {
        HttpURLConnection urlConnection=null;
        BufferedReader reader=null;

        String jsonStr=null;
        String appId="89d7f04f6c9ed1a948da0d08299b5afe";
        String appendToResponse="reviews,trailers";

        try
        {
            String BASE_URL="http://api.themoviedb.org/3/movie/"+params[0]+"?";
            String APPID="api_key";
            String APPEND_TO_RESPONSE="append_to_response";

            Uri builtUri= Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(APPEND_TO_RESPONSE,appendToResponse)
                    .appendQueryParameter(APPID,appId)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read the response from API request
            InputStream inputStream= urlConnection.getInputStream();
            if(inputStream == null)
                return null;
            else
                reader=new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder= new StringBuilder();

            String line;
            while ((line= reader.readLine())!=null)
            {
                builder.append(line).append("\n");
            }
            //If the response is empty
            if(builder.length()== 0)
                return null;

            jsonStr=builder.toString();
            Log.v("Videos",jsonStr);



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(urlConnection!=null)
            {
                urlConnection.disconnect();
            }
            if(reader!=null)
            {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        getTrailersAndReviewsDataFromJson(jsonStr);
        return null;
    }

    public void getTrailersAndReviewsDataFromJson(String JsonStr)
    {
        final String TMDB_MOVIE_REVIEW="reviews";
        final String TMDB_REVIEW_RESULTS="results";
        final String TMDB_REVIEW_ID="id";
        final String TMDB_REVIEW_AUTHOR="author";
        final String TMDB_REVIEW_CONTENT="content";
        final String TMDB_REVIEW_URL="url";

        final String TMDB_TRAILERS="trailers";
        final String TMDB_YOUTUBE="youtube";
        final String TMDB_TRAILER_NAME="name";
        final String TMDB_SOURCE="source";

    }
}
