package com.mycontacts.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mycontacts.R;
import com.mycontacts.adapters.ListViewAdapter;
import com.mycontacts.database.DataBase;
import com.mycontacts.modules.Contact;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private ListViewAdapter listViewAdapter;
    private ArrayList<Contact> contacts = new ArrayList<Contact>();
    private DataBase contactsDB;
    private List<Contact> tmpContacts;
    private TextView tvNoFavorite;
    private String FAVORITE = "favorite";
    private String CONTACT ="contact";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        tvNoFavorite = (TextView) findViewById(R.id.tv_no_favorite);

        contactsDB = new DataBase(this);
        tmpContacts = contactsDB.searchData(FAVORITE, "");
        for (int i = 0; i < tmpContacts.size(); i++) {
            contacts.add(tmpContacts.get(i));
        }

        if (contacts.size() == 0){
            tvNoFavorite.setVisibility(View.VISIBLE);
        }


        final ListView lvContacts = (ListView) findViewById(R.id.lv_contacts);
        listViewAdapter = new ListViewAdapter(this, R.layout.item_contact, contacts);
        lvContacts.setAdapter(listViewAdapter);

        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra(CONTACT, contacts.get(position));
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onResume() {
        contacts.clear();
        tmpContacts = contactsDB.searchData(FAVORITE, "");
        for (int i = 0; i < tmpContacts.size(); i++) {
            contacts.add(tmpContacts.get(i));
        }
        listViewAdapter.notifyDataSetChanged();
        if (contacts.size() == 0){
            tvNoFavorite.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }
}
