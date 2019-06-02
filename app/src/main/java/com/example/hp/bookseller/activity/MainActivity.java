package com.example.hp.bookseller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.bookseller.interfaces.MyResponseConnectivity;
import com.example.hp.bookseller.presenter.MainPresenter;
import com.example.hp.bookseller.service.FetchUserData;
import com.example.hp.bookseller.R;
import com.example.hp.bookseller.adapter.ViewitemsAdapter;
import com.example.hp.bookseller.contract.MainContract;
import com.example.hp.bookseller.model.BookBean;
import com.example.hp.bookseller.utils.SharedPreferencesUtil;
import com.example.hp.bookseller.utils.UtilActivity;
import com.example.hp.bookseller.utils.ViewItemClick;


import java.util.ArrayList;

import butterknife.BindView;

public class MainActivity extends UtilActivity implements NavigationView.OnNavigationItemSelectedListener
        ,MainContract.viewer, MyResponseConnectivity {
    static MainActivity m;
    public static MainActivity getM(){
        return m;
    }


    @BindView(R.id.recyclerviewall)
    RecyclerView mRecyclerView;

    private ArrayList<BookBean>  bookitemlist;
    private GridLayoutManager gridLayoutManager;
    private ViewitemsAdapter viewitemsAdapter;

    TextView navnameTextView,navemailTextView;

    MainPresenter presenter;
    private void init() {
        presenter =  new MainPresenter(this,this);
        if(isNetworkConnected(MainActivity.this))
            networkTasks();
        else
            showMsg(MainActivity.this);


        mRecyclerView=findViewById(R.id.recyclerviewall);
        mRecyclerView.setHasFixedSize(true);
        gridLayoutManager= new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addOnItemTouchListener( new ViewItemClick(MainActivity.this, new ViewItemClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position)
            {
                BookBean book=bookitemlist.get(position);
                Intent intent= new Intent(MainActivity.this, ViewBookDetails.class);
                intent.putExtra("Key_bookbean",book);
                startActivity(intent);
            }
        }));




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), InsertBook.class);
                startActivity(intent);

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        m=this;
        View header =navigationView.getHeaderView(0);

        navnameTextView=header.findViewById(R.id.nav_name);
        navemailTextView=header.findViewById(R.id.nav_email);
        navemailTextView.setText(SharedPreferencesUtil.getInstance(getApplicationContext()).getEmail());

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        switch(id) {
            case R.id.nav_userprofile:
                intent=new Intent(MainActivity.this, ViewUserDetails.class);
                startActivity(intent);
                break;
            case R.id.nav_bookslist:
                intent= new Intent(MainActivity.this, UserBooksDisplay.class);
                startActivity(intent);
                break;
            case R.id.nav_chat:
                intent= new Intent(MainActivity.this, ChatRoom.class);
                startActivity(intent);
                break;
            case R.id.nav_feedback:
                String [] to ={"tanyaanand.anand@gmail.com","dhruvanand.anand@gmail.com"};
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setData(Uri.parse("mailto:"));
                email.setType("text/plain");
                email.putExtra(Intent.EXTRA_CC,to);
                email.putExtra(Intent.EXTRA_SUBJECT,"Feedback/Query From BOOK DIG user");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                break;

            case R.id.menu_logout:
                AlertDialog.Builder dialog=new AlertDialog.Builder(this);
                dialog.setTitle("logout");
                dialog.setMessage("Are you Sure Want to Logout?");
                dialog.setPositiveButton("yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferencesUtil.getInstance(getApplicationContext()).onLogout();
                                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                );
                dialog.setNegativeButton("No",null);
                dialog.create().show();
                break;
        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem search= menu.findItem( R.id.search);
        SearchView searchView=(SearchView)MenuItemCompat.getActionView(search);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener(){
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        viewitemsAdapter.filter(newText);
                        return false;
                    }
                }
        );
        return true;
    }


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


    private void networkTasks(){
        initProgressDialog();
        // get UserDetails From Server
        Intent i = new Intent(getApplicationContext(), FetchUserData.class);
        startService(i);

        presenter.retreiveBookList();

    }

    @Override
    public void onMyResponseConnectivity(int i) {
        showProgressDialog(0);
        if(isNetworkConnected(MainActivity.this))
            networkTasks();

        else
            showMsg(MainActivity.this);
    }
}