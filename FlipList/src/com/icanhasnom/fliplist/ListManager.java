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

/**
 *
 * @author flip
 */
public class ListManager implements Parcelable, Serializable {
    ArrayList<String> categoryList = new ArrayList<String>();
    ArrayList<ListCategory> categoryObjList = new ArrayList<ListCategory>();
    ArrayList<ListItem> itemList = new ArrayList<ListItem>();
    Map<String,ItemList> itemListMap = new HashMap<String,ItemList>();
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
    public ArrayList<ListCategory> getCategoryObjList() {
    	return categoryObjList;
    }
    public ListItem addItem(String curCategory, String name, String description, Date dueDate) {
        ItemList myList = itemListMap.get(curCategory);
        ListItem myItem = new ListItem(curCategory, name, description, dueDate);
        
        myList.addListItem(myItem);
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
    public ArrayList<ListItem> getItemList(String myCat) throws NumberFormatException, ParseException {
        //ItemList myItemList = itemListMap.get(myCat);
        //ArrayList<ListItem> myItemArrayList = myItemList.getListItems();
    	// ** FIgure out why this is breaking below, should i really be using the id to get the cat? **
    	int catID = db.getCategoryID(myCat);
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
