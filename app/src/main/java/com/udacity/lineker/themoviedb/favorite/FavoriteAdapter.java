package com.udacity.lineker.themoviedb.favorite;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.udacity.lineker.themoviedb.R;
import com.udacity.lineker.themoviedb.database.MovieEntry;
import com.udacity.lineker.themoviedb.detail.MovieDetailActivity;
import com.udacity.lineker.themoviedb.util.ConnectionUtil;
import com.udacity.lineker.themoviedb.util.ModelUtil;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MovieViewHolder> {

    private List<MovieEntry> mMovieEntries;
    private Context mContext;

    public FavoriteAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.flavor_item, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        holder.movie = mMovieEntries.get(position);
        holder.mTextView.setText(holder.movie.getTitle());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ConnectionUtil.isOnline( v.getContext())) {
                    Toast.makeText(v.getContext(), R.string.error_connection, Toast.LENGTH_SHORT).show();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, ModelUtil.movieEntryToMovie(holder.movie));

                    context.startActivity(intent);
                }
            }
        });

        RequestOptions options = new RequestOptions();
        Glide.with(holder.mImageView.getContext())
                .load(holder.movie.getPosterPath())
                .apply(options.fitCenter())
                .into(holder.mImageView);
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mMovieEntries == null) {
            return 0;
        }
        return mMovieEntries.size();
    }

    public List<MovieEntry> getMovies() {
        return mMovieEntries;
    }

    public void setMovies(List<MovieEntry> movieEntries) {
        mMovieEntries = movieEntries;
        notifyDataSetChanged();
    }

    // Inner class for creating ViewHolders
    class MovieViewHolder extends RecyclerView.ViewHolder {

        public MovieEntry movie;

        public final View mView;
        public final ImageView mImageView;
        public final TextView mTextView;

        public MovieViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.avatar);
            mTextView = view.findViewById(R.id.title);
        }
    }
}