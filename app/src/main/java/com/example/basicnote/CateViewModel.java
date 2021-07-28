package com.example.basicnote;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CateViewModel extends AndroidViewModel {

    private Repository repository;
    private final LiveData<List<Category>> allCategories;

    public CateViewModel(@NonNull @NotNull Application application) {
        super(application);
        repository = new Repository(application);
        allCategories = repository.getAllCategory();
    }

    public  void deleteCategory
            (Category category) {repository.deleteCategory(category);}

            public void insertCat(Category category)
            {
                repository.insertCategory(category);
            }

    public void deleteAllCategories()
    {repository.deleteAllCategories();
    }
    LiveData<List<Category>> getAllCategories()
    { return allCategories; }
}
