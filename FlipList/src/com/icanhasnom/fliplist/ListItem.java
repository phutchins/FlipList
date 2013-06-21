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
    public boolean hasDueDateBool = false;
    public String notes;
    
    //java.text.DateFormat df = new SimpleDateFormat("MM/dd/yyy");
    
    public ListItem() {
    }
    public ListItem(int cat, String n, String desc, String crDate, String due) throws ParseException {
    	name = n;
    	description = desc;
    	Log.v("ListItem", "1) due: " + due + " hasDueDateBool: " + hasDueDateBool);
        if (due != "") {
        	dueDate = due;
        	hasDueDateBool = true;
        }
        //stringCatsToList(cats);
    	primaryCat = cat;
    	// TODO: Make this use crDate from DB if its not null (after I fix the due date being empty crashing problem)
        createDate = new Date().toString();
    	Log.v("ListItem", "1) due: " + due + " hasDueDateBool: " + hasDueDateBool);
    }
    public ListItem(int cat, String n, String desc, String due) {
    	name = n;
    	Log.v("ListItem Constructor", "Name: " + n);
    	Log.v("ListItem Constructor", "Description: " + desc);
    	//Log.v("ListItem Constructor", "Due Date: " + due);
    	Log.v("ListItem Constructor", "Category is: " + cat);
        //stringCatsToList(cats);
    	primaryCat = cat;
    	Log.v("ListItem", "1) due: " + due + " hasDueDateBool: " + hasDueDateBool);

    	if (desc != null) {
    		description = desc;
    	} else {
    		description = "";
    	}
        if (due != "") {
        	hasDueDateBool = true;
        	dueDate = due;
        } else {
        	dueDate = "";
        }
    	createDate = new Date().toString();
    	Log.v("ListItem", "2) due: " + due + " hasDueDateBool: " + hasDueDateBool);
    }
    public ListItem(int cat, String n, String desc) {
    	name = n;
    	primaryCat = cat;
    	if (desc != null) {
    		description = desc;
    	} else {
    		description = "";
    	}
    	createDate = new Date().toString();
    }
    public ListItem(int id, String cats, String n, String desc, String crDate, String due) throws ParseException {
    	itemID = id;
    	name = n;
        description = desc;
    	Log.v("ListItem", "1) due: " + due + " hasDueDateBool: " + hasDueDateBool);

        if (due != "") {
            dueDate = due;
        	hasDueDateBool = true;
        } else {
        	dueDate = "";
        }
        addToCats(cats);
        createDate = crDate;
    	Log.v("ListItem", "3) due: " + due + " hasDueDateBool: " + hasDueDateBool);
    }
    public String getDueDate() {
    	return dueDate;
    }
    public String getDueDatePretty() {
    	return dateToStringPretty(stringToDate(dueDate));
    }
    public Date getDueDateObj() {
    	return stringToDate(dueDate);
    }
    public void setDueDate(String dd) {
    	if (dd != null) {
    		dueDate = dd;
    		hasDueDateBool = true;
    	} else {
    		dueDate = "";
    	}
    }
    public void removeDueDate() {
    	dueDate = null;
    	hasDueDateBool = false;
    }
    public boolean hasDueDate() {
    	if(dueDate != null && dueDate.length() == 0) {
    		hasDueDateBool = false;
    	} else {
    		hasDueDateBool = true;
    	}
    	//hasDueDateBool = dueDate.isEmpty();
    	return hasDueDateBool;
    }
    public void setCreateDate(String cd) {
    	// Do some checking here?
    	createDate = cd;
    }
    public String dateToStringPretty(Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, HH:mm aa");
    	String dateStr = sdf.format(date);
    	return dateStr;
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
    public String getNotes() {
    	return notes;
    }
    public void setNotes(String n) {
    	notes = n;
    }
}
