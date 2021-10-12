package com.example.memorizewords;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CatAdapter extends ListAdapter<Category, CatAdapter.CatViewHolder> {
    setOnClickListener mlistener;

    protected CatAdapter(@NonNull @NotNull DiffUtil.ItemCallback<Category> diffCallback) {
        super(diffCallback);
    }

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

        Category category = getItem(position);
        holder.catName.setText(category.getCategory());
    }


  /*  public void setCategoryList(List<Category> list) {
        categoryList = list;

        //to update recyclerview

        notifyDataSetChanged();
    }*/


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
                    mlistener.onClick(getItem(getAdapterPosition()));
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



  /* public List<Category> getAllCats()
    {
        return categoryList;
    }*/

    @Override
    public void submitList(@Nullable @org.jetbrains.annotations.Nullable List<Category> list) {
        super.submitList(list);
    }

    @NonNull
    @NotNull
    @Override
    public List<Category> getCurrentList() {
        return super.getCurrentList();
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
        return getItem(position);
    }

    static class CategoryDiff extends DiffUtil.ItemCallback<Category> {

        @Override
        public boolean areItemsTheSame(@NonNull @NotNull Category oldItem, @NonNull @NotNull Category newItem) {
            return oldItem==newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull Category oldItem, @NonNull @NotNull Category newItem) {
            return  oldItem.getCategory().equals(newItem.getCategory());


        }
    }
    }

