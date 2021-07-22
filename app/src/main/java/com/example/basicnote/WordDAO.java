package com.example.basicnote;



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

    @Query("DELETE FROM word_Table")
    void deleteAll();

    @Update
    void update(Word word);

    @Delete
    void deleteWord(Word theWord);

    @Query("Select * from word_Table ORDER BY word ASC")
    LiveData<List<Word>> getOrderedWords();

}
