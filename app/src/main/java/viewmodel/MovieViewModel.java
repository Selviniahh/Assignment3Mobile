package viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import model.Movie;
import repository.MovieRepository;

public class MovieViewModel extends ViewModel {
    private MovieRepository repository;
    private LiveData<List<Movie>> moviesLiveData;

    public MovieViewModel() {
        repository = new MovieRepository();
        moviesLiveData = repository.getMoviesLiveData();
    }

    public LiveData<List<Movie>> getMovies() {
        return moviesLiveData;
    }

    public void fetchMovies() {
        repository.fetchMovies();
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
