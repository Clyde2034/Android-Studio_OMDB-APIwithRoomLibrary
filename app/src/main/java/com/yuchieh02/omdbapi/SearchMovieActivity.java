package com.yuchieh02.omdbapi;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.yuchieh02.omdbapi.room.MovieDatabase;
import com.yuchieh02.omdbapi.room.MovieEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class SearchMovieActivity extends AppCompatActivity {

    private EditText et_search;
    private Button btn_retrieve, btn_save;
    private RecyclerView recycleView_Second;
    private ImageButton imagebtn_back;
    private final String API_KEY = "79ffcd8e";
    private final OkHttpClient client = new OkHttpClient().newBuilder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build();
    private String imdbID, title, year, rated, released, runtime, genre, director, writer, actors, plot;
    private Handler mainHandler = new Handler();
    private ArrayList<HashMap<String, String>> receiveDATA = new ArrayList<>();
    private ProgressBar progressBar;
    private MovieDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_movie);

        et_search = findViewById(R.id.et_search);
        btn_retrieve = findViewById(R.id.btn_retrieve);
        btn_save = findViewById(R.id.btn_save);
        recycleView_Second = findViewById(R.id.recycleView_Second);
        imagebtn_back = findViewById(R.id.imagebtn_back);
        progressBar = findViewById(R.id.progressBar);

        db = Room.databaseBuilder(getApplicationContext(), MovieDatabase.class, "Movie")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        btn_retrieve.setOnClickListener(RETRIEVE);
        btn_save.setOnClickListener(SAVE_TO_DB);
        imagebtn_back.setOnClickListener(BACK);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    btn_retrieve.setEnabled(true);
                } else btn_retrieve.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private View.OnClickListener RETRIEVE = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            receiveDATA.clear();
            progressBar.setVisibility(View.VISIBLE);
            new Movie_GET(et_search.getText().toString()).start();
        }
    };

    private View.OnClickListener SAVE_TO_DB = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(SearchMovieActivity.this)
                    .setTitle("Add Data to SQLite DB")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            List<String> existsResult = db.movieDAO().getTitles();
                            for (int j = 0; j < receiveDATA.size(); j++) {
                                if (!existsResult.contains(receiveDATA.get(j).get("title"))) {
                                    String actors_temp = receiveDATA.get(j).get("actors").replace(", ", ",");
                                    ArrayList<String> actors_split = new ArrayList<>(Arrays.asList(actors_temp.split(",")));

                                    MovieEntity movieEntity = new MovieEntity(receiveDATA.get(j).get("title"), receiveDATA.get(j).get("year"),
                                            receiveDATA.get(j).get("rated"), receiveDATA.get(j).get("released"),
                                            receiveDATA.get(j).get("runtime"), receiveDATA.get(j).get("genre"),
                                            receiveDATA.get(j).get("director"), receiveDATA.get(j).get("writer"),
                                            actors_split, receiveDATA.get(j).get("plot"));

                                    db.movieDAO().insertMovieitem(movieEntity);
                                    Toast.makeText(SearchMovieActivity.this, "Successful to adding the data", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(SearchMovieActivity.this, "Failed to adding the data, because the data is exists.", Toast.LENGTH_LONG).show();
                                    continue;
                                }
                            }
                            onBackPressed();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    };

    private View.OnClickListener BACK = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

