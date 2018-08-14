package com.udacity.lineker.themoviedb.favorite;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.lineker.themoviedb.R;
import com.udacity.lineker.themoviedb.database.MovieEntry;

import java.util.List;

public class FavoriteFragment extends Fragment {

    public static final String POSITION_INDEX = "POSITION_INDEX";
    public static final String TOP_VIEW = "TOP_VIEW";
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private View noDataView;
    private FavoriteAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private int positionIndex = -1;
    private int topView;

    public FavoriteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_movie_list, container, false);

        this.noDataView = view.findViewById(R.id.no_data_title);
        swipeRefreshLayout = view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        this.recyclerView = view.findViewById(R.id.recyclerview);
        setupRecyclerView();
        setupViewModel();

        if (savedInstanceState != null) {
            this.positionIndex = savedInstanceState.getInt(POSITION_INDEX);
            this.topView = savedInstanceState.getInt(TOP_VIEW);
        }
        return view;
    }

    private void setupViewModel() {
        FavoriteViewModel viewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                noDataView.setVisibility(movieEntries == null || movieEntries.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                mAdapter.setMovies(movieEntries);
            }
        });
    }

    private void setupRecyclerView() {
        int mNoOfColumns = calculateNoOfColumns(getActivity());

        noDataView.setVisibility(View.VISIBLE);
        mGridLayoutManager = new GridLayoutManager(recyclerView.getContext(), mNoOfColumns);
        recyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new FavoriteAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        View startView = recyclerView.getChildAt(0);
        int topView = (startView == null) ? 0 : (startView.getTop() - recyclerView.getPaddingTop());

        outState.putInt(POSITION_INDEX, mGridLayoutManager.findFirstVisibleItemPosition());
        outState.putInt(TOP_VIEW, topView);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (positionIndex!= -1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mGridLayoutManager.scrollToPositionWithOffset(positionIndex, topView);
                    positionIndex = -1;
                }
            },200);
        }
    }
}
