package com.udacity.lineker.themoviedb.main;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.udacity.lineker.themoviedb.model.Movie;
import com.udacity.lineker.themoviedb.R;
import com.udacity.lineker.themoviedb.util.ConnectionUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetMoviesRequest extends AsyncTask<String, Void, List<Movie>> {
    private static final String LOG_TAG = GetMoviesRequest.class.getName();
    private static final String URL_MOVIES = "https://api.themoviedb.org/3/movie/%s?api_key=%s";
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String NOW_PLAYING = "now_playing";
    public static final String UPCOMING = "upcoming";
    public static final String URL_IMAGES = "http://image.tmdb.org/t/p/w185";

    private final GetMoviesRequestListener listener;
    Context context;

    public GetMoviesRequest(Context context, GetMoviesRequestListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        String type = params[0];
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

                movies.add(new Movie(id, posterPath, backdropPath));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Erro no parsing do JSON", e);
            return null;
        }

        return movies;
    }

    @Override
    protected void onPostExecute(List<Movie> result) {
        super.onPostExecute(result);
        if (result == null) {
            Toast.makeText(this.context, R.string.error_try_again, Toast.LENGTH_SHORT).show();
        }
        if (this.listener != null) this.listener.getMoviesRequestCompleted(result);
    }

    interface GetMoviesRequestListener {
        void getMoviesRequestCompleted(List<Movie> result);
    }
}
