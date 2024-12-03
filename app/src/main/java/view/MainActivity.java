package view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import model.Movie;
import viewmodel.MovieViewModel;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; 
    private MovieViewModel movieViewModel;
    private RecyclerView recyclerViewMovies;
    private MovieAdapter movieAdapter;
    private List<Movie> moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        recyclerViewMovies = findViewById(R.id.recyclerViewMovies);
        moviesList = new ArrayList<>();
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        movieAdapter = new MovieAdapter(this, moviesList, movieViewModel);
        recyclerViewMovies.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMovies.setAdapter(movieAdapter);

        // Observe LiveData
        movieViewModel.getMovies().observe(this, movies -> {
            moviesList.clear();
            moviesList.addAll(movies);
            movieAdapter.notifyDataSetChanged();
        });

        movieViewModel.fetchMovies();

        FloatingActionButton fabAdd = findViewById(R.id.fabAddMovie);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditMovieActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.buttonLogout).setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}
