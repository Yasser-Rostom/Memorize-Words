package com.example.basicnote;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

public class NewWordModel extends AndroidViewModel {
    private Repository mRepository;


    public NewWordModel (Application application) {
        super(application);
        mRepository = new Repository(application);
    }
    public void insert(Word word) { mRepository.insert(word); }

    public void update(Word word) { mRepository.update(word); }




}
