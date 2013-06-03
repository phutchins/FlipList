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
import android.os.Parcelable;
import android.util.Log;

/**
 *
 * @author flip
 */
public class ListManager implements Serializable {
    ArrayList<String> categoryList = new ArrayList<String>();
    ArrayList<ListCategory> categoryObjList = new ArrayList<ListCategory>();
    ArrayList<ListItem> itemList = new ArrayList<ListItem>();
    Map<Integer, ItemList> itemListMap = new HashMap<Integer, ItemList>();
    //Map<String,ListCategory> categoryListMap = new HashMap<String,ListCategory>();
    // Do we need a item list map of categories so we can retrieve by id instead of hitting the DB each time?
    // How should we handle items that are on more than one list? Use foreach to add to each item list?
    String defaultCategory = "Default";
    int defaultCategoryID = 1;
    String completedCategory = "Completed";
    int completedCategoryID = 2;
    String currentCategory;
    ItemList defaultList = new ItemList(defaultCategory);
    ItemList completedList = new ItemList(completedCategory);
    DatabaseHandler db;
    
    public ListManager(Context context) {
        itemListMap.put(defaultCategoryID, defaultList);
        categoryList.add(defaultCategory);
        itemListMap.put(completedCategoryID, completedList);
        categoryList.add(completedCategory);

        currentCategory = defaultCategory;
        db = new DatabaseHandler(context);
        
    }
    public ListCategory addCategory(String catName, String catDescription, int catTypeID) {
    	// Old
        //categoryList.add(newCat);
        //ListCategory myCategory = new ListCategory(newCat, "default description", "default type");
    	ListCategory myNewListCategory = new ListCategory(catName, catDescription, catTypeID);
    	
    	// Have db.addCategory return ID of new category
    	db.addCategory(myNewListCategory);
    	int catID = db.getCategoryID(catName);
    	Log.v("addCategory", "catID: " + catID);
    	Log.v("addCategory", "catName: " + catName);
    	// Add new category to itemListMap with key of id and value of category Name
    	itemListMap.put(catID, new ItemList(catName));
    	
        return myNewListCategory;
    }
    public void addObjCategory(ListCategory newCatObj) {
    	// New
    	String catName = newCatObj.getName();
    	categoryObjList.add(newCatObj);
    	//itemListMap.put(catName, new ItemList(catName));
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
    public ArrayList<ListCategory> getCategoryList() {
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
    public String getCurrentCategory() {
    	return currentCategory;
    }
    public void setCurrentCategory(String curCat) {
    	currentCategory = curCat;
    }
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
