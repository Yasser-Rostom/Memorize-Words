package com.example.basicnote;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Word.class, Category.class},
        version = 7,
        exportSchema = false)
public abstract class WordRoomDB extends RoomDatabase {
  public abstract WordDAO wordDao();
  public abstract CategoryDAO categoryDAO();

    private static volatile WordRoomDB INSTANCE;
  private static final int THREADS_NO = 4;
   static final ExecutorService dbWriteExecutor =
   Executors.newFixedThreadPool(THREADS_NO);

    //Sington approach
    static WordRoomDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WordRoomDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WordRoomDB.class, "word_database").allowMainThreadQueries().fallbackToDestructiveMigration()
                         //  .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
  private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            dbWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                WordDAO dao = INSTANCE.wordDao();

          //   dao.deleteAll();

          /*      Word word = new Word("Hello");
                dao.insert(word);
                word = new Word("World");
                dao.insert(word);*/


            });
        }
    };
}
