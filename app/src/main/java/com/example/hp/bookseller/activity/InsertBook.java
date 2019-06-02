package com.example.hp.bookseller.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hp.bookseller.R;
import com.example.hp.bookseller.contract.InsertBookContract;
import com.example.hp.bookseller.interfaces.MyResponseConnectivity;
import com.example.hp.bookseller.model.BookBean;
import com.example.hp.bookseller.presenter.InsertBookPresenter;
import com.example.hp.bookseller.utils.SharedPreferencesUtil;
import com.example.hp.bookseller.utils.UtilActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InsertBook extends UtilActivity
        implements AdapterView.OnItemSelectedListener,View.OnClickListener
        ,CompoundButton.OnCheckedChangeListener,InsertBookContract.viewer, MyResponseConnectivity {

    @BindView(R.id.BName)
    EditText BName;
    @BindView(R.id.BAuthor)
    EditText BAuthor;
    @BindView(R.id.BPublisher)
    EditText BPublisher;
    @BindView(R.id.BPrice)
    EditText BPrice;
    @BindView(R.id.ConditionNew)
    RadioButton ConditionNew;
    @BindView(R.id.ConditionOld)
    RadioButton ConditionOld;
    @BindView(R.id.BImage)
    ImageView BImage;
    @BindView(R.id.BInsert)
    Button BInsert;
    @BindView(R.id.spinner_trait1)
    Spinner trait;

    private BookBean bean;

    Bitmap bitmap;
    static final int GALLERY_REQUESTCODE=1;
    static final int CAMERA_REQUESTCODE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_book);
        ButterKnife.bind(this);
        init();
    }

    private InsertBookPresenter presenter;

    private void init() {
        bean = new BookBean();
        presenter = new InsertBookPresenter(this,this,findViewById(android.R.id.content));

        initMyResponseConnectivityListener(this);

        trait.setOnItemSelectedListener(this);

        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.book);
        bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);

        ConditionNew.setOnCheckedChangeListener(this);
        ConditionOld.setOnCheckedChangeListener(this);

        BInsert.setOnClickListener(this);
    }

    private void insertDataIntoBean() {
        bean.setName(BName.getText().toString().trim());
        bean.setAuthor(BAuthor.getText().toString().trim());
        bean.setPublisher(BPublisher.getText().toString().trim());
        bean.setPrice(BPrice.getText().toString().trim());
        bean.setImage(getStringImage(bitmap));
        bean.setUserName(SharedPreferencesUtil.getInstance(this).getEmail());
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


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton.getId()==R.id.ConditionNew) {
            boolean check = ConditionNew.isChecked();
            if(check) {
                bean.setCondition("new");
            }
        }
        if(compoundButton.getId()==R.id.ConditionOld) {
            boolean check = ConditionOld.isChecked();
            if(check) {
                bean.setCondition("old");
            }
        }
    }



    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.BImage:
                String[] title = {"Gallery","Camera"};
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Upload Image");
                builder.setItems(title, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        Intent intent;
                        switch (which) {
                            case 0:
                                intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUESTCODE);
                                break;
                            case 1:
                                intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(intent, CAMERA_REQUESTCODE);
                                }
                                break;
                        }
                    }
                });
                builder.show();
                break;
            case R.id.BInsert:
                if(isNetworkConnected(InsertBook.this))
                    insertFunc();
                else
                    showMsg(InsertBook.this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUESTCODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                bitmap=Bitmap.createScaledBitmap(bitmap,500,500,true);
                BImage.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == CAMERA_REQUESTCODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
            BImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.spinner_trait1) {
            if(presenter.getTraitadapter()!=null)
            bean.setTrait(presenter.getTraitadapter().getItem(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onInsertSuccess() {
        Toast.makeText(getApplicationContext(),"Book Inserted",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(InsertBook.this, MainActivity.class);
        MainActivity.getM().finish();
        showProgressDialog(0);
        startActivity(intent);
        finish();
    }

    @Override
    public void onInsertFailure() {
        Toast.makeText(getApplicationContext(),"Insertion Failed",Toast.LENGTH_LONG).show();
        showProgressDialog(0);
    }


    private void insertFunc(){
        initProgressDialog();
        showProgressDialog(1);
        insertDataIntoBean();
        presenter.insertBook(bean);
    }

    @Override
    public void onMyResponseConnectivity(int i) {
        if(i==0)
            showMsg(InsertBook.this);
        else {
            if(isNetworkConnected(InsertBook.this))
                insertFunc();
            else
                showMsg(InsertBook.this);
        }


    }
}
