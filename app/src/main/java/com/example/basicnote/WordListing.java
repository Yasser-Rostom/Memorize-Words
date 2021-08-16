package com.example.basicnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColor;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
    CatAdapter catAdapter;
    private static final int STORAGE_PERMISSION_CODE = 2021;
    boolean saved = false;

    RecyclerView recyclerView;
    String categoryName;
    ProgressBar progressBar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {




        switch (item.getItemId()) {
            case android.R.id.home://user clicked up button
                finish();
                return true;

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
                        writeDataIntoFile();
                    }
                } else {
                    //system OS < Marshmallow, call save pdf method
                    writeDataIntoFile();
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
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerview);
        progressBar = findViewById(R.id.progress_circular);
        //  final WordListAdapter adapter = new WordListAdapter(new WordListAdapter.WordDiff(), this);
        adapter = new WordListAdapter();
        catAdapter = new CatAdapter();
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
                            Log.d("onClick", "Word Listing after intent");

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
            Intent intent = new Intent(WordListing.this,
                    NewWordActivity.class);
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

    public String createFile() {
        //create folder
        File myDirectory = new File(Environment.getExternalStorageDirectory()+"/Download", "MemorizeWords");
        //check if folder exists
        if(!myDirectory.exists()) {
            myDirectory.mkdirs();

            Log.d("create file1",myDirectory.toString());
        }
        else
        {
            Log.d("folder exists",myDirectory.toString());

        }
        SimpleDateFormat formatter = new SimpleDateFormat
                ("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date now = new Date();

        String filePath = myDirectory.getAbsolutePath() + File.separator +
                "wordlist_" + formatter.format(now) + ".pdf";
       Log.d("create folder",filePath );
        return filePath;
    }

    public void writeDataIntoFile() {

        String filePath = createFile();
        Log.d("saving pdf", filePath);

        PDFBoxResourceLoader.init(getApplicationContext());
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();

        document.addPage(page);
        PDFont font = PDType1Font.HELVETICA;
        PDPageContentStream contentStream;

        try {
            // Define a content stream for adding to the PDF
            contentStream = new PDPageContentStream(document, page);

            contentStream.beginText();

            contentStream.setStrokingColor(44,61,79);
            contentStream.setFont(font, 70);
            contentStream.newLineAtOffset(20, 750);
            contentStream.setLeading(40);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            showProgressBar();
            executor.execute(() -> {

                try { //Background work here - function goes here

                    //TODO I need to add functions to return words by category
                    //  for (Category category: catAdapter.getAllCats())
                    /*for (Category category: catAdapter.getAllCats()) {
                        contentStream.showText(category.getCategory());
                        contentStream.newLine();
                        contentStream.showText("----------");
                        contentStream.newLine();*/
                    contentStream.newLine();
                    contentStream.showText("Category: " +categoryName);
                    contentStream.newLine();
                    contentStream.newLine();

                    contentStream.setNonStrokingColor(15, 38, 192);
                    contentStream.setFont(font, 50);
                    for (Word word : adapter.getAllWords()) {


                            contentStream.showText(word.getWord() + ": "
                                    + word.getTranslation());
                            contentStream.newLine();



                    }
                    contentStream.endText();
                    contentStream.close();


                    document.save(filePath);
                    saved = true;
                    document.close();


                } catch (IOException e) {
                    Log.d("error", String.valueOf(e));
                }


                handler.post(() -> {
                    //UI Thread work here - progress bar goes here
                    hideProgressBar();
                    openingPDF(filePath);
                    Toast.makeText(this, "the file has been saved in /Download/MemorizeWords", Toast.LENGTH_SHORT).show();

                });


            });

            executor.shutdown();


        } catch (IOException e) {
            Log.d("error", String.valueOf(e));

        }
    }

    public void openingPDF(String filePath) {

        Log.d("creating pdf", "entered method");


        Log.d("creating pdf", filePath);

        try {
            File file = new File(filePath);



            if (saved) {
                if (file.exists()) {
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

   /* private Table createSimpleExampleTable() {

        final TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(100, 50, 50, 50)
                .fontSize(8)
                .font(HELVETICA)
                .borderColor(Color.WHITE);

        // Add the header row ...
        tableBuilder.addRow(Row.builder()
                .add(TextCell.builder().text("Product").horizontalAlignment(LEFT).borderWidth(1).build())
                .add(TextCell.builder().text("2018").borderWidth(1).build())
                .add(TextCell.builder().text("2019").borderWidth(1).build())
                .add(TextCell.builder().text("Total").borderWidth(1).build())
                .backgroundColor(BLUE_DARK)
                .textColor(Color.WHITE)
                .font(HELVETICA_BOLD)
                .fontSize(9)
                .horizontalAlignment(CENTER)
                .build());

        // ... and some data rows
        double grandTotal = 0;
        for (int i = 0; i < DATA.length; i++) {
            final Object[] dataRow = DATA[i];
            final double total = (double) dataRow[1] + (double) dataRow[2];
            grandTotal += total;

            tableBuilder.addRow(Row.builder()
                    .add(TextCell.builder().text(String.valueOf(dataRow[0])).horizontalAlignment(LEFT).borderWidth(1).build())
                    .add(TextCell.builder().text(dataRow[1] + " €").borderWidth(1).build())
                    .add(TextCell.builder().text(dataRow[2] + " €").borderWidth(1).build())
                    .add(TextCell.builder().text(total + " €").borderWidth(1).build())
                    .backgroundColor(i % 2 == 0 ? BLUE_LIGHT_1 : BLUE_LIGHT_2)
                    .horizontalAlignment(RIGHT)
                    .build());
        }

        // Add a final row
        tableBuilder.addRow(Row.builder()
                .add(TextCell.builder().text("This spans over 3 cells, is right aligned and its text is so long that it even breaks. " +
                        "Also it shows the grand total in the next cell and furthermore vertical alignment is shown:")
                        .colSpan(3)
                        .lineSpacing(1f)
                        .borderWidthTop(1)
                        .textColor(WHITE)
                        .backgroundColor(BLUE_DARK)
                        .fontSize(6)
                        .font(HELVETICA_OBLIQUE)
                        .borderWidth(1)
                        .build())
                .add(TextCell.builder().text(grandTotal + " €").backgroundColor(LIGHT_GRAY)
                        .font(HELVETICA_BOLD_OBLIQUE)
                        .verticalAlignment(TOP)
                        .borderWidth(1)
                        .build())
                .horizontalAlignment(RIGHT)
                .build());

        return tableBuilder.build();
    }*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted from popup, call savepdf method

                    writeDataIntoFile();

                } else {
                    //permission was denied from popup, show error message
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    void hideProgressBar() {


        progressBar.setVisibility(View.GONE);
    }

    void showProgressBar() {
        //TODO change color of circle
        progressBar.setVisibility(View.VISIBLE);

    }
}







