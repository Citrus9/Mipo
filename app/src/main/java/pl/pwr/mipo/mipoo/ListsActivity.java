/*
 * This file is based on the CursorDSLV file that can be found
 * in the DragSortListView demo, available on github:
 *  
 *  https://github.com/bauerca/drag-sort-listview
 *  
 *  Special thanks to all those that have done so much to put
 *  DSLV together.
 */

package pl.pwr.mipo.mipoo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;

import pl.pwr.mipo.mipoo.db.ListsDatabaseAdapter;

public class ListsActivity extends ActionBarActivity {

	private MAdapter mMAdapter;
	private ListsDatabaseAdapter mDbHelper;
	private DragSortListView mDslv;
    private EditText input;
    private View positiveAction;
    private FloatingActionButton fab;
    private static final int PROFILE_SETTING = 1;
    private Toolbar mToolbar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lists);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(false);

//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mDbHelper = ListsDatabaseAdapter.getInstance(getBaseContext());
		mDbHelper.openConnection();

		displayItemList();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(mDslv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogView(0, "", false);
            }
        });
        fab.show();
	}

	private void displayItemList() {
		// The desired columns to be bound
		String[] columns = new String[] { ListsDatabaseAdapter.ITEM_NAME,
				ListsDatabaseAdapter.ITEM_POSITION };

		// the XML defined views which the data will be bound to
		int[] ids = new int[] { R.id.item_name, R.id.item_position_list };

		// pull all items from database
		Cursor cursor = mDbHelper.getAllListRecords();

		mMAdapter = new MAdapter(this, R.layout.list_items, null, columns, ids,
				0);

		mDslv = (DragSortListView) findViewById(R.id.item_list);

		// set dslv profile for faster scroll speeds
		mDslv.setDragScrollProfile(ssProfile);

		mDslv.setAdapter(mMAdapter);
		mMAdapter.changeCursor(cursor);

		mDslv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {
                mMAdapter.persistChanges();
                displayItemList();
				// Get the cursor, positioned to the corresponding row in the
				// result set
				Cursor cursor = (Cursor) listView.getItemAtPosition(position);

				// Get the item name and details from this row in the database.
                long listId = cursor.getLong(cursor
                        .getColumnIndex(ListsDatabaseAdapter.ITEM_KEY_ROWID));
                launchProductsActivity(listId);
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
                displayItemList();
				// Get the cursor, positioned to the corresponding row in the
				// result set
				Cursor cursor = (Cursor) listView.getItemAtPosition(position);

				// Get the item name and details from this row in the database.
                long listId = cursor.getLong(cursor
                        .getColumnIndex(ListsDatabaseAdapter.ITEM_KEY_ROWID));
                String listName = cursor.getString(cursor
                        .getColumnIndex(ListsDatabaseAdapter.ITEM_NAME));
                showDialogView(listId, listName, true);
				return true;
			}
		});
	}

    private void showDialogView(final long listId, String name, final boolean edit) {
        fab.hide();
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.listName)
                .customView(R.layout.dialog_lists, true)
                .positiveText(R.string.ok)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (edit) {

                            mDbHelper.updateListRecord(listId,
                                    input.getText().toString(), System.currentTimeMillis()+"");
                        } else {
                            mDbHelper.insertListRecord(
                                    input.getText().toString(), System.currentTimeMillis()+"");
                        }
                        fab.show();
                        mMAdapter.persistChanges();
                        displayItemList();
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
        input = (EditText) dialog.getCustomView().findViewById(R.id.name);
        input.setText(name);

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

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    private void launchProductsActivity(Long id) {
        Intent i = new Intent(this, ProductsActivity.class);
        Log.d("putExtra", "ID: " + id);
//        i.putExtra(ItemAddEditActivity.EDIT_ITEM, "edit");
        i.putExtra(ListsDatabaseAdapter.ITEM_KEY_ROWID, id);
        startActivity(i);
//        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_in);
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
			c.moveToPosition(-1);
			while (c.moveToNext()) {
				int listPos = getListPosition(c.getPosition());
				if (listPos == REMOVED) {
					mDbHelper
							.deleteListRecord(c.getInt(c.getColumnIndex("_id")));

				} else if (listPos != c.getPosition()) {
					mDbHelper.updateListPosition(
                            c.getInt(c.getColumnIndex("_id")), listPos);

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
			displayItemList();
			return (true);
		case R.id.save:
			mMAdapter.persistChanges();
			displayItemList();
			return (true);
		case R.id.add:
            showDialogView(0, "", false);
			break;
		}
		return (super.onOptionsItemSelected(item));
	}

	@Override
	protected void onResume() {
		super.onResume();

        fab.show();
		Cursor cursor = mDbHelper.getAllListRecords();
		mMAdapter.changeCursor(cursor);
	}

    @Override
    protected void onPause(){
        super.onPause();
        fab.hide();
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
