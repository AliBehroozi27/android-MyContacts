package com.mycontacts.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycontacts.R;
import com.mycontacts.adapters.ListViewAdapter;
import com.mycontacts.database.DataBase;
import com.mycontacts.modules.Contact;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AbsListView.MultiChoiceModeListener, android.app.LoaderManager.LoaderCallbacks {

    private SimpleCursorAdapter adapter;
    private android.view.ActionMode mActionMode;
    private ArrayList<Contact> contacts = new ArrayList<Contact>();
    private ArrayList<Contact> AllContacts = new ArrayList<Contact>();
    private ListViewAdapter listViewAdapter;
    private MenuItem mSearchItem;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ImageView closeButtonSearchBar;
    private ImageView searchButtonSearchBar;
    private DataBase contactsDB;
    private ClipboardManager clipboard;
    private ProgressDialog progressDialog;
    private List<Contact> tmpContacts;
    private EditText etSearchBar;
    private Intent intent;
    private TextView tvNoContact;
    private static final int REQUEST_CODE = 0;
    private String CONTACT = "contact";
    private long DURATION = 250;
    private int INDEX = 10;
    private String EXACT = "EXACT";
    private final int RESULT_LOAD_IMAGE = 1;
    private final int RESULT_CODE_ADD = 1;
    private ArrayList<Contact> tContacts;


    @Override
    protected void onResume() {
        contacts.clear();
        List<Contact> tmpContacts = contactsDB.getContacts();
        for (int i = 0; i < contactsDB.getDatabaseSize(); i++) {
            contacts.add(tmpContacts.get(i));
        }
        listViewAdapter.notifyDataSetChanged();
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //storage permission


        //textView
        tvNoContact = (TextView) findViewById(R.id.tv_no_contact);

        //initial db
        contactsDB = new DataBase(this);

        //toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //fab
        FloatingActionButton fab = findViewById(R.id.fab);
//        alertDialog = new AlertDialog.Builder(this)
//                .setTitle("Add Contact")
//                .setView(R.layout.create_contact)
//                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        EditText etName = (EditText) alertDialog.findViewById(R.id.et_name);
//                        EditText etNumber = (EditText) alertDialog.findViewById(R.id.et_number);
//                        EditText etEmail = (EditText) alertDialog.findViewById(R.id.et_email);
//                        Contact newContact = new Contact(contactsDB.getDatabaseSize() + 1, etName.getText().toString().trim(), etNumber.getText().toString().trim(), etEmail.getText().toString().trim(), false, "");
//
//                            Toast.makeText(getApplicationContext(), "contact already exists !", Toast.LENGTH_SHORT).show();
//                        } else {
//                            contactsDB.addContact(newContact);
//                            contacts.add(newContact);
//                        }
//                        listViewAdapter.notifyDataSetChanged();
//                        if (contactsDB.getDatabaseSize() != 0) {
//                            tvNoContact.setVisibility(View.INVISIBLE);
//                        }
//                        etEmail.setText("");
//                        etName.setText("");
//                        etNumber.setText("");
//                    }
//                }).create();
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
//                alertDialog.show();
                intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra(CONTACT, new Contact(-1, "", "", "", false, ""));
                if (mSearchItem.isActionViewExpanded()) {
                    mSearchItem.collapseActionView();
                    animateSearchToolbar(1, false, false);
                }
                startActivityForResult(intent, RESULT_CODE_ADD);

            }
        });

        //navigation drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //get contacts from db
        if (contactsDB.getDatabaseSize() == 0) {
            tvNoContact.setVisibility(View.VISIBLE);
        }
        List<Contact> tmpContacts = contactsDB.getContacts();
        for (int i = 0; i < contactsDB.getDatabaseSize(); i++) {
            contacts.add(tmpContacts.get(i));
            AllContacts.add(tmpContacts.get(i));
        }


        final ListView lvContacts = (ListView) findViewById(R.id.lv_contacts);
        listViewAdapter = new ListViewAdapter(this, R.layout.item_contact, contacts);
        lvContacts.setAdapter(listViewAdapter);
        lvContacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                onListItemSelect(position);
                return true;
            }
        });
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mActionMode == null) {
                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.putExtra(CONTACT, contacts.get(position));
                    startActivity(intent);

                    if (mSearchItem.isActionViewExpanded()) {
                        mSearchItem.collapseActionView();
                        animateSearchToolbar(1, false, false);
                    }
                } else {
                    onListItemSelect(position);
                }
            }
        });
    }

    private void getContacts(){

    }


    private void onListItemSelect(int position) {
        listViewAdapter.toggleSelection(position);
        boolean hasCheckedItems = listViewAdapter.getSelectedCount() > 0;
        if (hasCheckedItems && mActionMode == null)
            mActionMode = startActionMode(this);
        else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
        if (mActionMode != null)
            mActionMode.setTitle(String.valueOf(listViewAdapter.getSelectedCount()) + getString(R.string.selected));
    }

    private void setupCursorAdapter() {
        String[] uiBindFrom = {ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI};
        int[] uiBindTo = {R.id.tv_name, R.id.iv_contact};
        adapter = new SimpleCursorAdapter(
                this, R.layout.item_contact,
                null, uiBindFrom, uiBindTo,
                0);
    }

    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mSearchItem = menu.findItem(R.id.m_search);
        SearchView sv = (SearchView) mSearchItem.getActionView().findViewById(R.id.search_view);
        searchButtonSearchBar = (ImageView) sv.findViewById(R.id.search_button);
        closeButtonSearchBar = (ImageView) sv.findViewById(R.id.search_close_btn);
        etSearchBar = (EditText) sv.findViewById(R.id.search_src_text);
        closeButtonSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchItem.collapseActionView();
            }
        });

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                contacts.clear();
                List<Contact> tmpContacts = contactsDB.searchData("", query);
                for (int i = 0; i < tmpContacts.size(); i++) {
                    contacts.add(tmpContacts.get(i));
                }
                listViewAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (mSearchItem.isActionViewExpanded()) {
                    animateSearchToolbar(1, false, false);
                    contacts.clear();
                    List<Contact> tmpContacts = contactsDB.getContacts();
                    for (int i = 0; i < contactsDB.getDatabaseSize(); i++) {
                        contacts.add(tmpContacts.get(i));
                    }
                    listViewAdapter.notifyDataSetChanged();
                }
                etSearchBar.setText("");
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchButtonSearchBar.performClick();
                animateSearchToolbar(1, true, true);
                return true;
            }
        });
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.quantum_grey_600));
        if (show) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int width = toolbar.getWidth() -
                        (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                        ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar,
                        isRtl(getResources()) ? toolbar.getWidth() - width : width, toolbar.getHeight() / 2, 0.0f, (float) width);
                createCircularReveal.setDuration(DURATION);
                createCircularReveal.start();
            } else {
                TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, (float) (-toolbar.getHeight()), 0.0f);
                translateAnimation.setDuration(DURATION);
                toolbar.clearAnimation();
                toolbar.startAnimation(translateAnimation);
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int width = toolbar.getWidth() -
                        (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                        ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar,
                        isRtl(getResources()) ? toolbar.getWidth() - width : width, toolbar.getHeight() / 2, (float) width, 0.0f);
                createCircularReveal.setDuration(DURATION);
                createCircularReveal.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        toolbar.setBackgroundColor(getThemeColor(getBaseContext(), R.attr.colorPrimary));
                        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.quantum_grey_600));
                    }
                });
                createCircularReveal.start();
            } else {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (-toolbar.getHeight()));
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setDuration(DURATION);
                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        toolbar.setBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimary));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                toolbar.startAnimation(animationSet);
            }
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.quantum_grey_600));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.m_search) {
            item.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.performClick();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLoaderManager().initLoader(INDEX, null, MainActivity.this);
                    listViewAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CODE_ADD) {
            if(resultCode == Activity.RESULT_OK){
                listViewAdapter.notifyDataSetChanged();
                if (contactsDB.getDatabaseSize() != 0) {
                    tvNoContact.setVisibility(View.INVISIBLE);

                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_favorites) {
            intent = new Intent(getApplicationContext(), FavoriteActivity.class);
            if (mSearchItem.isActionViewExpanded()) {
                mSearchItem.collapseActionView();
                animateSearchToolbar(1, false, false);
            }
            startActivity(intent);
        } else if (id == R.id.nav_sync) {
            setupCursorAdapter();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
            } else {
                getLoaderManager().initLoader(INDEX, null, MainActivity.this);
                listViewAdapter.notifyDataSetChanged();
            }
        } else if (id == R.id.nav_changePassword) {
            intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
            if (mSearchItem.isActionViewExpanded()) {
                mSearchItem.collapseActionView();
                animateSearchToolbar(1, false, false);
            }
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public android.content.Loader onCreateLoader(int i, Bundle bundle) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_contacts));
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        CursorLoader cursorLoader = new CursorLoader(MainActivity.this,
                ContactsContract.Contacts.CONTENT_URI, // URI
                null, // projection fields
                null, // the selection criteria
                null, // the selection args
                null // the sort order
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(android.content.Loader loader, Object o) {
        Cursor c = (Cursor) o;
        int id = 0;
        //contacts.clear();
        tContacts = new ArrayList<Contact>();
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String photoUri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));
                Log.e("AAA" , photoUri+"  "+name);
                if (!(contactsDB.searchData(EXACT, name.trim()).size() > 0)) {
                    tContacts.add(new Contact(id, name.trim(), number.trim(), "", false, photoUri));}
                id += 1;
            }
            phones.close();
        }
        cursor.close();
        for(int i = 0 ; i < tContacts.size() ; i++){
            contacts.add(tContacts.get(i));
        }
        adapter.swapCursor(c);
        listViewAdapter.notifyDataSetChanged();
        addContactsToDB(tContacts);
        if (contactsDB.getDatabaseSize() != 0) {
            tvNoContact.setVisibility(View.INVISIBLE);
        }
        progressDialog.dismiss();


    }

    private void addContactsToDB(ArrayList<Contact> contacts) {
        contactsDB = new DataBase(getApplicationContext());
        for (int i = 0; i < contacts.size(); i++) {
            contactsDB.addContact(contacts.get(i));
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader loader) {
        adapter.swapCursor(null);
    }


    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.selection_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        ArrayList<Contact> selectedItems = listViewAdapter.getSelectedItems();
        ArrayList<Integer> selectedItemIds = listViewAdapter.getSelectedItemIds();
        int selectedItemsCount = listViewAdapter.getSelectedCount();
        switch (item.getItemId()) {
            case R.id.cab_delete:
                contactsDB.removeContact(selectedItems);
                for (int i = 0; i < selectedItemsCount; i++) {
                    onListItemSelect(selectedItemIds.get(0));
                }
                contacts.clear();
                tmpContacts = contactsDB.getContacts();
                for (int i = 0; i < contactsDB.getDatabaseSize(); i++) {
                    contacts.add(tmpContacts.get(i));
                }
                listViewAdapter.notifyDataSetChanged();
                if (contactsDB.getDatabaseSize() == 0) {
                    tvNoContact.setVisibility(View.VISIBLE);
                }
                return true;
            case R.id.cab_copy:
                String copyContacts = "";
                for (int i = 0; i < selectedItemsCount; i++) {
                    copyContacts += selectedItems.get(i).name + " : " + selectedItems.get(i).number + "\n";
                }
                for (int i = 0; i < selectedItemsCount; i++) {
                    onListItemSelect(selectedItemIds.get(0));
                }
                clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(copyContacts);

                Toast.makeText(this, getString(R.string.copy_clipboard), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        listViewAdapter.removeSelection();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
    }
}

