package com.example.firstappdadm.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.firstappdadm.R;
import com.example.firstappdadm.databases.QuotationDAO;
import com.example.firstappdadm.databases.QuotationRoomDatabase;
import com.example.firstappdadm.utility.Quotation;
import com.example.firstappdadm.utility.QuotationAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FavouriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private QuotationAdapter quotationAdapter;
    private QuotationDAO quotationDAO;
    private MenuItem clearItem;

    private View mainView;
    private FragmentManager fragmentManager;

    private CoordinatorLayout coordinator;

    private ItemTouchHelper touchHelper;

    public FavouriteFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mainView = inflater.inflate(R.layout.fragment_favourite, null);

        quotationDAO = QuotationRoomDatabase.getInstance(mainView.getContext()).getDAO();
        quotationAdapter = new QuotationAdapter(new ArrayList<Quotation>(), new QuotationAdapter.OnItemClickListener() {
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

        recyclerView = mainView.findViewById(R.id.recyclerQuotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(mainView.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(quotationAdapter);

        addQuotesToRecyclerView();

        getActivity().invalidateOptionsMenu();
        setHasOptionsMenu(true);
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.setFragmentResultListener("remove_all", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                deleteAllQuotes();
            }
        });

        coordinator = mainView.findViewById(R.id.coordinatorFavourite);

        touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.RIGHT) | makeMovementFlags(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                deleteSelectedQuotationSwipe(viewHolder.getAdapterPosition());
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        });
        touchHelper.attachToRecyclerView(recyclerView);
        return mainView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().invalidateOptionsMenu();
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        //new RunnableFavourite(this).start();
    }

    private void addQuotesToRecyclerView() {
        //List<Quotation> quotes = quotationDAO.getAllQuotes();
        //quotationAdapter.addQuote(quotes);

        new RunnableFavourite(this).start();

    }

    private void goToAuthorInfo(Quotation quote) throws UnsupportedEncodingException {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://en.wikipedia.org/wiki/Special:Search?search=" + URLEncoder.encode(quote.getAuthor(),
                "UTF-8")));
        if(quote.getAuthor().isEmpty()) {
            //Toast.makeText(mainView.getContext(), getResources().getString(R.string.author_impossible_not_possible_info), Toast.LENGTH_SHORT).show();
            Snackbar.make(coordinator, getResources().getString(R.string.author_impossible_not_possible_info), Snackbar.LENGTH_SHORT).show();
        }
        else if (intent.resolveActivity(mainView.getContext().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void deleteSelectedQuotation(int position){
        AlertDialog.Builder dialog = new AlertDialog.Builder(mainView.getContext());
        dialog.setMessage(getResources().getString(R.string.confirmDeleteDialog));
        dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Quotation quotationToErase = quotationAdapter.getQuotationList().get(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        quotationDAO.deleteQuote(quotationToErase);
                    }
                }).start();
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

    private void deleteSelectedQuotationSwipe(int position){
        Quotation quotationToErase = quotationAdapter.getQuotationList().get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                quotationDAO.deleteQuote(quotationToErase);
            }
        }).start();
        quotationAdapter.removeQuotationAt(position);

        clearItem.setVisible(quotationAdapter.getItemCount() > 0);

        Snackbar snack = Snackbar.make(coordinator, getResources().getString(R.string.quote_deleted), Snackbar.LENGTH_LONG);
        snack.setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        quotationDAO.addQuote(quotationToErase);
                    }
                }).start();
                List<Quotation> quotes = new ArrayList<>();
                quotes.add(quotationToErase);
                quotationAdapter.addQuote(quotes);
            }
        });
        snack.show();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.favourite_menu, menu);
        clearItem = menu.findItem(R.id.clearAllAction);
        boolean visible = quotationAdapter.getQuotationList().size() > 0;
        clearItem.setVisible(visible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.clearAllAction:
                //FavouriteDialogFragment;
                //(new FavouriteDialogFragment()).show(getActivity().getSupportFragmentManager(), null);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void deleteAllQuotes() {
        QuotationAdapter quotationAdapter = (QuotationAdapter) recyclerView.getAdapter();
        quotationAdapter.clearAllFavourites();
        new Thread(new Runnable() {
            @Override
            public void run() {
                quotationDAO.deleteAllQuotes();
            }
        }).start();
        clearItem.setVisible(false);
    }

    private class RunnableFavourite extends Thread {
        private WeakReference<FavouriteFragment> favReference;

        RunnableFavourite(FavouriteFragment favouriteFragment) {
            favReference = new WeakReference<>(favouriteFragment);

        }
        @Override
        public void run() {
            super.run();

            List<Quotation> quotes = getFavouriteActivity().quotationDAO.getAllQuotes();
            favReference.get().getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getFavouriteActivity().quotationAdapter.addQuote(quotes);
                }
            });

        }

        public FavouriteFragment getFavouriteActivity() {
            return favReference.get();
        }
    }

    private class FavouriteDialogFragment extends DialogFragment {


        public FavouriteDialogFragment() {

        }
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

            AlertDialog a = new AlertDialog.Builder(mainView.getContext(), R.style.CustomAppTheme).setMessage(getResources().getString(R.string.confirmClearAllDialogMessage))
            .setPositiveButton(getResources().getString(R.string.confirm),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getParentFragmentManager().setFragmentResult("remove_all", savedInstanceState);
                }
            }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();

            a.create();

            return super.onCreateDialog(savedInstanceState);
            /*
            switch(menuItem.getItemId()) {
                case R.id.clearAllAction:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mainView.getContext());
                    dialog.setMessage(getResources().getString(R.string.confirmClearAllDialogMessage));
                    dialog.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteAllQuotes();
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
            }*/

        }
    }
}