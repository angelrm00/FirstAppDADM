package com.example.firstappdadm.utility;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quotation_db")
public class Quotation {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "quote")
    @NonNull
    private String quote;
    @ColumnInfo(name = "author")
    private String author;

    public Quotation() {}

    public Quotation(String quote, String author) {
        this.quote = quote;
        this.author = author;
    }

    public int getId() { return id; }

    public String getQuote() {
        return quote;
    }

    public String getAuthor() {
        return author;
    }

    public void setId(int newId) { id = newId; }

    public void setQuote(String newQuote) {
        quote = newQuote;
    }

    public void setAuthor(String newAuthor) {
        author = newAuthor;
    }
}
