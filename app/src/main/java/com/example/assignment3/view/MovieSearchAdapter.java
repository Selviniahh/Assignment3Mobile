// File: view/MovieSearchAdapter.java
package com.example.assignment3.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3.R;
import com.example.assignment3.databinding.ItemMovieSearchBinding;
import com.example.assignment3.model.Movie;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieSearchAdapter extends RecyclerView.Adapter<MovieSearchAdapter.MovieSearchViewHolder> {

    private List<Movie> movies;
    private OnAddClickListener addClickListener;
    private OkHttpClient client;

    public interface OnAddClickListener {
        void onAddClick(Movie movie);
    }

    public MovieSearchAdapter(List<Movie> movies, OnAddClickListener listener) {
        this.movies = movies;
        this.addClickListener = listener;
        this.client = new OkHttpClient();
    }

    @NonNull
    @Override
    public MovieSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemMovieSearchBinding binding = ItemMovieSearchBinding.inflate(inflater, parent, false);
        return new MovieSearchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieSearchViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieSearchViewHolder extends RecyclerView.ViewHolder {
        private final ItemMovieSearchBinding binding;

        public MovieSearchViewHolder(ItemMovieSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.buttonAddToList.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Movie selectedMovie = movies.get(position);
                    addClickListener.onAddClick(selectedMovie);
                }
            });
        }

        public void bind(Movie movie) {
            binding.textViewTitle.setText(movie.getTitle());
            binding.textViewStudio.setText("Year: " + movie.getStudio());
            binding.textViewCriticsRating.setText("Rating: " + movie.getCriticsRating());

            if (!TextUtils.isEmpty(movie.getPoster())) {
                loadImageFromUrl(movie.getPoster(), binding.imageViewPoster);
            } else {
                binding.imageViewPoster.setImageResource(R.drawable.image); // Default image
            }
        }

        private void loadImageFromUrl(String url, ImageView imageView) {
            if (TextUtils.isEmpty(url) ||
                    !(url.startsWith("http://") || url.startsWith("https://"))) {
                new Handler(Looper.getMainLooper()).post(() ->
                        imageView.setImageResource(R.drawable.browser) // Default error image
                );
                return;
            }

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(() ->
                            imageView.setImageResource(R.drawable.browser) // Error image
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
                                imageView.setImageResource(R.drawable.browser) // Error image
                        );
                    }
                }
            });
        }
    }
}
