package pl.pwr.mipo.mipoo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.mobeta.android.dslv.DragSortCursorAdapter;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.ResourceDragSortCursorAdapter;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;

import org.w3c.dom.Text;

import pl.pwr.mipo.mipoo.db.ListsDatabaseAdapter;
import pl.pwr.mipo.mipoo.design.CheckBox;

/**
 * Created by CitrusPanc on 4/5/2015.
 */
public class ProductsActivity extends ActionBarActivity{

    private MAdapter mMAdapter;
    private ListsDatabaseAdapter mDbHelper;
    private DragSortListView mDslv;
    private Bundle mExtras;
    private EditText inputName;
    private CheckBox inputBox;
    private View positiveAction;
    private long extraListId;

    private FloatingActionButton fab;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDbHelper = ListsDatabaseAdapter.getInstance(getBaseContext());
        mDbHelper.openConnection();

        mExtras = getIntent().getExtras();


        extraListId = mExtras.getLong(ListsDatabaseAdapter.ITEM_KEY_ROWID);
        displayProductsList(extraListId);

//        getActionBar().setTitle(mDbHelper.getListNameById(extraListId));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(mDslv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogView(0, "", false, extraListId);
            }
        });


    }



    private void displayProductsList(final long listId) {
        // The desired columns to be bound
        String[] columns = new String[] {ListsDatabaseAdapter.PROD_NAME};

        // the XML defined views which the data will be bound to
        int[] ids = new int[] {R.id.item_name};

        // pull all items from database
        Cursor cursor = mDbHelper.getAllProdRecordsByList(listId);

        mMAdapter = new MAdapter(this, R.layout.product_items, null, columns, ids,
                0);

        mDslv = (DragSortListView) findViewById(R.id.item_list);

        // set dslv profile for faster scroll speeds
        mDslv.setDragScrollProfile(ssProfile);

        mDslv.setAdapter(mMAdapter);
        mMAdapter.changeCursor(cursor);



        mDslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                mMAdapter.persistChanges();
                displayProductsList(listId);
                // Get the cursor, positioned to the corresponding row in the
                // result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                long prodId = cursor.getLong(cursor
                        .getColumnIndex(ListsDatabaseAdapter.PROD_KEY_ROWID));

                String listName = cursor.getString(cursor
                        .getColumnIndex(ListsDatabaseAdapter.PROD_NAME));
                if(mDbHelper.getProdCompleteById(prodId)== 1)
                    mDbHelper.updateProductRecord(prodId, listName, 0);
                else mDbHelper.updateProductRecord(prodId, listName, 1);

