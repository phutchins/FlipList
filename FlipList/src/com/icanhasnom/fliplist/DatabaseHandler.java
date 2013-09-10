package com.icanhasnom.fliplist;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int DATABASE_VERSION = 65;
	
	// Database Name
	private static final String DATABASE_NAME = "fliplist";
	
	// Categories Table
	private static final String TABLE_CATEGORIES = "categories";
	// Categories Table Column Names
	private static final String KEY_CAT_ID = "id";
	private static final String KEY_CAT_NAME = "name";
	private static final String KEY_CAT_DESC = "desc";
	private static final String KEY_CAT_TYPE = "type";
	private static final String KEY_CAT_VISIBLE = "visible";
	private static final String KEY_CAT_FILTER = "filter";
	
	// Items Table
	private static final String TABLE_ITEMS = "items";
	// Items Table Column Names
	private static final String KEY_ITEM_ID = "id";
	private static final String KEY_ITEM_NAME = "name";
	private static final String KEY_ITEM_DESC = "desc";
	private static final String KEY_ITEM_NOTES = "notes";
	private static final String KEY_ITEM_PRIMARY_CAT = "cat";
	private static final String KEY_ITEM_SECONDARY_CATS = "cats";
	private static final String KEY_ITEM_HAS_DUE_DATE = "has_due_date";
	private static final String KEY_ITEM_HAS_DUE_TIME = "has_due_time";
	private static final String KEY_ITEM_DUE_DATETIME = "due_datetime";
	private static final String KEY_ITEM_CREATE_DATE = "create_date";
	private static final String KEY_ITEM_IS_COMPLETED = "is_completed";
	private static final String KEY_ITEM_COMPLETED_DATE = "completed_date";
	
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
	
	// Settings Table
	private static final String TABLE_SETTINGS = "settings";
	// Settings Table Column Names
	private static final String KEY_SETTING_ID = "id";
	private static final String KEY_SETTING_NAME = "name";
	private static final String KEY_SETTING_VAL1 = "val1";
	private static final String KEY_SETTING_VAL2 = "val2";
	
	
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
		String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "(" 
				+ KEY_CAT_ID + " INTEGER PRIMARY KEY," + KEY_CAT_NAME + " TEXT," 
				+ KEY_CAT_DESC + " TEXT," + KEY_CAT_TYPE + " TEXT," + KEY_CAT_VISIBLE
				+ " INTEGER," + KEY_CAT_FILTER + " INTEGER" + ")";
		String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
				+ KEY_ITEM_ID + " INTEGER PRIMARY KEY," 
				+ KEY_ITEM_NAME + " TEXT,"
				+ KEY_ITEM_DESC + " TEXT," 
				+ KEY_ITEM_NOTES + " TEXT,"
				+ KEY_ITEM_PRIMARY_CAT + " TEXT," 
				+ KEY_ITEM_SECONDARY_CATS + " TEXT," 
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
                + KEY_CAT_DESC + "," + KEY_CAT_TYPE + "," + KEY_CAT_VISIBLE + "," + KEY_CAT_FILTER + ") values(0, 'Uncategorized', 'Uncategorized','0', '1', 0)";
		String CREATE_ARCHIVE_CATEGORY = "insert into " + TABLE_CATEGORIES + "(" + KEY_CAT_ID + "," + KEY_CAT_NAME + ","
                + KEY_CAT_DESC + "," + KEY_CAT_TYPE + "," + KEY_CAT_VISIBLE + "," + KEY_CAT_FILTER + ") values(1, 'Archive', 'Archive Category','0', '0', 0)";
		String CREATE_ALL_CATEGORY = "insert into " + TABLE_CATEGORIES + "(" + KEY_CAT_ID + "," + KEY_CAT_NAME + ","
                + KEY_CAT_DESC + "," + KEY_CAT_TYPE + "," + KEY_CAT_VISIBLE + "," + KEY_CAT_FILTER + ") values(2, 'All Items', 'List All Items','0', '1', 1)";
		
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

		//String CREATE_SETTING_DEFCAT = "insert into " + TABLE_SETTINGS + "(" + KEY_SETTING_ID + "," + KEY_SETTING_NAME + ","
		//		+ KEY_SETTING_VAL1 + "," + KEY_SETTING_VAL2 + ") values(0, 'defaultCategory', '0', 'unused')";
		//String CREATE_SETTING_DEFTYPE = "insert into " + TABLE_SETTINGS + "(" + KEY_SETTING_ID + "," + KEY_SETTING_NAME + ","
		//		+ KEY_SETTING_VAL1 + "," + KEY_SETTING_VAL2 + ") values(1, 'defaultType', '0', 'unused')";
		//String CREATE_SETTING_COMPLETED = "insert into " + TABLE_SETTINGS + "(" + KEY_SETTING_ID + "," + KEY_SETTING_NAME + ","
		//		+ KEY_SETTING_VAL1 + "," + KEY_SETTING_VAL2 + ") values(2, 'defaultCompleted', '1', 'unused')";
		//String CREATE_SETTING_PROTECTED_CATS = "insert into " + TABLE_SETTINGS + "(" + KEY_SETTING_ID + "," + KEY_SETTING_NAME + ","
		//		+ KEY_SETTING_VAL1 + "," + KEY_SETTING_VAL2 + ") values(3, 'protectedCats', '0,1', 'unused')";
		
		db.execSQL(CREATE_CATEGORIES_TABLE);
		db.execSQL(CREATE_ITEMS_TABLE);
		db.execSQL(CREATE_TYPES_TABLE);
		db.execSQL(CREATE_FILTERS_TABLE);
		//db.execSQL(CREATE_SETTINGS_TABLE);
		db.execSQL(CREATE_UNCATEGORIZED_CATEGORY);
		db.execSQL(CREATE_ARCHIVE_CATEGORY);
		db.execSQL(CREATE_ALL_CATEGORY);
		db.execSQL(CREATE_TYPE_GENERIC);
		db.execSQL(CREATE_TYPE_GROCERY);
		db.execSQL(CREATE_TYPE_TODO);
		db.execSQL(CREATE_FILTER_NONE);
		db.execSQL(CREATE_FILTER_ALL);
		db.execSQL(CREATE_FILTER_ACTIVE);
		db.execSQL(CREATE_FILTER_NEXTSEVEN);
		db.execSQL(CREATE_FILTER_ALL_BY_CAT);
		//db.execSQL(CREATE_SETTING_DEFCAT);
		//db.execSQL(CREATE_SETTING_DEFTYPE);
		//db.execSQL(CREATE_SETTING_COMPLETED);
		//db.execSQL(CREATE_SETTING_PROTECTED_CATS);
		

	}
		
	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_TYPES);
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTERS);
			
		onCreate(db);
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
	    	// TODO: make this use the result from prefRemoveCompletedItemsDelay preference as a negative number
	    	//compareDateCal.add(Calendar.MINUTE, -delayInt);
			//compareDate = sdfDateTime.format(compareDateCal.getTime());
			//String dateTimeCompare = "datetime('now','-" + delayInt + " minute')";
	    	prefQueryStringValues = "(" + KEY_ITEM_IS_COMPLETED + "=? OR (" + KEY_ITEM_COMPLETED_DATE + " >= datetime('now',?) AND " + KEY_ITEM_IS_COMPLETED + " =?))";
	    	prefQueryStringValueArgs = "0;-" + delayInt + " minutes;1";
		}
		// Show all items always
		if (prefRemoveCompletedItems.equals("2")) {
			// Do nothing
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
		String secondaryCatsString = item.getSecondaryCatsString();
		int hasDueDate = item.hasDueDate()? 1 : 0;
		int hasDueTime = item.hasDueTime()? 1 : 0;
		int isCompleted = item.isCompleted()? 1 : 0;
		
		ContentValues values = new ContentValues();
		values.put(KEY_ITEM_NAME, item.getName());
		values.put(KEY_ITEM_DESC, item.getDescription());
		values.put(KEY_ITEM_NOTES, item.getNotes());
		values.put(KEY_ITEM_PRIMARY_CAT,  item.getPrimaryCat());
		values.put(KEY_ITEM_SECONDARY_CATS, secondaryCatsString);
		values.put(KEY_ITEM_HAS_DUE_DATE, hasDueDate);
		values.put(KEY_ITEM_HAS_DUE_TIME, hasDueTime);
		values.put(KEY_ITEM_DUE_DATETIME, item.getDueDateTime());
		values.put(KEY_ITEM_CREATE_DATE, item.getCreateDate());
		values.put(KEY_ITEM_IS_COMPLETED, isCompleted);
		values.put(KEY_ITEM_COMPLETED_DATE,  item.getCompletedDate());
		
		//Log.v("DatabaseHandler.addItem", "Adding Item " + item.getName() + " to the DB");
		//Log.v("DatabaseHandler.addItem", "item.getDueDateTime(): " + item.getDueDateTime());
		//Log.v("DatabaseHandler.addItem", " *item info: " + item.getName() + "," + item.getDescription() + "," + item.getNotes() + "," 
		//+ item.getPrimaryCat() + "," + secondaryCatsString + "," + item.getCreateDate() + "," + hasDueDate + "," + hasDueTime
		//+ item.getDueDateTime());
		
		db.insert(TABLE_ITEMS, null, values);
		db.close();
	}
	public Item getItem(int id) throws NumberFormatException, ParseException {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_ITEMS, new String[] { KEY_ITEM_ID, KEY_ITEM_NAME,
				KEY_ITEM_DESC, KEY_ITEM_NOTES, KEY_ITEM_PRIMARY_CAT, KEY_ITEM_SECONDARY_CATS, 
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
		String itemSecCats = cursor.getString(5);   // Not used yet
		Integer itemHasDueDate = cursor.getInt(6);
		Integer itemHasDueTime = cursor.getInt(7);
		String itemDueDate = cursor.getString(8);
		String itemCreateDate = cursor.getString(9);
		String itemCompletedDate = cursor.getString(10);
		
		Item item = new Item(itemID, itemPriCat, itemSecCats, itemName, itemDesc, itemNotes, itemCreateDate, itemDueDate);
		item.setHasDueDate(itemHasDueDate);
		item.setHasDueTime(itemHasDueTime);
		item.setCompletedDate(itemCompletedDate);
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
				item.setPrimaryCat(cursor.getInt(4));
				item.addToCats(cursor.getString(5));
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
		db.close();
		return itemList;
	}
	public ItemList getItemList(int catID) {
		ItemList itemList = new ItemList(catID);
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
		
		Category myCat = getCategory(catID);
		int filterID = myCat.getFilterID();
		//Log.v("DatabaseHandler.getItemList", "filterID: " + filterID);
		// Filter ID 0 means use preferences as there is no filter applied
		if (filterID == 0) {
			if (!prefQueryStringValues.isEmpty()) {
				dbValues = prefQueryStringValues + " AND cat=?";
				dbValueArgs = prefQueryStringValueArgs + ";" + catID;
			} else {
				dbValues = "cat=?";
				dbValueArgs = String.valueOf(catID);
			}
			//Log.v("DatabaseHandler.getItemList", "USING DEFAULTS - dbValues: " + dbValues + " dbValueArgs: " + dbValueArgs);
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
					dbValues = dbValues + " AND cat=?";
					dbValueArgs = dbValueArgs + ";" + catID;
				} else {
					dbValues = "cat=?";
					dbValueArgs = String.valueOf(catID);
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
				item.setPrimaryCat(cursor.getInt(4));
				item.addToCats(cursor.getString(5));
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
		return myFilter;
	}
	public int getItemsCount() {
		String countQuery = "SELECT * FROM " + TABLE_ITEMS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		// return count
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
		values.put(KEY_ITEM_PRIMARY_CAT, item.getPrimaryCat());
		values.put(KEY_ITEM_SECONDARY_CATS, item.getSecondaryCatsString());
		values.put(KEY_ITEM_HAS_DUE_DATE, hasDueDate);
		values.put(KEY_ITEM_HAS_DUE_TIME, hasDueTime);
		values.put(KEY_ITEM_DUE_DATETIME, item.getDueDateTime());
		values.put(KEY_ITEM_IS_COMPLETED, isCompleted);
		values.put(KEY_ITEM_COMPLETED_DATE, item.getCompletedDate());
		return db.update(TABLE_ITEMS, values, KEY_ITEM_ID + " = ?",
				new String[] { String.valueOf(item.getID()) });
	}
	public void deleteItem(Item item) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ITEMS, KEY_ITEM_ID + " = ?",
				new String[] { String.valueOf(item.getID()) });
		db.close();
	}
	
	// Categories
	public int addCategory(Category category) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CAT_NAME, category.getName());
		values.put(KEY_CAT_DESC, category.getDescription());
		values.put(KEY_CAT_TYPE, category.getType());
		values.put(KEY_CAT_VISIBLE, category.getVisible());
		values.put(KEY_CAT_FILTER, category.getFilterID());
		db.insert(TABLE_CATEGORIES, null, values);
		Cursor cursor = db.rawQuery("SELECT last_insert_rowid() FROM " + TABLE_CATEGORIES, null);
		cursor.moveToFirst();
		int newCatID = cursor.getInt(0);
		cursor.close();
		db.close();
		return newCatID;
	}
	public Category getCategory(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CATEGORIES,  new String[] { KEY_CAT_ID,
				KEY_CAT_NAME, KEY_CAT_DESC, KEY_CAT_TYPE, KEY_CAT_VISIBLE, KEY_CAT_FILTER }, KEY_CAT_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		Category category = new Category(cursor.getInt(0),
				cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
		// TOOD: should this be in the constructor? or not?
		category.setFilterID(cursor.getInt(5));
		return category;
	}
	public Category getCategoryByName(String catName) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_CATEGORIES,  new String[] { KEY_CAT_ID,
				KEY_CAT_NAME, KEY_CAT_DESC, KEY_CAT_TYPE, KEY_CAT_VISIBLE }, KEY_CAT_NAME + "=?",
				new String[] { catName }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		
		Category category = new Category(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)));
		return category;
	}
	public ItemType getItemType(int typeID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ITEM_TYPES, new String[] { KEY_TYPE_ID, KEY_TYPE_NAME, KEY_TYPE_DESC }, KEY_TYPE_ID
				+ "=?", new String[] { String.valueOf(typeID) }, null, null, null, null);
		ItemType myItemType = new ItemType(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
		return myItemType;
	}
	public ArrayList<Category> getAllCategories() {
		ArrayList<Category> categoryList = new ArrayList<Category>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Category category = new Category();
				category.setID(cursor.getInt(0));
				category.setName(cursor.getString(1));
				category.setDescription(cursor.getString(2));
				category.setType(cursor.getInt(3));
				category.setVisible(cursor.getInt(4));
				category.setFilterID(cursor.getInt(5));
				// Adding category to list
				categoryList.add(category);
			} while (cursor.moveToNext());
		}
		db.close();
		
		// return contact list
		return categoryList;
	}
	public ArrayList<Category> getCategories() {
		ArrayList<Category> categoryList = new ArrayList<Category>();
		// Select All Query
		//String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES;
		String isVisible = "1";
		String noFilter = "0";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CATEGORIES,  new String[] { KEY_CAT_ID,
				KEY_CAT_NAME, KEY_CAT_DESC, KEY_CAT_TYPE, KEY_CAT_VISIBLE, KEY_CAT_FILTER }, KEY_CAT_VISIBLE + "=?"
				+ " AND " + KEY_CAT_FILTER + "=?",
				new String[] { isVisible, noFilter }, null, null, null, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Category category = new Category();
				category.setID(Integer.parseInt(cursor.getString(0)));
				category.setName(cursor.getString(1));
				category.setDescription(cursor.getString(2));
				category.setType(Integer.parseInt(cursor.getString(3)));
				category.setVisible(Integer.parseInt(cursor.getString(4)));
				category.setFilterID(cursor.getInt(5));
				// Adding category to list
				categoryList.add(category);
			} while (cursor.moveToNext());
		}
		return categoryList;
	}
	public ArrayList<Category> getLists() {
		ArrayList<Category> categoryList = new ArrayList<Category>();
		// Select All Query
		//String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES;
		String isVisible = "1";
		String noFilter = "0";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CATEGORIES,  new String[] { KEY_CAT_ID,
				KEY_CAT_NAME, KEY_CAT_DESC, KEY_CAT_TYPE, KEY_CAT_VISIBLE, KEY_CAT_FILTER }, KEY_CAT_VISIBLE + "=?"
				+ " AND " + KEY_CAT_FILTER + ">?",
				new String[] { isVisible, noFilter }, null, null, null, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Category category = new Category();
				category.setID(Integer.parseInt(cursor.getString(0)));
				category.setName(cursor.getString(1));
				category.setDescription(cursor.getString(2));
				category.setType(Integer.parseInt(cursor.getString(3)));
				category.setVisible(Integer.parseInt(cursor.getString(4)));
				category.setFilterID(cursor.getInt(5));
				// Adding category to list
				categoryList.add(category);
			} while (cursor.moveToNext());
		}
		return categoryList;
	}
	public int getCategoriesCount() {
		String countQuery = "SELECT * FROM " + TABLE_CATEGORIES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		
		// return count
		return count;
	}
	public int getCategoryID(String catName) {
		// Fix this DB query to pull cat id using the name
		String catIdQuery = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + KEY_CAT_NAME + " = 'Default'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(catIdQuery, null);
		int catID = 0;
		if (cursor != null)
			cursor.moveToFirst();
		try {
			catID = Integer.parseInt(cursor.getString(0));
		} catch (CursorIndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return catID;
	}
	public void hideCategory(int catID) {
		int isVisible = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CAT_VISIBLE, isVisible);
		db.update(TABLE_CATEGORIES, values, KEY_CAT_VISIBLE + " = " + isVisible, null);
	}
	public void unHideCategory(int catId) {
		int isVisible = 1;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CAT_VISIBLE, isVisible);
		db.update(TABLE_CATEGORIES, values, KEY_CAT_VISIBLE + " = " + isVisible, null);
	}
	public int updateCategory(Category category) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_CAT_NAME, category.getName());
		values.put(KEY_CAT_DESC, category.getDescription().toString());
		values.put(KEY_CAT_TYPE, category.getType());
		values.put(KEY_CAT_VISIBLE, category.getVisible());
		values.put(KEY_CAT_FILTER,  category.getFilterID());
		String keyID = String.valueOf(category.getID());
		//Log.v("updateCategory", "DatabaseHandler.updateCategory.keyID: " + keyID);
		// updating row
		//return db.update(TABLE_CATEGORIES, values, KEY_CAT_ID + " = ?",
		//		new String[] { String.valueOf(category.getID()) });
		return db.update(TABLE_CATEGORIES, values, KEY_CAT_ID + " = " + keyID, null);
	}
	public boolean deleteCategory(int catID) {
		int rowsDeleted = 0;
		boolean success = false;
		SQLiteDatabase db = this.getWritableDatabase();
		rowsDeleted = db.delete(TABLE_CATEGORIES, KEY_CAT_ID + " = ?",
				new String[] { String.valueOf(catID) });
		db.close();
		if (rowsDeleted > 0)
			success = true;
		return success;
	}
	
	// Filters
	public ArrayList<Filter> getFilterList() {
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
		db.close();
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
		return itemTypeName;
	}

	
	// Settings
	public int getDefaultCatID() {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_SETTINGS, new String[] { KEY_SETTING_NAME, 
				KEY_SETTING_VAL1 }, KEY_SETTING_NAME + "=?",
				new String[] { String.valueOf("defaultCategory") }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		return Integer.parseInt(cursor.getString(1));
	}
	public int getDefaultTypeID() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_SETTINGS, new String[] { KEY_SETTING_NAME,
				KEY_SETTING_VAL1 }, KEY_SETTING_NAME + "=?",
				new String[] { String.valueOf("defaultType") }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		return Integer.parseInt(cursor.getString(1));
	}
	public void setCompletedCategory(int completedCatID) {
		SQLiteDatabase db = this.getWritableDatabase();
		String completedSettingName = "completedCategory";
		ContentValues values = new ContentValues();
		values.put(KEY_SETTING_VAL1, completedCatID);
		db.update(TABLE_SETTINGS, values, KEY_SETTING_NAME + " = " + completedSettingName, null);
	}
}
