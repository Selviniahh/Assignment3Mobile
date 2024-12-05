// File: viewmodel/SearchMovieViewModel.java
package com.example.assignment3.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assignment3.model.Movie;
import com.example.assignment3.repository.MovieRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchMovieViewModel extends ViewModel {

    private static final String API_KEY = "cd41df5d";
    private static final String BASE_URL = "https://www.omdbapi.com/";

    private final MutableLiveData<ArrayList<Movie>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final OkHttpClient client = new OkHttpClient();
    private final MovieRepository movieRepository = new MovieRepository();

    public LiveData<ArrayList<Movie>> getSearchResults() {
        return searchResults;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void searchMovies(String query) {
        String url = BASE_URL + "?s=" + query.replace(" ", "+") + "&apikey=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                errorMessage.postValue(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        if (json.has("Search")) {
                            JSONArray searchArray = json.getJSONArray("Search");
                            ArrayList<Movie> moviesList = new ArrayList<>();

                            for (int i = 0; i < searchArray.length(); i++) {
                                JSONObject movieJson = searchArray.getJSONObject(i);
                                Movie movie = new Movie();
                                movie.setTitle(movieJson.optString("Title"));
                                movie.setStudio(movieJson.optString("Year"));
                                movie.setPoster(movieJson.optString("Poster"));
                                movie.setCriticsRating("N/A");
                                moviesList.add(movie);
                            }

                            searchResults.postValue(moviesList);
                        } else {
                            errorMessage.postValue("No results found");
                        }
                    } catch (JSONException e) {
                        errorMessage.postValue("Error parsing data");
                    }
                } else {
                    errorMessage.postValue("Response not successful");
                }
            }
        });
    }

    public void addMovieToFirestore(Movie movie, AddMovieCallback callback) {
        movieRepository.addMovie(movie).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess();
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Unknown error");
            }
        });
    }


    public interface AddMovieCallback {
        void onSuccess();

        void onFailure(String error);
    }
}
