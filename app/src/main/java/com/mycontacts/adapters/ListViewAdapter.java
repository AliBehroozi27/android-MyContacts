package com.mycontacts.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycontacts.R;
import com.mycontacts.modules.Contact;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Contact> {

    Activity context;
    ArrayList<Contact> Contacts;
    private SparseBooleanArray mSelectedItemsIds;
    private ArrayList<Contact> selectedItems = new ArrayList<Contact>();
    private ArrayList<Integer> selectedItemIdsInLV = new ArrayList<Integer>();
    private InputStream imageStream;


    public ListViewAdapter(Activity context, int resId, ArrayList<Contact> Contacts) {
        super(context, resId, Contacts);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.Contacts = Contacts;
    }

    private class ViewHolder {
        TextView contactName;
        ImageView call, message , contactImage;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_contact, null);
            holder = new ViewHolder();
            holder.contactName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.call = (ImageView) convertView.findViewById(R.id.iv_call);
            holder.message = (ImageView) convertView.findViewById(R.id.iv_message);
            holder.contactImage = (ImageView) convertView.findViewById(R.id.iv_contact);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Contact Contact = getItem(position);
        Uri selectedImage;
        try{
            selectedImage= Uri.parse(Contact.photoUri);}
        catch (Exception e){
            selectedImage = Uri.parse("");
            Contact.photoUri = "";
        }
        imageStream = null;
        try {
            imageStream = context.getContentResolver().openInputStream(selectedImage);
        } catch (FileNotFoundException e) {
        }
        Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
        if (!Contact.photoUri.equals(""))
            try {
                holder.contactImage.setImageBitmap(yourSelectedImage);
            }catch (Exception e){
                holder.contactImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_person));
            }
        else
            holder.contactImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_person));


        holder.contactName.setText(Contact.name);
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", Contact.number, null)));
            }
        });
        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + Contact.number)));
            }
        });
        convertView.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4 : Color.TRANSPARENT);
        return convertView;
    }

    @Override
    public void add(Contact Contact) {
        Contacts.add(Contact);
        notifyDataSetChanged();
        Toast.makeText(context, Contacts.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void remove(Contact object) {
        Contacts.remove(object);
        notifyDataSetChanged();
    }


    public List<Contact> getContacts() {
        return Contacts;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value) {
            selectedItems.add(Contacts.get(position));
            selectedItemIdsInLV.add(position);
            mSelectedItemsIds.put(position, value);
        } else {
            selectedItems.remove(Contacts.get(position));
            selectedItemIdsInLV.remove((Object) position);
            mSelectedItemsIds.delete(position);
        }

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public ArrayList<Contact> getSelectedItems() {
        return selectedItems;
    }

    public ArrayList<Integer> getSelectedItemIds() {
        return selectedItemIdsInLV;
    }

}