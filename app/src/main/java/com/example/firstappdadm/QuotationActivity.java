package com.example.firstappdadm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.firstappdadm.databases.QuotationDAO;
import com.example.firstappdadm.databases.QuotationRoomDatabase;
import com.example.firstappdadm.utility.Quotation;

public class QuotationActivity extends AppCompatActivity {

    private int receivedQuotes = 0;
    private MenuItem addItem;
    private QuotationDAO quotationDAO;
    private TextView quotationTextView, authorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);

        String userName = PreferenceManager.getDefaultSharedPreferences(this).getString("username", "Nameless One");

        setWelcomeMessage(userName);
        quotationTextView = findViewById(R.id.quoteText);
        authorTextView = findViewById(R.id.autorText);
        quotationDAO = QuotationRoomDatabase.getInstance(this).getDAO();

        /*findViewById(R.id.refreshButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = findViewById(R.id.quoteText);
                textView.setText(getResources().getString(R.string.sampleQuotation));

                textView = findViewById(R.id.autorText);
                textView.setText(getResources().getString(R.string.sampleAutor));
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quotation_menu, menu);
        addItem = menu.findItem(R.id.addAction);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.addAction:
                addItem.setVisible(false);
                quotationDAO.addQuote(new Quotation(quotationTextView.getText().toString(),
                        authorTextView.getText().toString()));
                return true;
            case R.id.refreshAction:
                receivedQuotes++;
                setSampleQuote(receivedQuotes);
                addItem.setVisible(quotationDAO.searchQuote(quotationTextView.getText().toString()) == null);
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

    private void setSampleQuote(int counter) {
        TextView textView = findViewById(R.id.quoteText);
        String message = getResources().getString(R.string.sampleQuotation);
        String[] splitMessage = message.split("%1sd");
        splitMessage[0] += Integer.toString(counter);
        message = joinString(splitMessage);
        textView.setText(message);

        textView = findViewById(R.id.autorText);
        message = getResources().getString(R.string.sampleAutor);
        splitMessage = message.split("%1sd");
        splitMessage[0] += Integer.toString(counter);
        message = joinString(splitMessage);
        textView.setText(message);
    }
}