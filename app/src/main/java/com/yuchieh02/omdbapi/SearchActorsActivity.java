package com.yuchieh02.omdbapi;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.yuchieh02.omdbapi.room.MovieDatabase;
import com.yuchieh02.omdbapi.room.MovieEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActorsActivity extends AppCompatActivity {
    private ImageButton imagebtnBackActor;
    private EditText etSearchActor;
    private Button btnSearchActor;
    private RecyclerView recycleViewSecond_actor;
    private ProgressBar progressBarActor;
    private ArrayList<HashMap<String, String>> receiveDATA = new ArrayList<>();
    private MovieDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_actors);

        imagebtnBackActor = findViewById(R.id.imagebtn_back_actor);
        etSearchActor = findViewById(R.id.et_search_actor);
        btnSearchActor = findViewById(R.id.btn_search_actor);
        recycleViewSecond_actor = findViewById(R.id.recycleView_Second_actor);
        progressBarActor = findViewById(R.id.progressBar_actor);

        db = Room.databaseBuilder(getApplicationContext(), MovieDatabase.class, "Movie")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        btnSearchActor.setOnClickListener(ACTORS_SEARCH);

        imagebtnBackActor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        etSearchActor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    btnSearchActor.setEnabled(true);
                } else {
                    btnSearchActor.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private View.OnClickListener ACTORS_SEARCH = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            receiveDATA.clear();
            List<MovieEntity> result = db.movieDAO().getMoviesByActors(etSearchActor.getText().toString());
            if (result.size() != 0) {
                for (MovieEntity i : result) {
                    HashMap<String, String> hashmap = new HashMap<>();
                    hashmap.put("title", i.getTitle());
                    hashmap.put("year", i.getYear());
                    hashmap.put("rated", i.getRated());
                    hashmap.put("released", i.getReleased());
                    hashmap.put("runtime", i.getRuntime());
                    hashmap.put("genre", i.getGenre());
                    hashmap.put("director", i.getDirector());
                    hashmap.put("writer", i.getWriter());
                    MovieEntity.Converters converters = new MovieEntity.Converters();
                    String actors_format = converters.fromArrayList(i.getActors())
                            .replace("[\"", "")
                            .replace("\"]", "")
                            .replace("\",\"", ", ");
                    hashmap.put("actors", actors_format);
                    hashmap.put("plot", i.getPlot());
                    receiveDATA.add(hashmap);
                }
            } else {
                Toast.makeText(SearchActorsActivity.this, "Doesn't exists partial match data.", Toast.LENGTH_SHORT).show();
            }

            RecycleView_Movie recycleView_movie = new RecycleView_Movie(receiveDATA, SearchActorsActivity.this);
            recycleViewSecond_actor.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recycleViewSecond_actor.setAdapter(recycleView_movie);
            recycleView_movie.closeKeyBoard();
        }
    };
}