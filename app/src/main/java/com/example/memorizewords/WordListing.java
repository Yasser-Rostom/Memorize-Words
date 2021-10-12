package com.example.memorizewords;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDTrueTypeFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;


import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
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
    private static final String TAG = "Wordlisting";
    private static File filePath = new File(Environment.getExternalStorageDirectory() + "/Demo.xls");
    boolean saved = false;
    Sheet sheet = null;
    static Workbook workbook;
    private static Cell cell;

    //static int count = 0;
    AssetManager assetManager;

    static int counter = 0;
    PDPageContentStream contentStream;
    List<String> lines;
    PDPage page = new PDPage(PDRectangle.A4);

    //static int count2 = -1;
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
                if (adapter.getItemCount() > 0) {
                    if (!categoryName.isEmpty()) {
                        AlertDialog diaBox = AlertBuilderClass.AskDeleteAllWords(mWordViewModel, this, categoryName);
                        diaBox.show();
                    }
                } else {
                    Toast.makeText(this, "This category has no words!", Toast.LENGTH_SHORT).show();

                }

                return true;


            case R.id.action_export:
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

                if (Build.VERSION.SDK_INT >= 30){
                    if (!Environment.isExternalStorageManager()){


                        Intent getpermission = new Intent();

                        getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivity(getpermission);
                    }
                }
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
                    //system OS < Marshmallow, call save excel method
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
        adapter = new WordListAdapter(new WordListAdapter.WordDiff());
        catAdapter = new CatAdapter(new CatAdapter.CategoryDiff());
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
                    adapter.submitList(words);
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

                            //recyclerView.scheduleLayoutAnimation();
                            startActivity(i);
                        }

                        @Override
                        public void onLongClick(Word word) {

                        }
                    });


                });
        ExtendedFloatingActionButton fab = findViewById(R.id.fab);
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
                // recyclerView.scheduleLayoutAnimation();
            }
        }).attachToRecyclerView(recyclerView);
    }

    public String createFile() {
        //create folder
        File myDirectory = new File(Environment.getExternalStorageDirectory() + "/Download", "MemorizeWords");
        //check if folder exists
        if (!myDirectory.exists()) {
            myDirectory.mkdirs();

            Log.d("create file1", myDirectory.toString());
        } else {
            Log.d("folder exists", myDirectory.toString());

        }
        SimpleDateFormat formatter = new SimpleDateFormat
                ("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date now = new Date();

        String filePath = myDirectory.getAbsolutePath() + File.separator +
                "wordlist_" + formatter.format(now) + ".pdf";
        Log.d("create folder", filePath);
        return filePath;
    }

  /*  public void writeDataIntoFile(){

        if (adapter.getItemCount() > 0) {
            String filePath = createFile();
            Log.d("saving pdf", filePath);

           PDFBoxResourceLoader.init(getApplicationContext());
            PDDocument document = new PDDocument();
            assetManager = getAssets();


            PDFont font = PDTrueTypeFont.loadTTF(document, "Arial.ttf");
            document.addPage(page);


            try {


                contentStream = new PDPageContentStream(document, page);


                contentStream.setStrokingColor(44, 61, 79);
                contentStream.setFont(font, 18);
                contentStream.setLeading(14.5f);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                showProgressBar();
                executor.execute(() -> {

                    try { //Background work here - function goes here

                        //TODO I need to add functions to return words by category
                        //  for (Category category: catAdapter.getAllCats())

                        contentStream.beginText();
                        contentStream.newLineAtOffset(25, 770);

                        contentStream.showText("Category: " + categoryName);

                        contentStream.newLine();
                        contentStream.newLine();


                        contentStream.setNonStrokingColor(15, 38, 192);
                        contentStream.setFont(font, 12);
                        // contentStream.endText();
                        //   contentStream.newLineAtOffset(25, 770);
                        //   count += adapter.getCurrentList().size();
                        for (Word word : adapter.getCurrentList()) {

                            //TODO need to create new pages based on count2 number
                            lines = showMultiLineText(word.getWord() + ": "
                                            + word.getTranslation(),
                                    550,
                                    contentStream, font,
                                    12);
                            // count2 = count + lines.size();

                            if (counter > 50) {
                                contentStream.endText();
                                contentStream.close();

                                //Adding the blank page to the document
                                document.addPage(page = new PDPage());
                                Log.d("page", "new page added");

                                //taking a content for the new page
                                contentStream = new PDPageContentStream(document, page);
                                counter = 0;
                                contentStream.beginText();
                                contentStream.newLineAtOffset(25, 770);
                                contentStream.setNonStrokingColor(15, 38, 192);
                                contentStream.setFont(font, 12);
                                contentStream.setLeading(14.5f);
                                // contentStream.beginText();


                            }
                            for (String ln : lines) {
                                contentStream.showText(ln);
                                contentStream.newLine();
                                counter++;
                            }


//
//
                        }
                        Log.d("Counter", String.valueOf(counter));
//                     
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
        } else {
            Toast.makeText(this, "This category has no words!", Toast.LENGTH_SHORT).show();
        }
    }*/
  public void writeDataIntoFile() {


      if (adapter.getItemCount() > 0) {
          // String filePath = createFile();
          // Log.d("saving pdf", filePath);

              ExecutorService executor = Executors.newSingleThreadExecutor();
              Handler handler = new Handler(Looper.getMainLooper());
              showProgressBar();
              workbook = new HSSFWorkbook();

              executor.execute(() -> {




                  // Cell style for header row
                  CellStyle cellStyle = workbook.createCellStyle();
                  cellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
                  cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                  cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

                  // New Sheet

                  sheet = workbook.createSheet(categoryName);
                  Row row = sheet.createRow(0);

                  // Generate column headings

                  //Background work here - function goes here
                  cell = row.createCell(0);
                  cell.setCellValue("Term");
                  cell.setCellStyle(cellStyle);

                  cell = row.createCell(1);
                  cell.setCellValue("Meaning");
                  cell.setCellStyle(cellStyle);

                  int i = 0;
                  for (Word word : adapter.getCurrentList()) {

                      Row rowData = sheet.createRow(i + 1);
                      cell = rowData.createCell(0);
                      cell.setCellValue(word.getWord());

                      cell = rowData.createCell(1);
                      cell.setCellValue(word.getTranslation());
                      i++;
                  }
                  //TODo adding saving file function
                  exportDataIntoWorkbook(this);


                  handler.post(() -> {
                      //UI Thread work here - progress bar goes here
                      hideProgressBar();
                      // openingPDF(filePath);

                      boolean isWorkbookWrittenIntoStorage = exportDataIntoWorkbook(this);
                      if (isWorkbookWrittenIntoStorage)
                      {
                          Toast.makeText(this, "File has been saved in /Download/MemorizeWords", Toast.LENGTH_SHORT).show();

                      }




                  });


              });

              executor.shutdown();
         /* if (exportDataIntoWorkbook(this)) {

          } else{
              Toast.makeText(this, "Storage not available or read only", Toast.LENGTH_SHORT).show();
          }*/
      }
      else{
          Toast.makeText(this, "This category doesn't contain any words", Toast.LENGTH_SHORT).show();}
  }

    public static boolean exportDataIntoWorkbook(Context context) {
        boolean isWorkbookWrittenIntoStorage;

        // Check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e("Saving", "Storage not available or read only");
            return false;
        }

        // Creating a New HSSF Workbook (.xls format)

        isWorkbookWrittenIntoStorage = storeExcelInStorage(context);

        return isWorkbookWrittenIntoStorage;
    }


  private static boolean isExternalStorageReadOnly() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);
    }

    private static boolean storeExcelInStorage(Context context) {
        boolean isSuccess;
      File mydir = new File(Environment.getExternalStorageDirectory() +
                "/Download", "MemorizeWords");
        //check if folder exists
        if (!mydir.exists()) {
            mydir.mkdirs();

            Log.d("create file1", mydir.toString());
        } else {
            Log.d("folder exists", mydir.toString());

        }


        SimpleDateFormat formatter = new SimpleDateFormat
                ("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date now = new Date();

        String filePath = mydir.getAbsolutePath() + File.separator;
        Log.d("create folder", filePath);

        FileOutputStream fileOutputStream = null;

        try {

           File file = new File(filePath,"wordlist_" + formatter.format(now) + ".xls");
            if (!file.exists()){
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            Log.e(TAG, "Writing file" + filePath);
            isSuccess = true;
        } catch (IOException e) {
            Log.e(TAG, "Error writing Exception: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to save file due to Exception: ", e);
            isSuccess = false;
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Log.e("success", String.valueOf(isSuccess));
        return isSuccess;
    }
    /**
     * Checks if Storage is Available
     *
     * @return boolean
     */
    private static boolean isExternalStorageAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(externalStorageState);
    }
    /* private List<String> showMultiLineText(String text, int allowedWidth, PDPageContentStream contentStream,
                                           PDFont font,
                                           int fontSize) throws IOException {
        List<String> lines = new ArrayList<String>();
        String line = "";
        // split the text on spaces
        String[] words = text.split(" ");
        for (String word : words) {
            if (!line.isEmpty()) {
                line += " ";
            }
            // check if adding the word to the line surpasses the width of the page
            int size = (int) (fontSize * font.getStringWidth(line + word) / 1000);
            if (size > allowedWidth) {
                // if line + word surpasses the width of the page, add the line without the current word
                lines.add(line);
                // start new line with the current word
                line = word;
            } else {
                // if line + word fits the page width, add the current word to the line
                line += word;
            }
        }
        lines.add(line);
        return lines;

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







