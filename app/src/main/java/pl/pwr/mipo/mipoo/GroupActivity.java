package pl.pwr.mipo.mipoo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.theme.DrawerTheme;
import com.melnykov.fab.FloatingActionButton;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;

import java.util.Arrays;
import java.util.List;

import pl.pwr.mipo.mipoo.db.ListsDatabaseAdapter;


public class GroupActivity extends ActionBarActivity {

    private MAdapter mMAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private ListsDatabaseAdapter mDbHelper;
    private DragSortListView mDslv;
    private EditText input;
    private MultiAutoCompleteTextView input2;
    private View positiveAction;
    private FloatingActionButton fab;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        mDbHelper = ListsDatabaseAdapter.getInstance(getBaseContext());
        mDbHelper.openConnection();

        displayGroupList();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(mDslv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogView(0, "", false);
            }
        });
        fab.show();

        // mDbHelper.insertGroupRecord("Moja grupa");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    private void displayGroupList() {
        // The desired columns to be bound
        String[] columns = new String[]{ListsDatabaseAdapter.GROUP_NAME,
                ListsDatabaseAdapter.GROUP_POSITION};

        // the XML defined views which the data will be bound to
        int[] ids = new int[]{R.id.group_name, R.id.group_position_list};

        // pull all items from database
        Cursor cursor = mDbHelper.getAllGroupRecords();

        mMAdapter = new MAdapter(this, R.layout.group_items, null, columns, ids,
                0);

        mDslv = (DragSortListView) findViewById(R.id.group_list);

        // set dslv profile for faster scroll speeds
        mDslv.setDragScrollProfile(ssProfile);

        mDslv.setAdapter(mMAdapter);
        mMAdapter.changeCursor(cursor);

        mDslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                mMAdapter.persistChanges();
                displayGroupList();
                // Get the cursor, positioned to the corresponding row in the
                // result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the item name and details from this row in the database.
                long groupId = cursor.getLong(cursor
                        .getColumnIndex(ListsDatabaseAdapter.GROUP_KEY_ROWID));
//                launchProductsActivity(listId);
//				String itemDetails = cursor.getString(cursor
//						.getColumnIndex("item_details"));
//				Toast.makeText(getApplicationContext(),
//						itemName + ": " + itemDetails, Toast.LENGTH_SHORT)
//						.show();

            }
        });

        mDslv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> listView, View view,
                                           int position, long id) {
                mMAdapter.persistChanges();
                displayGroupList();
                // Get the cursor, positioned to the corresponding row in the
                // result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the item name and details from this row in the database.
                long groupId = cursor.getLong(cursor
                        .getColumnIndex(ListsDatabaseAdapter.GROUP_KEY_ROWID));
                String groupName = cursor.getString(cursor
                        .getColumnIndex(ListsDatabaseAdapter.GROUP_NAME));
                showDialogView(groupId, groupName, true);
                return true;
            }
        });
    }

    private void showDialogView(final long groupId, String name, final boolean edit) {
        fab.hide();
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.groupName)
                .customView(R.layout.dialog_group, true)
                .positiveText(R.string.ok)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (edit) {
                            mDbHelper.updateGroupRecord(groupId,
                                    input.getText().toString());
                        } else {
                            mDbHelper.insertGroupRecord(
                                    input.getText().toString());
                         //   String user = input2.getText().toString();
                         //   List<String> users = Arrays.asList(user.split(","));
                         //   for (int i = 0; i < users.size(); i++) {
                                // mDbHelper.insertUserRecord(users.get(i));
                          //  }
                        }
                        fab.show();
                        mMAdapter.persistChanges();
                        displayGroupList();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        fab.show();
                    }

                }).build();

        dialog.setOnDismissListener(new MaterialDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                fab.show();
            }
        });

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        input = (EditText) dialog.getCustomView().findViewById(R.id.nameGroup);
        input.setText(name);

        input2 = (MultiAutoCompleteTextView) dialog.getCustomView().findViewById(R.id.input2);
        //(AutoCompleteTextView) dialog.getCustomView().findViewById(R.id.multiAutoComplete);
        String[] users = getResources().getStringArray(R.array.colorList);
        //new String[] { ListsDatabaseAdapter.KEY_NAME};

   //     adapter = new ArrayAdapter<String>(this, R.layout.autocomplete_row, users);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this,R.layout.autocomplete_row,users);
    //   Log.d(getPackageName(), input2 != null ? "input2 is not null!" : "input2 is null!");
        input2.setAdapter(adapter);
       input2.setThreshold(2);
       input2.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Button button = (Button) dialog.getCustomView().findViewById(R.id.button);

        DialogInterface.OnClickListener delete = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        };
        //button.setOnClickListener(delete);

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    private DragSortListView.DragScrollProfile ssProfile = new DragSortListView.DragScrollProfile() {
        @Override
        public float getSpeed(float w, long t) {
            if (w > 0.8f) {
                // Traverse all views in a millisecond
                return ((float) mMAdapter.getCount()) / 0.001f;
            } else {
                return 10.0f * w;
            }
        }
    };

    private class MAdapter extends SimpleDragSortCursorAdapter {

        public void persistChanges() {
            Cursor c = getCursor();
//            c.moveToPosition(-1);
            while (c.moveToNext()) {
                int groupPos = getListPosition(c.getPosition());
                if (groupPos == REMOVED) {
                    mDbHelper.deleteGroupRecord(c.getInt(c.getColumnIndex("_id")));

                } else if (groupPos != c.getPosition()) {
                    mDbHelper.updateGroupPosition(
                            c.getInt(c.getColumnIndex("_id")), groupPos);

                }
            }
        }

        public MAdapter(Context ctxt, int rmid, Cursor c, String[] cols,
                        int[] ids, int something) {
            super(ctxt, rmid, c, cols, ids, something);
            mContext = ctxt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            return v;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.activity_cursor, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel:
                displayGroupList();
                return (true);
            case R.id.save:
                mMAdapter.persistChanges();
                displayGroupList();
                return (true);
            case R.id.add:
                showDialogView(0, "", false);
                break;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onResume() {
        super.onResume();

        fab.show();
        Cursor cursor = mDbHelper.getAllGroupRecords();
        mMAdapter.changeCursor(cursor);
    }

    @Override
    protected void onPause() {
        super.onPause();
        fab.hide();
        Log.d("CURSOR", "OnPause()");
        mMAdapter.persistChanges();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("CURSOR", "OnDestroy()");
        mMAdapter.persistChanges();
    }
}