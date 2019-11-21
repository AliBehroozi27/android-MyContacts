package com.mycontacts.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mycontacts.R;
import com.mycontacts.database.DataBase;
import com.mycontacts.modules.Contact;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditActivity extends AppCompatActivity {

    private Contact contact;
    private EditText etName, etNumber, etEmail;
    private Button bSave;
    private DataBase dataBase;
    private Intent intent;
    private Contact newContact;
    private String CONTACT = "contact";
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView myImage;
    private final int REQUEST_CODE = 2;
    private ImageView imageView;
    private InputStream imageStream;
    private ImageView ivContact;
    private String EXACT = "EXACT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        dataBase = new DataBase(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        contact = (Contact) bundle.getSerializable(CONTACT);

        etName = (EditText) findViewById(R.id.et_name);
        etNumber = (EditText) findViewById(R.id.et_number);
        etEmail = (EditText) findViewById(R.id.et_email);
        ivContact = (ImageView) findViewById(R.id.iv_contact);
        bSave = (Button) findViewById(R.id.b_save);

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

        //fab
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                } else {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contact.id == -1) {
                    newContact = new Contact(contact.id, etName.getText().toString().trim(), etNumber.getText().toString().trim(), etEmail.getText().toString().trim(), contact.bookMarked, contact.photoUri);
                    if (newContact.name.trim().equals(getString(R.string.empty))) {
                        Toast.makeText(getApplicationContext(), getString(R.string.name_empty), Toast.LENGTH_SHORT).show();
                    } else if (newContact.number.trim().equals("")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.number_empty), Toast.LENGTH_SHORT).show();
                    } else {
                        if (dataBase.searchData(EXACT, newContact.name).size() > 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.contact_exists), Toast.LENGTH_SHORT).show();
                        } else {
                            dataBase.addContact(newContact);
                            intent = new Intent();
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    }
                } else {
                    newContact = new Contact(contact.id, etName.getText().toString(), etNumber.getText().toString(), etEmail.getText().toString(), contact.bookMarked, contact.photoUri);
                    dataBase.editContact(contact.id, newContact);
                    Toast.makeText(getBaseContext(), getString(R.string.contact_edited), Toast.LENGTH_SHORT).show();
                    intent = new Intent();
                    intent.putExtra(CONTACT, newContact);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_loading_image), Toast.LENGTH_SHORT).show();
            }
            Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
            imageView = (ImageView) findViewById(R.id.iv_contact);
            imageView.setImageBitmap(yourSelectedImage);
            newContact = new Contact(contact.id, etName.getText().toString(), etNumber.getText().toString(), etEmail.getText().toString(), contact.bookMarked, selectedImage.toString());
            dataBase.editContact(contact.id, newContact);
            contact = newContact;
        }
    }

    @Override
    public void onBackPressed() {
        intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        }
    }

}

