package com.example.basicnote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewWordActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.basicnote.REPLY";
    public static final String EXTRA_REPLY1 = "com.example.android.basicnote.REPLY1";
    public boolean checkEdit;
    private EditText mEditWordView, mEditMeaningView;
  private NewWordModel viewModel;
    //var to check if true, to update, if false, we insert a new word
   // private int mID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_word);
        mEditWordView = findViewById(R.id.edit_word);
        mEditMeaningView = findViewById(R.id.edit_word2);
        final Button button = findViewById(R.id.button_save);
        Intent i = getIntent();
       viewModel = ViewModelProviders.of(this).get(NewWordModel.class);
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(mEditWordView.getText()
            ) || TextUtils.isEmpty(mEditMeaningView.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            }
            else {
                String word = mEditWordView.getText().toString();
                String meaning = mEditMeaningView.getText().toString();
                String category;
                if (i.getStringExtra("categoryFab") != null)
                {
                     category = i.getStringExtra("categoryFab");
                }
                else {
                    category = i.getStringExtra("category");
                }

                Log.d("NewWord","cat " +category);
                Word theWord = new Word(word,meaning,category);

                if (checkEdit)
                {
                    theWord.setId(i.getIntExtra("id", -1));
                    viewModel.update(theWord);
                }
                else {
                    viewModel.insert(theWord);
                }


                setResult(RESULT_OK, replyIntent);

             //   Toast.makeText(NewWordActivity.this, word, Toast.LENGTH_SHORT).show();
            }
            finish();
        });

        if (i.hasExtra("word"))
        {
            setTitle("Edit Word");
            mEditWordView.setText(i.getStringExtra("word"));
            mEditMeaningView.setText(i.getStringExtra("translation"));
            checkEdit = true;
        }
        else
        {

            setTitle("New Word");
            checkEdit = false;

        }
    }

}