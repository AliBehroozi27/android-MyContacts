package com.mycontacts.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mycontacts.modules.Contact;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "Contacts",
            TABLE_NAME = "Contacts",
            ID = "id",
            NAME = "name",
            BOOKMARKED = "bookmarked",
            EMAIL = "email",
            PHOTO_URI = "photo_uri",
            NUMBER = "number";
    private static Integer DATABASE_VERSION = 1;
    private ArrayList<Contact> Contacts;
    private Cursor cursor;
    private Context context;
    private int lastId;
    private static final String FAVORITE = "favorite" ,EXACT = "EXACT";

    public DataBase(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER ," + NAME
                + " text ," + NUMBER + " text ," + EMAIL + " text ," + BOOKMARKED + " bool ," + PHOTO_URI + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Contacts");
        onCreate(db);
    }

    public void addContact(Contact Contact) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ID + "= (SELECT MAX(ID)FROM " + TABLE_NAME + ")", null);
        if (cursor.moveToFirst()) {
            do {
                lastId = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put(ID, lastId + 1);
        values.put(NAME, Contact.name);
        values.put(NUMBER, Contact.number);
        values.put(EMAIL, Contact.email);
        values.put(BOOKMARKED, Contact.bookMarked);
        values.put(PHOTO_URI, Contact.photoUri);
        database.insert(TABLE_NAME, null, values);
        database.close();

    }

    public void removeContact(ArrayList<Contact> sc) {
        SQLiteDatabase database = this.getWritableDatabase();
        for (int i = 0; i < sc.size(); i++) {
            int id = sc.get(i).id;
            database.delete(TABLE_NAME, ID + "= " + id, null);
        }
        database.close();
    }

    public void editContact(Integer idPrevious, Contact ContactEdited) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, ContactEdited.id);
        values.put(NAME, ContactEdited.name);
        values.put(NUMBER, ContactEdited.number);
        values.put(EMAIL, ContactEdited.email);
        values.put(BOOKMARKED, ContactEdited.bookMarked);
        values.put(PHOTO_URI, ContactEdited.photoUri);
        database.update(TABLE_NAME, values, ID + "= " + idPrevious, null);
        database.close();
    }

    public ArrayList<Contact> getContacts() {
        Contacts = new ArrayList<Contact>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        Contact Contact;
        if (cursor.moveToFirst()) {
            do {
                Contact = new Contact(0, null, null, null, false , null);
                Contact.id = cursor.getInt(0);
                Contact.name = cursor.getString(1);
                Contact.number = cursor.getString(2);
                Contact.email = cursor.getString(3);
                Contact.bookMarked = cursor.getInt(4) > 0;
                Contact.photoUri = cursor.getString(5);
                Contacts.add(Contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return Contacts;
    }


    public int getDatabaseSize() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_NAME, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public ArrayList<Contact> searchData(String gen, String searched) {
        Contacts = new ArrayList<Contact>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor;
        switch (gen) {
            case EXACT:
                cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + NAME + " = ?" , new String[]{searched});
                break;
            case FAVORITE:
                cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + BOOKMARKED + "= " + '1', null);
                break;
            default:
                cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + NAME + " LIKE '%" + searched + "%'", null);
        }
        Contact Contact;
        if (cursor.moveToFirst()) {
            do {
                Contact = new Contact(0, null, null, null, false , null);
                Contact.id = cursor.getInt(0);
                Contact.name = cursor.getString(1);
                Contact.number = cursor.getString(2);
                Contact.email = cursor.getString(3);
                Contact.bookMarked = cursor.getInt(4) > 0;
                Contact.photoUri = cursor.getString(5);
                Contacts.add(Contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return Contacts;
    }
}

