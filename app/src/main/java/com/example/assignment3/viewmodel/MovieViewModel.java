package com.example.assignment3.viewmodel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
//.
import com.example.assignment3.model.Movie;
import com.example.assignment3.repository.MovieRepository;

public class MovieViewModel extends ViewModel {
    private MovieRepository repository;
    private LiveData<List<Movie>> moviesLiveData;

    public MovieViewModel() {
        repository = new MovieRepository();
        moviesLiveData = repository.getMoviesLiveData();
        repository.fetchMovies();
    }

    public LiveData<List<Movie>> getMoviesLiveData() {
        return moviesLiveData;
    }

    public void addMovie(Movie movie) {
        repository.addMovie(movie);
    }

    public void updateMovie(Movie movie) {
        repository.updateMovie(movie);
    }

    public void deleteMovie(String movieId) {
        repository.deleteMovie(movieId);
    }
}
