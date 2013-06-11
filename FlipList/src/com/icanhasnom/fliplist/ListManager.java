/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icanhasnom.fliplist;

import java.text.ParseException;
import java.util.*;
import java.io.*;

import android.content.Context;
import android.os.Parcel;
import android.util.Log;

/**
 *
 * @author flip
 */
public class ListManager implements Serializable {
//public class ListManager {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// TODO: Create settings ArrayList and populate from DB (maybe in constructor?)
	ArrayList<String> categoryList = new ArrayList<String>();
    ArrayList<ListCategory> categoryObjList = new ArrayList<ListCategory>();
    ArrayList<ListItem> itemList = new ArrayList<ListItem>();
    Map<Integer, ItemList> itemListMap = new HashMap<Integer, ItemList>();
    //Map<String,ListCategory> categoryListMap = new HashMap<String,ListCategory>();
    // Do we need a item list map of categories so we can retrieve by id instead of hitting the DB each time?
    // How should we handle items that are on more than one list? Use foreach to add to each item list?
    
    // Get these from the settings table in the DB
    //public String defaultCategory = "Default";
    //public int defaultCategoryID = 0;
    //public String completedCategory = "Completed";
    //public int completedCategoryID = 1;
    public int defaultTypeID = 0;
    public String defaultType = "Generic";
    
    // TODO: Still using these?
    //ItemList defaultList = new ItemList(defaultCategory);
    //ItemList completedList = new ItemList(completedCategory);
    
    DatabaseHandler db;
    
    // TODO: Do we use string currentCategory? Should this be changed to an object?
    //       obj might be useful if we're passing around the reference to myListMan
    ListCategory currentCategoryObj;
    String currentCategory;
    
    public ListManager(Context context) {
        //itemListMap.put(defaultCategoryID, defaultList);
        //categoryList.add(defaultCategory);
        //itemListMap.put(completedCategoryID, completedList);
        //categoryList.add(completedCategory);
    	
    	db = new DatabaseHandler(context);
    	
    	int currentCategoryID = db.getDefaultCatID();
    	Log.v("ListManager", "constructor db.getDefaultCatID: " + currentCategoryID);
    	currentCategoryObj = db.getCategory(currentCategoryID);
    	currentCategory = currentCategoryObj.getName();
    	
    	buildItemListMap();
    }
    public void buildItemListMap() {
    	ArrayList<ListCategory> categoryList = db.getAllCategories();
    	for(int i = 0; i < categoryList.size(); i++) {
    		itemListMap.clear();
    		ListCategory curCat = (ListCategory) categoryList.get(i);
    		int curCatID = curCat.getID();
    		ItemList curList = new ItemList(curCat.getName());
    		itemListMap.put(curCatID, curList);
    	}	
    }
    
