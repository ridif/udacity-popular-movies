package com.ridif.popularmovies.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ridif.popularmovies.R;
import com.ridif.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static com.ridif.popularmovies.activity.PopularMoviesActivity.API_KEY;

public class DetailActivity extends AppCompatActivity {

    public final static String DETAIL_ACTIVITY_EXTRA = "We are go to the movie detail";

    private TextView mTitle;
    private TextView mYear;
    private TextView mDuration;
    private TextView mVote;
    private TextView mDescription;
    private ImageView mPoster;

    private String movieId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        Intent intentFromMain = getIntent();
        if (intentFromMain.hasExtra(DETAIL_ACTIVITY_EXTRA)) {
            movieId = intentFromMain.getStringExtra(DETAIL_ACTIVITY_EXTRA);
        }

        mTitle = (TextView) findViewById(R.id.tv_movie_title);
        mYear = (TextView) findViewById(R.id.tv_movie_year);
        mDuration = (TextView) findViewById(R.id.tv_movie_duration);
        mVote = (TextView) findViewById(R.id.tv_movie_vote);
        mDescription = (TextView) findViewById(R.id.tv_movie_description);
        mPoster = (ImageView) findViewById(R.id.iv_movie_poster);

        loadDetailMovieData(movieId);
    }

    private void loadDetailMovieData(String movieId) {
        URL detailMovieDataUrl = NetworkUtils.buildDetailMovieData(movieId, API_KEY);
        new FetchDetailMovieTask().execute(detailMovieDataUrl);
    }

    public class FetchDetailMovieTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... vURLs) {
            URL movieUrl = vURLs[0];
            String movieData = null;
            try {
                movieData = NetworkUtils.getResponseFromHttpUrl(movieUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieData;
        }

        @Override
        protected void onPostExecute(String vS) {
            super.onPostExecute(vS);
            try {
                JSONObject movieDataJSON = new JSONObject(vS);
                String title = movieDataJSON.getString("original_title");
                String year = movieDataJSON.getString("release_date").substring(0, 4);
                String duration = movieDataJSON.getString("runtime") + "min";
                String vote = movieDataJSON.getString("vote_average") + "/10";
                String description = movieDataJSON.getString("overview");
                String poster = movieDataJSON.getString("poster_path").substring(1);

                mTitle.setText(title);
                mYear.setText(year);
                mDuration.setText(duration);
                mVote.setText(vote);
                mDescription.setText(description);
                Picasso.with(DetailActivity.this).load(String.valueOf(NetworkUtils.buildImageUrl(poster))).into(mPoster);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
