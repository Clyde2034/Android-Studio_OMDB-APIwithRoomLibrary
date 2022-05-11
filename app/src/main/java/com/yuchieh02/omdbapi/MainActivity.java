package com.yuchieh02.omdbapi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.yuchieh02.omdbapi.room.MovieDatabase;
import com.yuchieh02.omdbapi.room.MovieEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity {

    private Button btn_AddMovieToDB, btn_SearchForMovies, btn_SearchForActors, btn_clear, btn_extendapp;
    private EditText et_search_extend;
    private MovieDatabase db;
    private Handler mainHandler = new Handler();
    private ArrayList<HashMap<String, String>> receiveDATA = new ArrayList<>();
    private RecyclerView recycleView_First;
    private final OkHttpClient client = new OkHttpClient().newBuilder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build();
    private String imdbID;
    private final String API_KEY = "79ffcd8e";
    private ProgressBar progressBar_Main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_AddMovieToDB = findViewById(R.id.btn_AddMovieToDB);
        btn_SearchForMovies = findViewById(R.id.btn_SearchForMovies);
        btn_SearchForActors = findViewById(R.id.btn_SearchForActors);
        btn_extendapp = findViewById(R.id.btn_extendapp);
        btn_clear = findViewById(R.id.btn_clear);
        recycleView_First = findViewById(R.id.recycleView_First);
        progressBar_Main = findViewById(R.id.progressBar_Main);
        et_search_extend = findViewById(R.id.et_search_extend);

        db = Room.databaseBuilder(getApplicationContext(), MovieDatabase.class, "Movie")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        btn_AddMovieToDB.setOnClickListener(ADD_MOVIE_TO_DB);
        btn_SearchForMovies.setOnClickListener(SEARCH_FOR_MOVIES);
        btn_SearchForActors.setOnClickListener(SEARCH_FOR_ACTORS);
        btn_extendapp.setOnClickListener(EXTEND);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.movieDAO().DeleteAllItem();
                Toast.makeText(MainActivity.this, "Successful to clear SQLite DB.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private View.OnClickListener ADD_MOVIE_TO_DB = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final boolean[] result = {false};
            //MovieDatabase db = Room.databaseBuilder(getApplicationContext(), MovieDatabase.class, "Movie").build();
            String[] array_title = getResources().getStringArray(R.array.HardCode_title);
            int[] array_year = getResources().getIntArray(R.array.HardCode_year);
            String[] array_rated = getResources().getStringArray(R.array.HardCode_rated);
            String[] array_released = getResources().getStringArray(R.array.HardCode_released);
            String[] array_runtime = getResources().getStringArray(R.array.HardCode_runtime);
            String[] array_genre = getResources().getStringArray(R.array.HardCode_genre);
            String[] array_director = getResources().getStringArray(R.array.HardCode_director);
            String[] array_writer = getResources().getStringArray(R.array.HardCode_writer);
            String[] array_actors = getResources().getStringArray(R.array.HardCode_actors);
            String[] array_plot = getResources().getStringArray(R.array.HardCode_plot);
            try {
                for (int i = 0; i < 5; i++) {
                    String actors_temp = array_actors[i].replace(", ", ",");
                    ArrayList<String> actors_split = new ArrayList<>(Arrays.asList(actors_temp.split(",")));

                    int finalTemp = i;
                    if (!db.movieDAO().getTitles().contains(array_title[finalTemp])) {
                        MovieEntity movieEntity = new MovieEntity(array_title[finalTemp], String.valueOf(array_year[finalTemp]), array_rated[finalTemp], array_released[finalTemp], array_runtime[finalTemp], array_genre[finalTemp], array_director[finalTemp], array_writer[finalTemp], actors_split, array_plot[finalTemp]);
                        db.movieDAO().insertMovieitem(movieEntity);
                        result[0] = true;
                    }
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                            } catch (Exception e) {
                                //nop
                            }
                        }
                    };
                    new Thread(runnable).start();
                }
                if (result[0]) {
                    Toast.makeText(MainActivity.this, "Successful to adding the data.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to adding the data, because the data is exists.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                //nop
            }
        }
    };

    private View.OnClickListener SEARCH_FOR_MOVIES = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, SearchMovieActivity.class));
        }
    };

    private View.OnClickListener SEARCH_FOR_ACTORS = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, SearchActorsActivity.class));
        }
    };

    private View.OnClickListener EXTEND = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            receiveDATA.clear();
            progressBar_Main.setVisibility(View.VISIBLE);
            new Movie_GET(et_search_extend.getText().toString()).start();
        }
    };

    private void GET_DATA_imdbID(String search) {

        String search_encode = null;
        try {
            search_encode = URLEncoder.encode(search, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String URL = "https://www.omdbapi.com/?s=" + search_encode + "&apikey=" + API_KEY;
        Request request = new Request.Builder()
                .url(URL)
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());
            JSONArray jsonArray = jsonObject.getJSONArray("Search");
            for (int i = 0; i < jsonArray.length(); i++) {
                imdbID = jsonArray.getJSONObject(i).getString("imdbID");
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("imdbID", imdbID);
                GET_DATA_Info(hashMap, imdbID);
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    RecycleView_Movie recycleView_movie = new RecycleView_Movie(receiveDATA, MainActivity.this);
                    recycleView_First.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recycleView_First.setAdapter(recycleView_movie);
                    recycleView_movie.closeKeyBoard();
                }
            });
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "搜尋失敗，可能搜尋結果過多", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void GET_DATA_Info(HashMap hashmap, String id) {
        String URL = "https://www.omdbapi.com/?i=" + id + "&apikey=" + API_KEY;
        Request request = new Request.Builder()
                .url(URL)
                .build();

        try {
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());
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
            //nop
        }
    }

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
                    progressBar_Main.setVisibility(View.INVISIBLE);
                }
            });

        }
    }
}