//    private class Cardview_adapter extends RecyclerView.Adapter<Cardview_adapter.ViewHolder> {
//
//        class ViewHolder extends RecyclerView.ViewHolder {
//
//            private TextView cardview_item_title, cardview_item_year, cardview_item_rated, cardview_item_released,
//                    cardview_item_runtime, cardview_item_genre, cardview_item_director, cardview_item_writer, cardview_item_actors,
//                    cardview_item_plot;
//            private View mView;
//
//            public ViewHolder(@NonNull View itemView) {
//                super(itemView);
//                cardview_item_title = itemView.findViewById(R.id.cardview_item_title);
//                cardview_item_year = itemView.findViewById(R.id.cardview_item_year);
//                cardview_item_rated = itemView.findViewById(R.id.cardview_item_rated);
//                cardview_item_released = itemView.findViewById(R.id.cardview_item_released);
//                cardview_item_runtime = itemView.findViewById(R.id.cardview_item_runtime);
//                cardview_item_genre = itemView.findViewById(R.id.cardview_item_genre);
//                cardview_item_director = itemView.findViewById(R.id.cardview_item_director);
//                cardview_item_writer = itemView.findViewById(R.id.cardview_item_writer);
//                cardview_item_actors = itemView.findViewById(R.id.cardview_item_actors);
//                cardview_item_plot = itemView.findViewById(R.id.cardview_item_plot);
//                mView = itemView;
//            }
//        }
//
//        @NonNull
//        @Override
//        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//            holder.cardview_item_title.setText("Title：" + receiveDATA.get(holder.getAdapterPosition()).get("title"));
//            holder.cardview_item_year.setText("Year：" + receiveDATA.get(holder.getAdapterPosition()).get("year"));
//            holder.cardview_item_rated.setText("Rated：" + receiveDATA.get(holder.getAdapterPosition()).get("rated"));
//            holder.cardview_item_released.setText("Released：" + receiveDATA.get(holder.getAdapterPosition()).get("released"));
//            holder.cardview_item_runtime.setText("Runtime：" + receiveDATA.get(holder.getAdapterPosition()).get("runtime"));
//            holder.cardview_item_genre.setText("Genre：" + receiveDATA.get(holder.getAdapterPosition()).get("genre"));
//            holder.cardview_item_director.setText("Director：" + receiveDATA.get(holder.getAdapterPosition()).get("director"));
//            holder.cardview_item_writer.setText("Writer：" + receiveDATA.get(holder.getAdapterPosition()).get("writer"));
//            holder.cardview_item_actors.setText("Actors：" + receiveDATA.get(holder.getAdapterPosition()).get("actors"));
//            holder.cardview_item_plot.setText("Plot：" + receiveDATA.get(holder.getAdapterPosition()).get("plot"));
//        }
//
//        @Override
//        public int getItemCount() {
//            return receiveDATA.size();
//        }
//
//    }

    private void GET_DATA_imdbID(String search) {

        String search_encode = null;
        try {
            search_encode = URLEncoder.encode(search, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String URL = "https://www.omdbapi.com/?t=" + search_encode + "&apikey=" + API_KEY;
        Request request = new Request.Builder()
                .url(URL)
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());
            HashMap<String, String> hashmap = new HashMap<>();
            hashmap.put("title", jsonObject.getString("Title"));
            hashmap.put("year", jsonObject.getString("Year"));
            hashmap.put("rated", jsonObject.getString("Rated"));
            hashmap.put("released", jsonObject.getString("Released"));
            hashmap.put("runtime", jsonObject.getString("Runtime"));
            hashmap.put("genre", jsonObject.getString("Genre"));
            hashmap.put("director", jsonObject.getString("Director"));
            hashmap.put("writer", jsonObject.getString("Writer"));
            hashmap.put("actors", jsonObject.getString("Actors"));
            hashmap.put("plot", jsonObject.getString("Plot"));
            receiveDATA.add(hashmap);
        } catch (IOException | JSONException e) {
            Toast.makeText(SearchMovieActivity.this, "Failed to fetching the data.", Toast.LENGTH_LONG).show();
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                RecycleView_Movie recycleView_movie=new RecycleView_Movie(receiveDATA,SearchMovieActivity.this);
                recycleView_Second.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recycleView_Second.setAdapter(recycleView_movie);
                recycleView_movie.closeKeyBoard();
            }
        });
    }

//    private void GET_DATA_Info(HashMap hashmap, String id) {
//        String URL = "https://www.omdbapi.com/?i=" + id + "&apikey=" + API_KEY;
//        Request request = new Request.Builder()
//                .url(URL)
//                .build();
//
//        try {
//            Response response = client.newCall(request).execute();
//            JSONObject jsonObject = new JSONObject(response.body().string());
//            hashmap.put("title", jsonObject.getString("Title"));
//            hashmap.put("year", jsonObject.getString("Year"));
//            hashmap.put("rated", jsonObject.getString("Rated"));
//            hashmap.put("released", jsonObject.getString("Released"));
//            hashmap.put("runtime", jsonObject.getString("Runtime"));
//            hashmap.put("genre", jsonObject.getString("Genre"));
//            hashmap.put("director", jsonObject.getString("Director"));
//            hashmap.put("writer", jsonObject.getString("Writer"));
//            hashmap.put("actors", jsonObject.getString("Actors"));
//            hashmap.put("plot", jsonObject.getString("Plot"));
//            receiveDATA.add(hashmap);
//        } catch (IOException | JSONException e) {
//            //nop
//
//        }
//    }

    class Movie_GET extends Thread {
        String search;

        public Movie_GET(String search) {
            this.search = search;
        }

        @Override
        public void run() {
            GET_DATA_imdbID(search);

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    //progressbar
                    progressBar.setVisibility(View.INVISIBLE);
                    btn_save.setEnabled(true);
                }
            });

        }
    }
}