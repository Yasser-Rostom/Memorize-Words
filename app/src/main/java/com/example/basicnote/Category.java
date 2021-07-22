package com.example.basicnote;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_Table")
public class Category {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Category(String cate, int id)
    {
        this.category = cate;
        this.id = id;
    }

}
