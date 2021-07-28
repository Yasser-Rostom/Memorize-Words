package com.example.basicnote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String categoryName;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                mWordViewModel.deleteAllWords(categoryName);
                return true;

            case R.id.action_export:
                exportToPDF();
                return true;

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
        setContentView(R.layout.activity_main);
         recyclerView = findViewById(R.id.recyclerview);

        //  final WordListAdapter adapter = new WordListAdapter(new WordListAdapter.WordDiff(), this);
        final WordListAdapter adapter = new WordListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL
                , false));

        recyclerView.setHasFixedSize(true);

        mWordViewModel = new ViewModelProvider(this).get(ViewModel.class);
        Intent catIntent = getIntent();
         categoryName = catIntent.getStringExtra("category");
        if(categoryName != null)  setTitle(categoryName);

        Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();


        mWordViewModel.getSpecificWords(categoryName).
                observe(this, words -> {

            // Update the cached copy of the words in the adapter.
            adapter.setWordList(words);
            adapter.setOnItemClickListener(new WordListAdapter.setOnClickListener() {
                @Override
                public void onClick(Word word) {
                    Intent i = new Intent(MainActivity.this,
                            NewWordActivity.class);

                    i.putExtra("id", word.getId());
                    i.putExtra("word", word.getWord());
                    i.putExtra("category", categoryName);
                    i.putExtra("translation", word.getTranslation());
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
            Intent intent = new Intent(MainActivity.this, NewWordActivity.class);
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
        // create a new document
        PdfDocument document = new PdfDocument();

        // create a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.
                Builder(100, 100, 0).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        // draw something on the page
    //    View content = getContentView();
    //   content.draw(page.getCanvas());

        // finish the page
        document.finishPage(page);

        // add more pages

        // write the document content
    //    document.writeTo(getOutputStream());

        // close the document
        document.close();

    }

}





