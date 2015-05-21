/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package pl.pwr.mipo.mipoo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.HashMap;

public class ListsDatabaseAdapter extends SQLiteOpenHelper {

	private static ListsDatabaseAdapter sSingleton;
	private SQLiteDatabase mDb;

	private static final String DATABASE_NAME = "lists";
	private static final int SCHEMA_VERSION = 5;

	public static final String ITEM_KEY_ROWID = "_id";
	public static final String ITEM_TABLE = "list_table";
	public static final String ITEM_NAME = "list_name";
	public static final String ITEM_POSITION = "list_position";

    public static final String PROD_KEY_ROWID = "_id";
    public static final String PROD_TABLE = "product_table";
    public static final String PROD_NAME = "product_name";
    public static final String PROD_POSITION = "product_position";
    public static final String PROD_COMPLETE = "product_complete";
    public static final String PROD_LIST_ID = "list_id";

    // Login table name
    private static final String TABLE_LOGIN = "login";
    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";

	private final String sort_order = "ASC"; // ASC or DESC

	// String to create database table
	private static final String DATABASE_CREATE_LISTS =
            "CREATE TABLE " + ITEM_TABLE + " (" +
                    ITEM_KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ITEM_NAME +" TEXT, " +
                    ITEM_POSITION +" INTEGER);";

    private static final String DATABASE_CREATE_PRODUCTS =
            "CREATE TABLE " + PROD_TABLE+ " (" +
                    PROD_KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PROD_NAME +" TEXT, " +
                    PROD_POSITION +" INTEGER, " +
                    PROD_COMPLETE +" INTEGER, " +
                    PROD_LIST_ID + " INTEGER NOT NULL REFERENCES " + ITEM_TABLE + "(" + ITEM_KEY_ROWID + "));";

    private static final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT" + ")";

	// Methods to setup database singleton and connections
	synchronized public static ListsDatabaseAdapter getInstance(Context ctxt) {
		if (sSingleton == null) {
			sSingleton = new ListsDatabaseAdapter(ctxt);
		}
		return sSingleton;
	}

	public ListsDatabaseAdapter(Context ctxt) {
		super(ctxt, DATABASE_NAME, null, SCHEMA_VERSION);
		//sSingleton = this;
	}

	public ListsDatabaseAdapter openConnection() throws SQLException {
		if (mDb == null) {
			mDb = sSingleton.getWritableDatabase();
		}
		return this;
	}

	public synchronized void closeConnection() {
		if (sSingleton != null) {
			sSingleton.close();
			mDb.close();
			sSingleton = null;
			mDb = null;
		}
	}

	// initial database load with dummy records
	
	@Override
	public void onCreate(SQLiteDatabase mDb) {
		try {
			mDb.beginTransaction();

			mDb.execSQL(DATABASE_CREATE_LISTS);
            mDb.execSQL(DATABASE_CREATE_PRODUCTS);
            mDb.execSQL(CREATE_LOGIN_TABLE);

			mDb.setTransactionSuccessful();

		} finally {
			mDb.endTransaction();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase mDb, int oldVersion, int newVersion) {
		// DON'T DO IT ON PRODUCTION VERSION
		mDb.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
        mDb.execSQL("DROP TABLE IF EXISTS " + PROD_TABLE);
        mDb.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
		onCreate(mDb);
	}

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String uid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email

        // Inserting Row
        long id = db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection

//        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
        }
        cursor.close();
        db.close();
        // return user
//        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Getting user login status return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();

