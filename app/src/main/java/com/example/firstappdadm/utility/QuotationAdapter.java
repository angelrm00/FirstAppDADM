package com.example.firstappdadm.utility;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstappdadm.R;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class QuotationAdapter extends RecyclerView.Adapter<QuotationAdapter.ViewHolder> implements OnItemClickListener {

    private List<Quotation> listQuotes;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public QuotationAdapter(List<Quotation> quotations, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
        listQuotes = quotations;
        onItemClickListener = clickListener;
        onItemLongClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quotation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Quotation quote = listQuotes.get(position);

        holder.textviewQuote.setText(quote.getQuote());
        holder.textviewAuthor.setText(quote.getAuthor());
    }

    @Override
    public int getItemCount() {
        return listQuotes.size();
    }

    @Override
    public void onItemClick(Quotation quotation) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textviewQuote;
        private TextView textviewAuthor;

        ViewHolder(View v) {
            super(v);
            textviewAuthor = v.findViewById(R.id.itemQuote);
            textviewQuote = v.findViewById(R.id.itemAuthor);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        onItemClickListener.onItemClick(getAdapterPosition());
                        onItemLongClickListener.onItemLongClick(getAdapterPosition());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void clearAllFavourites() {
        int size = listQuotes.size();
        listQuotes.clear();
        notifyItemRangeRemoved(0, size);
    }

    public interface OnItemClickListener {
        void onItemClick(int position) throws UnsupportedEncodingException;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public List<Quotation> getQuotationList() { return listQuotes; }

    public void removeQuotationAt(int position) {
        listQuotes.remove(position);
        notifyItemRemoved(position);
    }
}
