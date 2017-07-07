package com.ridif.popularmovies.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ridif.popularmovies.R;
import com.ridif.popularmovies.adapter.PopularMoviesAdapter;
import com.ridif.popularmovies.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class PopularMoviesActivity extends AppCompatActivity {

    private static final String TAG = PopularMoviesActivity.class.getSimpleName();
    // TODO Add themoviedb api key
    public static final String API_KEY = "add your own tmdb api key";

    private RecyclerView mPopularMoviesRecyclerView;
    private PopularMoviesAdapter mPopularMoviesAdapter;
    private String sortState;

    private TextView mErrorMessageDisplay;
    private ImageView mCloudOffImageView;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey("sorted")) {
            sortState = "popular";
        } else {
            sortState = savedInstanceState.getString("sorted");
        }

        setContentView(R.layout.activity_popular_movies);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mCloudOffImageView = (ImageView) findViewById(R.id.iv_cloud_off);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mPopularMoviesRecyclerView = (RecyclerView) findViewById(R.id.rv_popular_movies);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mPopularMoviesRecyclerView.setLayoutManager(layoutManager);
        mPopularMoviesRecyclerView.setHasFixedSize(true);
        mPopularMoviesAdapter = new PopularMoviesAdapter();
        mPopularMoviesRecyclerView.setAdapter(mPopularMoviesAdapter);

        sortMovie(sortState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sorted", sortState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mPopularMoviesAdapter.setMovieData(null);
                sortState = "popular";
                sortMovie(sortState);
            case R.id.action_popular:
                sortState = getString(R.string.popular);
                sortMovie(sortState);
                break;
            case R.id.action_now_playing:
                sortState = getString(R.string.now_playing);
                sortMovie(sortState);
                break;
            case R.id.action_top_rated:
                sortState = getString(R.string.top_rated);
                sortMovie(sortState);
                break;
            case R.id.action_upcoming:
                sortState = getString(R.string.upcoming);
                sortMovie(sortState);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sortMovie(String sortBy) {
        showMovieDataView();
        URL sortedMovieUrl = NetworkUtils.buildSortUrl(sortBy, API_KEY);
        if (isOnline()) {
            new FetchMovieTask().execute(sortedMovieUrl);
        } else {
            showErrorMessage();
        }
    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mCloudOffImageView.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mPopularMoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mPopularMoviesRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mCloudOffImageView.setVisibility(View.VISIBLE);
    }

    public class FetchMovieTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... vURLs) {
            URL movieUrl = vURLs[0];
            String movieResult = null;
            try {
                movieResult = NetworkUtils.getResponseFromHttpUrl(movieUrl);
            } catch (IOException vE) {
                vE.printStackTrace();
            }
            return movieResult;
        }

        @Override
        protected void onPostExecute(String movieData) {
            super.onPostExecute(movieData);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            try {
                JSONObject root = new JSONObject(movieData);
                JSONArray jsonArray = root.getJSONArray("results");
                mPopularMoviesAdapter.setMovieData(jsonArray);
            } catch (Exception vE) {
                vE.printStackTrace();
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
