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
	public String description = "";
    public String dueDateTime = "";
    public String createDate;
    public int primaryCat;
    public List<String> secondaryCats = new ArrayList<String>();
    public boolean hasDueDateBool = false;
    public boolean hasDueTimeBool = false;
    public String notes = "";
    
    public ListItem() {
    }
    public ListItem(int cat, String n, String desc, String crDate, String due) throws ParseException {
    	name = n;
    	description = desc;
    	Log.v("ListItem", "1) due: " + due + " hasDueDateBool: " + hasDueDateBool);
        if (due != null) {
        	dueDateTime = due;
        	hasDueDateBool = true;
        	Log.v("ListItem.hasDueDateBool", "1) Setting hasDueDateBool to true");
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
        if (due != null) {
        	hasDueDateBool = true;
        	dueDateTime = due;
        	Log.v("ListItem.hasDueDateBool", "2) Setting hasDueDateBool to true");
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
    public ListItem(int cat, String n) {
    	name = n;
    	primaryCat = cat;
    	createDate = new Date().toString();
    }
    public ListItem(int id, int priCat, String cats, String n, String desc, String itemNotes, String crDate, String dueDT) throws ParseException {
    	itemID = id;
    	primaryCat = priCat;
        addToCats(cats);
    	name = n;
        description = desc;
        notes = itemNotes;
        createDate = crDate;
    	dueDateTime = dueDT;
    }
    public String getDueDateTime() {
    	return dueDateTime;
    }
    public String getDueDateTimePretty() {
    	return dateToStringPretty(stringToDate(dueDateTime));
    }
    public Date getDueDateObj() {
    	return stringToDate(dueDateTime);
    }
    public void setDueDateTime(String ddt) {
    	dueDateTime = ddt;
    }
    public Boolean setDueDate(String dd) {
    	Boolean success = false;
    	Calendar cal = Calendar.getInstance();
    	// TODO: Fix this below to use localle
    	SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd");
    	if (dd != null && isDateValid(dd)) {
	    	try {
				cal.setTime(sdfDateTime.parse(dueDateTime));
				Log.v("setDueDate", "DueDate: " + sdfDateTime.format(cal.getTime()));
				cal.setTime(sdfTime.parse(dd));
				Log.v("setDueDate", "New DueDate: " + sdfDateTime.format(cal.getTime()));
				dueDateTime = cal.getTime().toString();
				Log.v("setDueDate", "dueDateTime String: " + dueDateTime);
				hasDueDateBool = true;
				success = true;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return success;
    }
    public Boolean setDueTime(String dt) {
    	Calendar cal = Calendar.getInstance();
    	// TODO: Fix this below to use localle
    	Boolean success = false;
    	SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
    	if (dt != null && isTimeValid(dt)) {
	    	try {
				cal.setTime(sdfDateTime.parse(dueDateTime));
				Log.v("setDueTime", "DueTime: " + sdfDateTime.format(cal.getTime()));
				cal.setTime(sdfTime.parse(dt));
				Log.v("setDueTime", "New DueTime: " + sdfDateTime.format(cal.getTime()));
				dueDateTime = cal.getTime().toString();
				Log.v("setDueTime", "dueDateTime String: " + dueDateTime);
				hasDueTimeBool = true;
				success = true;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return success;
    }
    public void setHasDueDate(Boolean hasDD) {
    	hasDueDateBool = hasDD;
    }
    public void setHasDueDate(int hasDD) {
    	if (hasDD == 1) {
    		hasDueDateBool = true;
    	} else {
    		hasDueDateBool = false;
    	}
    }
    public void setHasDueTime(Boolean hasDT) {
    	hasDueTimeBool = hasDT;
    }
    public void setHasDueTime(int hasDT) {
    	if (hasDT == 1) {
    		hasDueTimeBool = true;
    	} else {
    		hasDueTimeBool = false;
    	}
    }
    public boolean hasDueDate() {
    	return hasDueDateBool;
    }
    public boolean hasDueTime() {
    	return hasDueTimeBool;
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
    public String getDueTimePretty() {
    	String timeStr = "Set Time";
    	if (hasDueTimeBool) {
    		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aa");
    		timeStr = sdf.format(stringToDate(dueDateTime));
    	}
		return timeStr;
    }
    public String getDueDatePretty() {
    	String dateStr = "Set Date";
    	Log.v("ListItem.getDueDatePretty", "hasDueDateBool: " + hasDueDateBool + " hasDueTimeBool: " + hasDueTimeBool + " dueDate: " + dueDateTime);
    	if (hasDueDateBool && dueDateTime != null) {
    		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd");
    		dateStr = sdf.format(stringToDate(dueDateTime));
    	}
    	return dateStr;
    }
    public String dateToString(Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String dateStr = sdf.format(date);
    	return dateStr;
    }
    public boolean isDateTimeValid(String dateTimeToValidate) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	sdf.setLenient(false);
    	
    	try {
    		Date date = sdf.parse(dateTimeToValidate);
    		Log.v("DateTime Validator", "Date: " + dateTimeToValidate + " is valid.");
    	} catch (ParseException e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    public boolean isDateValid(String dateToValidate) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	sdf.setLenient(false);
    	
    	try {
    		Date date = sdf.parse(dateToValidate);
    		Log.v("Date Validator", "Date: " + dateToValidate + " is valid.");
    	} catch (ParseException e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    public boolean isTimeValid(String timeToValidate) {
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    	sdf.setLenient(false);
    	
    	try {
    		Date date = sdf.parse(timeToValidate);
    		Log.v("Time Validator", "Date: " + timeToValidate + " is valid.");
    	} catch (ParseException e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
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
