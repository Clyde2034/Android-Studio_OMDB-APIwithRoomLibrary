package com.yuchieh02.omdbapi.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Entity(tableName = "MovieSQLite")
public class MovieEntity {

    @PrimaryKey
    @ColumnInfo(name = "Title")
    @NonNull
    public String Title;
    @ColumnInfo(name = "Year")
    public String Year;
    @ColumnInfo(name = "Rated")
    public String Rated;
    @ColumnInfo(name = "Released")
    public String Released;
    @ColumnInfo(name = "Runtime")
    public String Runtime;
    @ColumnInfo(name = "Genre")
    public String Genre;
    @ColumnInfo(name = "Director")
    public String Director;
    @ColumnInfo(name = "Writer")
    public String Writer;
    @ColumnInfo(name = "Actors")
    public String Actors;
    @ColumnInfo(name = "Plot")
    public String Plot;

    public MovieEntity() {

    }

    public MovieEntity(String title, String year, String rated, String released, String runtime, String genre, String director, String writer, ArrayList<String> actors, String plot) {
        Title = title;
        Year = year;
        Rated = rated;
        Released = released;
        Runtime = runtime;
        Genre = genre;
        Director = director;
        Writer = writer;
        Converters converters = new Converters();
        Actors = converters.fromArrayList(actors);
        Plot = plot;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getReleased() {
        return Released;
    }

    public void setReleased(String released) {
        Released = released;
    }

    public String getRated() {
        return Rated;
    }

    public void setRated(String rated) {
        Rated = rated;
    }

    public String getRuntime() {
        return Runtime;
    }

    public void setRuntime(String runtime) {
        Runtime = runtime;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }

    public String getDirector() {
        return Director;
    }

    public void setDirector(String director) {
        Director = director;
    }

    public String getWriter() {
        return Writer;
    }

    public void setWriter(String writer) {
        Writer = writer;
    }

    public ArrayList<String> getActors() {
        Converters converters = new Converters();
        return converters.fromString(Actors);
    }

    public void setActors(ArrayList<String> actors) {
        Converters converters = new Converters();
        Actors = converters.fromArrayList(actors);
    }

    public String getPlot() {
        return Plot;
    }

    public void setPlot(String plot) {
        Plot = plot;
    }

    public static class Converters {
        @TypeConverter
        public ArrayList<String> fromString(String value) {
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            return new Gson().fromJson(value, listType);
        }

        @TypeConverter
        public String fromArrayList(ArrayList<String> list) {
            Gson gson = new Gson();
            String json = gson.toJson(list);
            return json;
        }
    }
}
