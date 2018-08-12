package com.udacity.lineker.themoviedb.detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.lineker.themoviedb.R;
import com.udacity.lineker.themoviedb.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewRecyclerViewAdapter
        extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<Review> mValues;

    public void setReviews(List<Review> reviews) {
        this.mValues = reviews;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Review review;

        public final View mView;
        public final TextView mTextViewAuthor;
        public final TextView mTextViewContent;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTextViewAuthor = view.findViewById(R.id.author);
            mTextViewContent = view.findViewById(R.id.content);
        }
    }

    public ReviewRecyclerViewAdapter(Context context, List<Review> items) {
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
                .inflate(R.layout.flavor_item_review, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.review = mValues.get(position);
        holder.mTextViewAuthor.setText(holder.mTextViewAuthor.getContext().getString(R.string.author) + ": " + holder.review.getAuthor());
        holder.mTextViewContent.setText(holder.review.getContent());
    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }
}
