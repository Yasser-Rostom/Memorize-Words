package com.example.memorizewords;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity
{
    @Override
    protected void onCreate
            (@Nullable @org.jetbrains.annotations.Nullable
                                        Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this,
                CategoryListing.class));
    }
}

