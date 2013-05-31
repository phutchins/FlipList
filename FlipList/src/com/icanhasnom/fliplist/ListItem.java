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

public class ListItem implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int itemID;
	public String name;
	public String description;
    public Date dueDate;
    public ArrayList<String> categories;
    public String[] category;
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
    public ListItem(String cat, String n, String desc, Date due) {
    	name = n;
    	categories.add(cat);
    	description = desc;
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
    public void setDueDate(int dd) throws ParseException {
    	String dueDateString = "" + dd;
        dueDate = df.parse(dueDateString);
    }
    public void setCreateDate(int cd) throws ParseException {
    	String createDateString = "" + cd;
    	createDate = df.parse(createDateString);
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
