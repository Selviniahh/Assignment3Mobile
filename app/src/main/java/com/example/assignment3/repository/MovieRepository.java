package com.example.assignment3.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.example.assignment3.model.Movie;

public class MovieRepository {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<List<Movie>> moviesLiveData = new MutableLiveData<>();
    private List<Movie> moviesList = new ArrayList<>();

    public LiveData<List<Movie>> getMoviesLiveData() {
        return moviesLiveData;
    }

    public void fetchMovies() {
        db.collection("movies").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Movie newMovie = dc.getDocument().toObject(Movie.class);
                            newMovie.setId(dc.getDocument().getId());
                            moviesList.add(newMovie);
                            break;
                        case MODIFIED:
                            Movie modifiedMovie = dc.getDocument().toObject(Movie.class);
                            modifiedMovie.setId(dc.getDocument().getId());
                            int index = findMovieIndexById(modifiedMovie.getId());
                            if (index != -1) {
                                moviesList.set(index, modifiedMovie);
                            }
                            break;
                        case REMOVED:
                            String removedId = dc.getDocument().getId();
                            int removeIndex = findMovieIndexById(removedId);
                            if (removeIndex != -1) {
                                moviesList.remove(removeIndex);
                            }
                            break;
                    }
                }
                moviesLiveData.postValue(moviesList);
            }
        });
    }

    private int findMovieIndexById(String id) {
        for (int i = 0; i < moviesList.size(); i++) {
            if (moviesList.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public void addMovie(Movie movie) {
        db.collection("movies").add(movie).addOnSuccessListener(documentReference -> {
            // Movie added successfully
        }).addOnFailureListener(e -> {
            // Handle failure
        });
    }

    public void updateMovie(Movie movie) {
        db.collection("movies").document(movie.getId()).set(movie).addOnSuccessListener(aVoid -> {
        }).addOnFailureListener(e -> {
        });
    }

    public void deleteMovie(String movieId) {
        db.collection("movies").document(movieId).delete().addOnSuccessListener(aVoid -> {
        }).addOnFailureListener(e -> {
        });
    }
}
