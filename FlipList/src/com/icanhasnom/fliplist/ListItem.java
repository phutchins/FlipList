/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icanhasnom.fliplist;

/**
 *
 * @author flip
 */
import java.util.*;
import java.text.*;
import java.io.*;

import android.util.Log;

public class ListItem implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int itemID;
	public String name;
	public String description;
    public String dueDate;
    public String createDate;
    public int primaryCat;
    public List<String> secondaryCats = new ArrayList<String>();
    public boolean isSelected;
    
    //java.text.DateFormat df = new SimpleDateFormat("MM/dd/yyy");
    
    public ListItem() {
    }
    public ListItem(int cat, String n, String desc, String crDate, String due) throws ParseException {
    	name = n;
    	description = desc;
    	dueDate = due;
        //stringCatsToList(cats);
    	primaryCat = cat;
        createDate = new Date().toString();
    }
    public ListItem(int cat, String n, String desc, String due) {
    	name = n;
    	Log.v("ListItem Constructor", "Name: " + n);
    	Log.v("ListItem Constructor", "Description: " + desc);
    	Log.v("ListItem Constructor", "Due Date: " + due);
    	Log.v("ListItem Constructor", "Category is: " + cat);
        //stringCatsToList(cats);
    	primaryCat = cat;
    	if (desc != null) {
    		description = desc;
    	} else {
    		description = "";
    	}
    	dueDate = due;
    	createDate = new Date().toString();
    }
    public ListItem(int id, String cats, String n, String desc, String crDate, String due) throws ParseException {
    	itemID = id;
    	name = n;
        description = desc;
        dueDate = due;
        addToCats(cats);
        createDate = crDate;
    }
    public String getDueDate() {
    	return dueDate;
    }
    public Date getDueDateObj() {
    	return stringToDate(dueDate);
    }
    public void setDueDate(String dd) {
    	dueDate = dd;
    }
    public void setCreateDate(String cd) {
    	// Do some checking here?
    	createDate = cd;
    }
    public String dateToString(Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String dateStr = sdf.format(date);
    	return dateStr;
    }
    public Date stringToDate(String dateStr) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return date;
    }
    public void setDescription(String d) {
        description = d;
    }
    public void setName(String n) {
    	name = n;
    }
    public String getName() {
    	return name;
    }
    public void setID(int id) {
    	itemID = id;
    }
    public int getID() {
    	return itemID;
    }
    public String getCreateDate() {
        return createDate;
    }
    public int getPrimaryCat() {
    	return primaryCat;
    }
    public List<String> getSecondaryCats() {
        return secondaryCats;
    }
    public String getSecondaryCatsString() {
    	String secondaryCatsString = secondaryCats.toArray().toString();
    	return secondaryCatsString;
    }
    public void addToCats(String cats) {
    	// TODO: Make this check for duplicates
    	String[] catList = cats.split(",");
        int catListSize = catList.length;
        for (int i = 0; i < catListSize; i++) {
        	secondaryCats.add(catList[i]);
        }
    }
    public void addSecondaryCat(String cat) {
    	secondaryCats.add(cat);
    }
    public void setPrimaryCat(int cat) {
    	primaryCat = cat;
    }
    public void rmCategory(String cat) {
    	secondaryCats.remove(cat);
    }
    public String getDescription() {
        return description;
    }
}
