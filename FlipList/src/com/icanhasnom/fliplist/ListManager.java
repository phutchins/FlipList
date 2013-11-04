/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icanhasnom.fliplist;

import java.util.*;
import java.io.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 *
 * @author flip
 */
public class ListManager implements Serializable {
	private static final long serialVersionUID = 1L;
	ArrayList<Flist> flistList = new ArrayList<Flist>();
    ItemList itemList = null;
    Map<Integer, ItemList> itemListMap = new HashMap<Integer, ItemList>();
    //Map<String,ListCategory> categoryListMap = new HashMap<String,ListCategory>();
    // Do we need a item list map of categories so we can retrieve by id instead of hitting the DB each time?
    // How should we handle items that are on more than one list? Use foreach to add to each item list?
    
    // Get these from the settings table in the DB
    
    public int defaultTypeID = 0;
    public String defaultType = "Generic";
	public int defaultCategoryID = 0;
    //public int completedCatID = 1;
    public int archiveCategory = 1;
    public Flist currentCategory;
    
    public transient DatabaseHandler db;
    public ListManager(Context context) {
    	db = new DatabaseHandler(context);
		Log.v("ListManager.constructor", "3) context: " + context);
		updateListManagerState();
    }
    public void updateListManagerState() {
    	// TODO: Use this to replace the below calls everywhere in here
    	populateFlistList();
    	buildItemListMap();
    }
    public void updateItem(Item item) {
    	List<String> myCategories = new ArrayList<String>();
    	if (item.getCategoriesString() == null) {
    		myCategories.add("0");
    	}
    	db.updateItem(item);
    }
    public void deleteItem(Item item) {
    	db.deleteItem(item);
    }
    public void populateFlistList() {
    	flistList = db.getFlists();
    }
    public void buildItemListMap() {
		itemListMap.clear();
    	ItemList myItemList = null;
    	for(int i = 0; i < flistList.size(); i++) {
    		Flist curFlist = (Flist) flistList.get(i);
    		int curFlistID = curFlist.getID();
			myItemList = db.getItemList(curFlistID);
			myItemList.setName(curFlist.getName());
			myItemList.setID(curFlist.getID());
    		itemListMap.put(curFlistID, myItemList);
    	}	
    }
    public void populateItemList(int catID) {
    	itemList = db.getItemList(catID);
    }
    
    // Adding Objects
    //public ListCategory addCategory(String catName, String catDescription, int catTypeID) {
    	// Old
        //categoryList.add(newCat);
        //ListCategory myCategory = new ListCategory(newCat, "default description", "default type");
    //	ListCategory myNewListCategory = new ListCategory(catName, catDescription, catTypeID);
    	
    	// TODO: Have db.addCategory return ID of new category
    //	db.addCategory(myNewListCategory);
    //	int catID = db.getCategoryID(catName);
    //	Log.v("addCategory", "catID: " + catID);
    //	Log.v("addCategory", "catName: " + catName);
    	// Add new category to itemListMap with key of id and value of category Name
    	// TODO: this is broken, need to rebuild itemListMap
    //	buildItemListMap();
    	
    //    return myNewListCategory;
    //}
    public int addFlist(Flist flist) {
    	// TODO: Merge updateObjCategory into this? Maybe make addUpdateObjCategory() which does 
    	//       a check to see if it exists and updates it if it already exists and adds it if
    	//       it doesn't.
    	Log.v("ListManager.addFlist", "Adding Flist: " + flist.getName());
    	Integer newFlistID = db.addFlist(flist);
    	buildItemListMap();
    	populateFlistList();
    	return newFlistID;
    }
	public Item addItem(int flistID, String name) {
		Item myItem = new Item(flistID, name);
		
		List<String> myCategories = new ArrayList<String>();
		Flist myFlist = getFlist(flistID);
		myCategories.add(myFlist.getDefaultCategoryIDString());
		
		myItem.setCategories(myCategories);
		db.addItem(myItem);
		buildItemListMap();
		return myItem;
	}
    public Item addItem(int curFlist, String name, String description, String dueDate) {
        Item myItem = new Item(curFlist, name, description, dueDate);
        db.addItem(myItem);
        buildItemListMap();
        return myItem;
    }
    public void addType(ItemType newCatType) {
    	// Check if type exists
    	// Add type to db if doesn't exist
    	// Add type to typeList
    }

    
    // Updating Objects
    public void updateObjFlist(Flist flistToUpdate) {
    	// update category in DB here & update category list (or pull from DB and refresh)
    	//int catID = catToUpdate.getID();
    	//String catName = catToUpdate.getName();
    	db.updateFlist(flistToUpdate);
    	//itemListMap.put(catID, new ItemList(catName));
    }
    public void moveItem(Item myItem, String fromList, String toList) {
        ItemList myFromList = itemListMap.get(fromList);
        ItemList myToList = itemListMap.get(toList);
        myFromList.removeListItem(myItem);
        if (fromList != "Completed") {
            myToList.addListItem(myItem);
        }
    }
    public void completeItem(Item myItem) {
        myItem.setCompleted(true);
        db.updateItem(myItem);
    }
    public void unCompleteItem(Item myItem) {
        myItem.setCompleted(false);
        db.updateItem(myItem);
    }
    
