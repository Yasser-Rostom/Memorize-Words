package com.example.basicnote;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class WordRepository {
    private WordDAO mWordDao;
    private LiveData<List<Word>> mAllWords;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    WordRepository(Application application) {
        WordRoomDB db = WordRoomDB.getDatabase(application);
        mWordDao = db.wordDao();
        mAllWords = mWordDao.getOrderedWords();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    void update(Word word)
    {
        WordRoomDB.dbWriteExecutor.execute(() -> {
            mWordDao.update(word);
        });
    }
    void deleteWord(Word word)
    {
        WordRoomDB.dbWriteExecutor.execute(() -> {
            mWordDao.deleteWord(word);
        });
    }
    void deleteAllWords ()
    {
        mWordDao.deleteAll();
    }
    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Word word) {
        WordRoomDB.dbWriteExecutor.execute(() -> {
            mWordDao.insert(word);
        });
    }
}