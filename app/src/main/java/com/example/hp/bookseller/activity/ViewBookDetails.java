package com.example.hp.bookseller.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.example.hp.bookseller.R;
import com.example.hp.bookseller.contract.ViewBookDetailsContract;
import com.example.hp.bookseller.model.BookBean;
import com.example.hp.bookseller.presenter.ViewBookDetailsPresenter;
import com.example.hp.bookseller.utils.UtilActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewBookDetails extends UtilActivity implements View.OnClickListener , ViewBookDetailsContract.viewer {

    /*
    In this after clicking main activity data of books is viewed.
    */

    @BindView(R.id.buttoncall)
    Button buttoncall;

    ViewBookDetailsPresenter presenter;

    void initviews() {
        buttoncall.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);
        ButterKnife.bind(this);
        initviews();

        Intent rcv=getIntent();
        BookBean bookBean= (BookBean) rcv.getSerializableExtra("Key_bookbean");
        presenter = new ViewBookDetailsPresenter(this,this,findViewById(android.R.id.content));
        presenter.setData(bookBean);
        showDialog();
        presenter.retreiveUserDetails();

        // for action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttoncall:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Posted By \n"+presenter.getUser().getName());
                builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                    String call1 = presenter.getUser().getPhone();
                    phoneIntent.setData(Uri.parse("tel:" + call1));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(phoneIntent);
                }
            });
                builder.setNegativeButton("Send Message", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //                Toast.makeText(getApplicationContext(),"Under Construction",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ViewBookDetails.this, Chat.class);
                        intent.putExtra("reciver",presenter.getUser());
                        startActivity(intent);
                        Log.i("Reciver Info ",presenter.getUser().toString());

                    }
                });
                builder.create().show();
        }
    }

    @Override
    public void onRetreiveUserDetailSuccess() {
        dismissDialog();

    }

    @Override
    public void onRetreiveUserDetailFailure() {
        dismissDialog();

    }
}
