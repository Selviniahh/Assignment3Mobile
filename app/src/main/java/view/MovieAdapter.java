package view;

import android.content.Context;
import android.content.Intent;
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
import com.example.assignment3.databinding.ItemMovieBinding;

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
        private final ItemMovieBinding binding;

        public MovieViewHolder(ItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.buttonEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Movie selectedMovie = moviesList.get(position);
                    Intent intent = new Intent(context, AddEditMovieActivity.class);
                    intent.putExtra("movie", selectedMovie);
                    context.startActivity(intent);
                }
            });

            binding.buttonDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Movie selectedMovie = moviesList.get(position);
                    movieViewModel.deleteMovie(selectedMovie.getId());
                    moviesList.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }

        public void bind(Movie movie) {
            binding.textViewTitle.setText(movie.getTitle());
            binding.textViewStudio.setText("Studio: " + movie.getStudio());
            binding.textViewCriticsRating.setText("Rating: " + movie.getCriticsRating());

            if (!TextUtils.isEmpty(movie.getPoster())) {
                loadImageFromUrl(movie.getPoster(), binding.imageViewPoster);
            } else {
                binding.imageViewPoster.setImageResource(R.drawable.image);
            }
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemMovieBinding binding = ItemMovieBinding.inflate(inflater, parent, false);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = moviesList.get(position);
        holder.bind(movie);
    }

    private void loadImageFromUrl(String url, ImageView imageView) {
        if (TextUtils.isEmpty(url) ||
                !(url.startsWith("http://") || url.startsWith("https://"))) {
            new Handler(Looper.getMainLooper()).post(() ->
                    imageView.setImageResource(R.drawable.browser)
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
