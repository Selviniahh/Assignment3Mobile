// File: view/SearchMoviesActivity.java
package com.example.assignment3.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.assignment3.databinding.ActivitySearchMoviesBinding;
import com.example.assignment3.model.Movie;
import com.example.assignment3.viewmodel.SearchMovieViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchMoviesActivity extends AppCompatActivity implements MovieSearchAdapter.OnAddClickListener {
    private ActivitySearchMoviesBinding binding;
    private SearchMovieViewModel searchMovieViewModel;
    private MovieSearchAdapter searchAdapter;
    private List<Movie> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchMoviesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchMovieViewModel = new ViewModelProvider(this).get(SearchMovieViewModel.class);

        searchResults = new ArrayList<>();
        searchAdapter = new MovieSearchAdapter(searchResults, this);

        binding.recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewSearchResults.setAdapter(searchAdapter);

        binding.buttonSearchMovies.setOnClickListener(v -> {
            String query = binding.editTextSearchQuery.getText().toString().trim();
            if (!TextUtils.isEmpty(query)) {
                performSearch(query);
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonBack.setOnClickListener(v -> {
            finish(); 
        });

        searchMovieViewModel.getSearchResults().observe(this, movies -> {
            if (movies != null && !movies.isEmpty()) {
                searchResults.clear();
                searchResults.addAll(movies);
                searchAdapter.notifyDataSetChanged();
            } else {
                searchResults.clear();
                searchAdapter.notifyDataSetChanged();
                Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
            }
        });

        searchMovieViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String query) {
        searchMovieViewModel.searchMovies(query);
    }

    @Override
    public void onAddClick(Movie movie) {
        // Add the selected movie to Firestore
        searchMovieViewModel.addMovieToFirestore(movie, new SearchMovieViewModel.AddMovieCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(SearchMoviesActivity.this, "Movie added to your list!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(SearchMoviesActivity.this, "Failed to add movie: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
