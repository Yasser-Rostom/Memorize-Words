package com.example.memorizewords;



import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "word_Table",foreignKeys =
@ForeignKey(entity = Category.class,
        parentColumns = "category",
        childColumns = "category_name",
        onDelete = ForeignKey.SET_NULL,
        onUpdate = ForeignKey.CASCADE
        ), indices = {@Index("category_name")})

public class Word {


    public String getWord() {
        return this.word;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWord(@NonNull String word) {
        this.word = word;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @ColumnInfo(name = "category_name")
    private String categoryName;

    @NonNull
    @ColumnInfo(name = "word")
    private String word;

    @ColumnInfo(name = "translation")
    private String translation;


    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }


    public Word (@NonNull String word, String translation, String categoryName){
        this.word = word;
        this.translation = translation;
        this.categoryName = categoryName;
    }

}
