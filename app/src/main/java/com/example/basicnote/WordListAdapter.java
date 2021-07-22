package com.example.basicnote;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WordListAdapter extends RecyclerView.Adapter <WordListAdapter.WordViewHolder> {
    setOnClickListener mlistener;
 List<Word> wordList;
    @NonNull
    @NotNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);

        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull WordViewHolder holder, int position) {

        Word current = wordList.get(position);
       /*wordList.sort(new Comparator<Word>() {
           @Override
           public int compare(Word o1, Word o2) {
               return Integer.compare( current.getId(),current.getId());
           }
       });*/
        holder.wordItemView.setText(current.getWord());
        holder.wordItemMeaning.setText(current.getTranslation());
    }
    public void setWordList(List<Word> list) {
        wordList = list;

        //to update recyclerview

        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {

        if (wordList != null)
            return wordList.size();
         return 0;

    }

    public  class WordViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;

        private final TextView wordItemMeaning;
        public WordViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            wordItemView = (TextView) itemView.findViewById(R.id.textView1);
            wordItemMeaning = (TextView) itemView.findViewById(R.id.textView2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mlistener != null)
                    {
                        mlistener.onClick(wordList.get(getAdapterPosition()));
                    }

                }
            });
        }
    }

    public interface setOnClickListener
    {
        void onClick(Word word);
        void onLongClick(Word word);
    }
    public void setOnItemClickListener (setOnClickListener listener)
    {
        this.mlistener = listener;
    }
    public Word getWord(int position)
    {
        return wordList.get(position);
    }
}