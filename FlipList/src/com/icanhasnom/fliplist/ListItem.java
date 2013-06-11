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
    public Date dueDate;
    public ArrayList<String> categories = new ArrayList<String>();
    public Date createDate;
    public boolean isSelected;
    
    java.text.DateFormat df = new SimpleDateFormat("MM/dd/yyy");
    
    public ListItem() {
    }
    public ListItem(String cat, String n, String desc, int crDate, int due) throws ParseException {
    	name = n;
    	description = desc;
        String dueDateString = "" + due;
        dueDate = df.parse(dueDateString);
        categories.add(cat);
        createDate = new Date();
    }
    public ListItem(int cat, String n, String desc, Date due) {
    	name = n;
    	Log.v("ListItem Constructor", "Category: " + cat);
    	Log.v("ListItem Constructor", "Name: " + n);
    	Log.v("ListItem Constructor", "Description: " + desc);
    	Log.v("ListItem Constructor", "Due Date: " + due);
    	String catString = cat + "";
    	Log.v("ListItem Constructor", "Category is: " + catString);
    	//categories.add(catString);
    	categories.add("1");
    	if (desc != null) {
    		description = desc;
    	} else {
    		description = "";
    	}
    	dueDate = due;
    	createDate = new Date();
    }
    public ListItem(int id, String cat, String n, String desc, int crDate, int due) throws ParseException {
    	itemID = id;
    	name = n;
        description = desc;
        String dueDateString = "" + due;
        dueDate = df.parse(dueDateString);
        categories.add(cat);
        String crDateString = "" + crDate;
        createDate = df.parse(crDateString);
    }
    public String getDueDate() {
        DateFormat dateFormatter = DateFormat.getDateInstance();
        String dateUS = dateFormatter.format(dueDate);
        return dateUS;
    }
    public void setDueDate(int dd) {
    	String dueDateString = "" + dd;
        try {
			dueDate = df.parse(dueDateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void setCreateDate(int cd) {
    	String createDateString = "" + cd;
    	try {
			createDate = df.parse(createDateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    public boolean isSelected() {
    	return isSelected;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public ArrayList<String> getCategories() {
        return categories;
    }
    // Maybe make this so it can accept more than one category at a time if comma separated
    // ToDo: string to array to parse comma separated categories from DB
    public void addCategory(String cat) {
        categories.add(cat);
    }
    public void rmCategory(String cat) {
    	categories.remove(cat);
    }
    public String getDescription() {
        return description;
    }
}
