package viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import model.Movie;
import repository.MovieRepository;

public class MovieViewModel extends ViewModel {
    private MovieRepository repository;
    private LiveData<List<Movie>> moviesLiveData;
    private MutableLiveData<List<Movie>> filteredMoviesLiveData = new MutableLiveData<>();
    private List<Movie> allMovies = new ArrayList<>();

    public MovieViewModel() {
        repository = new MovieRepository();
        moviesLiveData = repository.getMoviesLiveData();
        repository.fetchMovies();

        moviesLiveData.observeForever(movies -> {
            allMovies.clear();
            if (movies != null) {
                allMovies.addAll(movies);
            }
            filteredMoviesLiveData.setValue(allMovies);
        });
    }

    public LiveData<List<Movie>> getFilteredMovies() {
        return filteredMoviesLiveData;
    }

    public void filterMovies(String query) {
        if (TextUtils.isEmpty(query)) {
            filteredMoviesLiveData.setValue(allMovies);
            return;
        }

        List<Movie> filteredList = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase();

        for (Movie movie : allMovies) {
            if (movie.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                filteredList.add(movie);
            }
        }

        filteredMoviesLiveData.setValue(filteredList);
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
