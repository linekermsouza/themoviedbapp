package com.udacity.lineker.themoviedb.main;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.udacity.lineker.themoviedb.R;
import com.udacity.lineker.themoviedb.favorite.FavoriteFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String IS_ON_FAVORITE = "isOnFavorite";
    public static final String TITLE = "TITLE";
    Toolbar toolbar;
    private OnChangeListListener onChangeListListener;
    private boolean isOnFavorite;
    private int pendingId;

    public interface OnChangeListListener {
        void onChangeList(String type);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            changeView(R.id.most_popular);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        boolean result = true;

        boolean changeView = (this.isOnFavorite && id != R.id.favorites)
                || (!this.isOnFavorite && id== R.id.favorites);
        if (changeView) {
            changeView(id);
        } else {
            updateList(id);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return result;
    }

    private void updateList(int id) {
        if (id == R.id.most_popular) {
            toolbar.setTitle(R.string.most_popular);
            this.onChangeListListener.onChangeList(GetMoviesRequest.POPULAR);
        } else if (id == R.id.most_voted) {
            toolbar.setTitle(R.string.most_voted);
            this.onChangeListListener.onChangeList(GetMoviesRequest.TOP_RATED);
        } else if (id == R.id.in_theaters) {
            toolbar.setTitle(R.string.in_theaters);
            this.onChangeListListener.onChangeList(GetMoviesRequest.NOW_PLAYING);
        } else if (id == R.id.next_in_theaters) {
            toolbar.setTitle(R.string.next_in_theaters);
            this.onChangeListListener.onChangeList(GetMoviesRequest.UPCOMING);
        }
    }

    public void changeView(int viewId) {

        Fragment fragment = null;
        int title = 0;

        switch (viewId) {
            case R.id.favorites:
                this.isOnFavorite = true;
                fragment = new FavoriteFragment();
                title  = R.string.favorites;
                break;
            case R.id.most_popular:
                title = title == 0 ? R.string.most_popular : title;
            case R.id.most_voted:
                title = title == 0 ? R.string.most_voted : title;
            case R.id.in_theaters:
                title = title == 0 ? R.string.in_theaters : title;
            case R.id.next_in_theaters:
                title = title == 0 ? R.string.next_in_theaters : title;
                this.isOnFavorite = false;
                fragment = new MainActivityFragment();
                this.pendingId = viewId;
                break;

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

    }

    public void setOnChangeListListener(OnChangeListListener onChangeListListener) {
        this.onChangeListListener = onChangeListListener;
        if (this.pendingId != 0) {
            updateList(this.pendingId);
            this.pendingId = 0;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_ON_FAVORITE, this.isOnFavorite);
        outState.putString(TITLE, getSupportActionBar().getTitle().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.isOnFavorite = savedInstanceState.getBoolean(IS_ON_FAVORITE);
        getSupportActionBar().setTitle(savedInstanceState.getString(TITLE));
    }
}
