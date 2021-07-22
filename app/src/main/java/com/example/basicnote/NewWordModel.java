package com.example.basicnote;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NewWordModel extends AndroidViewModel {
    private WordRepository mRepository;


    public NewWordModel (Application application) {
        super(application);
        mRepository = new WordRepository(application);
    }
    public void insert(Word word) { mRepository.insert(word); }

    public void update(Word word) { mRepository.update(word); }




}
