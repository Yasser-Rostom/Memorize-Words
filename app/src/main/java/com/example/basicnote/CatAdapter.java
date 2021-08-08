package com.example.basicnote;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class CatAdapter extends RecyclerView.Adapter <CatAdapter.CatViewHolder> {
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
    public  class CatViewHolder extends RecyclerView.ViewHolder {
        private final TextView catName;
        public CatViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            catName = itemView.findViewById(R.id.textView1);
            itemView.setOnClickListener(v -> {
                if(mlistener != null &
                        getAdapterPosition()!= RecyclerView.NO_POSITION)
                {
                    mlistener.onClick(categoryList.get(getAdapterPosition()));
                    Log.d("onclick","category adapter");
                }

            });
            itemView.setOnLongClickListener(v ->
            {
                if(mlistener != null &
                        getAdapterPosition()!= RecyclerView.NO_POSITION)
                {
                    mlistener.onLongClick(categoryList.get(getAdapterPosition()));
                }
                return true;
            });
        }
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

