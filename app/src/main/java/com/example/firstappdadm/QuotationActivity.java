package com.example.firstappdadm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.firstappdadm.databases.QuotationDAO;
import com.example.firstappdadm.databases.QuotationRoomDatabase;
import com.example.firstappdadm.utility.Quotation;
import com.example.firstappdadm.utility.RequestQuotation;

import java.lang.ref.WeakReference;
import java.net.URL;

public class QuotationActivity extends AppCompatActivity {

    private int receivedQuotes = 0;
    private MenuItem addItem;
    private MenuItem refreshItem;
    private QuotationDAO quotationDAO;
    private TextView quotationTextView, authorTextView;
    private RequestQueue queue;
    private final String URL = "https://api.forismatic.com/api/1.0/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);

        String userName = PreferenceManager.getDefaultSharedPreferences(this).getString("username", "Nameless One");

        setWelcomeMessage(userName);
        quotationTextView = findViewById(R.id.quoteText);
        authorTextView = findViewById(R.id.autorText);
        quotationDAO = QuotationRoomDatabase.getInstance(this).getDAO();

        queue = Volley.newRequestQueue(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quotation_menu, menu);
        addItem = menu.findItem(R.id.addAction);
        refreshItem = menu.findItem(R.id.refreshAction);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.addAction:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        quotationDAO.addQuote(new Quotation(quotationTextView.getText().toString(),
                                authorTextView.getText().toString()));
                    }
                }).start();

                addItem.setVisible(false);
                return true;
            case R.id.refreshAction:
                showProgressBar();
                if(isConnectedToInternet()) {
                    queue.start();
                    int method = PreferenceManager.getDefaultSharedPreferences(this).getString("httpmethod", "get").equals("get")? 0: 1;
                    String languageCode = PreferenceManager.getDefaultSharedPreferences(this).getString("language", "");
                    String body = "?method=getQuote&format=json&lang=" + languageCode;
                    RequestQuotation request = new RequestQuotation(method, URL + body, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            quotationReceived(new Quotation());
                        }
                    }, new Response.Listener<Quotation>() {
                        @Override
                        public void onResponse(Quotation response) {
                            quotationReceived(response);
                        }
                    }, body);
                    queue.add(request);
                } else Toast.makeText(this, getResources().getString(R.string.device_not_connected), Toast.LENGTH_SHORT).show();
                endProgressBar();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void setWelcomeMessage(String userName) {
        TextView quoteText = findViewById(R.id.quoteText);
        String welcomeMessage = getResources().getString(R.string.welcomeMessage);
        String[] splitMessage = welcomeMessage.split("%1s");
        if(userName.isEmpty())
            splitMessage[1] = "Nameless One" + splitMessage[1];
        else
            splitMessage[1] = userName + splitMessage[1];

        welcomeMessage = joinString(splitMessage);
        quoteText.setText(welcomeMessage);
    }

    private String joinString(String[] arrayString) {
        String result = "";

        for(String a: arrayString) {
            result = result + a;
        }

        return result;
    }

    private class RunnableQuotation extends Thread {
        private WeakReference<QuotationActivity> quotationReference;

        RunnableQuotation(QuotationActivity quotationActivity) {
            quotationReference = new WeakReference<>(quotationActivity);
        }

        @Override
        public void run() {
            super.run();
            String quoteText = getQuotationActivity().quotationTextView.getText().toString();
            boolean ocultar = getQuotationActivity().quotationDAO.searchQuote(quoteText) == null;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getQuotationActivity().addItem.setVisible(ocultar);
                    getQuotationActivity().refreshItem.setVisible(true);
                }
            });

        }

        public QuotationActivity getQuotationActivity() {
            return quotationReference.get();
        }
    }

    protected boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(connectivityManager == null) return false;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public void showProgressBar() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        addItem.setVisible(false);
        refreshItem.setVisible(false);
    }

    public void quotationReceived(Quotation quote) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        if(quote.getQuote() == null) {
            Toast.makeText(this, getResources().getString(R.string.unable_get_quote), Toast.LENGTH_SHORT).show();
            refreshItem.setVisible(true);
        }
        else {
            quotationTextView.setText(quote.getQuote());
            authorTextView.setText(quote.getAuthor());
            new RunnableQuotation(this).start();
        }
    }

    public void endProgressBar() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        refreshItem.setVisible(true);
    }
}