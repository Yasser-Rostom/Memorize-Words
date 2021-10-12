package com.example.memorizewords;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class NewWordActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String EXTRA_REPLY = "com.example.android.basicnote.REPLY";
    public static final String EXTRA_REPLY1 = "com.example.android.basicnote.REPLY1";
    public boolean checkEdit;
    String newCategory;

    private TextInputEditText mEditMeaningView,mEditWordView;

    List<String> array;
    private NewWordModel viewModel;


    CatAdapter adapter;
    //variable to check if true, to update, if false, we insert a new word
    // private int mID;
    //TODO add back button
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_word);
      //  adapter=new CatAdapter(new CatAdapter.CategoryDiff());
        array = new ArrayList<>();

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        mEditWordView = findViewById(R.id.edit_word);
        mEditMeaningView = findViewById(R.id.edit_word2);
        final Button button = findViewById(R.id.button_save);
        Spinner spinner = findViewById(R.id.spinner);

        LinearLayout linearLayout = findViewById(R.id.linearLayout1);
        Intent i = getIntent();
        viewModel = ViewModelProviders.of(this).get(NewWordModel.class);
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(mEditWordView.getText()
            ) || TextUtils.isEmpty(mEditMeaningView.getText())) {
                Toast.makeText(this, "Please fill both fields", Toast.LENGTH_SHORT).show();
                // setResult(RESULT_CANCELED, replyIntent);
            } else {
                String word = mEditWordView.getText().toString().trim();
                String meaning = mEditMeaningView.getText().toString().trim();
                String category;
                if (i.getStringExtra("categoryFab") != null)
                {
                    category = i.getStringExtra("categoryFab");
                }
                else
                    {
                    if (newCategory.isEmpty())
                    {
                        category = i.getStringExtra("category");
                    }
                    else {category = newCategory;}

                }

                Log.d("NewWord", "cat " + category);
                Word theWord = new Word(word, meaning, category);

                if (checkEdit) {
                    theWord.setId(i.getIntExtra("id", -1));
                    viewModel.update(theWord);
                } else
                    {
                    viewModel.insert(theWord);
                }


                setResult(RESULT_OK, replyIntent);
                finish();
                //   Toast.makeText(NewWordActivity.this, word, Toast.LENGTH_SHORT).show();
            }

        });




            // Set the ArrayAdapter (ad) data on the
            // Spinner which binds data to spinner


        if (i.hasExtra("word")) {
            setTitle("Edit Word");
            mEditWordView.setText(i.getStringExtra("word"));
            mEditMeaningView.setText(i.getStringExtra("translation"));

            checkEdit = true;
        } else {

            setTitle("New Word");
            checkEdit = false;

        }
        if (checkEdit) {
            linearLayout.setVisibility(View.VISIBLE);
            viewModel.bringCateList().observe(this, categories -> {
                //  adapter.submitList(categories);

                for (Category category : categories) {
                    array.add(category.getCategory());
                    Log.d("addingtoarray", category.getCategory());
                }
                Log.d("arraysize", String.valueOf(categories.size()));
                if (array.size() > 0) {


                    spinner.setOnItemSelectedListener(this);
                    ArrayAdapter ad
                            = new ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            array);

                    ad.setDropDownViewResource(
                            android.R.layout
                                    .simple_spinner_dropdown_item);
                    spinner.setAdapter(ad);
                    spinner.setSelection(array.indexOf(i.getStringExtra("category")));

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://user clicked up button
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        newCategory = array.get(position);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
