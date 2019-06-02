package com.example.hp.bookseller.activity;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.hp.bookseller.R;
import com.example.hp.bookseller.presenter.ViewUserDetailsPresenter;
import com.example.hp.bookseller.utils.SharedPreferencesUtil;
import com.example.hp.bookseller.utils.UtilActivity;


public class ViewUserDetails extends UtilActivity {
    private ViewUserDetailsPresenter presenter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.userdetails_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.userdetails_update:
                Intent intent=new Intent(ViewUserDetails.this, RegistrationActivity.class);
                intent.putExtra("keyuser",SharedPreferencesUtil.getInstance(this).getUserDetails());
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdetails);

        presenter = new ViewUserDetailsPresenter(this,findViewById(android.R.id.content));
        presenter.setData();

    }

}
