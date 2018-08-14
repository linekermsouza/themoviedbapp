package com.udacity.lineker.themoviedb.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.lineker.themoviedb.R;
import com.udacity.lineker.themoviedb.model.Movie;
import com.udacity.lineker.themoviedb.util.ConnectionUtil;

import java.util.List;

public class MainActivityFragment extends Fragment implements
        MainActivity.OnChangeListListener,
        LoaderManager.LoaderCallbacks<List<Movie>> {

    public static final String NO_DATA_ERROR = "NO_DATA_ERROR";
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private TextView noDataView;
    private String currentListType = GetMoviesRequest.POPULAR;
    private MovieRecyclerViewAdapter mAdapter;
    public MainActivityFragment() {
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
                if (!ConnectionUtil.isOnline( MainActivityFragment.this.getActivity())) {
                    Toast.makeText(MainActivityFragment.this.getActivity(), R.string.error_connection, Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    updateViewModel(null);
                    updateData();
                }
            }
        });

        this.recyclerView = view.findViewById(R.id.recyclerview);
        setupRecyclerView(null);
        if (ConnectionUtil.isOnline( MainActivityFragment.this.getActivity())) {
            updateData();
        }

        if (savedInstanceState != null) {
            this.noDataView.setText(savedInstanceState.getString(NO_DATA_ERROR));
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setOnChangeListListener(this);
    }

    private void updateData() {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(GetMoviesRequest.REQUEST_MOVIES_TYPE_EXTRA, this.currentListType);
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        Loader<String> getMoviesLoader = loaderManager.getLoader(GetMoviesRequest.REQUEST_MOVIES_LOADER);
        this.swipeRefreshLayout.setRefreshing(true);
        if (getMoviesLoader == null) {
            loaderManager.initLoader(GetMoviesRequest.REQUEST_MOVIES_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(GetMoviesRequest.REQUEST_MOVIES_LOADER, queryBundle, this);
        }
    }

    private void setupRecyclerView(List<Movie> movies) {
        int mNoOfColumns = calculateNoOfColumns(getActivity());

        noDataView.setText(R.string.empty_list);
        noDataView.setVisibility(movies == null || movies.size() == 0 ? View.VISIBLE : View.INVISIBLE);
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), mNoOfColumns));
        mAdapter = new MovieRecyclerViewAdapter(getActivity(),
                movies);
        recyclerView.setAdapter(mAdapter);
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    @Override
    public void onChangeList(String type) {
        this.currentListType = type;
        updateViewModel(null);
        mAdapter.setMovies(null);
        updateData();
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        return new GetMoviesRequest(this.getActivity(), args, viewModel.getMovies());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        if (getActivity() == null) return;
        swipeRefreshLayout.setRefreshing(false);
        if (data != null) {
            updateViewModel(data);
            noDataView.setVisibility(data == null || data.size() == 0 ? View.VISIBLE : View.INVISIBLE);
            mAdapter.setMovies(data);
        } else {
            noDataView.setVisibility(View.VISIBLE);
            if (ConnectionUtil.isOnline(this.getActivity())) {
                noDataView.setText(R.string.error_try_again);
            } else {
                noDataView.setText(R.string.error_connection);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {

    }

    private void updateViewModel(List<Movie> movies) {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.setMovies(movies);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(NO_DATA_ERROR, this.noDataView.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
