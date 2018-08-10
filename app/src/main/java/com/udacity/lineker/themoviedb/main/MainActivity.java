package com.udacity.lineker.themoviedb.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.udacity.lineker.themoviedb.R;
import com.udacity.lineker.themoviedb.favorite.FavoriteActivity;
import com.udacity.lineker.themoviedb.util.ConnectionUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    private OnChangeListListener onChangeListListener;

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

        if (id == R.id.about) {
            //Toast.makeText(this, R.string.about_text, Toast.LENGTH_LONG).show();
            Context context = this;
            Intent intent = new Intent(context, FavoriteActivity.class);

            context.startActivity(intent);
        } else if (!ConnectionUtil.isOnline(this)) {
            Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
            result = false;
        } else if (id == R.id.most_popular) {
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return result;
    }

    public void setOnChangeListListener(OnChangeListListener onChangeListListener) {
        this.onChangeListListener = onChangeListListener;
    }
}
