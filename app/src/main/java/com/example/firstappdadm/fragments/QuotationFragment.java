package com.example.firstappdadm.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.firstappdadm.R;
import com.example.firstappdadm.databases.QuotationDAO;
import com.example.firstappdadm.databases.QuotationRoomDatabase;
import com.example.firstappdadm.utility.Quotation;
import com.example.firstappdadm.utility.RequestQuotation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

public class QuotationFragment extends Fragment {

    private int receivedQuotes = 0;
    private FloatingActionButton addItem;
    private MenuItem refreshItem;
    private QuotationDAO quotationDAO;
    private TextView quotationTextView, authorTextView;
    private RequestQueue queue;
    private final String URL = "https://api.forismatic.com/api/1.0/";
    private boolean showAdd = false;
    private View mainView;
    private SwipeRefreshLayout swipeRefresh;
    private CoordinatorLayout coordinator;

    public QuotationFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mainView = inflater.inflate(R.layout.fragment_quotation, null);
        //setContentView(R.layout.fragment_quotation);

        quotationTextView = mainView.findViewById(R.id.quoteText);
        authorTextView = mainView.findViewById(R.id.autorText);
        quotationDAO = QuotationRoomDatabase.getInstance(getContext()).getDAO();

        queue = Volley.newRequestQueue(getContext());

        String userName = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("username", "Nameless One");
        if(savedInstanceState == null)
            setWelcomeMessage(userName);
        else {
            quotationTextView.setText(savedInstanceState.getString("QUOTE_TEXT"));
            authorTextView.setText(savedInstanceState.getString("AUTHOR_TEXT"));
            showAdd = savedInstanceState.getBoolean("SHOW_ADD");
        }

        getActivity().invalidateOptionsMenu();
        setHasOptionsMenu(true);

        swipeRefresh = mainView.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newQuoteGet();
            }
        });

        addItem = mainView.findViewById(R.id.addActionButton);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        quotationDAO.addQuote(new Quotation(quotationTextView.getText().toString(),
                                authorTextView.getText().toString()));
                    }
                }).start();

                addItem.setVisibility(View.INVISIBLE);
            }
        });
        if(showAdd)
        addItem.setVisibility(View.VISIBLE);
                else
                    addItem.setVisibility(View.INVISIBLE);

        coordinator = mainView.findViewById(R.id.coordinatorQuotes);

        return mainView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("QUOTE_TEXT", quotationTextView.getText().toString());
        outState.putString("AUTHOR_TEXT", authorTextView.getText().toString());
        super.onSaveInstanceState(outState);



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.quotation_menu, menu);
        refreshItem = menu.findItem(R.id.refreshAction);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.refreshAction:
                newQuoteGet();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void newQuoteGet() {
        showProgressBar();
        if(isConnectedToInternet()) {
            queue.start();
            int method = PreferenceManager.getDefaultSharedPreferences(mainView.getContext()).getString("httpmethod", "get").equals("get")? 0: 1;
            String languageCode = PreferenceManager.getDefaultSharedPreferences(mainView.getContext()).getString("language", "");
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
        } else {
            //Toast.makeText(getContext(), getResources().getString(R.string.device_not_connected), Toast.LENGTH_SHORT).show();
            Snackbar.make(coordinator, getResources().getString(R.string.device_not_connected), Snackbar.LENGTH_LONG).show();
        }
        endProgressBar();
    }

    private void setWelcomeMessage(String userName) {
        TextView quoteText = mainView.findViewById(R.id.quoteText);
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
        private WeakReference<QuotationFragment> quotationReference;

        RunnableQuotation(QuotationFragment quotationFragment) {
            quotationReference = new WeakReference<>(quotationFragment);
        }

        @Override
        public void run() {
            super.run();
            String quoteText = getQuotationActivity().quotationTextView.getText().toString();
            boolean ocultar = getQuotationActivity().quotationDAO.searchQuote(quoteText) == null;

            quotationReference.get().getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(ocultar) {
                        getQuotationActivity().addItem.setVisibility(View.VISIBLE);
                    }
                    else {
                        getQuotationActivity().addItem.setVisibility(View.INVISIBLE);
                    }
                    getQuotationActivity().refreshItem.setVisible(true);
                }
            });

        }

        public QuotationFragment getQuotationActivity() {
            return quotationReference.get();
        }
    }

    protected boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null) return false;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public void showProgressBar() {
        swipeRefresh.setRefreshing(true);
    }

    public void quotationReceived(Quotation quote) {


        if(quote.getQuote() == null) {
            //Toast.makeText(mainView.getContext(), getResources().getString(R.string.unable_get_quote), Toast.LENGTH_SHORT).show();
            Snackbar.make(coordinator, getResources().getString(R.string.unable_get_quote), Snackbar.LENGTH_SHORT).show();
            refreshItem.setVisible(true);
        }
        else {
            quotationTextView.setText(quote.getQuote());
            authorTextView.setText(quote.getAuthor());
            new RunnableQuotation(this).start();
        }
    }

    public void endProgressBar() {
        swipeRefresh.setRefreshing(false);
        refreshItem.setVisible(true);
    }
}