//        Log.d(TAG, "Deleted all user info from sqlite");
    }

	// Methods for Items

	public Cursor getAllListRecords() {
		return mDb.query(ITEM_TABLE, new String[] {ITEM_KEY_ROWID, ITEM_NAME, ITEM_POSITION }, null, null, null, null,
				ITEM_POSITION + " " + sort_order);
	}

    public Cursor getAllProdRecordsByList(long list_id) {
        return mDb.query(PROD_TABLE, new String[] {PROD_KEY_ROWID, PROD_NAME, PROD_POSITION, PROD_COMPLETE, PROD_LIST_ID },
                PROD_LIST_ID + "=" + list_id, null, null, null,
                PROD_POSITION + " " + sort_order);
    }

	public Cursor getListRecord(long rowId) throws SQLException {
        Cursor mLetterCursor = mDb.query(true, ITEM_TABLE, new String[] {
                        ITEM_KEY_ROWID, ITEM_NAME, ITEM_POSITION },
                ITEM_KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mLetterCursor != null) {
            mLetterCursor.moveToFirst();
        }
        return mLetterCursor;
    }

    public Cursor getProdRecord(long rowId, long list_id) throws SQLException {
        Cursor mLetterCursor = mDb.query(true, PROD_TABLE, new String[] {
                        PROD_KEY_ROWID, PROD_NAME, PROD_POSITION, PROD_COMPLETE, PROD_LIST_ID },
                PROD_KEY_ROWID + "=" + rowId + " AND " + PROD_LIST_ID + "=" + list_id, null, null, null, null, null);
        if (mLetterCursor != null) {
            mLetterCursor.moveToFirst();
        }
        return mLetterCursor;
    }

	public long insertListRecord(String item_name) {

        int item_Position = getMaxColumnData();
		ContentValues initialItemValues = new ContentValues();
		initialItemValues.put(ITEM_NAME, item_name);
		initialItemValues.put(ITEM_POSITION, (item_Position + 1));

		return mDb.insert(ITEM_TABLE, null, initialItemValues);
	}

    public long insertProductRecord(String item_name, long list_id, int complete) {
        int item_Position = getMaxProductPosition(list_id);
        ContentValues initialItemValues = new ContentValues();
        initialItemValues.put(PROD_NAME, item_name);
        initialItemValues.put(PROD_COMPLETE, complete);
        initialItemValues.put(PROD_LIST_ID, list_id);
        initialItemValues.put(PROD_POSITION, (item_Position + 1));

        return mDb.insert(PROD_TABLE, null, initialItemValues);
    }

	public boolean deleteListRecord(long rowId) {
        mDb.delete(PROD_TABLE, PROD_LIST_ID + " = " + rowId, null);
		return mDb.delete(ITEM_TABLE, ITEM_KEY_ROWID + "=" + rowId, null) > 0;
	}

    public boolean deleteProductRecord(long rowId) {
        return mDb.delete(PROD_TABLE, PROD_KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean updateListRecord(long rowId, String item_name) {
		ContentValues ItemArgs = new ContentValues();
		ItemArgs.put(ITEM_NAME, item_name);
		return mDb.update(ITEM_TABLE, ItemArgs, ITEM_KEY_ROWID + "=" + rowId,
				null) > 0;
	}

    public boolean updateProductRecord(long rowId, String item_name, int complete) {
        ContentValues ItemArgs = new ContentValues();
        ItemArgs.put(PROD_NAME, item_name);
        ItemArgs.put(PROD_COMPLETE, complete);
        return mDb.update(PROD_TABLE, ItemArgs, PROD_KEY_ROWID + "=" + rowId,
                null) > 0;
    }

	public boolean updateListPosition(long rowId, Integer position) {
		ContentValues ItemArgs = new ContentValues();
		ItemArgs.put(ITEM_POSITION, position);
		return mDb.update(ITEM_TABLE, ItemArgs, ITEM_KEY_ROWID + "=" + rowId,
				null) > 0;
	}

    public boolean updateProductPosition(long rowId, Integer position) {
        ContentValues ItemArgs = new ContentValues();
        ItemArgs.put(PROD_POSITION, position);
        return mDb.update(PROD_TABLE, ItemArgs, PROD_KEY_ROWID + "=" + rowId,
                null) > 0;
    }

	public boolean deleteAllLists() {

		int doneDelete = 0;
        mDb.delete(PROD_TABLE, null, null);
		doneDelete = mDb.delete(ITEM_TABLE, null, null);
		Log.w("Lists Database Deleted", Integer.toString(doneDelete)
				+ ITEM_TABLE + " removed from database.");
		return doneDelete > 0;
	}

//	public void addDummyRecords(int numRecords) {
//		ContentValues cv_items = new ContentValues();
//		int mStartPosition = getMaxColumnData();
//		int mNewPosition;
//		if (mStartPosition == 0) {
//			mNewPosition = 0;
//		} else {
//			mNewPosition = mStartPosition + 1;
//		}
//
//		for (int i = 0; i < numRecords; i++) {
//			String itemName = "Note " + (mNewPosition + 1);
//			String itemDetails = "Note " + (mNewPosition + 1)
//					+ " stuff to do...";
//
//			cv_items.put(ITEM_NAME, itemName);
//			cv_items.put(ITEM_POSITION, mNewPosition);
//			mDb.insert(ITEM_TABLE, ITEM_NAME, cv_items);
//
//			mNewPosition++;
//		}
//	}

	public int getMaxColumnData() {

		final SQLiteStatement stmt = mDb
				.compileStatement("SELECT MAX("+ITEM_POSITION+") FROM "+ITEM_TABLE);

		return (int) stmt.simpleQueryForLong();
	}

    public int getMaxProductPosition(long list_id) {

        final SQLiteStatement stmt = mDb
                .compileStatement("SELECT MAX("+PROD_POSITION+") FROM "+PROD_TABLE + " WHERE " + PROD_LIST_ID + " = " + list_id);

        return (int) stmt.simpleQueryForLong();
    }

    public String getListNameById(long list_id) {

        final SQLiteStatement stmt = mDb
                .compileStatement("SELECT "+ITEM_NAME+" FROM "+ITEM_TABLE + " WHERE " + ITEM_KEY_ROWID + " = " +list_id);

        return (String) stmt.simpleQueryForString();
    }

    public long getListIdByPos(long list_pos) {

        final SQLiteStatement stmt = mDb
                .compileStatement("SELECT " + ITEM_KEY_ROWID + " FROM " + ITEM_TABLE + " WHERE " + ITEM_POSITION + " = " + list_pos);

        return (long) stmt.simpleQueryForLong();
    }

    public int getProdCompleteById(long prod_id) {

        final SQLiteStatement stmt = mDb
                .compileStatement("SELECT "+ PROD_COMPLETE+" FROM "+PROD_TABLE + " WHERE " + PROD_KEY_ROWID + " = " +prod_id);
        return (int) stmt.simpleQueryForLong();
    }

    public long getProdIdByPos(long prod_pos, long list_id) {

        final SQLiteStatement stmt = mDb
                .compileStatement("SELECT " + PROD_KEY_ROWID + " FROM " + PROD_TABLE + " WHERE " + PROD_POSITION + " = " + prod_pos + " AND " + PROD_LIST_ID + " = " + list_id);

        return (long) stmt.simpleQueryForLong();
    }

    public String getProdNameById(long prod_id) {

        final SQLiteStatement stmt = mDb
                .compileStatement("SELECT " + PROD_NAME + " FROM " + PROD_TABLE + " WHERE " + PROD_KEY_ROWID + " = " + prod_id);

        return (String) stmt.simpleQueryForString();
    }

    public int getProdPosById(long prod_id) {

        final SQLiteStatement stmt = mDb
                .compileStatement("SELECT " + PROD_POSITION + " FROM " + PROD_TABLE + " WHERE " + PROD_KEY_ROWID + " = " + prod_id);

        return (int) stmt.simpleQueryForLong();
    }

//	public void createInitialItemsDatabase() {
//
//		ContentValues cv_items = new ContentValues();
//
//		cv_items.put(ITEM_NAME, "Note to self");
//		cv_items.put(ITEM_DETAILS,
//				"Learn how to create a cool drag and drop list.");
//		cv_items.put(ITEM_POSITION, 0);
//		/*
//		 * The second argument of the insert statement is for the �null column
//		 * hack� when the ContentValues instance is empty � the column named as
//		 * the �null column hack� will be explicitly assigned the value NULL in
//		 * the SQL INSERT statement generated by insert(). This is required due
//		 * to a quirk in SQLite�s support for the SQL INSERT statement.
//		 * -CommonsWare
//		 */
//		mDb.insert(ITEM_TABLE, ITEM_NAME, cv_items);
//
//		cv_items.put(ITEM_NAME, "Buy groceries");
//		cv_items.put(
//				ITEM_DETAILS,
//				"Need: Ice cream, candy, soda, chips, salsa and more ice cream...oh, and don't forget the cake and cookies to go with the ice cream.");
//		cv_items.put(ITEM_POSITION, 1);
//		mDb.insert(ITEM_TABLE, ITEM_NAME, cv_items);
//
//		cv_items.put(ITEM_NAME, "Note 3");
//		cv_items.put(ITEM_DETAILS, "Note 3");
//		cv_items.put(ITEM_POSITION, 2);
//		mDb.insert(ITEM_TABLE, ITEM_NAME, cv_items);
//
//		cv_items.put(ITEM_NAME, "Note 4");
//		cv_items.put(ITEM_DETAILS, "Note 4");
//		cv_items.put(ITEM_POSITION, 3);
//		mDb.insert(ITEM_TABLE, ITEM_NAME, cv_items);
//
//		cv_items.put(ITEM_NAME, "Note 5");
//		cv_items.put(ITEM_DETAILS, "Note 5");
//		cv_items.put(ITEM_POSITION, 4);
//		mDb.insert(ITEM_TABLE, ITEM_NAME, cv_items);
//
//		cv_items.put(ITEM_NAME, "Note 6");
//		cv_items.put(ITEM_DETAILS, "Note 6");
//		cv_items.put(ITEM_POSITION, 5);
//		mDb.insert(ITEM_TABLE, ITEM_NAME, cv_items);
//
//		cv_items.put(ITEM_NAME, "Note 7");
//		cv_items.put(ITEM_DETAILS, "Note 7");
//		cv_items.put(ITEM_POSITION, 6);
//		mDb.insert(ITEM_TABLE, ITEM_NAME, cv_items);
//
//		cv_items.put(ITEM_NAME, "Note 8");
//		cv_items.put(ITEM_DETAILS, "Note 8");
//		cv_items.put(ITEM_POSITION, 7);
//		mDb.insert(ITEM_TABLE, ITEM_NAME, cv_items);
//
//		cv_items.put(ITEM_NAME, "Note 9");
//		cv_items.put(ITEM_DETAILS, "Note 9");
//		cv_items.put(ITEM_POSITION, 8);
//		mDb.insert(ITEM_TABLE, ITEM_NAME, cv_items);
//
//		cv_items.put(ITEM_NAME, "Note 10");
//		cv_items.put(ITEM_DETAILS, "Note 10");
//		cv_items.put(ITEM_POSITION, 9);
//		mDb.insert(ITEM_TABLE, ITEM_NAME, cv_items);
//	}
}