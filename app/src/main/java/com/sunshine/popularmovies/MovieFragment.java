package com.sunshine.popularmovies;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
import java.util.Collections;


public class MovieFragment extends Fragment {
    private CustomMovieAdapter mCustomMovieAdapter;

    MovieData mMovieData;
    GridView gridView;
    ArrayList<MovieData> mMovieDataArrayList;


    //onCreate is used to create the fragment. In this put components which has to be retained when fragment is paused or stopped & then resumed.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("Movie Data")) {
            mMovieDataArrayList = new ArrayList<>(Collections.singletonList(mMovieData));
        }
        else {
            mMovieDataArrayList = savedInstanceState.getParcelableArrayList("Movie data");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("Movie Data",mMovieDataArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh) {
            movieDataUpdate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        movieDataUpdate();

    }

    public void movieDataUpdate() {
        FetchMovieData fetchMovieData = new FetchMovieData();
        fetchMovieData.execute(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("sort_by","popular"));
    }


    //In this fragment draws its UI for the first time
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.grid_view_fragment);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String title = mCustomMovieAdapter.getItem(position).title;
                String overview = mCustomMovieAdapter.getItem(position).overview;
                String release_date=mCustomMovieAdapter.getItem(position).release_date;
                String vote_average = mCustomMovieAdapter.getItem(position).vote_average;
                String poster_path = mCustomMovieAdapter.getItem(position).ImageUrl;

                MovieData movieData=new MovieData(title,overview,release_date,vote_average,poster_path);
                Intent intent = new Intent(getActivity(),DetailActivity.class)
                        .putExtra("Movie Data",movieData);
                startActivity(intent);


            }
        });
        return rootView;
    }



    public class FetchMovieData extends AsyncTask<String, Void, ArrayList<MovieData>> {

        @Override
        protected ArrayList<MovieData> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            String appid = "Enter Your API Key";


            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/"+params[0]+"?";
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
            final String TMDB_TITLE = "original_title";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_RELEASE_DATE = "release_date";
            final String BASE_URL = "http://image.tmdb.org/t/p/w185/";

            if(movieJsonStr!=null)
            {
                try {
                    JSONObject json = new JSONObject(movieJsonStr);
                    JSONArray array = json.getJSONArray("results");
                    mMovieDataArrayList=new ArrayList<>();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject movieData = array.getJSONObject(i);
                        String poster_path = BASE_URL + movieData.getString(TMDB_POSTER_PATH);
                        String overview = movieData.getString(TMDB_OVERVIEW);
                        String title = movieData.getString(TMDB_TITLE);
                        String vote_average = movieData.getString(TMDB_VOTE_AVERAGE);
                        String release_date = movieData.getString(TMDB_RELEASE_DATE);
                        mMovieData = new MovieData(title,overview,vote_average,release_date,poster_path);
                        mMovieDataArrayList.add(mMovieData);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Internet Connectivity", "Enable Data Connection");
                }
            }
            else
            {
                Toast.makeText(getActivity(),"Something Went Wrong...Check Your Internet Connection",Toast.LENGTH_LONG).show();
            }
            return mMovieDataArrayList;
        }


        @Override
        protected void onPostExecute(ArrayList<MovieData> strings) {
            if (strings != null) {
                if(getActivity()!=null)//http://stackoverflow.com/questions/28414480/android-nullpointerexception-from-creating-an-adapter
                {
                    mCustomMovieAdapter = new CustomMovieAdapter(getActivity(),strings);
                    gridView.setAdapter(mCustomMovieAdapter);
                    mCustomMovieAdapter.notifyDataSetChanged();
                }
            }
            else
            {
                Toast.makeText(getActivity(),"Something Went Wrong...Check Your Internet Connection & Try Again",Toast.LENGTH_LONG).show();
            }
        }
    }

}
