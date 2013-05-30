package com.icanhasnom.fliplist;

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

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
	
	// Database Name
	private static final String DATABASE_NAME = "listManager";
	
	// Categories Table
	private static final String TABLE_CATEGORIES = "categories";
	// Categories Table Column Names
	private static final String KEY_CAT_ID = "id";
	private static final String KEY_CAT_NAME = "name";
	private static final String KEY_CAT_DESC = "desc";
	private static final String KEY_CAT_TYPE = "type";
	
	// Items Table
	private static final String TABLE_ITEMS = "items";
	// Items Table Columns Names
	private static final String KEY_ITEM_ID = "id";
	private static final String KEY_ITEM_NAME = "name";
	private static final String KEY_ITEM_DESC = "desc";
	private static final String KEY_ITEM_CATS = "cats";
	private static final String KEY_ITEM_DUE_DATE = "due_date";
	private static final String KEY_ITEM_CREATE_DATE = "create_date";
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "(" 
				+ KEY_CAT_ID + " INTEGER PRIMARY KEY," + KEY_CAT_NAME + " TEXT," 
				+ KEY_CAT_DESC + " TEXT," + KEY_CAT_TYPE + " TEXT" + ")";
		String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
				+ KEY_ITEM_ID + " INTEGER PRIMARY KEY,"
				+ KEY_ITEM_DESC + " TEXT," + KEY_ITEM_CATS + " TEXT,"
				+ KEY_ITEM_DUE_DATE + " NUMERIC," + KEY_ITEM_CREATE_DATE 
				+ " NUMERIC" + ")";
		
		db.execSQL(CREATE_CATEGORIES_TABLE);
		db.execSQL(CREATE_ITEMS_TABLE);
	}
		
	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE  IF EXISTS " + TABLE_CATEGORIES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
			
		onCreate(db);
	}
	public void addCategory(ListCategory category) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_CAT_NAME, category.getName());
		values.put(KEY_CAT_DESC, category.getDescription());
		values.put(KEY_CAT_TYPE, category.getType());
		
		db.insert(TABLE_CATEGORIES, null, values);
		db.close();
	}
	public void addItem(ListItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_ITEM_DESC, item.getDescription());
		values.put(KEY_ITEM_CATS, item.getCategories().toString());
		values.put(KEY_ITEM_CREATE_DATE, item.getCreateDate().toString());
		values.put(KEY_ITEM_DUE_DATE, item.getDueDate());
		
		db.insert(TABLE_ITEMS, null, values);
		db.close();
	}
	public ListCategory getCategory(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_CATEGORIES,  new String[] { KEY_CAT_ID,
				KEY_CAT_NAME, KEY_CAT_DESC, KEY_CAT_TYPE }, KEY_CAT_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		
		ListCategory category = new ListCategory(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3));
		// return category
		return category;
	}
	public ListItem getItem(int id) throws NumberFormatException, ParseException {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_ITEMS, new String[] { KEY_ITEM_ID,
				KEY_ITEM_DESC, KEY_ITEM_CATS, KEY_ITEM_CREATE_DATE, KEY_ITEM_DUE_DATE }, KEY_ITEM_ID
				+ "?", new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		
		ListItem item = new ListItem(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)),
				Integer.parseInt(cursor.getString(4)));
		// return item
		return item;
	}
	public List<ListCategory> getAllCategories() {
		List<ListCategory> categoryList = new ArrayList<ListCategory>();
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
				category.setType(cursor.getString(3));
				// Adding category to list
				categoryList.add(category);
			} while (cursor.moveToNext());
		}
		db.close();
		
		// return contact list
		return categoryList;
	}
	public List<ListItem> getAllItems() throws NumberFormatException, ParseException {
		List<ListItem> itemList = new ArrayList<ListItem>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_ITEMS;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ListItem item = new ListItem();
				item.setID(Integer.parseInt(cursor.getString(0)));
				item.setDescription(cursor.getString(1));
				item.addCategory(cursor.getString(2));
				item.setCreateDate(Integer.parseInt(cursor.getString(3)));
				item.setDueDate(Integer.parseInt(cursor.getString(4)));
				// Adding category to list
				itemList.add(item);
			} while (cursor.moveToNext());
		}
		db.close();
		
		// return contact list
		return itemList;
	}
	public List<ListItem> getAllItemsFromCategory(int catID) throws NumberFormatException, ParseException {
		List<ListItem> itemList = new ArrayList<ListItem>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_ITEMS + " WHERE " + KEY_ITEM_CATS + " = " + catID;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ListItem item = new ListItem();
				item.setID(Integer.parseInt(cursor.getString(0)));
				item.setDescription(cursor.getString(1));
				item.addCategory(cursor.getString(2));
				item.setCreateDate(Integer.parseInt(cursor.getString(3)));
				item.setDueDate(Integer.parseInt(cursor.getString(4)));
				// Adding category to list
				itemList.add(item);
			} while (cursor.moveToNext());
		}
		db.close();
		
		// return contact list
		return itemList;
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
	
	//Cursor cursor = db.query(TABLE_ITEMS, new String[] { KEY_ITEM_ID,
	//		KEY_ITEM_DESC, KEY_ITEM_CATS, KEY_ITEM_CREATE_DATE, KEY_ITEM_DUE_DATE }, KEY_ITEM_ID
	//		+ "?", new String[] { String.valueOf(id) }, null, null, null, null);
	
	
	public int getCategoryID(String catName) {
		// Fix this DB query to pull cat id using the name
		String catIdQuery = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + KEY_CAT_NAME + " = ?";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(catIdQuery, new String[] { catName });
		String catID = "0";
		if (cursor != null)
			cursor.moveToFirst();
		try {
			catID = cursor.getString(0);
		} catch (CursorIndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cursor.close();
		
		// return count
		return Integer.parseInt(catID);
	}
	public int getItemsCount() {
		String countQuery = "SELECT * FROM " + TABLE_ITEMS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		
		// return count
		return cursor.getCount();
	}
	public int updateItem(ListItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_ITEM_DESC, item.getDescription());
		values.put(KEY_ITEM_CATS, item.getCategories().toString());
		values.put(KEY_ITEM_DUE_DATE, item.getDueDate());
		
		// updating row
		return db.update(TABLE_ITEMS, values, KEY_ITEM_ID + " = ?",
				new String[] { String.valueOf(item.getID()) });
		
	}
	public int updateCategory(ListCategory category) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_CAT_NAME, category.getName());
		values.put(KEY_CAT_DESC, category.getDescription().toString());
		values.put(KEY_CAT_TYPE, category.getType());
		
		// updating row
		return db.update(TABLE_ITEMS, values, KEY_CAT_ID + " = ?",
				new String[] { String.valueOf(category.getID()) });
	}
	public void deleteItem(ListItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ITEMS, KEY_ITEM_ID + " = ?",
				new String[] { String.valueOf(item.getID()) });
		db.close();
	}
	public void deleteCategory(ListCategory category) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CATEGORIES, KEY_CAT_ID + " = ?",
				new String[] { String.valueOf(category.getID()) });
		db.close();
	}
}
