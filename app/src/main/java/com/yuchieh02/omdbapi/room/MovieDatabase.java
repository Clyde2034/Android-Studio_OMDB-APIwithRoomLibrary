package com.yuchieh02.omdbapi.room;

import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@androidx.room.Database(entities = {MovieEntity.class}, version = 3)
@TypeConverters(MovieEntity.Converters.class)
public abstract class MovieDatabase extends RoomDatabase {

    public abstract MovieDAO movieDAO();

}
