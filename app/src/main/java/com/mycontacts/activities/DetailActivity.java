package com.mycontacts.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycontacts.R;
import com.mycontacts.database.DataBase;
import com.mycontacts.modules.Contact;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private static final String TEL = "tel";
    private static final String SMS = "sms:";
    private static final String YES = "yes";
    private static final String NO = "no";


    private Contact contact;
    private TextView etName, etNumber, etEmail;
    private ImageView ivCall, ivMessage, ivDelete;
    private boolean flag;
    private DataBase dataBase;
    private ImageView ivEdit;
    private Intent intent;
    private FloatingActionButton fab;
    private boolean bookmarked;
    private int RESULT_CODE = 1;
    private View sep;
    private String CONTACT ="contact";
    private InputStream imageStream;
    private ImageView ivContact;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        contact = (Contact) bundle.getSerializable(CONTACT);
        dataBase = new DataBase(getApplicationContext());
        bookmarked = contact.bookMarked;


        etName = (TextView) findViewById(R.id.et_name);
        etNumber = (TextView) findViewById(R.id.et_number);
        etEmail = (TextView) findViewById(R.id.et_email);
        ivContact = (ImageView) findViewById(R.id.iv_contact);
        sep = (View) findViewById(R.id.sep2);

        etName.setText(contact.name);
        etNumber.setText(contact.number);
        etEmail.setText(contact.email);

        if (etEmail.getText().equals(getString(R.string.empty))){
            etEmail.setVisibility(View.INVISIBLE);
            sep.setVisibility(View.INVISIBLE);
        }

        ivCall = (ImageView) findViewById(R.id.iv_call);
        ivMessage = (ImageView) findViewById(R.id.iv_message);
        ivDelete = (ImageView) findViewById(R.id.iv_delete);
        ivEdit = (ImageView) findViewById(R.id.iv_edit);

        Uri selectedImage = Uri.parse(contact.photoUri);
        imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(selectedImage);
        } catch (FileNotFoundException e) {
        }
        Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
        if (!contact.photoUri.equals(getString(R.string.empty)))
            ivContact.setImageBitmap(yourSelectedImage);
        
        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts(TEL, etNumber.getText() + "", null)));
            }
        });

        ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SMS + etNumber.getText() + "")));
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataBase = new DataBase(getApplicationContext());
                AlertDialog alertDialog = new AlertDialog.Builder(DetailActivity.this)
                        .setTitle(getString(R.string.contact_deletion))
                        .setPositiveButton(YES, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ArrayList<Contact> tmp = new ArrayList<Contact>();
                                tmp.add(contact);
                                dataBase.removeContact(tmp);
                                finish();
                            }
                        })
                        .setNegativeButton(NO, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).create();
                alertDialog.show();
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra(CONTACT , contact);
                startActivityForResult(intent , RESULT_CODE);
            }
        });


        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (bookmarked) {
            fab.setImageDrawable(getDrawable(R.drawable.ic_favorite));
            bookmarked = true;
        }else {
            fab.setImageDrawable(getDrawable(R.drawable.ic_favorite_not));
            bookmarked = false;
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                if (!bookmarked) {
                    dataBase.editContact(contact.id, new Contact(contact.id, contact.name, contact.number, contact.email, true , contact.photoUri));
                    fab.setImageDrawable(getDrawable(R.drawable.ic_favorite));
                    bookmarked = true;
                    Toast.makeText(getBaseContext(),getString(R.string.add_favorite), Toast.LENGTH_SHORT).show();
                }else {
                    dataBase.editContact(contact.id, new Contact(contact.id, contact.name, contact.number, contact.email, false , contact.photoUri));
                    fab.setImageDrawable(getDrawable(R.drawable.ic_favorite_not));
                    bookmarked = false;
                    Toast.makeText(getBaseContext(), getString(R.string.remove_favorite) , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CODE) {
            if(resultCode == Activity.RESULT_OK){
                contact = (Contact) data.getSerializableExtra(CONTACT);
                etName.setText(contact.name);
                etNumber.setText(contact.number);
                etEmail.setText(contact.email);

                Uri selectedImage = Uri.parse(contact.photoUri);
                imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                }
                Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                if (!contact.photoUri.equals(getString(R.string.empty)))
                    ivContact.setImageBitmap(yourSelectedImage);

                if (!etEmail.getText().equals(getString(R.string.empty))){
                    etEmail.setVisibility(View.VISIBLE);
                    sep.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
