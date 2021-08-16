package com.example.basicnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CategoryListing extends AppCompatActivity  {


    private String m_Text = "";

    private int number;
    private CateViewModel cateViewModel;
    RecyclerView recyclerView;
    ViewModel viewModel;
     CatAdapter adapter;
     //Category category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        recyclerView = findViewById(R.id.recyclerviewCat);

       adapter = new CatAdapter();
        cateViewModel = ViewModelProviders.of(this)
                .get(CateViewModel.class);
        viewModel = ViewModelProviders.
                of(this).get(ViewModel.class);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2
                , StaggeredGridLayoutManager.VERTICAL));
        cateViewModel.getAllCategories().observe(this, categories ->
        {
            adapter.setCategoryList(categories);
            adapter.setOnItemClickListener(new CatAdapter.setOnClickListener() {
                @Override
                public void onClick(Category category) {

                    Intent intent = new Intent(CategoryListing.this,
                            WordListing.class);
                    intent.putExtra("category", category.getCategory());
                    startActivity(intent);

                }

                @Override
                public void onLongClick(Category category) {



                    Log.d("OnLongClcik", "entered inside categorylisting");

                }
            });
        });


        FloatingActionButton fab = findViewById(R.id.fabCat);

        fab.setOnClickListener(view -> {
            categoryManipulation(new Category(""));

        });
    }





    void categoryManipulation(Category categoryToHandle)
        {
            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
            View mView = layoutInflaterAndroid.inflate(R.layout.input_dialog, null);
            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
            alertDialogBuilderUserInput.setView(mView);

            final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
            if (categoryToHandle !=null)
            {
                userInputDialogEditText.setText(categoryToHandle.getCategory());
            }
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
                                if (categoryToHandle.getCategory().isEmpty())
                                {


                                    cateViewModel.insertCat(category);
                                }
                                else
                                {
                                    category.setId(categoryToHandle.getId());

                                    cateViewModel.update(category);
                                    /*cateViewModel.updateWordsByCategory(
                                            categoryToHandle.getCategory(),
                                            category.getCategory());*/

                                }


                            }
                        }
                    }).setNegativeButton("Cancel",
                    (dialogBox, id) -> dialogBox.cancel());

            AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();
        }



    @Override
    public boolean onContextItemSelected(MenuItem item) {

        adapter.getCategory(item.getGroupId());
        switch (item.getItemId()) {
            case 0:
                categoryManipulation(adapter.getCategory(item.getGroupId()));
                return true;

            case 1:
                AlertDialog diaBox = AskOption(adapter.getCategory(item.getGroupId()));
                diaBox.show();

                return true;
            default:
                return super.onContextItemSelected(item);

        }

    }
   /* @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case 1:


                categoryManipulation(category);
                break;

            case 2:


                break;
        }
        return true;
    }
*/

    private AlertDialog AskOption(Category category)
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete the selected category?")


                .setPositiveButton("Delete", (dialog, whichButton) -> {
                    //your deleting code
                    cateViewModel.deleteCategory(category);
                    dialog.dismiss();
                })
                .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                .create();

        return myQuittingDialogBox;
    }
}