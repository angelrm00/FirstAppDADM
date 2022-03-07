package com.example.firstappdadm.utility;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.firstappdadm.FavouriteActivity;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class RequestQuotation extends Request<Quotation> {

    Response.Listener<Quotation> listener;
    String body;

    public RequestQuotation(int method, String url, @Nullable Response.ErrorListener errorListener, Response.Listener<Quotation> listener, String body) {
        super(method, url, errorListener);
        this.listener = listener;
        this.body = body;
    }

    @Override
    protected Response<Quotation> parseNetworkResponse(NetworkResponse response) {
        String reply = "";
        try {
            reply = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Quotation q = gson.fromJson(reply, Quotation.class);

        //Fix para las citas en ruso, ya que GSON no permite serializar caracteres rusos
        /*
        if( PreferenceManager.getDefaultSharedPreferences(new FavouriteActivity()).getString("language", "").equals("ru")) {
            String newText, newAuthor;
            String textPlain = new String(response.data);
            String[] aux = textPlain.split("quoteText");
            newText = aux[1].substring(3);

            aux = textPlain.split("quoteAuthor");
            newAuthor = aux[1].substring(3);

            q = new Quotation(newText, newAuthor);
        }
        */
        /*
        //fix quotes in russian

        String quote = "", author = "";
        try {
            byte[] utf8JsonString = q.getQuote().getBytes("UTF8");
            quote = new String(utf8JsonString);

            utf8JsonString = q.getAuthor().getBytes("UTF8");
            author = new String(utf8JsonString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("textos", quote);
        Log.d("textos", author);
        q = new Quotation(quote, author);*/
        return Response.success(q, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(Quotation response) {
        listener.onResponse(response);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if(body != null) return body.getBytes(StandardCharsets.UTF_8);
        else return null;
    }
}
