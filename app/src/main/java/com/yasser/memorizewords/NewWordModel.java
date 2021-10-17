package com.yasser.memorizewords;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NewWordModel extends AndroidViewModel {
    private Repository mRepository;


    public NewWordModel (Application application) {
        super(application);
        mRepository = new Repository(application);
    }
    public void insert(Word word) { mRepository.insert(word); }

    public void update(Word word) { mRepository.update(word); }

    public LiveData<List<Category>> bringCateList()
    {
        return  mRepository.getAllCategory();

    }


}
