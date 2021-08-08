package com.example.basicnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.parser.Table;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordListing extends AppCompatActivity {
    WordListAdapter adapter;
    private static final int STORAGE_PERMISSION_CODE = 2021;
    boolean saved = false;

    RecyclerView recyclerView;
    String categoryName;
    ProgressBar progressBar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                mWordViewModel.deleteAllWords(categoryName);
                return true;

            case R.id.action_export:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    //system OS >= Marshmallow(6.0), check if permission is enabled or not
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        //permission was not granted, request it
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, STORAGE_PERMISSION_CODE);
                    } else {
                        //permission already granted, call save pdf method
                        exportToPDF();
                    }
                } else {
                    //system OS < Marshmallow, call save pdf method
                    exportToPDF();
                }
                return true;


            //  checkPermission(WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private ViewModel mWordViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_listing);


        recyclerView = findViewById(R.id.recyclerview);
        progressBar = findViewById(R.id.progress_circular);
        //  final WordListAdapter adapter = new WordListAdapter(new WordListAdapter.WordDiff(), this);
        adapter = new WordListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL
                , false));

        recyclerView.setHasFixedSize(true);

        mWordViewModel = new ViewModelProvider(this).get(ViewModel.class);
        Intent catIntent = getIntent();
        categoryName = catIntent.getStringExtra("category");
        if (categoryName != null) setTitle(categoryName);


        mWordViewModel.getSpecificWords(categoryName).
                observe(this, words -> {
                    // ToDo add a search bar when data available


                    // Update the cached copy of the words in the adapter.
                    adapter.setWordList(words);
                    adapter.setOnItemClickListener(new WordListAdapter.setOnClickListener() {
                        @Override
                        public void onClick(Word word) {
                            Intent i = new Intent(WordListing.this,
                                    NewWordActivity.class);


                            i.putExtra("id", word.getId());
                            i.putExtra("word", word.getWord());
                            i.putExtra("category", categoryName);
                            i.putExtra("translation", word.getTranslation());
                            Log.d("onclick", "Word Listing after intent");

                            recyclerView.scheduleLayoutAnimation();
                            startActivity(i);
                        }

                        @Override
                        public void onLongClick(Word word) {

                        }
                    });


                });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(WordListing.this, NewWordActivity.class);
            intent.putExtra("categoryFab", categoryName);
            startActivity(intent);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {


                mWordViewModel.deleteWord(
                        adapter.getWord(viewHolder.getAdapterPosition()));
                recyclerView.scheduleLayoutAnimation();
            }
        }).attachToRecyclerView(recyclerView);
    }

    public void exportToPDF() {
        Log.d("creating pdf", "entered method");

        PDFBoxResourceLoader.init(getApplicationContext());
        PDDocument document = new PDDocument();

        PDPage page = new PDPage();

        document.addPage(page);
        PDFont font = PDType1Font.HELVETICA;


        SimpleDateFormat formatter = new SimpleDateFormat
                ("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date now = new Date();
        String filePath = Environment.getExternalStorageDirectory().
                getPath() + "/Download/wordlist_" + formatter.format(now) + ".pdf";

        PDPageContentStream contentStream;

        try {
            // Define a content stream for adding to the PDF
            contentStream = new PDPageContentStream(document, page);

            contentStream.beginText();

            contentStream.setNonStrokingColor(15, 38, 192);
            contentStream.setFont(font, 50);
            contentStream.newLineAtOffset(20, 750);
            contentStream.setLeading(40);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            showProgressBar();
            executor.execute(() -> {

                try { //Background work here - function goes here
                    saved = true;
                    for (Word word : adapter.getAllWords()) {

                        contentStream.showText(word.getWord() + ": "
                                + word.getTranslation());
                        contentStream.newLine();


                    }
                    contentStream.endText();
                    contentStream.close();

                    document.save(filePath);
                    document.close();
                    Toast.makeText(this, "the file has been saved!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                handler.post(() -> {
                    //UI Thread work here - progress bar goes here
                    hideProgressBar();
                });


            });

            executor.shutdown();


        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.d("creating pdf", filePath);

        try {

            // pdfDocument.writeTo(new FileOutputStream(file));


            Log.d("creating pdf", filePath);

            if (saved) {
                if (!TextUtils.isEmpty(filePath)) {
                    Intent intent = new Intent(WordListing.this,
                            Viewer.class);
                    intent.putExtra("path", filePath);
                    startActivity(intent);


                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted from popup, call savepdf method

                    exportToPDF();

                } else {
                    //permission was denied from popup, show error message
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    void hideProgressBar() {


        progressBar.setVisibility(View.INVISIBLE);
    }

    void showProgressBar() {
        //TODO make the progress bar rotate
        progressBar.setVisibility(View.VISIBLE);

    }
}







