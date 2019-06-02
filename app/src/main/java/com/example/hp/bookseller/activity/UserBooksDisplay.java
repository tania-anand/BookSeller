package com.example.hp.bookseller.activity;


import android.content.DialogInterface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.hp.bookseller.R;
import com.example.hp.bookseller.adapter.ViewitemsAdapter;
import com.example.hp.bookseller.contract.UserBookDisplayContract;
import com.example.hp.bookseller.interfaces.MyResponseConnectivity;
import com.example.hp.bookseller.model.BookBean;
import com.example.hp.bookseller.presenter.UserBookDisplayPresenter;
import com.example.hp.bookseller.utils.UtilActivity;
import com.example.hp.bookseller.utils.ViewItemClick;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserBooksDisplay extends UtilActivity implements UserBookDisplayContract.viewer, MyResponseConnectivity {

    @BindView(R.id.recyclerviewuser)
    RecyclerView mRecyclerView;
    private ArrayList<BookBean>  bookitemlist;
    BookBean bookBean1;

    int arraylistPosition;

    UserBookDisplayPresenter presenter;

    int whichAction = 100;


    void init() {

        ButterKnife.bind(this);

        presenter = new UserBookDisplayPresenter(this,this);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addOnItemTouchListener( new ViewItemClick(getApplicationContext(), new ViewItemClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                arraylistPosition=position;
                bookBean1=bookitemlist.get(arraylistPosition);
                showOptionsDialog();
            }
        }));


        if(isNetworkConnected(UserBooksDisplay.this)){
            initProgressDialog();
            showProgressDialog(1);
            presenter.retreiveBookList();
        }else
            showMsg(UserBooksDisplay.this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = {"Delete"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        showDeleteConfirm();
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void showDeleteConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete: "+bookBean1.getName());
        builder.setMessage("Are you sure to delete?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    whichAction = 101;
                    if(isNetworkConnected(UserBooksDisplay.this)) {
                        showProgressDialog(1);
                            // for delete
                            presenter.deleteBook(bookBean1);
                    }else
                        showMsg(UserBooksDisplay.this);
            }});
        builder.setNegativeButton("Cancel",null);
        builder.create().show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        init();

    }

    ViewitemsAdapter viewitemsAdapter;

    @Override
    public void onFetchBookListSuccess(ArrayList <BookBean> arrayList) {
        bookitemlist = arrayList;
        viewitemsAdapter = new ViewitemsAdapter(getApplicationContext(), R.layout.content_main, bookitemlist);
        mRecyclerView.setAdapter(viewitemsAdapter);
        showProgressDialog(0);

    }

    @Override
    public void onFetchBookListFailure(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        showProgressDialog(0);

    }

    @Override
    public void onDeleteItemSuccess(BookBean bookBean) {
        Toast.makeText(getApplicationContext(), "Book Deleted ", Toast.LENGTH_LONG).show();
        showProgressDialog(0);
        if(bookitemlist.contains(bookBean)){
            int index = bookitemlist.indexOf(bookBean);
            bookitemlist.remove(index);
            viewitemsAdapter.notifyDataSetChanged();

        }
        whichAction = 100;

    }

    @Override
    public void onDeleteItemFailure(String message) {
        Toast.makeText(getApplicationContext(), "Item Not Deleted ", Toast.LENGTH_LONG).show();
        showProgressDialog(0);
        whichAction = 100;

    }

    @Override
    public void onMyResponseConnectivity(int i) {
        showProgressDialog(0);
        if(isNetworkConnected(UserBooksDisplay.this)) {
            showProgressDialog(1);
            if (whichAction == 101) {
                // for delete
                presenter.deleteBook(bookBean1);
            } else {
                presenter.retreiveBookList();
            }
        }else
            showMsg(UserBooksDisplay.this);

    }

}
