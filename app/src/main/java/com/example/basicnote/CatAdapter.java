package com.example.basicnote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.zip.Inflater;

public class CatAdapter extends RecyclerView.Adapter <CatAdapter.CatViewHolder> {

    @NonNull
    @NotNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_cat, parent, false);

        return  new CatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CatViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class CatViewHolder extends RecyclerView.ViewHolder {
        public CatViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