    // Adding Objects
    public ListCategory addCategory(String catName, String catDescription, int catTypeID) {
    	// Old
        //categoryList.add(newCat);
        //ListCategory myCategory = new ListCategory(newCat, "default description", "default type");
    	ListCategory myNewListCategory = new ListCategory(catName, catDescription, catTypeID);
    	
    	// TODO: Have db.addCategory return ID of new category
    	db.addCategory(myNewListCategory);
    	int catID = db.getCategoryID(catName);
    	Log.v("addCategory", "catID: " + catID);
    	Log.v("addCategory", "catName: " + catName);
    	// Add new category to itemListMap with key of id and value of category Name
    	// TODO: this is broken, need to rebuild itemListMap
    	buildItemListMap();
    	
        return myNewListCategory;
    }
    public void addObjCategory(ListCategory newCatObj) {
    	// TODO: Merge updateObjCategory into this? Maybe make addUpdateObjCategory() which does 
    	//       a check to see if it exists and updates it if it already exists and adds it if
    	//       it doesn't.
    	String catName = newCatObj.getName();
    	int catID = newCatObj.getID();
    	categoryObjList.add(newCatObj);
    	itemListMap.put(catID, new ItemList(catName));
    }
    public ListItem addItem(int curCategory, String name, String description, Date dueDate) {
        ItemList myList = itemListMap.get(curCategory);
        ListItem myItem = new ListItem(curCategory, name, description, dueDate);
        db.addItem(myItem);
        Log.v("ListManager.addItem", "name: " + myItem.getName());
        Log.v("ListManager.addItem", "curCategory: " + curCategory);
        // ItemList curCatList = itemListMap.get(curCategory);
        
        Log.v("ListManager.addItem", "itemListMap.get(curCategory): " + myList.listName);
        myList.addListItem(myItem);
        Log.v("ListManager.addItem", "Added Item: " + myItem.getName() + " To Category: " + db.getCategory(curCategory).getName());
        return myItem;
    }
    public void addCategoryType(CategoryType newCatType) {
    	// Check if type exists
    	// Add type to db if doesn't exist
    	// Add type to typeList
    }

    
    // Updating Objects
    public void updateObjCategory(ListCategory catToUpdate) {
    	// update category in DB here & update category list (or pull from DB and refresh)
    	int catID = catToUpdate.getID();
    	String catName = catToUpdate.getName();
    	db.updateCategory(catToUpdate);
    	itemListMap.put(catID, new ItemList(catName));
    }
    public void moveItem(ListItem myItem, String fromList, String toList) {
        ItemList myFromList = itemListMap.get(fromList);
        ItemList myToList = itemListMap.get(toList);
        myFromList.removeListItem(myItem);
        if (fromList != "Completed") {
            myToList.addListItem(myItem);
        }
    }
    public void completeItem(ListItem myItem, int fromList) {
        ItemList myFromList = itemListMap.get(fromList);
        // TODO: Put this into settings and make a settings class to cache settings
        int completedListID = 2;
        ItemList myToList = itemListMap.get(completedListID);
        Log.v("ListManager.completeItem", "myFromList Name: " + myFromList.getName());
        Log.v("ListManager.completeItem", "myToList Name: " + myToList.getName());
        myFromList.removeListItem(myItem);
        if (fromList != completedListID) {
            myToList.addListItem(myItem);
        }
    }
    
    // Retrieving Lists
    public ArrayList<ListCategory> getCategoryList() {
    	// TODO: Add a variable to save the category list
    	//       check if the catlist exists (maybe see if its been updated recently?)
    	//       and populate from the DB if it doesn't exist or has been updated
    	//       Create Variable to set in in the ListManager that is true if a certain datatype
    	//       has been updated, then update the list and set the variable to false
    	ArrayList<ListCategory> retCatListObjs = db.getAllCategories();
        return retCatListObjs;
    }
    public String[] getCategoryListStrings() {
    	ArrayList<ListCategory> retCatList = db.getAllCategories();
    	String[] catListStrings = new String[retCatList.size()];
    	int index = 0;
    	for (ListCategory value : retCatList) {
    		catListStrings[index] = (String) value.getName();
    		index++;
    	}
    	return catListStrings;
    }
    public ItemList getItemList(int catID) throws NumberFormatException, ParseException {
    	ItemList myItemList;
        if (itemListMap.containsKey(catID)) {
        	myItemList = itemListMap.get(catID);
        } else {
        	ArrayList<ListItem> myItemArrayList = (ArrayList<ListItem>) db.getAllItemsFromCategory(catID);
        	myItemList = new ItemList(catID);
        	itemListMap.put(catID, myItemList);
        }
        //ArrayList<ListItem> myItemArrayList = myItemList.getListItems();
    	// ** FIgure out why this is breaking below, should i really be using the id to get the cat? **
    	
        return myItemList;
    }
    public String[] getItemListArray(String myCat) {
        ItemList myItemList = itemListMap.get(myCat);
        ArrayList<ListItem> myItemArrayList = myItemList.getListItems();
        String[] myItemArray = new String[myItemArrayList.size()];
        myItemArray = myItemArrayList.toArray(myItemArray);
        return myItemArray;
    }
    public ArrayList<CategoryType> getCategoryTypesList() {
    	ArrayList<CategoryType> categoryTypesList = db.getCategoryTypesList();
    	return categoryTypesList;
    }

    // Retrieving Objects
    public CategoryType getCategoryType(int typeID) {
    	CategoryType catType = db.getCategoryType(typeID);
    	return catType;
    }
    
    // Get/Set Primitives
    public String getCurrentCategory() {
    	return currentCategory;
    }
    public void setCurrentCategory(String curCat) {
    	currentCategory = curCat;
    }
    public String getCategoryTypeName(int typeID) {
    	String typeName = db.getCategoryTypeName(typeID);
    	return typeName;
    }
    
    // Do we need these? Don't think so if we're not doing parcelable any more
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
