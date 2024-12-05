package com.example.assignment3.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.assignment3.databinding.ActivityAddEditMovieBinding;

import com.example.assignment3.model.Movie;
import com.example.assignment3.viewmodel.MovieViewModel;

public class AddEditMovieActivity extends AppCompatActivity {

    private ActivityAddEditMovieBinding binding;
    private MovieViewModel movieViewModel;
    private Movie movieToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddEditMovieBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        if (getIntent().hasExtra("movie")) {
            setTitle("Edit Movie");
            movieToEdit = (Movie) getIntent().getSerializableExtra("movie");
            populateFields(movieToEdit);
            binding.buttonSave.setText("Update");
        } else {
            setTitle("Add Movie");
            binding.buttonSave.setText("Add");
        }

        binding.buttonSave.setOnClickListener(v -> saveMovie());

        binding.buttonCancel.setOnClickListener(v -> {
            Intent intent = new Intent(AddEditMovieActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
//a
    private void populateFields(Movie movie) {
        binding.editTextTitle.setText(movie.getTitle());
        binding.editTextStudio.setText(movie.getStudio());
        binding.editTextCriticsRating.setText(movie.getCriticsRating());
        binding.editTextPosterUrl.setText(movie.getPoster());
        binding.checkBoxFavorite.setChecked(movie.isFavorite()); 
    }

    private void saveMovie() {
        String title = binding.editTextTitle.getText().toString().trim();
        String studio = binding.editTextStudio.getText().toString().trim();
        String criticsRating = binding.editTextCriticsRating.getText().toString().trim();
        String posterUrl = binding.editTextPosterUrl.getText().toString().trim();
        boolean isFavorite = binding.checkBoxFavorite.isChecked(); 

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(studio)) {
            Toast.makeText(this, "Title and Studio are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(posterUrl) &&
                !(posterUrl.startsWith("http://") || posterUrl.startsWith("https://"))) {
            Toast.makeText(this, "Please enter a valid URL for the poster", Toast.LENGTH_SHORT).show();
            binding.editTextPosterUrl.requestFocus();
            return;
        }

        if (movieToEdit != null) {
            movieToEdit.setTitle(title);
            movieToEdit.setStudio(studio);
            movieToEdit.setCriticsRating(criticsRating);
            movieToEdit.setPoster(posterUrl);
            movieToEdit.setFavorite(isFavorite);
            movieViewModel.updateMovie(movieToEdit);
            Toast.makeText(this, "Movie updated", Toast.LENGTH_SHORT).show();
        } else {
            Movie newMovie = new Movie();
            newMovie.setTitle(title);
            newMovie.setStudio(studio);
            newMovie.setCriticsRating(criticsRating);
            newMovie.setPoster(posterUrl);
            newMovie.setFavorite(isFavorite); 
            movieViewModel.addMovie(newMovie);
            Toast.makeText(this, "Movie added", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