    // Removing Objects
    public boolean rmFlist(int catID) {
    	// TODO: Make this remove all items in category or move them to some other list
    	//       or warn user that all items on the list will be deleted, or give the option
    	//       to delete them all or move them to a different list
    	boolean success;
    	success = db.deleteFlist(catID);
    	return success;
    }
    
    // Retrieving Lists
    //public ArrayList<Flist> getCategories() {
    	// TODO: Add a variable to save the category list
    	//       check if the catlist exists (maybe see if its been updated recently?)
    	//       and populate from the DB if it doesn't exist or has been updated
    	//       Create Variable to set in in the ListManager that is true if a certain datatype
    	//       has been updated, then update the list and set the variable to false
    //	ArrayList<Flist> retCatListObjs = db.getCategories();
    //    return retCatListObjs;
    //}
    /*
    public ArrayList<Flist> getLists() {
    	ArrayList<Flist> retCatListObjs = db.getLists();
        return retCatListObjs;
    }
    */
    /*
    public ArrayList<Flist> getCategoryListAll() {
    	ArrayList<Flist> retCatListObjs = db.getCategories();
        return retCatListObjs;
    }
    */
    public ArrayList<Flist> getFlists() {
    	ArrayList<Flist> retFlistListObjs = db.getFlists();
        return retFlistListObjs;
    }
    public ArrayList<Category> getCategories() {
    	ArrayList<Category> retCategoryListObjs = db.getCategories();
        return retCategoryListObjs;
    }
    public ArrayList<Filter> getFilters() {
    	ArrayList<Filter> retFilterListObjs = db.getFilters();
        return retFilterListObjs;
    }
    /*
    public String[] getCategoryListStrings() {
    	ArrayList<Flist> retCatList = db.getCategories();
    	String[] catListStrings = new String[retCatList.size()];
    	int index = 0;
    	for (Flist value : retCatList) {
    		catListStrings[index] = (String) value.getName();
    		index++;
    	}
    	return catListStrings;
    }
    */
    public ItemList getItemList(int catID) {
    	ItemList myItemList;
    	updateListManagerState();
    	myItemList = itemListMap.get(catID);
        return myItemList;
    }
    public String[] getItemListArray(String myCat) {
        ItemList myItemList = itemListMap.get(myCat);
        ArrayList<Item> myItemArrayList = myItemList.getListItems();
        String[] myItemArray = new String[myItemArrayList.size()];
        myItemArray = myItemArrayList.toArray(myItemArray);
        return myItemArray;
    }
    public ArrayList<ItemType> getTypes() {
    	ArrayList<ItemType> categoryTypesList = db.getItemTypeList();
    	return categoryTypesList;
    }
    //public ArrayList<Filter> getFilters() {
    //	ArrayList<Filter> filterList = db.getFilters();
    //	return filterList;
    //}

    // Retrieving Objects
    public ItemType getType(int typeID) {
    	ItemType catType = db.getItemType(typeID);
    	return catType;
    }
    public Flist getFlist(int flistID) {
    	Flist myFlist = db.getFlist(flistID);
    	return myFlist;
    }
    public Category getCategory(int categoryID) {
    	Category myCategory = db.getCategory(categoryID);
    	return myCategory;
    }
    public String getTypeName(int typeID) {
    	String typeName = db.getItemTypeName(typeID);
    	return typeName;
    }
    public String getFlistName(int flistID) {
    	Flist flist = db.getFlist(flistID);
    	return flist.getName();
    }
    


}
