/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icanhasnom.fliplist;

import java.util.*;
import java.io.*;

/**
 *
 * @author flip
 */
public class ListManager implements Serializable {
    ArrayList<String> categoryList = new ArrayList<String>();
    ArrayList<ListItem> itemList = new ArrayList<ListItem>();
    Map<String,ItemList> itemListMap = new HashMap<String,ItemList>();
    String defaultCategory = "Default";
    String completedCategory = "Completed";
    String currentCategory;
    ItemList defaultList = new ItemList(defaultCategory);
    ItemList completedList = new ItemList(completedCategory);
    
    public ListManager() {
        itemListMap.put(defaultCategory, defaultList);
        categoryList.add(defaultCategory);
        itemListMap.put(completedCategory, completedList);
        categoryList.add(completedCategory);
        currentCategory = defaultCategory;
    }
    public void addCategory(String newCat) {
        itemListMap.put(newCat, new ItemList(newCat));
        categoryList.add(newCat);
    }
    public void addItem(String curCategory, String description, Date dueDate) {
        ItemList myList = itemListMap.get(curCategory);
        ListItem myItem = new ListItem(curCategory, description, dueDate);
        myList.addListItem(myItem);
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
    public String[] getCategoryList() {
        String[] retCatList = new String[categoryList.size()];
        retCatList = categoryList.toArray(retCatList);
        return retCatList;
    }
    public ArrayList<ListItem> getItemList(String myCat) {
        ItemList myItemList = itemListMap.get(myCat);
        ArrayList<ListItem> myItemArrayList = myItemList.getListItems();
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
}