//                mMAdapter.persistChanges();
//                displayProductsList(listId);
                // Get the item name and details from this row in the database.
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
                displayProductsList(listId);
                // Get the cursor, positioned to the corresponding row in the
                // result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the item name and details from this row in the database.
                long prodId = cursor.getLong(cursor
                        .getColumnIndex(ListsDatabaseAdapter.PROD_KEY_ROWID));
                String listName = cursor.getString(cursor
                        .getColumnIndex(ListsDatabaseAdapter.PROD_NAME));
                showDialogView(prodId, listName, true, extraListId);
                return true;
            }
        });
    }

    private void showDialogView(final long prodId, String name, final boolean edit, final long listId) {
        fab.hide();
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.prodName)
                .customView(R.layout.dialog_products, true)
                .positiveText(R.string.ok)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        int complete=0;
                        if(inputBox.isChecked())complete=1;
                        else complete =0;
                        if (edit) {
                            mDbHelper.updateProductRecord(prodId,
                                    inputName.getText().toString(), complete);
                        } else {
                            mDbHelper.insertProductRecord(
                                    inputName.getText().toString(), listId, complete);
                        }
                        mMAdapter.persistChanges();
                        displayProductsList(extraListId);
                        fab.show();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        fab.show();
                    }
                }).build();

        dialog.setOnDismissListener(new MaterialDialog.OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialog) {
                fab.show();
            }
        });

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        inputName = (EditText) dialog.getCustomView().findViewById(R.id.name);
        inputName.setText(name);

        inputBox = (CheckBox) dialog.getCustomView().findViewById(R.id.boxComplete);
        if (edit) {
            if (mDbHelper.getProdCompleteById(prodId) == 1)
                inputBox.setChecked(true);
            else inputBox.setChecked(false);
        }
        else {
            inputBox.setChecked(false);
        }

        dialog.show(); // disabled by default
    }

    private class MAdapter extends SimpleDragSortCursorAdapter {

        public void persistChanges() {
            Cursor c = getCursor();
            c.moveToPosition(-1);
            while (c.moveToNext()) {
                int listPos = getListPosition(c.getPosition());
                if (listPos == REMOVED) {
                    mDbHelper
                            .deleteProductRecord(c.getInt(c.getColumnIndex("_id")));
                } else if (listPos != c.getPosition()) {
                    mDbHelper.updateProductPosition(
                            c.getInt(c.getColumnIndex("_id")), listPos);
                }
            }
        }

        public MAdapter(Context ctxt, int rmid, Cursor c, String[] cols,
                        int[] ids, int something) {
            super(ctxt, rmid, c, cols, ids, something);
            mContext = ctxt;
        }
//        public MAdapter(Context ctxt, Cursor c, int flags) {
//            super(ctxt, flags, c);
////            mContext = ctxt;
//        }

//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            ViewHolder holder = null;
//            Log.v("ConvertView", String.valueOf(position));
//
//            Cursor cursor = (Cursor) listView.getItemAtPosition(position);
//
//            // Get the item name and details from this row in the database.
//            long prodId = cursor.getLong(cursor
//                    .getColumnIndex(ListsDatabaseAdapter.PROD_KEY_ROWID));
//
//            if (convertView == null) {
//                LayoutInflater vi = (LayoutInflater) getSystemService(
//                        Context.LAYOUT_INFLATER_SERVICE);
//                convertView = vi.inflate(R.layout.product_items, null);
//
//                holder = new ViewHolder();
//                holder.prodName = (TextView) convertView.findViewById(R.id.item_name);
//                holder.prodPos = (TextView) convertView.findViewById(R.id.item_position_list);
//                holder.completeBox = (CheckBox) convertView.findViewById(R.id.completeBox);
//                convertView.setTag(holder);
//
//
//                if (mDbHelper.getProdCompleteById(mDbHelper.getProdIdByPos(position, extraListId)) == 1) {
//                    System.out.println(mDbHelper.getProdCompleteById(mDbHelper.getProdIdByPos(position, extraListId)));
//                    holder.completeBox.setChecked(true);
//                }
//                else {
//                    System.out.println(mDbHelper.getProdCompleteById(mDbHelper.getProdIdByPos(position, extraListId)));
//                    holder.completeBox.setChecked(false);
//                }
//
//                holder.completeBox.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        Log.d("нажалась хуйня", "вот такая хуйня");
//
////                        if(mDbHelper.getProdCompleteById(prodId)== 1)
////                            mDbHelper.updateProductRecord(prodId, listName, 0);
////                        else mDbHelper.updateProductRecord(prodId, listName, 1);
//
//                        if (mDbHelper.getProdCompleteById(mDbHelper.getProdIdByPos(position, extraListId)) == 1) {
//                            ((CheckBox) v).setChecked(false);
//                            mDbHelper.updateProductRecord(mDbHelper.getProdIdByPos(position, extraListId),
//                                    mDbHelper.getProdNameById(mDbHelper.getProdIdByPos(position, extraListId)), 0);
//                        } else {
//                            ((CheckBox) v).setChecked(true);
//                            mDbHelper.updateProductRecord(mDbHelper.getProdIdByPos(position, extraListId),
//                                    mDbHelper.getProdNameById(mDbHelper.getProdIdByPos(position, extraListId)), 1);
//                        }
//                    }
//                });
//
////                holder.prodName.setText(mDbHelper.getProdNameById(mDbHelper.getProdIdByPos(position, extraListId)));
////                holder.prodPos.setText(mDbHelper.getProdCompleteById(mDbHelper.getProdIdByPos(position, extraListId)));
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            Cursor cursor = (Cursor) mDslv.getItemAtPosition(position);
            final long listId = cursor.getLong(cursor
                    .getColumnIndex(ListsDatabaseAdapter.PROD_KEY_ROWID));

            boolean complete;
            final CheckBox cb = (CheckBox) v.findViewById(R.id.completeBox);
            TextView tv = (TextView) v.findViewById(R.id.item_name);
            final String listName = cursor.getString(cursor
                    .getColumnIndex(ListsDatabaseAdapter.PROD_NAME));

            if(mDbHelper.getProdCompleteById(listId)==1){
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tv.setTextColor(getResources().getColor(R.color.material_drawer_secondary_text));
                cb.setChecked(true);
            } else {

                tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tv.setTextColor(getResources().getColor(R.color.material_drawer_primary_text));
                cb.setChecked(false);
            }

            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProductsActivity.this,"Item bought " + mDbHelper.getProdCompleteById(listId), Toast.LENGTH_SHORT).show();
                    Log.d("SMTH", "CLICKED");
                    if(mDbHelper.getProdCompleteById(listId)==1){
                        mDbHelper.updateProductRecord(listId, listName, 0);
                    } else {
                        mDbHelper.updateProductRecord(listId, listName, 1);
                    }
                }
            });
            return v;
        }

//        @Override
//        public View newView(Context context, Cursor cursor, ViewGroup parent) {
//            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            return li.inflate(R.layout.product_items, parent, false);
//        }
//
//        @Override
//        public void bindView(View view, Context context, Cursor cursor) {
//            CheckBox itemComplete = (CheckBox) view.findViewById(R.id.checkbox);
//            TextView prodName = (TextView) view.findViewById(R.id.item_name);
//            TextView prodPos = (TextView) view.findViewById(R.id.item_position_list);
//            long prodId = cursor.getLong(cursor
//                    .getColumnIndex(ListsDatabaseAdapter.PROD_KEY_ROWID));
//            long prodPosition = cursor.getLong(cursor.getColumnIndex(ListsDatabaseAdapter.PROD_POSITION));
//            prodName.setText(mDbHelper.getProdNameById(prodId));
//            prodPos.setText(""+prodPosition);
////            itemComplete.setChecked(true);
//        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.activity_cursor, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, ListsActivity.class));
                return (true);
            case R.id.add:
                showDialogView(0, "", false, extraListId);
                break;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Cursor cursor = mDbHelper.getAllProdRecordsByList(extraListId);
        mMAdapter.changeCursor(cursor);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("CURSOR", "OnPause()");
        mMAdapter.persistChanges();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("CURSOR", "OnDestroy()");
        mMAdapter.persistChanges();
    }

}

