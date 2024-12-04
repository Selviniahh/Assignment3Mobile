package view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.assignment3.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import model.Movie;
import viewmodel.MovieViewModel;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private MovieViewModel movieViewModel;
    private MovieAdapter movieAdapter;
    private List<Movie> moviesList;
    private ActivityMainBinding binding;

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

        movieViewModel.getFilteredMovies().observe(this, movies -> {
            moviesList.clear();
            if (movies != null) {
                moviesList.addAll(movies);
            }
            movieAdapter.notifyDataSetChanged();
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

        binding.buttonSearch.setOnClickListener(v -> {
            String query = binding.editTextSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                movieViewModel.filterMovies(query);
            } else {
                movieViewModel.filterMovies("");
                Toast.makeText(this, "Showing all movies", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
