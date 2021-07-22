package com.example.basicnote;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CateViewModel extends AndroidViewModel {

    private WordRepository repository;
    private final LiveData<List<Category>> allCategories;

    public CateViewModel(@NonNull @NotNull Application application) {
        super(application);
        repository = new WordRepository(application);
        allCategories = repository.getAllCategory();
    }

    public  void deleteWord
            (Category category) {repository.deleteCategory(category);}


    public void deleteAllWords()
    {repository.deleteAllCategories();
    }
    LiveData<List<Category>> getAllCategories()
    { return allCategories; }
}
