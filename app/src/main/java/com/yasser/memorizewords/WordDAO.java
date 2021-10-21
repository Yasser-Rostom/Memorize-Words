package com.yasser.memorizewords;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WordDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Word word);

    @Query("DELETE FROM word_Table where category_name = :category")
    void deleteAll(String category);

    @Update
    void update(Word word);

    @Query("UPDATE word_Table set category_name = :newCategory " +
            "where category_name = :oldCategory")
    void updateByCategory(String oldCategory, String newCategory);

    @Delete
    void deleteWord(Word theWord);

    @Query("Select * from word_Table ORDER BY id ASC")
    LiveData<List<Word>> getOrderedWords();

    @Query("Select * from word_Table where category_name = :category ORDER BY id")
    LiveData<List<Word>> getWords(String category);

    @Query("SELECT * FROM word_Table WHERE word LIKE :search OR translation LIKE :search")
    List<Word> getSearchedNote(String search);

    @Query("SELECT COUNT(*) FROM word_Table WHERE category_name = :category;")
    int countCategory(String category);

}
