package com.ridif.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ridif.popularmovies.R;
import com.ridif.popularmovies.activity.DetailActivity;
import com.ridif.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by devel on 07/06/2017.
 */

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesAdapterViewHolder> {
    private static final String TAG = PopularMoviesAdapter.class.getSimpleName();
    private JSONArray mMoviesJSONArray;


    @Override
    public PopularMoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.list_item_movies, parent, shouldAttachToParentImmediately);
        return new PopularMoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PopularMoviesAdapterViewHolder holder, int position) {
        String posterPath;
        try {
            holder.mLoadingProgressBar.setVisibility(View.GONE);
            holder.mMovieImageView.setVisibility(View.VISIBLE);
            JSONObject movie = mMoviesJSONArray.getJSONObject(position);
            posterPath = movie.getString("poster_path");
            Picasso.with(holder.context)
                    .load(String.valueOf(NetworkUtils.buildImageUrl(posterPath.substring(1))))
                    .into(holder.mMovieImageView);
        } catch (Exception vE) {
            vE.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (mMoviesJSONArray == null) {
            return 0;
        } else {
            return mMoviesJSONArray.length();
        }

    }

    public void setMovieData(JSONArray vMovieData) {
        mMoviesJSONArray = vMovieData;
        notifyDataSetChanged();

    }

    public class PopularMoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Context context = null;
        public final ImageView mMovieImageView;
        public final ProgressBar mLoadingProgressBar;

        public PopularMoviesAdapterViewHolder(View vView) {
            super(vView);
            mMovieImageView = vView.findViewById(R.id.iv_movie);
            mLoadingProgressBar = vView.findViewById(R.id.pb_loading);
            context = vView.getContext();
            vView.setOnClickListener(this);

        }

        @Override
        public void onClick(View vView) {
            Intent detailIntent = new Intent(context, DetailActivity.class);
            JSONObject movieDetailJSON;
            try {
                movieDetailJSON = mMoviesJSONArray.getJSONObject(getAdapterPosition());
                detailIntent.putExtra(DetailActivity.DETAIL_ACTIVITY_EXTRA, movieDetailJSON.getString("id"));

            } catch (Exception vE) {
                vE.printStackTrace();
            }
            context.startActivity(detailIntent);
        }
    }

}
