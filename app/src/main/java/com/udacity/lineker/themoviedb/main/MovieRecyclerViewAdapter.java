package com.udacity.lineker.themoviedb.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.udacity.lineker.themoviedb.util.ConnectionUtil;
import com.udacity.lineker.themoviedb.model.Movie;
import com.udacity.lineker.themoviedb.R;
import com.udacity.lineker.themoviedb.detail.MovieDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class MovieRecyclerViewAdapter
        extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<Movie> mValues;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Movie movie;

        public final View mView;
        public final ImageView mImageView;
        public final TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.avatar);
            mTextView = view.findViewById(R.id.title);
        }
    }

    public MovieRecyclerViewAdapter(Context context, List<Movie> items) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        if (items == null) {
            items = new ArrayList<>();
        }
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flavor_item, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.movie = mValues.get(position);
        holder.mTextView.setText(holder.movie.getTitle());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ConnectionUtil.isOnline( v.getContext())) {
                    Toast.makeText(v.getContext(), R.string.error_connection, Toast.LENGTH_SHORT).show();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, holder.movie);

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

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
