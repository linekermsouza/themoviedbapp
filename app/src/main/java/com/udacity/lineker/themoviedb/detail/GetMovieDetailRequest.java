package com.udacity.lineker.themoviedb.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import com.udacity.lineker.themoviedb.R;
import com.udacity.lineker.themoviedb.model.Movie;
import com.udacity.lineker.themoviedb.model.Trailer;
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

public class GetMovieDetailRequest extends AsyncTaskLoader<Movie> {
    private static final String LOG_TAG = GetMovieDetailRequest.class.getName();
    private static final String URL_MOVIE_DETAIL = "https://api.themoviedb.org/3/movie/%s?api_key=%s";
    private static final String URL_MOVIE_TRAILERS = "https://api.themoviedb.org/3/movie/%s/videos?api_key=%s";
    private static final String IMAGE_YOUTUBE_URL = "https://img.youtube.com/vi/%s/0.jpg";
    public static final String REQUEST_MOVIE_ID_EXTRA = "id";
    public static final int REQUEST_MOVIE_DETAIL_LOADER = 2;
    public static final String IMAGE_TMDB_URL = "http://image.tmdb.org/t/p/w185";
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

            // trailers
            urlString = String.format(URL_MOVIE_TRAILERS, id, ConnectionUtil.API_KEY);
            request = new Request.Builder()
                    .url(urlString)
                    .build();
            response = client.newCall(request).execute();
            json = response.body().string();
            movie.setTrailers(getTrailers(json));

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
            String posterPath = IMAGE_TMDB_URL + movie.getString("poster_path");
            String backdropPath = IMAGE_TMDB_URL + movie.getString("backdrop_path");

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

    private List<Trailer> getTrailers(String jsonString) {

        List<Trailer> trailersResult = new ArrayList<>();

        try {
            JSONObject trailerList = new JSONObject(jsonString);
            JSONArray trailersArray = trailerList.getJSONArray("results");

            JSONObject trailer;
            for (int i = 0; i < trailersArray.length(); i++) {
                trailer = new JSONObject(trailersArray.getString(i));
                String key = trailer.getString("key");
                String urlImage = String.format(IMAGE_YOUTUBE_URL, key);
                String title = trailer.getString("name");

                trailersResult.add(new Trailer(key, urlImage, title));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Erro no parsing do JSON", e);
            return null;
        }

        return trailersResult;
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
