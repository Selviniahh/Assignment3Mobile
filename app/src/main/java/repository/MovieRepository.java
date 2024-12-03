package repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import model.Movie;

public class MovieRepository {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<List<Movie>> moviesLiveData = new MutableLiveData<>();

    public LiveData<List<Movie>> getMoviesLiveData() {
        return moviesLiveData;
    }

    public void fetchMovies() {
        db.collection("movies")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Movie> movies = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Movie movie = document.toObject(Movie.class);
                        movie.setId(document.getId()); // Save the document ID
                        movies.add(movie);
                    }
                    moviesLiveData.postValue(movies);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }

    public void addMovie(Movie movie) {
        db.collection("movies")
                .add(movie)
                .addOnSuccessListener(documentReference -> {
                    // Movie added successfully
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }

    public void updateMovie(Movie movie) {
        db.collection("movies")
                .document(movie.getId())
                .set(movie)
                .addOnSuccessListener(aVoid -> {
                    // Movie updated successfully
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }

    public void deleteMovie(String movieId) {
        db.collection("movies")
                .document(movieId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Movie deleted successfully
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }
}
