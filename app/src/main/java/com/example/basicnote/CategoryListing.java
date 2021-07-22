package com.example.basicnote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

public class CategoryListing extends AppCompatActivity {

    CateViewModel cateViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        cateViewModel = ViewModelProviders.of(this)
                .get(CateViewModel.class);

    }
}