package com.example.basicnote;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class WordRepository {
    private WordDAO mWordDao;
    private LiveData<List<Word>> mAllWords;

    private CategoryDAO mCategoryDao;
    private LiveData<List<Category>> allCategory;


    WordRepository(Application application) {
        WordRoomDB db = WordRoomDB.getDatabase(application);

        mWordDao = db.wordDao();
        mAllWords = mWordDao.getOrderedWords();

        mCategoryDao = db.categoryDAO();
        allCategory = mCategoryDao.getCategory();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }
    LiveData<List<Category>> getAllCategory() {return allCategory;}
    void update(Word word)
    {
        WordRoomDB.dbWriteExecutor.execute(() -> {
            mWordDao.update(word);
        });
    }
    void updateCategory(Category category)
    {
        WordRoomDB.dbWriteExecutor.execute(() -> {
            mCategoryDao.update(category);
        });
    }
    void deleteWord(Word word)
    {
        WordRoomDB.dbWriteExecutor.execute(() -> {
            mWordDao.deleteWord(word);
        });
    }
    void deleteCategory(Category category)
    {
        WordRoomDB.dbWriteExecutor.execute(() -> {
            mCategoryDao.delete(category);
        });
    }
    void deleteAllWords ()
    {
        mWordDao.deleteAll();
    }
    void deleteAllCategories ()
    {
        mCategoryDao.deleteAll();
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Word word) {
        WordRoomDB.dbWriteExecutor.execute(() -> {
            mWordDao.insert(word);
        });
    }
    void insertCategory(Category category) {
        WordRoomDB.dbWriteExecutor.execute(() -> {
            mCategoryDao.insert(category);
        });
    }
}