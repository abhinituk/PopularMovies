package com.sunshine.popularmovies.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;

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

//This class is used to fetch review by making a request to  /movie/{id}/reviews endpoint.
public class FetchReviewTask extends AsyncTask<Integer, Void, ArrayList<String>> {

    private final Context mContext;
    public FetchReviewTask(Context context) {
       this.mContext=context;
    }

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
            //Log.v("json string",jsonString);



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
            getReviewDataFromJson(jsonString,params[0]);
            //Log.v("Data Sent", String.valueOf(params[0])+jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }


    //This method is used to get appropriate review data from Json string received.
    private void getReviewDataFromJson(String jsonString, int movieId) throws JSONException {

        //Field available in JSON strings
        final String TMDB_REVIEW_RESULT="results";
        final String TMDB_REVIEW_AUTHOR="author";
        final String TMDB_REVIEW_CONTENT="content";

        if(jsonString!=null)
        {
            //Log.v("Json Reveived",jsonString);
            JSONObject object= new JSONObject(jsonString);
            JSONArray result= object.getJSONArray(TMDB_REVIEW_RESULT);
            Vector<ContentValues> valuesVector= new Vector<>(result.length());

            for(int i=0;i<result.length();i++)
            {
                JSONObject review= result.getJSONObject(i);

                //Getting the author name
                String author = review.getString(TMDB_REVIEW_AUTHOR);

                //Getting the review by author
                String content= review.getString(TMDB_REVIEW_CONTENT);

                //Log.v("Data",author+content);

                //Putting the review data into content values.
                ContentValues values= new ContentValues();
                values.put(MovieContract.ReviewEntry.COL_REVIEW_ID,movieId);
                values.put(MovieContract.ReviewEntry.COL_REVIEW_AUTHOR,author);
                values.put(MovieContract.ReviewEntry.COL_REVIEW_CONTENT,content);

                valuesVector.add(values);
            }
            //Log.v("Values Vector size", String.valueOf(valuesVector.size()));
            if(valuesVector.size()>0)
            {
                ContentValues contentValues[]= new ContentValues[valuesVector.size()];
                valuesVector.toArray(contentValues);
            }


            Cursor cursor = mContext.getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            //Log.v("Review Cursor size", String.valueOf(cursor.getCount()));
            assert cursor != null;
            valuesVector = new Vector<>(cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cursor, cv);
                    valuesVector.add(cv);
                } while (cursor.moveToNext());
            }
            //Log.d("Fetch ", "FetchReview Complete. " + valuesVector.size() + " Inserted");

        }
    }

}
