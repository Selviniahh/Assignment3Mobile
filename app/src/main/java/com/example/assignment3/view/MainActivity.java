package com.example.assignment3.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.assignment3.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import com.example.assignment3.model.Movie;
import com.example.assignment3.viewmodel.MovieViewModel;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private MovieViewModel movieViewModel;
    private MovieAdapter movieAdapter;
    private List<Movie> moviesList;
    private ActivityMainBinding binding;

    private List<Movie> allMoviesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        moviesList = new ArrayList<>();
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        movieAdapter = new MovieAdapter(this, moviesList, movieViewModel);
        binding.recyclerViewMovies.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMovies.setAdapter(movieAdapter);

        movieViewModel.getMoviesLiveData().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                allMoviesList.clear();
                if (movies != null) {
                    allMoviesList.addAll(movies);
                }
                applyFilters(); 
            }
        });

        binding.buttonSearch.setOnClickListener(v -> {
            applyFilters();
        });

        binding.checkBoxShowFavorites.setOnCheckedChangeListener((buttonView, isChecked) -> {
            applyFilters();
        });

        binding.fabAddMovie.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditMovieActivity.class);
            startActivity(intent);
        });

        binding.buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void applyFilters() {
        String query = binding.editTextSearch.getText().toString().trim().toLowerCase();
        boolean showFavorites = binding.checkBoxShowFavorites.isChecked();

        List<Movie> filteredList = new ArrayList<>();

        for (Movie movie : allMoviesList) {
            boolean matchesQuery = movie.getTitle().toLowerCase().contains(query);
            boolean matchesFavorite = !showFavorites || movie.isFavorite();

            if (matchesQuery && matchesFavorite) {
                filteredList.add(movie);
            }
        }

        moviesList.clear();
        moviesList.addAll(filteredList);
        movieAdapter.notifyDataSetChanged();
    }
}
