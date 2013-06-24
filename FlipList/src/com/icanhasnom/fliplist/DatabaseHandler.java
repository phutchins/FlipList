package com.icanhasnom.fliplist;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int DATABASE_VERSION = 28;
	
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
	
	// Settings Table
	private static final String TABLE_SETTINGS = "settings";
	// Settings Table Column Names
	private static final String KEY_SETTING_ID = "id";
	private static final String KEY_SETTING_NAME = "name";
	private static final String KEY_SETTING_VAL1 = "val1";
	private static final String KEY_SETTING_VAL2 = "val2";
	
	
	// Types Table
	private static final String TABLE_CATEGORY_TYPES = "types";
	// Types Table Column Names
	private static final String KEY_TYPE_ID = "id";
	private static final String KEY_TYPE_NAME = "name";
	private static final String KEY_TYPE_DESC = "desc";
	
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "(" 
				+ KEY_CAT_ID + " INTEGER PRIMARY KEY," + KEY_CAT_NAME + " TEXT," 
				+ KEY_CAT_DESC + " TEXT," + KEY_CAT_TYPE + " TEXT," + KEY_CAT_VISIBLE
				+ " INTEGER" + ")";
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
				+ KEY_ITEM_CREATE_DATE + " TEXT" + ")";
		String CREATE_TYPES_TABLE = "CREATE TABLE " + TABLE_CATEGORY_TYPES + "("
				+ KEY_TYPE_ID + " INTEGER PRIMARY KEY," + KEY_TYPE_NAME + " TEXT,"
				+ KEY_TYPE_DESC + " TEXT" + ")";
		
		String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
				+ KEY_SETTING_ID + " INTEGER PRIMARY KEY," + KEY_SETTING_NAME + " TEXT,"
				+ KEY_SETTING_VAL1 + " TEXT," + KEY_SETTING_VAL2 + " TEXT" + ")";
		
		// TODO: Make these undeletable? Atleast the completed category.
		//       Undeletable setting mande, make delete method check undeletable before deleting category
		//        and notify user in a tost if category cannot be deleted.
		String CREATE_DEFAULT_CATEGORY = "insert into " + TABLE_CATEGORIES + "(" + KEY_CAT_ID + "," + KEY_CAT_NAME + ","
                + KEY_CAT_DESC + "," + KEY_CAT_TYPE + "," + KEY_CAT_VISIBLE + ") values(0, 'Default', 'Default Category','0', '1')";
		String CREATE_COMPLETED_CATEGORY = "insert into " + TABLE_CATEGORIES + "(" + KEY_CAT_ID + "," + KEY_CAT_NAME + ","
                + KEY_CAT_DESC + "," + KEY_CAT_TYPE + "," + KEY_CAT_VISIBLE + ") values(1, 'Completed', 'Completed Category','0', '0')";
		
		String CREATE_TYPE_GENERIC = "insert into " + TABLE_CATEGORY_TYPES + "(" + KEY_TYPE_ID + "," + KEY_TYPE_NAME + ","
				+ KEY_TYPE_DESC + ") values(0, 'Generic', 'Generic items')";
		String CREATE_TYPE_GROCERY = "insert into " + TABLE_CATEGORY_TYPES + "(" + KEY_TYPE_ID + "," + KEY_TYPE_NAME + ","
				+ KEY_TYPE_DESC + ") values(1, 'Grocery List', 'List used for grocery items')";
		String CREATE_TYPE_TODO = "insert into " + TABLE_CATEGORY_TYPES + "(" + KEY_TYPE_ID + "," + KEY_TYPE_NAME + ","
				+ KEY_TYPE_DESC + ") values(2, 'ToDo', 'Tasks that you need to complete')";
		
		String CREATE_SETTING_DEFCAT = "insert into " + TABLE_SETTINGS + "(" + KEY_SETTING_ID + "," + KEY_SETTING_NAME + ","
				+ KEY_SETTING_VAL1 + "," + KEY_SETTING_VAL2 + ") values(0, 'defaultCategory', '0', 'unused')";
		String CREATE_SETTING_DEFTYPE = "insert into " + TABLE_SETTINGS + "(" + KEY_SETTING_ID + "," + KEY_SETTING_NAME + ","
				+ KEY_SETTING_VAL1 + "," + KEY_SETTING_VAL2 + ") values(1, 'defaultType', '0', 'unused')";
		String CREATE_SETTING_COMPLETED = "insert into " + TABLE_SETTINGS + "(" + KEY_SETTING_ID + "," + KEY_SETTING_NAME + ","
				+ KEY_SETTING_VAL1 + "," + KEY_SETTING_VAL2 + ") values(2, 'defaultCompleted', '1', 'unused')";
		String CREATE_SETTING_PROTECTED_CATS = "insert into " + TABLE_SETTINGS + "(" + KEY_SETTING_ID + "," + KEY_SETTING_NAME + ","
				+ KEY_SETTING_VAL1 + "," + KEY_SETTING_VAL2 + ") values(3, 'protectedCats', '0,1', 'unused')";
		
		db.execSQL(CREATE_CATEGORIES_TABLE);
		db.execSQL(CREATE_ITEMS_TABLE);
		db.execSQL(CREATE_TYPES_TABLE);
		db.execSQL(CREATE_SETTINGS_TABLE);
		db.execSQL(CREATE_DEFAULT_CATEGORY);
		db.execSQL(CREATE_COMPLETED_CATEGORY);
		db.execSQL(CREATE_TYPE_GENERIC);
		db.execSQL(CREATE_TYPE_GROCERY);
		db.execSQL(CREATE_TYPE_TODO);
		db.execSQL(CREATE_SETTING_DEFCAT);
		db.execSQL(CREATE_SETTING_DEFTYPE);
		db.execSQL(CREATE_SETTING_COMPLETED);
		db.execSQL(CREATE_SETTING_PROTECTED_CATS);
	}
		
	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_TYPES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
			
		onCreate(db);
	}
	
	
	// Items
	public void addItem(ListItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		String secondaryCatsString = item.getSecondaryCatsString();
		int hasDueDate = item.hasDueDate()? 1 : 0;
		int hasDueTime = item.hasDueTime()? 1 : 0;
		
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
		
		Log.v("DatabaseHandler.addItem", "Adding Item " + item.getName() + " to the DB");
		Log.v("DatabaseHandler.addItem", " *item info: " + item.getName() + "," + item.getDescription() + "," + item.getNotes() + "," 
		+ item.getPrimaryCat() + "," + secondaryCatsString + "," + item.getCreateDate() + "," + hasDueDate + "," + hasDueTime
		+ item.getDueDateTime());
		
		db.insert(TABLE_ITEMS, null, values);
		db.close();
	}
	public ListItem getItem(int id) throws NumberFormatException, ParseException {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_ITEMS, new String[] { KEY_ITEM_ID, KEY_ITEM_NAME,
				KEY_ITEM_DESC, KEY_ITEM_NOTES, KEY_ITEM_PRIMARY_CAT, KEY_ITEM_SECONDARY_CATS, 
				KEY_ITEM_HAS_DUE_DATE, KEY_ITEM_HAS_DUE_TIME, KEY_ITEM_DUE_DATETIME, KEY_ITEM_CREATE_DATE, }, 
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
		
		ListItem item = new ListItem(itemID, itemPriCat, itemSecCats, itemName, itemDesc, itemNotes, itemCreateDate, itemDueDate);
		item.setHasDueDate(itemHasDueDate);
		item.setHasDueTime(itemHasDueTime);
		// return item
		return item;
	}
	public ArrayList<ListItem> getAllItems() throws NumberFormatException, ParseException {
		ArrayList<ListItem> itemList = new ArrayList<ListItem>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_ITEMS;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ListItem item = new ListItem();
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

		Cursor cursor = db.query(TABLE_ITEMS, new String[] { KEY_ITEM_ID, KEY_ITEM_NAME, 
				KEY_ITEM_DESC, KEY_ITEM_NOTES, KEY_ITEM_PRIMARY_CAT, KEY_ITEM_SECONDARY_CATS, 
				KEY_ITEM_HAS_DUE_DATE, KEY_ITEM_HAS_DUE_TIME, KEY_ITEM_DUE_DATETIME, KEY_ITEM_CREATE_DATE }, 
				KEY_ITEM_PRIMARY_CAT + "=?", new String[] { String.valueOf(catID) }, null, null, null, null);
		
		Log.v("DatabaseHandler.getItemList", "Trying to get ItemList for catID: " + catID);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ListItem item = new ListItem();
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
				
				Log.v("DatabaseHandler", "getItemList - Getting item - Name (" + item.getName() + ") ID: (" + item.getID() + ") PriCatID: " + item.getPrimaryCat());
				// Adding category to list
				itemList.addListItem(item);
			} while (cursor.moveToNext());
			Log.v("DatabaseHandler.getItemList", "cursor.size(): " + cursor.getCount());
		}
		// return contact list
		return itemList;
	}
	public int getItemsCount() {
		String countQuery = "SELECT * FROM " + TABLE_ITEMS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		// return count
		return cursor.getCount();
	}
	public int updateItem(ListItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		int hasDueDate = item.hasDueDate()? 1 : 0;
		int hasDueTime = item.hasDueTime()? 1 : 0;
		
		ContentValues values = new ContentValues();
		values.put(KEY_ITEM_NAME, item.getName());
		values.put(KEY_ITEM_DESC, item.getDescription());
		values.put(KEY_ITEM_NOTES, item.getNotes());
		values.put(KEY_ITEM_PRIMARY_CAT, item.getPrimaryCat());
		values.put(KEY_ITEM_SECONDARY_CATS, item.getSecondaryCatsString());
		values.put(KEY_ITEM_HAS_DUE_DATE, hasDueDate);
		values.put(KEY_ITEM_HAS_DUE_TIME, hasDueTime);
		values.put(KEY_ITEM_DUE_DATETIME, item.getDueDateTime());
		
		// updating row
		return db.update(TABLE_ITEMS, values, KEY_ITEM_ID + " = ?",
				new String[] { String.valueOf(item.getID()) });
	}
	public void deleteItem(ListItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ITEMS, KEY_ITEM_ID + " = ?",
				new String[] { String.valueOf(item.getID()) });
		db.close();
	}
	
	// Categories
	public void addCategory(ListCategory category) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_CAT_NAME, category.getName());
		values.put(KEY_CAT_DESC, category.getDescription());
		values.put(KEY_CAT_TYPE, category.getType());
		values.put(KEY_CAT_VISIBLE, category.getVisible());
		
		Log.v("addCategory", "DatabaseHandler.category.getName: " + category.getName());
		
		db.insert(TABLE_CATEGORIES, null, values);
		db.close();
	}
	public ListCategory getCategory(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_CATEGORIES,  new String[] { KEY_CAT_ID,
				KEY_CAT_NAME, KEY_CAT_DESC, KEY_CAT_TYPE, KEY_CAT_VISIBLE }, KEY_CAT_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		
		ListCategory category = new ListCategory(cursor.getInt(0),
				cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
		// return category
		Log.v("DatabaseHandler.getCategory", "ID: " + category.getID() + " Name: " + category.getName());
		return category;
	}
	public ListCategory getCategoryByName(String catName) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_CATEGORIES,  new String[] { KEY_CAT_ID,
				KEY_CAT_NAME, KEY_CAT_DESC, KEY_CAT_TYPE, KEY_CAT_VISIBLE }, KEY_CAT_NAME + "=?",
				new String[] { catName }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		
		ListCategory category = new ListCategory(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)));
		// return category
		return category;
	}
	public CategoryType getCategoryType(int typeID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CATEGORY_TYPES, new String[] { KEY_TYPE_ID, KEY_TYPE_NAME, KEY_TYPE_DESC }, KEY_TYPE_ID
				+ "=?", new String[] { String.valueOf(typeID) }, null, null, null, null);
		CategoryType myCategoryType = new CategoryType(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
		return myCategoryType;
	}
	public ArrayList<ListCategory> getAllCategories() {
		ArrayList<ListCategory> categoryList = new ArrayList<ListCategory>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ListCategory category = new ListCategory();
				category.setID(Integer.parseInt(cursor.getString(0)));
				category.setName(cursor.getString(1));
				category.setDescription(cursor.getString(2));
				category.setType(Integer.parseInt(cursor.getString(3)));
				category.setVisible(Integer.parseInt(cursor.getString(4)));
				// Adding category to list
				categoryList.add(category);
			} while (cursor.moveToNext());
		}
		db.close();
		
		// return contact list
		return categoryList;
	}
	public ArrayList<ListCategory> getCategories() {
		ArrayList<ListCategory> categoryList = new ArrayList<ListCategory>();
		// Select All Query
		//String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES;
		String isVisible = "1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CATEGORIES,  new String[] { KEY_CAT_ID,
				KEY_CAT_NAME, KEY_CAT_DESC, KEY_CAT_TYPE, KEY_CAT_VISIBLE }, KEY_CAT_VISIBLE + "=?",
				new String[] { isVisible }, null, null, null, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ListCategory category = new ListCategory();
				category.setID(Integer.parseInt(cursor.getString(0)));
				category.setName(cursor.getString(1));
				category.setDescription(cursor.getString(2));
				category.setType(Integer.parseInt(cursor.getString(3)));
				category.setVisible(Integer.parseInt(cursor.getString(4)));
				// Adding category to list
				categoryList.add(category);
			} while (cursor.moveToNext());
		}
		
		// return contact list
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
		
		// return count
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
	public int updateCategory(ListCategory category) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_CAT_NAME, category.getName());
		values.put(KEY_CAT_DESC, category.getDescription().toString());
		values.put(KEY_CAT_TYPE, category.getType());
		values.put(KEY_CAT_VISIBLE, category.getVisible());
		String keyID = String.valueOf(category.getID());
		Log.v("updateCategory", "DatabaseHandler.updateCategory.keyID: " + keyID);
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
	
	
	// Types
	public ArrayList<CategoryType> getCategoryTypesList() {
		ArrayList<CategoryType> typeList = new ArrayList<CategoryType>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_CATEGORY_TYPES;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				CategoryType type = new CategoryType();
				type.setID(Integer.parseInt(cursor.getString(0)));
				type.setName(cursor.getString(1));
				type.setDescription(cursor.getString(2));

				// Adding type to list
				typeList.add(type);
			} while (cursor.moveToNext());
		}
		db.close();
		
		// return contact list
		return typeList;
	}
	public String getCategoryTypeName(int typeID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CATEGORY_TYPES, new String[] { KEY_TYPE_ID, KEY_TYPE_NAME, KEY_TYPE_DESC }, KEY_TYPE_ID
				+ "=?", new String[] { String.valueOf(typeID) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		String categoryTypeName = cursor.getString(1);
		return categoryTypeName;
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
