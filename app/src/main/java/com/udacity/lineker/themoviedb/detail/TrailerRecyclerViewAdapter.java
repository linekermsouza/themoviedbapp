package com.udacity.lineker.themoviedb.detail;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.udacity.lineker.themoviedb.R;
import com.udacity.lineker.themoviedb.model.Trailer;
import com.udacity.lineker.themoviedb.util.ConnectionUtil;

import java.util.ArrayList;
import java.util.List;

public class TrailerRecyclerViewAdapter
        extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.ViewHolder> {

    public static final String URL_YOUTUBE = "http://www.youtube.com/watch?v=";
    public static final String VND_YOUTUBE = "vnd.youtube:";
    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<Trailer> mValues;

    public void setTrailers(List<Trailer> trailers) {
        this.mValues = trailers;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Trailer trailer;

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

    public TrailerRecyclerViewAdapter(Context context, List<Trailer> items) {
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
                .inflate(R.layout.flavor_item_trailer, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.trailer = mValues.get(position);
        holder.mTextView.setText(holder.trailer.getTitle());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ConnectionUtil.isOnline( v.getContext())) {
                    Toast.makeText(v.getContext(), R.string.error_connection, Toast.LENGTH_SHORT).show();
                } else {
                    watchYoutubeVideo(v.getContext(), holder.trailer.getKey());
                }
            }
        });

        RequestOptions options = new RequestOptions();
        Glide.with(holder.mImageView.getContext())
                .load(holder.trailer.getUrlImage())
                .apply(options.fitCenter())
                .into(holder.mImageView);
    }

    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(VND_YOUTUBE + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(URL_YOUTUBE + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }
}
