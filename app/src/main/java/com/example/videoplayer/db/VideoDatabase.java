package com.example.videoplayer.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.videoplayer.model.Video;

/**
 * @author Lokesh chennamchetty
 * @date 20/09/2020
 */
@Database(entities = {Video.class}, version = 1, exportSchema = false)
public abstract class VideoDatabase extends RoomDatabase {

    public abstract VideoDao videoDao();

    private static volatile VideoDatabase INSTANCE;

    public  static VideoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (VideoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            VideoDatabase.class,"video_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
