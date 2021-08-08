package com.example.basicnote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class Viewer extends AppCompatActivity {

    PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        pdfView = findViewById(R.id.pdfviewer);
        Intent intent = getIntent();
        File file = new File(intent.getStringExtra("path"));
        pdfView.fromFile(file).load();

    }
}