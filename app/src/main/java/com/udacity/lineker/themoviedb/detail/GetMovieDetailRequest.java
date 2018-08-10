package com.udacity.lineker.themoviedb.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import com.udacity.lineker.themoviedb.R;
import com.udacity.lineker.themoviedb.model.Movie;
import com.udacity.lineker.themoviedb.util.ConnectionUtil;
import com.udacity.lineker.themoviedb.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetMovieDetailRequest extends AsyncTaskLoader<Movie> {
    private static final String LOG_TAG = GetMovieDetailRequest.class.getName();
    private static final String URL_MOVIE_DETAIL = "https://api.themoviedb.org/3/movie/%s?api_key=%s";
    public static final String REQUEST_MOVIE_ID_EXTRA = "id";
    public static final int REQUEST_MOVIE_DETAIL_LOADER = 2;
    private final Bundle args;
    private Movie movie;

    public GetMovieDetailRequest(Context context, Bundle args, Movie movie) {
        super(context);
        this.args = args;
        this.movie = movie;
    }

    @Override
    protected void onStartLoading() {

        if (args == null) {
            return;
        }

        if (movie != null) {
            deliverResult(movie);
        } else {
            forceLoad();
        }
    }

    @Override
    public Movie loadInBackground() {
        int id = args.getInt(REQUEST_MOVIE_ID_EXTRA);
        String urlString = String.format(URL_MOVIE_DETAIL, id, ConnectionUtil.API_KEY);


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlString)
                .build();


        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();
            Movie movie = getMovieDetail(json);

            return movie;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Falha ao acessar Web service", e);
        }

        return null;
    }

    private Movie getMovieDetail(String jsonString) {

        Movie movieResult = null;

        try {
            JSONObject movie = new JSONObject(jsonString);

            Log.i(LOG_TAG, "nome=" + movie.getString("poster_path"));

            int id = movie.getInt("id");
            String posterPath = "http://image.tmdb.org/t/p/w185" + movie.getString("poster_path");
            String backdropPath = "http://image.tmdb.org/t/p/w185" + movie.getString("backdrop_path");

            JSONArray genreArray = movie.getJSONArray("genres");
            JSONObject genre;
            String genreStr = "";
            for (int i = 0; i <genreArray.length(); i++) {
                genre = new JSONObject(genreArray.getString(i));
                genreStr += ", " + genre.getString("name");
            }

            movieResult = new Movie(id, posterPath, backdropPath);
            movieResult.setTitle(movie.getString("title"));
            movieResult.setGenre(genreStr.substring(2));
            movieResult.setRate(movie.getString("vote_average"));
            movieResult.setVote(movie.getString("vote_count"));
            movieResult.setRuntime(movie.getString("runtime") + " " + this.getContext().getString(R.string.minutes));
            movieResult.setRelease(DateUtil.fromYYYYMMDDtoDDMMYYYY(movie.getString("release_date")));
            movieResult.setSummary(movie.getString("overview"));

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Erro no parsing do JSON", e);
            return null;
        }

        return movieResult;
    }

    @Override
    public void deliverResult(Movie result) {
        movie = result;
        if (result == null) {
            Toast.makeText(this.getContext(), R.string.error_try_again, Toast.LENGTH_SHORT).show();
        }
        super.deliverResult(result);
    }
}
