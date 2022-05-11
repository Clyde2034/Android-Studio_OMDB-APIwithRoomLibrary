package com.yuchieh02.omdbapi.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieDAO {
    @Query("Select * from MovieSQLite")
    List<MovieEntity> getMovieitemList();

    @Query("Select * from MovieSQLite where Actors like '%' || :actors_name || '%' collate NOCASE")
    List<MovieEntity> getMoviesByActors(String actors_name);

    @Query("Select Title from MovieSQLite ")
    List<String> getTitles();

    @Insert
    void insertMovieitem(MovieEntity movieEntity);

    @Delete
    void DeleteItem(MovieEntity movieEntity);

    @Query("Delete from MovieSQLite")
    void DeleteAllItem();
}
