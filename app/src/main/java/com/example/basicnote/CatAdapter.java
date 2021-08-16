package com.example.basicnote;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class CatAdapter extends RecyclerView.Adapter <CatAdapter.CatViewHolder>{
    setOnClickListener mlistener;
    List<Category> categoryList;
    @NonNull
    @NotNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_cat, parent, false);

        return new CatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CatViewHolder holder, int position) {

        Category category = categoryList.get(position);
        holder.catName.setText(category.getCategory());
    }

    @Override
    public int getItemCount() {

        if (categoryList != null)
        {
            return categoryList.size();
        }
        return 0;

    }
    public void setCategoryList(List<Category> list) {
        categoryList = list;

        //to update recyclerview

        notifyDataSetChanged();
    }


    public  class CatViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        private final TextView catName;
        private final LinearLayout linearLayout;

        public CatViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            catName = itemView.findViewById(R.id.textView1);
            linearLayout = itemView.findViewById(R.id.linearRecycler);
            itemView.setOnClickListener(v -> {
                if (mlistener != null &
                        getAdapterPosition() != RecyclerView.NO_POSITION) {
                    mlistener.onClick(categoryList.get(getAdapterPosition()));
                    Log.d("onclick", "category adapter");
                }

            });
            linearLayout.setOnCreateContextMenuListener(this);




        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(),0,0,"Update");
            menu.add(getAdapterPosition(),1,0,"Delete");
        }





    }



    public List<Category> getAllCats()
    {
        return categoryList;
    }

    public interface setOnClickListener
    {
        void onClick(Category category);

        //to be added for selection
        void onLongClick(Category category);
    }
    public void setOnItemClickListener (CatAdapter.setOnClickListener listener)
    {
        this.mlistener = listener;
    }
    public Category getCategory(int position)
    {
        return categoryList.get(position);
    }

    }

