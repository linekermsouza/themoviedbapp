package com.udacity.lineker.themoviedb.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.udacity.lineker.themoviedb.util.ConnectionUtil;
import com.udacity.lineker.themoviedb.model.Movie;
import com.udacity.lineker.themoviedb.R;

import java.util.List;

public class MainActivityFragment extends Fragment implements GetMoviesRequest.GetMoviesRequestListener, MainActivity.OnChangeListListener {

    private MovieRecyclerViewAdapter flavorAdapter;
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private View noDataView;
    private String currentListType = GetMoviesRequest.POPULAR;


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
                    updateData();
                }
            }
        });

        this.recyclerView = view.findViewById(R.id.recyclerview);
        if (!ConnectionUtil.isOnline( MainActivityFragment.this.getActivity())) {
            Toast.makeText(MainActivityFragment.this.getActivity(), R.string.error_connection, Toast.LENGTH_SHORT).show();
        } else {
            swipeRefreshLayout.setRefreshing(true);
            updateData();
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
        new GetMoviesRequest(this.getActivity(), this).execute(this.currentListType);
    }

    private void setupRecyclerView(List<Movie> movies) {
        int mNoOfColumns = calculateNoOfColumns(getActivity());

        noDataView.setVisibility(movies.size() == 0 ? View.VISIBLE : View.INVISIBLE);
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), mNoOfColumns));
        recyclerView.setAdapter(new MovieRecyclerViewAdapter(getActivity(),
                movies));
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    @Override
    public void getMoviesRequestCompleted(List<Movie> result) {
        if (getActivity() == null) return;
        swipeRefreshLayout.setRefreshing(false);
        if (result != null) {
            setupRecyclerView(result);
        }
    }

    @Override
    public void onChangeList(String type) {
        swipeRefreshLayout.setRefreshing(true);
        this.currentListType = type;
        updateData();
    }
}
