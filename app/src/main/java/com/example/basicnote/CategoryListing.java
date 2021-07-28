package com.example.basicnote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CategoryListing extends AppCompatActivity {

    private String m_Text = "";

    private CateViewModel cateViewModel;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        recyclerView= findViewById(R.id.recyclerviewCat);
        final CatAdapter adapter = new CatAdapter();
        cateViewModel = ViewModelProviders.of(this)
                .get(CateViewModel.class);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2
                , StaggeredGridLayoutManager.VERTICAL));
        cateViewModel.getAllCategories().observe(this,categories ->
        {
            adapter.setCategoryList(categories);
            adapter.setOnItemClickListener(new CatAdapter.setOnClickListener() {
                @Override
                public void onClick(Category category) {

                    Intent intent = new Intent(CategoryListing.this,
                            WordListing.class);
                    intent.putExtra("category",category.getCategory());
                    startActivity(intent);

                }

                @Override
                public void onLongClick(Category category) {



                }
            });
        });


        FloatingActionButton fab = findViewById(R.id.fabCat);
        fab.setOnClickListener(view -> {
            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
            View mView = layoutInflaterAndroid.inflate(R.layout.input_dialog, null);
            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
            alertDialogBuilderUserInput.setView(mView);

            final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);

            alertDialogBuilderUserInput
                    .setCancelable(false)
                    .setPositiveButton("Save", (dialogBox, id) -> {

                        m_Text = userInputDialogEditText.getText().toString().trim();
                        if (m_Text.isEmpty()){
                            Toast.makeText(this, "No new category was saved", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (m_Text.isEmpty()) {
                                Toast.makeText(this, "No new category was saved",
                                        Toast.LENGTH_SHORT).show();
                            } else {

                                Category category = new Category(m_Text);

                                cateViewModel.insertCat(category);
                            }
                        }
                        }).setNegativeButton("Cancel",
                            (dialogBox, id) -> dialogBox.cancel());

            AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();
        });


    }
}