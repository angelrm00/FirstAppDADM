package com.example.firstappdadm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class QuotationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);

        setWelcomeMessage();

        findViewById(R.id.refreshButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = findViewById(R.id.quoteText);
                textView.setText(getResources().getString(R.string.sampleQuotation));

                textView = findViewById(R.id.autorText);
                textView.setText(getResources().getString(R.string.sampleAutor));
            }
        });
    }

    private void setWelcomeMessage() {
        TextView quoteText = findViewById(R.id.quoteText);
        String welcomeMessage = getResources().getString(R.string.welcomeMessage);
        String[] splitMessage = welcomeMessage.split("%1s");
        splitMessage[1] = "Nameless One" + splitMessage[1];

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
}