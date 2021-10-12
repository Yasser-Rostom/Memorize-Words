package com.example.memorizewords;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WordListAdapter extends ListAdapter<Word, WordListAdapter.WordViewHolder> {
    setOnClickListener mlistener;

    @NonNull
    @NotNull
    @Override
    public List<Word> getCurrentList() {
        return super.getCurrentList();
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    protected WordListAdapter(@NonNull @NotNull DiffUtil.ItemCallback<Word> diffCallback) {
        super(diffCallback);
    }

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
        Word current = getItem(position);
        holder.bind(current.getWord(),current.getTranslation());

       /*wordList.sort(new Comparator<Word>() {
           @Override
           public int compare(Word o1, Word o2) {
               return Integer.compare( current.getId(),current.getId());
           }
       });*/
        holder.wordItemView.setText(current.getWord());
        holder.wordItemMeaning.setText(current.getTranslation());
    }





    public class WordViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;

        private final TextView wordItemMeaning;
        public WordViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            wordItemView = (TextView) itemView.findViewById(R.id.textView1);
            wordItemMeaning = (TextView) itemView.findViewById(R.id.textView2);

            itemView.setOnClickListener(v -> {
                if(mlistener != null)
                {
                    Log.d("onclick","word adapter click event");
                    mlistener.onClick(getItem(getAdapterPosition()));
                }

            });
        }
        public void bind(String word, String translation) {
            wordItemView.setText(word);
            wordItemMeaning.setText(translation);

        }
    }

    public interface setOnClickListener
    {
        void onClick(Word word);
        //to be added for selection
        void onLongClick(Word word);
    }
    public void setOnItemClickListener (setOnClickListener listener)
    {
        this.mlistener = listener;
        Log.d("onClick","word adapter listener method");
    }
    public Word getWord(int position)
    {
        return getItem(position);
    }

    static class WordDiff extends DiffUtil.ItemCallback<Word> {

        @Override
        public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
            return oldItem.getWord().equals(newItem.getWord()) &&
                    oldItem.getTranslation().equals(newItem.getTranslation()) &&
                    oldItem.getCategoryName().equals(newItem.getCategoryName());
        }
    }
}