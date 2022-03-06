package com.example.firstappdadm.databases;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.firstappdadm.utility.Quotation;

import java.util.List;

@Dao
public interface QuotationDAO {
    @Insert
    public void addQuote(Quotation quote);
    @Delete
    public void deleteQuote(Quotation quote);

    @Query("SELECT * FROM quotation_db")
    public List<Quotation> getAllQuotes();
    @Query("SELECT * FROM quotation_db WHERE quote = :quoteText")
    public Quotation searchQuote(String quoteText);
    @Query("DELETE FROM quotation_db")
    public void deleteAllQuotes();
}
