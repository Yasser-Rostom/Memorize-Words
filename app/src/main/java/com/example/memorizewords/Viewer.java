package com.example.memorizewords;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Viewer extends AppCompatActivity {

    PDFView pdfView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        progressBar = findViewById(R.id.progressBar2);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        pdfView = findViewById(R.id.pdfviewer);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        showProgressBar();
        executor.execute(() -> {

            //Background work here - function goes here

            Intent intent = getIntent();
            File file = new File(intent.getStringExtra("path"));
            pdfView.fromFile(file).load();

            handler.post(() -> {
                //UI Thread work here - progress bar goes here
                hideProgressBar();

                Toast.makeText(this, "the file has been saved in /Download/MemorizeWords", Toast.LENGTH_SHORT).show();

            });


        });

        executor.shutdown();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://user clicked up button
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    void hideProgressBar() {


        progressBar.setVisibility(View.GONE);
    }

    void showProgressBar() {
        //TODO change color of circle
        progressBar.setVisibility(View.VISIBLE);

    }
}