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
public class ListManager implements Parcelable, Serializable {
    ArrayList<String> categoryList = new ArrayList<String>();
    ArrayList<ListCategory> categoryObjList = new ArrayList<ListCategory>();
    ArrayList<ListItem> itemList = new ArrayList<ListItem>();
    Map<String,ItemList> itemListMap = new HashMap<String,ItemList>();
    //Map<String,ListCategory> categoryListMap = new HashMap<String,ListCategory>();
    // Do we need a item list map of categories so we can retrieve by id instead of hitting the DB each time?
    // How should we handle items that are on more than one list? Use foreach to add to each item list?
    String defaultCategory = "Default";
    String completedCategory = "Completed";
    String currentCategory;
    ItemList defaultList = new ItemList(defaultCategory);
    ItemList completedList = new ItemList(completedCategory);
    DatabaseHandler db;
    
    public ListManager(Context context) {
        itemListMap.put(defaultCategory, defaultList);
        categoryList.add(defaultCategory);
        itemListMap.put(completedCategory, completedList);
        categoryList.add(completedCategory);

        currentCategory = defaultCategory;
        db = new DatabaseHandler(context);
        
    }
    public ListCategory addCategory(String catName, String catDescription, int catTypeID) {
    	// Old
        //itemListMap.put(newCat, new ItemList(newCat));
        //categoryList.add(newCat);
        //ListCategory myCategory = new ListCategory(newCat, "default description", "default type");
    	ListCategory myNewListCategory = new ListCategory(catName, catDescription, catTypeID);
    	db.addCategory(myNewListCategory);
    	
        return myNewListCategory;
    }
    public void addObjCategory(ListCategory newCatObj) {
    	// New
    	String catName = newCatObj.getName();
    	categoryObjList.add(newCatObj);
    	itemListMap.put(catName, new ItemList(catName));
    }
    public ListItem addItem(int curCategory, String name, String description, Date dueDate) {
        ItemList myList = itemListMap.get(curCategory);
        ListItem myItem = new ListItem(curCategory, name, description, dueDate);
        db.addItem(myItem);
        myList.addListItem(myItem);
        Log.v("ListItem.addItem", "Added Item: " + myItem.getName() + " To Category: " + db.getCategory(curCategory));
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
    public void completeItem(ListItem myItem, String fromList) {
        ItemList myFromList = itemListMap.get(fromList);
        ItemList myToList = itemListMap.get("Completed");
        myFromList.removeListItem(myItem);
        if (fromList != "Completed") {
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
    public ArrayList<ListItem> getItemList(int catID) throws NumberFormatException, ParseException {
        //ItemList myItemList = itemListMap.get(myCat);
        //ArrayList<ListItem> myItemArrayList = myItemList.getListItems();
    	// ** FIgure out why this is breaking below, should i really be using the id to get the cat? **

    	ArrayList<ListItem> myItemArrayList = (ArrayList<ListItem>) db.getAllItemsFromCategory(catID);
        return myItemArrayList;
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
