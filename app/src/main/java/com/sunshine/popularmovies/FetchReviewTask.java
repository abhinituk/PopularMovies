package com.sunshine.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;

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

/**
 * Created by Abhishek on 23-06-2016.
 */
public class FetchReviewTask extends AsyncTask<Integer, Void, ArrayList<String>> {

    ArrayList<String> mArrayListReview= new ArrayList<>();
    @Override
    protected ArrayList<String> doInBackground(Integer... params) {

        HttpURLConnection urlConnection=null;
        BufferedReader reader=null;

        String jsonString=null;
        String app_id="89d7f04f6c9ed1a948da0d08299b5afe";

        final String BASE_URL="http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?";
        String APP_ID="api_key";

        Uri uri=Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(APP_ID,app_id)
                .build();

        try {
            URL url= new URL(uri.toString());

            urlConnection= (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            StringBuilder buffer= new StringBuilder();
            InputStream inputStream= urlConnection.getInputStream();
            if(inputStream == null)
                return null;
            reader= new BufferedReader(new InputStreamReader(inputStream));

            String line;
            if((line = reader.readLine())!=null)
            {
                buffer.append(line).append("\n");
            }

            if(buffer.length() == 0)
                return null;
            jsonString= buffer.toString();



        } catch (IOException  e) {
            e.printStackTrace();
        }
        finally {
            if(urlConnection!=null)
            {
                urlConnection.disconnect();
            }
            if(reader!=null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        try {
            return getReviewDataFromJson(jsonString,params[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    public ArrayList<String> getReviewDataFromJson(String jsonString, int movieId) throws JSONException {

        final String TMDB_REVIEW_RESULT="results";
        final String TMDB_REVIEW_AUTHOR="author";
        final String TMDB_REVIEW_CONTENT="content";

        if(jsonString!=null)
        {
            JSONObject object= new JSONObject(jsonString);
            JSONArray result= object.getJSONArray(TMDB_REVIEW_RESULT);

            for(int i=0;i<result.length();i++)
            {
                JSONObject review= result.getJSONObject(i);
                String author = review.getString(TMDB_REVIEW_AUTHOR);
                String content= review.getString(TMDB_REVIEW_CONTENT);

                String reviewData= author+"\n"+content;
                mArrayListReview.add(reviewData);
            }
        }
        return mArrayListReview;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        if(strings!=null)
        {
            DetailFragment.mArrayListReview=strings;
        }
    }
}
