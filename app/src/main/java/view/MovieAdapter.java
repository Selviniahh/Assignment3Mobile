package view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3.R;

import java.io.IOException;
import java.util.List;

import model.Movie;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import viewmodel.MovieViewModel;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> moviesList;
    private Context context;
    private MovieViewModel movieViewModel;
    private OkHttpClient client;

    public MovieAdapter(Context context, List<Movie> moviesList, MovieViewModel movieViewModel) {
        this.context = context;
        this.moviesList = moviesList;
        this.movieViewModel = movieViewModel;
        client = new OkHttpClient();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewPoster;
        public TextView textViewTitle, textViewStudio, textViewCriticsRating;
        public View buttonEdit, buttonDelete;

        public MovieViewHolder(View view) {
            super(view);
            imageViewPoster = view.findViewById(R.id.imageViewPoster);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewStudio = view.findViewById(R.id.textViewStudio);
            textViewCriticsRating = view.findViewById(R.id.textViewCriticsRating);
            buttonEdit = view.findViewById(R.id.buttonEdit);
            buttonDelete = view.findViewById(R.id.buttonDelete);

            buttonEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Movie selectedMovie = moviesList.get(position);
                    Intent intent = new Intent(context, AddEditMovieActivity.class);
                    intent.putExtra("movie", selectedMovie);
                    context.startActivity(intent);
                }
            });

            buttonDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Movie selectedMovie = moviesList.get(position);
                    movieViewModel.deleteMovie(selectedMovie.getId());
                    // Remove the item from the list 
                    moviesList.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_movie, parent, false);

        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = moviesList.get(position);

        holder.textViewTitle.setText(movie.getTitle());
        holder.textViewStudio.setText("Studio: " + movie.getStudio());
        holder.textViewCriticsRating.setText("Rating: " + movie.getCriticsRating());

        // Load poster image if available
        if (!TextUtils.isEmpty(movie.getPoster())) {
            loadImageFromUrl(movie.getPoster(), holder.imageViewPoster);
        } else {
            holder.imageViewPoster.setImageResource(R.drawable.image); 
        }
    }

    private void loadImageFromUrl(String url, ImageView imageView) {
        // Validate the URL
        if (TextUtils.isEmpty(url) ||
                !(url.startsWith("http://") || url.startsWith("https://"))) {
            // Set a placeholder image if URL is invalid
            new Handler(Looper.getMainLooper()).post(() ->
                    imageView.setImageResource(R.drawable.browser) 
            );
            return;
        }

        // Proceed to load the image if URL is valid
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        imageView.setImageResource(R.drawable.browser)
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    byte[] imageBytes = response.body().bytes();
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        imageView.setImageBitmap(bitmap);
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            imageView.setImageResource(R.drawable.browser)
                    );
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
