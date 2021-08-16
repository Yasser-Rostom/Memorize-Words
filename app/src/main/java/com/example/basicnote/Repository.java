package com.example.basicnote;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class Repository {
    private WordDAO mWordDao;
    private LiveData<List<Word>> mAllWords;
    private LiveData<List<Word>> someWords;
    private CategoryDAO mCategoryDao;
    private LiveData<List<Category>> allCategory;


    Repository(Application application) {
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
    LiveData<List<Word>> getWordsByCategory(String category) {
        someWords = mWordDao.getWords(category);
        return someWords;
    }


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
    void updateByCategory(String oldCategory, String newCategory)
    {
        WordRoomDB.dbWriteExecutor.execute(() -> {
            mWordDao.updateByCategory(oldCategory,newCategory);
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
    void deleteAllWords (String category)
    {
        mWordDao.deleteAll(category);
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
  /*  void listWords(String word) {
        WordRoomDB.dbWriteExecutor.execute(() -> {
            mWordDao.getWords(word);
        });
    }*/
    void insertCategory(Category category) {
        WordRoomDB.dbWriteExecutor.execute(() -> {
            mCategoryDao.insert(category);
        });
    }
}