package com.icanhasnom.fliplist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHandler extends SQLiteOpenHelper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int DATABASE_VERSION = 72;
	
	// Database Name
	private static final String DATABASE_NAME = "fliplist";

    String currentDBPath = "/data/"+ "com.icanhasnom.fliplist" +"/databases/" + DATABASE_NAME;
    String backupDBPath = "/FlipList/" + DATABASE_NAME;
    
    String importDatabaseFileName;
    Handler handler;
	
	// Flists Table
	private static final String TABLE_FLISTS = "flists";
	// Flists Table Column Names
	private static final String KEY_FLIST_ID = "id";
	private static final String KEY_FLIST_NAME = "name";
	private static final String KEY_FLIST_DESC = "desc";
	private static final String KEY_FLIST_TYPE = "type";
	private static final String KEY_FLIST_VISIBLE = "visible";
	private static final String KEY_FLIST_FILTER = "filter";
	
	// Categories Table
	private static final String TABLE_CATEGORIES = "categories";
	// Categories Table Column Names
	private static final String KEY_CAT_ID = "id";
	private static final String KEY_CAT_NAME = "name";
	private static final String KEY_CAT_DESC = "desc";
	private static final String KEY_CAT_VISIBLE = "visible";
	
	// Filters Table
	private static final String TABLE_FILTERS = "filters";
	// Filters Table Column Names
	private static final String KEY_FILTER_ID = "id";
	private static final String KEY_FILTER_NAME = "name";
	private static final String KEY_FILTER_DESC = "desc";
	private static final String KEY_FILTER_QUERY = "query";
	private static final String KEY_FILTER_VALUES = "query_values";
	private static final String KEY_FILTER_VALUE_ARGS = "query_value_args";
	private static final String KEY_FILTER_GROUP_BY = "group_by";
	private static final String KEY_FILTER_ORDER_BY = "order_by";
	private static final String KEY_FILTER_HIDDEN = "hidden";
	private static final String KEY_FILTER_BY_CATEGORY = "filter_by_cat";
	
	// Items Table
	private static final String TABLE_ITEMS = "items";
	// Items Table Column Names
	private static final String KEY_ITEM_ID = "id";
	private static final String KEY_ITEM_NAME = "name";
	private static final String KEY_ITEM_DESC = "desc";
	private static final String KEY_ITEM_NOTES = "notes";
	private static final String KEY_ITEM_FLIST = "flist";
	private static final String KEY_ITEM_CATEGORIES = "categories";
	private static final String KEY_ITEM_HAS_DUE_DATE = "has_due_date";
	private static final String KEY_ITEM_HAS_DUE_TIME = "has_due_time";
	private static final String KEY_ITEM_DUE_DATETIME = "due_datetime";
	private static final String KEY_ITEM_CREATE_DATE = "create_date";
	private static final String KEY_ITEM_IS_COMPLETED = "is_completed";
	private static final String KEY_ITEM_COMPLETED_DATE = "completed_date";
	
	// Types Table
	private static final String TABLE_ITEM_TYPES = "types";
	// Types Table Column Names
	private static final String KEY_TYPE_ID = "id";
	private static final String KEY_TYPE_NAME = "name";
	private static final String KEY_TYPE_DESC = "desc";
	
	private SharedPreferences mySharedPreferences = null;
	private String prefRemoveCompletedItemsDelay;
	private String prefRemoveCompletedItems;
	
	String prefQueryStringValues = null;
	String prefQueryStringValueArgs = null;
	
	public transient Context context;
	
	public DatabaseHandler(Context c) {
		super(c, DATABASE_NAME, null, DATABASE_VERSION);
		context = c;
		//Log.v("DatabaseHandler.constructor", "5) context: " + context);
	}
	
	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_FLISTS_TABLE = "CREATE TABLE " + TABLE_FLISTS + "(" 
				+ KEY_FLIST_ID + " INTEGER PRIMARY KEY," + KEY_FLIST_NAME + " TEXT," 
				+ KEY_FLIST_DESC + " TEXT," + KEY_FLIST_TYPE + " TEXT," + KEY_FLIST_VISIBLE
				+ " INTEGER," + KEY_FLIST_FILTER + " INTEGER" + ")";
		String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "(" 
				+ KEY_CAT_ID + " INTEGER PRIMARY KEY," + KEY_CAT_NAME + " TEXT," 
				+ KEY_CAT_DESC + " TEXT," + KEY_CAT_VISIBLE
				+ " INTEGER" + ")";
		String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
				+ KEY_ITEM_ID + " INTEGER PRIMARY KEY," 
				+ KEY_ITEM_NAME + " TEXT,"
				+ KEY_ITEM_DESC + " TEXT," 
				+ KEY_ITEM_NOTES + " TEXT,"
				+ KEY_ITEM_FLIST + " TEXT," 
				+ KEY_ITEM_CATEGORIES + " TEXT," 
				+ KEY_ITEM_HAS_DUE_DATE + " TEXT,"
				+ KEY_ITEM_HAS_DUE_TIME + " TEXT,"
				+ KEY_ITEM_DUE_DATETIME + " TEXT," 
				+ KEY_ITEM_CREATE_DATE + " TEXT,"
				+ KEY_ITEM_IS_COMPLETED + " INTEGER,"
				+ KEY_ITEM_COMPLETED_DATE + " TEXT" + ")";
		String CREATE_TYPES_TABLE = "CREATE TABLE " + TABLE_ITEM_TYPES + "("
				+ KEY_TYPE_ID + " INTEGER PRIMARY KEY," 
				+ KEY_TYPE_NAME + " TEXT,"
				+ KEY_TYPE_DESC + " TEXT" + ")";
		String CREATE_FILTERS_TABLE = "CREATE TABLE " + TABLE_FILTERS + "("
				+ KEY_FILTER_ID + " INTEGER PRIMARY KEY," 
				+ KEY_FILTER_NAME + " TEXT,"
				+ KEY_FILTER_DESC + " TEXT," 
				+ KEY_FILTER_QUERY + " TEXT,"
				+ KEY_FILTER_VALUES + " TEXT," 
				+ KEY_FILTER_VALUE_ARGS + " TEXT,"
				+ KEY_FILTER_GROUP_BY + " TEXT,"
				+ KEY_FILTER_ORDER_BY + " TEXT,"
				+ KEY_FILTER_HIDDEN + " INTEGER,"
				+ KEY_FILTER_BY_CATEGORY + " INTEGER"+ ")";
		
		//String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
		//		+ KEY_SETTING_ID + " INTEGER PRIMARY KEY," + KEY_SETTING_NAME + " TEXT,"
		//		+ KEY_SETTING_VAL1 + " TEXT," + KEY_SETTING_VAL2 + " TEXT" + ")";
		
		// TODO: Make these undeletable? Atleast the completed category.
		//       Undeletable setting mande, make delete method check undeletable before deleting category
		//        and notify user in a tost if category cannot be deleted.
		String CREATE_UNCATEGORIZED_CATEGORY = "insert into " + TABLE_CATEGORIES + "(" + KEY_CAT_ID + "," + KEY_CAT_NAME + ","
                + KEY_CAT_DESC + "," + KEY_CAT_VISIBLE + ") values(0, 'Uncategorized', 'Uncategorized', '1')";
		String CREATE_ARCHIVE_CATEGORY = "insert into " + TABLE_CATEGORIES + "(" + KEY_CAT_ID + "," + KEY_CAT_NAME + ","
                + KEY_CAT_DESC + "," + KEY_CAT_VISIBLE + ") values(1, 'Archive', 'Archive Category', '0')";
		String CREATE_ALL_FLIST = "insert into " + TABLE_FLISTS + "(" + KEY_FLIST_ID + "," + KEY_FLIST_NAME + ","
                + KEY_FLIST_DESC + "," + KEY_FLIST_TYPE + "," + KEY_FLIST_VISIBLE + "," + KEY_FLIST_FILTER + ") values(0, 'Default List', 'List All Items','0', '1', 0)";
		
		String CREATE_TYPE_GENERIC = "insert into " + TABLE_ITEM_TYPES + "(" + KEY_TYPE_ID + "," + KEY_TYPE_NAME + ","
				+ KEY_TYPE_DESC + ") values(0, 'Generic', 'Generic items')";
		String CREATE_TYPE_GROCERY = "insert into " + TABLE_ITEM_TYPES + "(" + KEY_TYPE_ID + "," + KEY_TYPE_NAME + ","
				+ KEY_TYPE_DESC + ") values(1, 'Grocery List', 'List used for grocery items')";
		String CREATE_TYPE_TODO = "insert into " + TABLE_ITEM_TYPES + "(" + KEY_TYPE_ID + "," + KEY_TYPE_NAME + ","
				+ KEY_TYPE_DESC + ") values(2, 'ToDo', 'Tasks that you need to complete')";
		
		String CREATE_FILTER_NONE = "insert into " + TABLE_FILTERS + "(" 
				+ KEY_FILTER_ID + "," + KEY_FILTER_NAME + "," + KEY_FILTER_DESC + "," + KEY_FILTER_QUERY + "," 
				+ KEY_FILTER_VALUES + "," + KEY_FILTER_VALUE_ARGS + "," + KEY_FILTER_GROUP_BY + "," 
				+ KEY_FILTER_ORDER_BY + "," + KEY_FILTER_HIDDEN + "," + KEY_FILTER_BY_CATEGORY
				+ ") values(0, 'None', 'No Filter', '', '', '', '', '', 0, 0)";

		String FILTER_ALL_QUERY = "SELECT * FROM items";
		String FILTER_ALL_VALUES = null;
		String FILTER_ALL_VALUE_ARGS = null;
		String FILTER_ALL_GROUP_BY = "+" + KEY_ITEM_IS_COMPLETED;
		String FILTER_ALL_ORDER_BY = "+" + KEY_ITEM_CREATE_DATE;
		String CREATE_FILTER_ALL = "insert into " + TABLE_FILTERS + "(" + KEY_FILTER_ID + "," + KEY_FILTER_NAME + ","
				+ KEY_FILTER_DESC + "," + KEY_FILTER_QUERY + "," + KEY_FILTER_VALUES + "," + KEY_FILTER_VALUE_ARGS + "," 
				+ KEY_FILTER_GROUP_BY + ","
				+ KEY_FILTER_ORDER_BY + "," + KEY_FILTER_HIDDEN + "," + KEY_FILTER_BY_CATEGORY
				+ ") values(1, 'All', 'Show All items', \"" + FILTER_ALL_QUERY + "\", null, null, null, null, 0, 0)";

		String FILTER_ACTIVE_QUERY = "SELECT * FROM items";
		String FILTER_ACTIVE_VALUES = "is_completed=?";
		String FILTER_ACTIVE_VALUE_ARGS = "0";
		String FILTER_ACTIVE_GROUP_BY = "+" + KEY_ITEM_IS_COMPLETED;
		String FILTER_ACTIVE_ORDER_BY = "+" + KEY_ITEM_CREATE_DATE;
		String CREATE_FILTER_ACTIVE = "insert into " + TABLE_FILTERS + "(" + KEY_FILTER_ID + "," + KEY_FILTER_NAME + ","
				+ KEY_FILTER_DESC + "," + KEY_FILTER_QUERY + "," + KEY_FILTER_VALUES + "," + KEY_FILTER_VALUE_ARGS + "," 
				+ KEY_FILTER_GROUP_BY + ","
				+ KEY_FILTER_ORDER_BY + "," + KEY_FILTER_HIDDEN + "," + KEY_FILTER_BY_CATEGORY
				+ ") values(2, 'Active', 'Show Active Items', \"" + FILTER_ACTIVE_QUERY + "\",\"" + FILTER_ACTIVE_VALUES + "\", \"" + FILTER_ACTIVE_VALUE_ARGS + "\", null, null, 0, 1)";
		
		String FILTER_NEXTSEVEN_QUERY = "SELECT * FROM items";
		String FILTER_NEXTSEVEN_VALUES = "is_completed=? AND has_due_date=? AND due_datetime <= datetime('now',?)";
		String FILTER_NEXTSEVEN_VALUE_ARGS = "0;1;+7 days";
		String FILTER_NEXTSEVEN_GROUP_BY = "+" + KEY_ITEM_IS_COMPLETED;
		String FILTER_NEXTSEVEN_ORDER_BY = "+" + KEY_ITEM_DUE_DATETIME;
		String CREATE_FILTER_NEXTSEVEN = "insert into " + TABLE_FILTERS + "(" + KEY_FILTER_ID + "," + KEY_FILTER_NAME + ","
				+ KEY_FILTER_DESC + "," + KEY_FILTER_QUERY + "," + KEY_FILTER_VALUES + "," + KEY_FILTER_VALUE_ARGS + "," 
				+ KEY_FILTER_GROUP_BY + ","
				+ KEY_FILTER_ORDER_BY + "," + KEY_FILTER_HIDDEN + "," + KEY_FILTER_BY_CATEGORY
				+ ") values(3, 'Next 7 Days', 'Show items due in next 7 days', \"" + FILTER_NEXTSEVEN_QUERY + "\",\"" + FILTER_NEXTSEVEN_VALUES + "\",\"" + FILTER_NEXTSEVEN_VALUE_ARGS + "\", null, null, 0, 1)";

		String FILTER_ALL_BY_CAT_QUERY = "SELECT * FROM items";
		String FILTER_ALL_BY_CAT_VALUES = null;
		String FILTER_ALL_BY_CAT_VALUE_ARGS = null;
		String FILTER_ALL_BY_CAT_GROUP_BY = "+" + KEY_ITEM_IS_COMPLETED;
		String FILTER_ALL_BY_CAT_ORDER_BY = "+" + KEY_ITEM_CREATE_DATE;
		String CREATE_FILTER_ALL_BY_CAT = "insert into " + TABLE_FILTERS + "(" + KEY_FILTER_ID + "," + KEY_FILTER_NAME + ","
				+ KEY_FILTER_DESC + "," + KEY_FILTER_QUERY + "," + KEY_FILTER_VALUES + "," + KEY_FILTER_VALUE_ARGS + "," 
				+ KEY_FILTER_GROUP_BY + ","
				+ KEY_FILTER_ORDER_BY + "," + KEY_FILTER_HIDDEN + "," + KEY_FILTER_BY_CATEGORY
				+ ") values(4, 'All by Cat', 'Show All items', \"" + FILTER_ALL_BY_CAT_QUERY + "\", null, null, null, null, 0, 1)";

		//CREATE_FILTER_ACTIVE = DatabaseUtils.sqlEscapeString(CREATE_FILTER_ACTIVE);
		
		db.execSQL(CREATE_FLISTS_TABLE);
		db.execSQL(CREATE_CATEGORIES_TABLE);
		db.execSQL(CREATE_ITEMS_TABLE);
		db.execSQL(CREATE_TYPES_TABLE);
		db.execSQL(CREATE_FILTERS_TABLE);
		db.execSQL(CREATE_UNCATEGORIZED_CATEGORY);
		db.execSQL(CREATE_ARCHIVE_CATEGORY);
		db.execSQL(CREATE_ALL_FLIST);
		db.execSQL(CREATE_TYPE_GENERIC);
		db.execSQL(CREATE_TYPE_GROCERY);
		db.execSQL(CREATE_TYPE_TODO);
		db.execSQL(CREATE_FILTER_NONE);
		db.execSQL(CREATE_FILTER_ALL);
		db.execSQL(CREATE_FILTER_ACTIVE);
		db.execSQL(CREATE_FILTER_NEXTSEVEN);
		db.execSQL(CREATE_FILTER_ALL_BY_CAT);
	}
		
	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTables(db);
		onCreate(db);
	}
	public void dropTables(SQLiteDatabase db) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLISTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_TYPES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTERS);
	}
	public void getPreferences() {
		//PreferencesHelper myPrefs= new PreferencesHelper(getApplicationContext());  

		//myPrefs.GetPreferences(R.string.remove_completed_key, R.integer.remove_completed_default);  
		//mySharedPreferences = (PreferenceManager) context.getSharedPreferences();

    	mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        prefRemoveCompletedItems = mySharedPreferences.getString(context.getString(R.string.remove_completed_key), context.getResources().getString(R.integer.remove_completed_default));
        prefRemoveCompletedItemsDelay = mySharedPreferences.getString(context.getString(R.string.remove_completed_delay_key), context.getResources().getString(R.integer.remove_completed_delay_default));
	}
	public void getQueryValuesFromPrefs() {
		// TODO: Move this to ListManager
		getPreferences();

		//String compareDate = null;
    	//Calendar compareDateCal = Calendar.getInstance();
		//Log.v("DatabaseHandler.getQueryValuesFromPrefs()", "prefRemoveCompletedItems: " + prefRemoveCompletedItems);
		//Log.v("DatabaseHandler.getQueryValuesFromPrefs()", "prefRemoveCompletedItemsDelay: " + prefRemoveCompletedItemsDelay);
		// Only show Non-Completed items
		if (prefRemoveCompletedItems.equals("0")) {
			prefQueryStringValues = KEY_ITEM_IS_COMPLETED + "=?";
			prefQueryStringValueArgs = "0";
		}
		// Remove completed items after delay
		if (prefRemoveCompletedItems.equals("1")) {
			Integer delayInt = Integer.valueOf(prefRemoveCompletedItemsDelay);
			//SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
			//Log.v("DatabaseHandler.getQueryValuesFromPrefs()", "prefRemoveCompletedItemsDelay: " + prefRemoveCompletedItemsDelay);
			//Log.v("DatabaseHandler.getQueryValuesFromPrefs()", "delayInt: " + delayInt);
	    	// TODO: make this use the result from prefRemoveCompletedItemsDelay preference as a negative number
	    	//compareDateCal.add(Calendar.MINUTE, -delayInt);
			//compareDate = sdfDateTime.format(compareDateCal.getTime());
			//String dateTimeCompare = "datetime('now','-" + delayInt + " minute')";
	    	prefQueryStringValues = "(" + KEY_ITEM_IS_COMPLETED + "=? OR (" + KEY_ITEM_COMPLETED_DATE + " >= datetime('now',?,'utc') AND " + KEY_ITEM_IS_COMPLETED + " =?))";
	    	prefQueryStringValueArgs = "0;-" + delayInt + " minutes;1";
		}
		// Show all items always
		if (prefRemoveCompletedItems.equals("2")) {
			prefQueryStringValues = "";
		}
	}
	public static int hoursAgo(String datetime) {
		// TODO: Use whats needed in this and remove it
	    Calendar date = Calendar.getInstance();
	    try {
			date.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(datetime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    Calendar now = Calendar.getInstance(); // Get time now
	    long differenceInMillis = now.getTimeInMillis() - date.getTimeInMillis();
	    long differenceInHours = (differenceInMillis) / 1000L / 60L / 60L; // Divide by millis/sec, secs/min, mins/hr
	    return (int)differenceInHours;
	}
	// Items
	public void addItem(Item item) {
		SQLiteDatabase db = this.getWritableDatabase();
		String itemCategories = item.getCategoriesString();
		//Log.v("DatabaseHandler.addItem", "item.getCategoriesString(): " + item.getCategoriesString());
		int hasDueDate = item.hasDueDate()? 1 : 0;
		int hasDueTime = item.hasDueTime()? 1 : 0;
		int isCompleted = item.isCompleted()? 1 : 0;
		
		ContentValues values = new ContentValues();
		values.put(KEY_ITEM_NAME, item.getName());
		values.put(KEY_ITEM_DESC, item.getDescription());
		values.put(KEY_ITEM_NOTES, item.getNotes());
		values.put(KEY_ITEM_FLIST,  item.getFlist());
		values.put(KEY_ITEM_CATEGORIES, itemCategories);
		values.put(KEY_ITEM_HAS_DUE_DATE, hasDueDate);
		values.put(KEY_ITEM_HAS_DUE_TIME, hasDueTime);
		values.put(KEY_ITEM_DUE_DATETIME, item.getDueDateTime());
		values.put(KEY_ITEM_CREATE_DATE, item.getCreateDate());
		values.put(KEY_ITEM_IS_COMPLETED, isCompleted);
		values.put(KEY_ITEM_COMPLETED_DATE,  item.getCompletedDate());
		
		//Log.v("DatabaseHandler.addItem", "Adding Item " + item.getName() + " to the DB");
		//Log.v("DatabaseHandler.addItem", "item.getDueDateTime(): " + item.getDueDateTime());
		//Log.v("DatabaseHandler.addItem", " *item info: " + item.getName() + "," + item.getDescription() + "," + item.getNotes() + "," 
		//+ item.getFlist() + "," + itemCategories + "," + item.getCreateDate() + "," + hasDueDate + "," + hasDueTime
		//+ item.getDueDateTime());
		
		db.insert(TABLE_ITEMS, null, values);
		//db.close();
	}
	public Item getItem(int id) throws NumberFormatException, ParseException {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_ITEMS, new String[] { KEY_ITEM_ID, KEY_ITEM_NAME,
				KEY_ITEM_DESC, KEY_ITEM_NOTES, KEY_ITEM_FLIST, KEY_ITEM_CATEGORIES, 
				KEY_ITEM_HAS_DUE_DATE, KEY_ITEM_HAS_DUE_TIME, KEY_ITEM_DUE_DATETIME, 
				KEY_ITEM_CREATE_DATE, KEY_ITEM_COMPLETED_DATE }, 
				KEY_ITEM_ID + "?", new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		int itemID = cursor.getInt(0);
		String itemName = cursor.getString(1);
		String itemDesc = cursor.getString(2);
		String itemNotes = cursor.getString(3);
		Integer itemPriCat = cursor.getInt(4);
		List<String> itemCategories = new ArrayList<String>(Arrays.asList(cursor.getString(5).split(",")));
		Integer itemHasDueDate = cursor.getInt(6);
		Integer itemHasDueTime = cursor.getInt(7);
		String itemDueDate = cursor.getString(8);
		String itemCreateDate = cursor.getString(9);
		String itemCompletedDate = cursor.getString(10);
		
		Item item = new Item(itemID, itemPriCat, itemCategories, itemName, itemDesc, itemNotes, itemCreateDate, itemDueDate);
		item.setHasDueDate(itemHasDueDate);
		item.setHasDueTime(itemHasDueTime);
		item.setCompletedDate(itemCompletedDate);
		//db.close();
		// return item
		return item;
	}
	public ArrayList<Item> getAllItems() throws NumberFormatException, ParseException {
		ArrayList<Item> itemList = new ArrayList<Item>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_ITEMS;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Item item = new Item();
				item.setID(cursor.getInt(0));
				item.setName(cursor.getString(1));
				item.setDescription(cursor.getString(2));
				item.setNotes(cursor.getString(3));
				item.setFlist(cursor.getInt(4));
				List<String> myCategories = new ArrayList<String>(Arrays.asList(cursor.getString(5).split(",")));
				item.setCategories(myCategories);
				item.setHasDueDate(cursor.getInt(6));
				item.setHasDueTime(cursor.getInt(7));
				item.setDueDateTime(cursor.getString(8));
				item.setCreateDate(cursor.getString(9));
				item.setCompleted(cursor.getInt(10));
				item.setCompletedDate(cursor.getString(11));
				// Adding category to list
				itemList.add(item);
			} while (cursor.moveToNext());
		}
		//db.close();
		return itemList;
	}
	public ItemList getItemList(int flistID) {
		ItemList itemList = new ItemList(flistID);
		SQLiteDatabase db = this.getReadableDatabase();
		
		getQueryValuesFromPrefs();

		//Log.v("DatabaseHandler.getItemList", "prefQueryStringValues: " + prefQueryStringValues);
		//Log.v("DatabaseHandler.getItemList", "prefQueryStringValueArgs: " + prefQueryStringValueArgs);

		String[] dbColumns = null;
		//String dbQuery = "SELECT * FROM ITEMS";
		String dbValues = null;
		String dbValueArgs = null;
		String[] dbValueArgsArray = null;
		String dbGroupBy = null;
		String dbHaving = null;
		String dbOrderBy = KEY_ITEM_IS_COMPLETED;
		
		Flist myFlist = getFlist(flistID);
		int filterID = myFlist.getFilterID();
		Log.v("DatabaseHandler.getItemList", "filterID: " + filterID);
		// Filter ID 0 means use preferences as there is no filter applied
		//filterID = 1;
		Log.v("DatabaseHandler.getItemList", "prefQueryStringValues: " + prefQueryStringValues);
		Log.v("DatabaseHandler.getItemList", "prefQueryStringValueArgs: " + prefQueryStringValueArgs);
		if (filterID == 0) {
			if (!prefQueryStringValues.isEmpty()) {
				// No filter so show all completed items in the selected category
				dbValues = prefQueryStringValues + " AND " + KEY_ITEM_FLIST + "=?";
				dbValueArgs = prefQueryStringValueArgs + ";" + flistID;
			} else {
				// Add filter from preferences
				dbValues = KEY_ITEM_FLIST + "=?";
				dbValueArgs = String.valueOf(flistID);
			}
			Log.v("DatabaseHandler.getItemList", "USING DEFAULTS - dbValues: " + dbValues + " dbValueArgs: " + dbValueArgs);
		} else {
			// Get the selected filter and do not use defaults or preferences
			// TODO: use something like myFilter.getQuery(catID) to build and return the full query
			Filter myFilter = getFilter(filterID);
			//dbQuery = myFilter.getQueryString();
			//Log.v("DatabaseHandler.getItemList", "dbQuery: '" + dbQuery + "'");
			dbValues = myFilter.getValues();
			//Log.v("DatabaseHandler.getItemList", "dbValues: '" + dbValues + "'");
			dbValueArgs = myFilter.getValueArgs();
			//Log.v("DatabaseHandler.getItemList", "dbValueArgs: '" + dbValueArgs + "'");
			dbGroupBy = myFilter.getGroupBy();
			//Log.v("DatabaseHandler.getItemList", "dbGroupBy: '" + dbGroupBy + "'");
			dbOrderBy = myFilter.getOrderBy();
			//Log.v("DatabaseHandler.getItemList", "dbOrderBy: '" + dbOrderBy + "'");
			if (myFilter.filterByCurrentCategory) {
				if (dbValues != null) {
					dbValues = dbValues + " AND " + KEY_ITEM_FLIST + "=?";
					dbValueArgs = dbValueArgs + ";" + flistID;
				} else {
					dbValues = KEY_ITEM_FLIST + "=?";
					dbValueArgs = String.valueOf(flistID);
				}
			}
		}
		
		//Log.v("DatabaseHandler.getItemList", "Category: " + myCat.getName() + " Values: " + dbValues);
		//Log.v("DatabaseHandler.getItemList", "dbValueArgs: " + dbValueArgs);
		if (dbValueArgs != null) dbValueArgsArray = dbValueArgs.split(";");
		//Log.v("DatabaseHandler.getItemList", "DEFAULTS - dbValueArgs: " + dbValueArgs);
		
		Cursor cursor = db.query(TABLE_ITEMS, dbColumns, dbValues, dbValueArgsArray, dbGroupBy, dbHaving, dbOrderBy);
		//Cursor cursor = db.rawQuery(dbQuery , null);
		
		//Cursor cursor = db.query(TABLE_ITEMS, new String[] { KEY_ITEM_ID, KEY_ITEM_NAME, 
		//		KEY_ITEM_DESC, KEY_ITEM_NOTES, KEY_ITEM_PRIMARY_CAT, KEY_ITEM_SECONDARY_CATS, 
		//		KEY_ITEM_HAS_DUE_DATE, KEY_ITEM_HAS_DUE_TIME, KEY_ITEM_DUE_DATETIME, KEY_ITEM_CREATE_DATE, KEY_ITEM_IS_COMPLETED }, 
		//		KEY_ITEM_PRIMARY_CAT + "=?", new String[] { String.valueOf(catID) }, null, null, null, null);
		
		if (cursor.moveToFirst()) {
			do {
				Item item = new Item();
				item.setID(cursor.getInt(0));
				item.setName(cursor.getString(1));
				item.setDescription(cursor.getString(2));
				item.setNotes(cursor.getString(3));
				item.setFlist(cursor.getInt(4));
				// Commented below will create a new object that can be manipulated like a normal List
				// List<String> myCategories = new ArrayList<String>(Arrays.asList(cursor.getString(5).split(",")));
				List<String> myCategories = Arrays.asList(cursor.getString(5).split(","));
				item.setCategories(myCategories);
				item.setHasDueDate(cursor.getInt(6));
				item.setHasDueTime(cursor.getInt(7));
				item.setDueDateTime(cursor.getString(8));
				item.setCreateDate(cursor.getString(9));
				item.setCompleted(cursor.getInt(10));
				item.setCompletedDate(cursor.getString(11));
				itemList.addListItem(item);
				//Log.v("DatabaseHandler.getItemList", "Got Item: " + cursor.getString(1) + " from Category: " + myCat.getName() + " - createDate: " + cursor.getString(9) + " dueDateTime: " + cursor.getString(8) + " isCompleted: " + cursor.getString(10) + " completedDate: " + cursor.getString(11));
			} while (cursor.moveToNext());
		}
		//db.close();
		// return contact list
		return itemList;
	}
	public Filter getFilter(int filterID) {
		Filter myFilter = null;
		Boolean filterByCategory = false;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM 'filters' WHERE id=" + filterID, null);
		if (cursor.moveToFirst()) {
			do {
				myFilter = new Filter(cursor.getInt(0));
				myFilter.setName(cursor.getString(1));
				myFilter.setDescription(cursor.getString(2));
				myFilter.setQuery(cursor.getString(3));
				myFilter.setValues(cursor.getString(4));
				myFilter.setValueArgs(cursor.getString(5));
				myFilter.setOrderBy(cursor.getString(6));
				myFilter.setHidden(cursor.getInt(7));
				if (cursor.getInt(8) == 1) filterByCategory = true;
				myFilter.setFilterByCategory(filterByCategory);
			} while (cursor.moveToNext());
		}
		//db.close();
		return myFilter;
	}
	public int getItemsCount() {
		String countQuery = "SELECT * FROM " + TABLE_ITEMS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		//db.close();
		return cursor.getCount();
	}
	public int updateItem(Item item) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		int hasDueDate = item.hasDueDate()? 1 : 0;
		int hasDueTime = item.hasDueTime()? 1 : 0;
		int isCompleted = item.isCompleted()? 1 : 0;
		
		ContentValues values = new ContentValues();
		values.put(KEY_ITEM_NAME, item.getName());
		values.put(KEY_ITEM_DESC, item.getDescription());
		values.put(KEY_ITEM_NOTES, item.getNotes());
		values.put(KEY_ITEM_FLIST, item.getFlist());
		values.put(KEY_ITEM_CATEGORIES, item.getCategoriesString());
		values.put(KEY_ITEM_HAS_DUE_DATE, hasDueDate);
		values.put(KEY_ITEM_HAS_DUE_TIME, hasDueTime);
		values.put(KEY_ITEM_DUE_DATETIME, item.getDueDateTime());
		values.put(KEY_ITEM_IS_COMPLETED, isCompleted);
		values.put(KEY_ITEM_COMPLETED_DATE, item.getCompletedDate());
		int returnVal = db.update(TABLE_ITEMS, values, KEY_ITEM_ID + " = ?",
				new String[] { String.valueOf(item.getID()) });
		//db.close();
		return returnVal;
	}
	public void deleteItem(Item item) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ITEMS, KEY_ITEM_ID + " = ?",
				new String[] { String.valueOf(item.getID()) });
		//db.close();
	}
	
	// Categories
	public int addFlist(Flist flist) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		//Log.v("DatabaseHandler.addFlist", "Adding Flist " + flist.getName() + " Desc: " + flist.getDescription() + " Type: " + flist.getType() + " Visible: " + flist.getVisible() + "Filter: " + flist.getFilterID());
		values.put(KEY_FLIST_NAME, flist.getName());
		values.put(KEY_FLIST_DESC, flist.getDescription());
		values.put(KEY_FLIST_TYPE, flist.getType());
		values.put(KEY_FLIST_VISIBLE, flist.getVisible());
		values.put(KEY_FLIST_FILTER, flist.getFilterID());
		db.insert(TABLE_FLISTS, null, values);
		Cursor cursor = db.rawQuery("SELECT last_insert_rowid() FROM " + TABLE_FLISTS, null);
		cursor.moveToFirst();
		int newFlistID = cursor.getInt(0);
		cursor.close();
		db.close();
		return newFlistID;
	}
	public int addCategory(Category category) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CAT_NAME, category.getName());
		values.put(KEY_CAT_DESC, category.getDescription());
		values.put(KEY_CAT_VISIBLE, category.getVisible());
		db.insert(TABLE_CATEGORIES, null, values);
		Cursor cursor = db.rawQuery("SELECT last_insert_rowid() FROM " + TABLE_CATEGORIES, null);
		cursor.moveToFirst();
		int newCatID = cursor.getInt(0);
		cursor.close();
		db.close();
		return newCatID;
	}
	public Flist getFlist(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_FLISTS,  new String[] { KEY_FLIST_ID,
				KEY_FLIST_NAME, KEY_FLIST_DESC, KEY_FLIST_TYPE, KEY_FLIST_VISIBLE, KEY_FLIST_FILTER }, KEY_FLIST_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		Flist flist = new Flist(cursor.getInt(0),
				cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
		flist.setFilterID(cursor.getInt(5));
		cursor.close();
		// TODO This db.close breaks everything!!!! Why!?
		//db.close();
		return flist;
	}
	public Category getCategory(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CATEGORIES,  new String[] { KEY_CAT_ID,
				KEY_CAT_NAME, KEY_CAT_DESC, KEY_CAT_VISIBLE }, KEY_CAT_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		Category category = new Category(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
		db.close();
		return category;
	}
	public Flist getFlistByName(String flistName) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_FLISTS,  new String[] { KEY_FLIST_ID,
				KEY_FLIST_NAME, KEY_FLIST_DESC, KEY_FLIST_VISIBLE }, KEY_FLIST_NAME + "=?",
				new String[] { flistName }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		
		Flist flist = new Flist(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)));
		db.close();
		return flist;
	}
	public ItemType getItemType(int typeID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ITEM_TYPES, new String[] { KEY_TYPE_ID, KEY_TYPE_NAME, KEY_TYPE_DESC }, KEY_TYPE_ID
				+ "=?", new String[] { String.valueOf(typeID) }, null, null, null, null);
		ItemType myItemType = new ItemType(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
		db.close();
		return myItemType;
	}
	public ArrayList<Flist> getAllFlists() {
		ArrayList<Flist> flistList = new ArrayList<Flist>();
		String selectQuery = "SELECT * FROM " + TABLE_FLISTS;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Flist flist = new Flist();
				flist.setID(cursor.getInt(0));
				flist.setName(cursor.getString(1));
				flist.setDescription(cursor.getString(2));
				flist.setVisible(cursor.getInt(3));
				// Adding category to list
				flistList.add(flist);
			} while (cursor.moveToNext());
		}
		db.close();
		
		// return contact list
		return flistList;
	}
	public ArrayList<Flist> getFlists() {
		ArrayList<Flist> flistList = new ArrayList<Flist>();
		String isVisible = "1";
		String noFilter = "0";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_FLISTS,  new String[] { KEY_FLIST_ID,
				KEY_FLIST_NAME, KEY_FLIST_DESC, KEY_FLIST_TYPE, KEY_FLIST_VISIBLE, KEY_FLIST_FILTER }, KEY_FLIST_VISIBLE + "=?"
				+ " AND " + KEY_FLIST_FILTER + "=?",
				new String[] { isVisible, noFilter }, null, null, null, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Flist flist = new Flist();
				flist.setID(Integer.parseInt(cursor.getString(0)));
				flist.setName(cursor.getString(1));
				flist.setDescription(cursor.getString(2));
				flist.setType(Integer.parseInt(cursor.getString(3)));
				flist.setVisible(Integer.parseInt(cursor.getString(4)));
				flist.setFilterID(cursor.getInt(5));
				// Adding category to list
				flistList.add(flist);
			} while (cursor.moveToNext());
		}
		db.close();
		return flistList;
	}
	public ArrayList<Category> getCategories() {
		ArrayList<Category> categoryList = new ArrayList<Category>();
		String isVisible = "1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CATEGORIES,  new String[] { KEY_CAT_ID,
				KEY_CAT_NAME, KEY_CAT_DESC, KEY_CAT_VISIBLE }, KEY_CAT_VISIBLE + "=?",
				new String[] { isVisible }, null, null, null, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Category category = new Category();
				category.setID(Integer.parseInt(cursor.getString(0)));
				category.setName(cursor.getString(1));
				category.setDescription(cursor.getString(2));
				category.setVisible(cursor.getInt(3));
				// Adding category to list
				categoryList.add(category);
			} while (cursor.moveToNext());
		}
		db.close();
		return categoryList;
	}
	public int getFlistsCount() {
		String countQuery = "SELECT * FROM " + TABLE_FLISTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		//db.close();
		// return count
		return count;
	}
	public void hideFlist(int flistID) {
		int isVisible = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_FLIST_VISIBLE, isVisible);
		db.update(TABLE_FLISTS, values, KEY_FLIST_VISIBLE + " = " + isVisible, null);
		db.close();
	}
	public void unHideFlist(int flistId) {
		int isVisible = 1;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_FLIST_VISIBLE, isVisible);
		db.update(TABLE_FLISTS, values, KEY_FLIST_VISIBLE + " = " + isVisible, null);
		db.close();
	}
	public int updateFlist(Flist flist) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_FLIST_NAME, flist.getName());
		values.put(KEY_FLIST_DESC, flist.getDescription().toString());
		values.put(KEY_FLIST_TYPE, flist.getType());
		values.put(KEY_FLIST_VISIBLE, flist.getVisible());
		values.put(KEY_FLIST_FILTER,  flist.getFilterID());
		String keyID = String.valueOf(flist.getID());
		//Log.v("updateCategory", "DatabaseHandler.updateCategory.keyID: " + keyID);
		// updating row
		//return db.update(TABLE_CATEGORIES, values, KEY_CAT_ID + " = ?",
		//		new String[] { String.valueOf(category.getID()) });
		int returnVal = db.update(TABLE_FLISTS, values, KEY_FLIST_ID + " = " + keyID, null);
		db.close();
		return returnVal;
	}
	public boolean deleteFlist(int flistID) {
		int rowsDeleted = 0;
		boolean success = false;
		SQLiteDatabase db = this.getWritableDatabase();
		rowsDeleted = db.delete(TABLE_FLISTS, KEY_FLIST_ID + " = ?",
				new String[] { String.valueOf(flistID) });
		db.close();
		if (rowsDeleted > 0)
			success = true;
		return success;
	}
	
	// Filters
	public ArrayList<Filter> getFilters() {
		ArrayList<Filter> filterList = new ArrayList<Filter>();
		String selectQuery = "SELECT * FROM " + TABLE_FILTERS;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Filter filter = new Filter(cursor.getInt(0));
				filter.setName(cursor.getString(1));
				filter.setDescription(cursor.getString(2));
				filter.setQuery(cursor.getString(3));
				filter.setValues(cursor.getString(4));
				filter.setHidden(cursor.getInt(5));
				filterList.add(filter);
			} while (cursor.moveToNext());
		}
		//db.close();
		return filterList;
	}
	
	
	// Types
	public ArrayList<ItemType> getItemTypeList() {
		ArrayList<ItemType> typeList = new ArrayList<ItemType>();
		String selectQuery = "SELECT * FROM " + TABLE_ITEM_TYPES;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				ItemType type = new ItemType();
				type.setID(Integer.parseInt(cursor.getString(0)));
				type.setName(cursor.getString(1));
				type.setDescription(cursor.getString(2));
				typeList.add(type);
			} while (cursor.moveToNext());
		}
		db.close();
		return typeList;
	}
	public String getItemTypeName(int typeID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ITEM_TYPES, new String[] { KEY_TYPE_ID, KEY_TYPE_NAME, KEY_TYPE_DESC }, KEY_TYPE_ID
				+ "=?", new String[] { String.valueOf(typeID) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		String itemTypeName = cursor.getString(1);
		//db.close();
		return itemTypeName;
	}
	
	// Backup & Restore
	public void exportDB(){
		File sd = Environment.getExternalStorageDirectory();
	    File data = Environment.getDataDirectory();
	    FileChannel source=null;
	    FileChannel destination=null;

	    // Use one of these to set timestamp on DB Backup File
	    Time now = new Time();
	    now.setToNow();
	       
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
	    String currentDateandTime = sdf.format(new Date(0));
	       
	    //File backupDB = new File(sd, backupDBPath);
	    File currentDB = new File(data, currentDBPath);
	    // TODO: Set DB backup filename to reflect current date time string that can be parsed for user readability
	    File backupDB = new File(sd, backupDBPath + "-" + "123456");
	    try {
	        source = new FileInputStream(currentDB).getChannel();
	        destination = new FileOutputStream(backupDB).getChannel();
	        destination.transferFrom(source, 0, source.size());
	        source.close();
	        destination.close();
	        Toast.makeText(context, "DB Exported!", Toast.LENGTH_LONG).show();
	    } catch(IOException e) {
	        e.printStackTrace();
	        Toast.makeText(context, "Export Failed!", Toast.LENGTH_LONG).show();
	        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
	    }
	}
	
	private String getAppDir() {
		return "/FlipList/";
	}

	public void restoreDBSelectFile() {
		handler = new Handler();
		File sd = Environment.getExternalStorageDirectory();
		String select_file = "Select File";
		File importFolder = new File(sd, "/FlipList/");
		final CharSequence[] importFiles = importFolder.list();
		
		if (importFiles == null || importFiles.length == 0) {
			Toast.makeText(context, "Source folder empty", Toast.LENGTH_SHORT).show();
			return;
		}
		
		importDatabaseFileName = (String) importFiles[0];
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(select_file);
		builder.setSingleChoiceItems(importFiles, 0, new 
				android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				importDatabaseFileName = (String) importFiles[whichButton];
				handler.post(restoreDatabaseRunable);
				
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	// TODO: Should this be happening like this?
	private SQLiteDatabase getDatabase() {
		SQLiteDatabase db = this.getWritableDatabase();
		return db;
	}
	
	private Runnable restoreDatabaseRunable = new Runnable() {
        public void run() {
            try {
                // open database in readonly mode
                SQLiteDatabase db = SQLiteDatabase.openDatabase(
                        getAppDir() + "/" + importDatabaseFileName,
                        null, SQLiteDatabase.OPEN_READONLY);
                // check version compatibility
                // only same version of the db can be restored
                if (getDatabase().getVersion() != db.getVersion()) {
                    Toast.makeText(context, context.getString(R.string.restore_db_version_conflict),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                db.close();
            } catch (SQLiteException e) {
                Toast.makeText(context, context.getString(R.string.restore_file_error) + ": " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                return;
            }
            // TODO: closing current db (is this right??)
            getDatabase().close();
            try {
                File data = Environment.getDataDirectory();
                if (Environment.getExternalStorageDirectory() != null) {
                    String restoreDBPath = getAppDir() + "/" + importDatabaseFileName;
                    File restoreDB = new File(restoreDBPath);
                    File currentDB = new File(data, currentDBPath);
                    FileChannel src = new FileInputStream(restoreDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    //myApp.setDatabase();
                }
            }
            catch (Exception e) {
                Log.e("DatabaseHandler.RestoreDatabaseRunable", e.getMessage());
                //myApp.setDatabase();
            }
        }
    };
}
