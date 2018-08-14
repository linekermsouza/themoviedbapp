package com.udacity.lineker.themoviedb.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.udacity.lineker.themoviedb.model.Movie;
import com.udacity.lineker.themoviedb.util.ConnectionUtil;
import com.udacity.lineker.themoviedb.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetMoviesRequest extends AsyncTaskLoader<List<Movie>> {
    private static final String LOG_TAG = GetMoviesRequest.class.getName();
    public static final int REQUEST_MOVIES_LOADER = 1;
    public static final String REQUEST_MOVIES_TYPE_EXTRA = "type";
    private static final String URL_MOVIES = "https://api.themoviedb.org/3/movie/%s?api_key=%s";
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String NOW_PLAYING = "now_playing";
    public static final String UPCOMING = "upcoming";
    public static final String URL_IMAGES = "http://image.tmdb.org/t/p/w185";
    private final Bundle args;

    List<Movie> movies;

    public GetMoviesRequest(Context context, Bundle args, List<Movie> movies) {
        super(context);
        this.args = args;
        this.movies = movies;
    }

    @Override
    protected void onStartLoading() {

        if (args == null) {
            return;
        }

        if (movies != null) {
            deliverResult(movies);
        } else {
            forceLoad();
        }
    }



    @Override
    public List<Movie> loadInBackground() {
        String type = args.getString(REQUEST_MOVIES_TYPE_EXTRA);
        String urlString = String.format(URL_MOVIES, type, ConnectionUtil.API_KEY);


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlString)
                .build();


        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();
            List<Movie> movies = getMovies(json);

            return movies;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Falha ao acessar Web service", e);
        }

        return null;
    }

    private List<Movie> getMovies(String jsonString) {

        List<Movie> movies = new ArrayList<Movie>();

        try {
            JSONObject movieList = new JSONObject(jsonString);
            JSONArray moviesArray = movieList.getJSONArray("results");

            JSONObject movie;

            for (int i = 0; i < moviesArray.length(); i++) {
                movie = new JSONObject(moviesArray.getString(i));

                Log.i(LOG_TAG, "nome=" + movie.getString("poster_path"));

                int id = movie.getInt("id");
                String posterPath = URL_IMAGES + movie.getString("poster_path");
                String backdropPath = URL_IMAGES + movie.getString("backdrop_path");

                Movie newMovie = new Movie(id, posterPath, backdropPath);
                newMovie.setTitle(movie.getString("title"));
                newMovie.setRate(movie.getString("vote_average"));
                newMovie.setVote(movie.getString("vote_count"));
                newMovie.setRelease(DateUtil.fromYYYYMMDDtoDDMMYYYY(movie.getString("release_date")));
                newMovie.setSummary(movie.getString("overview"));

                movies.add(newMovie);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Erro no parsing do JSON", e);
            return null;
        }

        return movies;
    }

    @Override
    public void deliverResult(List<Movie> result) {
        movies = result;
        super.deliverResult(result);
    }
}
