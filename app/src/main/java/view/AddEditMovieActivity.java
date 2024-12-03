package view;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.assignment3.R;

import model.Movie;
import viewmodel.MovieViewModel;

public class AddEditMovieActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextStudio, editTextCriticsRating, editTextPosterUrl;
    private Button buttonSave;

    private MovieViewModel movieViewModel;
    private Movie movieToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_movie);

        // Initialize ViewModel
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextStudio = findViewById(R.id.editTextStudio);
        editTextCriticsRating = findViewById(R.id.editTextCriticsRating);
        editTextPosterUrl = findViewById(R.id.editTextPosterUrl);
        buttonSave = findViewById(R.id.buttonSave);

        // Check if editing existing movie
        if (getIntent().hasExtra("movie")) {
            setTitle("Edit Movie");
            movieToEdit = (Movie) getIntent().getSerializableExtra("movie");
            populateFields(movieToEdit);
            buttonSave.setText("Update");
        } else {
            setTitle("Add Movie");
            buttonSave.setText("Add");
        }

        buttonSave.setOnClickListener(v -> saveMovie());
    }

    private void populateFields(Movie movie) {
        editTextTitle.setText(movie.getTitle());
        editTextStudio.setText(movie.getStudio());
        editTextCriticsRating.setText(movie.getCriticsRating());
        editTextPosterUrl.setText(movie.getPoster());
    }

    private void saveMovie() {
        String title = editTextTitle.getText().toString().trim();
        String studio = editTextStudio.getText().toString().trim();
        String criticsRating = editTextCriticsRating.getText().toString().trim();
        String posterUrl = editTextPosterUrl.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(studio)) {
            Toast.makeText(this, "Title and Studio are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (movieToEdit != null) {
            // Update existing movie
            movieToEdit.setTitle(title);
            movieToEdit.setStudio(studio);
            movieToEdit.setCriticsRating(criticsRating);
            movieToEdit.setPoster(posterUrl);
            movieViewModel.updateMovie(movieToEdit);
            Toast.makeText(this, "Movie updated", Toast.LENGTH_SHORT).show();
        } else {
            // Add new movie
            Movie newMovie = new Movie();
            newMovie.setTitle(title);
            newMovie.setStudio(studio);
            newMovie.setCriticsRating(criticsRating);
            newMovie.setPoster(posterUrl);
            movieViewModel.addMovie(newMovie);
            Toast.makeText(this, "Movie added", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
