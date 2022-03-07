package com.example.firstappdadm.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.firstappdadm.utility.Quotation;

@Database(version=1, entities = {Quotation.class})
public abstract class QuotationRoomDatabase extends RoomDatabase {

    private static QuotationRoomDatabase qrd;

    public static synchronized QuotationRoomDatabase getInstance(Context context) {
        if(qrd == null) {
            qrd = Room.databaseBuilder(context,
                    QuotationRoomDatabase.class,
                    "quotation_db").build();
        }
        return qrd;
    }

    public abstract QuotationDAO getDAO();
}
