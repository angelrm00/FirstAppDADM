package com.example.firstappdadm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.firstappdadm.databases.QuotationDAO;
import com.example.firstappdadm.databases.QuotationRoomDatabase;
import com.example.firstappdadm.utility.Quotation;
import com.example.firstappdadm.utility.QuotationAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuotationAdapter quotationAdapter;
    private QuotationDAO quotationDAO;
    private MenuItem clearItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        /*findViewById(R.id.authorInfoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://en.wikipedia.org/wiki/Special:Search?search=" + "Albert Einstein"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });*/
        quotationDAO = QuotationRoomDatabase.getInstance(this).getDAO();
        quotationAdapter = new QuotationAdapter(quotationDAO.getAllQuotes(), new QuotationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    goToAuthorInfo(quotationAdapter.getQuotationList().get(position));
                } catch (UnsupportedEncodingException e) {
                }
            }
        }, new QuotationAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                deleteSelectedQuotation(position);
            }
        });

        recyclerView = findViewById(R.id.recyclerQuotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(quotationAdapter);

    }

    public List<Quotation> getMockQuotations() {
        List<Quotation> quotes = new ArrayList<>();
        Quotation quotation = new Quotation();

        for(int i = 1; i <= 10; i++) {
            quotation.setQuote(getResources().getString(R.string.sampleQuotation) + " i");
            quotation.setAuthor(getResources().getString(R.string.sampleAutor));
            quotes.add(quotation);
        }

        return quotes;
    }

    private void goToAuthorInfo(Quotation quote) throws UnsupportedEncodingException {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://en.wikipedia.org/wiki/Special:Search?search=" + URLEncoder.encode(quote.getAuthor(),
                "UTF-8")));
        if(quote.getAuthor().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.author_impossible_not_possible_info), Toast.LENGTH_SHORT).show();
        }
        else if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void deleteSelectedQuotation(int position){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(getResources().getString(R.string.confirmDeleteDialog));
        dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                QuotationAdapter quotationAdapter = (QuotationAdapter) recyclerView.getAdapter();
                quotationDAO.deleteQuote(quotationAdapter.getQuotationList().get(position));
                quotationAdapter.removeQuotationAt(position);
                clearItem.setVisible(quotationAdapter.getItemCount() > 0);
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favourite_menu, menu);
        clearItem = menu.findItem(R.id.clearAllAction);
        menu.findItem(R.id.clearAllAction).setVisible(quotationAdapter.getItemCount() > 0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.clearAllAction:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage(getResources().getString(R.string.confirmClearAllDialogMessage));
                dialog.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quotationDAO.deleteAllQuotes();
                        QuotationAdapter quotationAdapter = (QuotationAdapter) recyclerView.getAdapter();
                        quotationAdapter.clearAllFavourites();
                        clearItem.setVisible(false);
                    }
                });
                dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}