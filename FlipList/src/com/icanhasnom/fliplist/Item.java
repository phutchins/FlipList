package com.icanhasnom.fliplist;

/**
 *
 * @author flip
 */
import java.util.*;
import java.text.*;
import java.io.*;

import android.util.Log;

// TODO: Set up to take more than one category

public class Item implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int itemID;
	public String name;
	public String description = "";
	Calendar myCal = Calendar.getInstance();
    public String dueDateTime;
    public String createDate;
    public int primaryCat;
    public List<String> secondaryCats = new ArrayList<String>();
    public boolean hasDueDateBool = false;
    public boolean hasDueTimeBool = false;
    public String notes = "";
    public boolean isCompleted = false;
    public String completedDate = null;
    
    // Constructors
    // TODO: Do i need this many?
    public Item() {
    	dueDateTime = getCurrentDateTime();
	    createDate = getCurrentDateTime();
    }
    public String getCurrentDateTime() {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.ENGLISH);
	    String currentDateTime = sdf.format(new Date());
	    return currentDateTime;
    }
    public Item(int cat, String n, String desc, String crDate, String due) throws ParseException {
    	name = n;
    	description = desc;
        if (due != null) {
        	dueDateTime = due;
        	hasDueDateBool = true;
        	Log.v("Item Constructor", "dueDateTime: " + due);
        }
    	primaryCat = cat;
	    createDate = getCurrentDateTime();
    	Log.v("Item Constructor", "createDate: " + createDate);

    }
    public Item(int cat, String n, String desc, String due) {
    	name = n;
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
	    createDate = getCurrentDateTime();
    	Log.v("ListItem", "2) due: " + due + " hasDueDateBool: " + hasDueDateBool);
    }
    public Item(int cat, String n, String desc) {
    	name = n;
    	primaryCat = cat;
    	if (desc != null) {
    		description = desc;
    	} else {
    		description = "";
    	}
	    createDate = getCurrentDateTime();
    	dueDateTime = getCurrentDateTime();
    }
    public Item(int cat, String n) {
    	name = n;
    	primaryCat = cat;
	    createDate = getCurrentDateTime();
    	dueDateTime = getCurrentDateTime();
    }
    public Item(int id, int priCat, String cats, String n, String desc, String itemNotes, String crDate, String dueDT) throws ParseException {
    	itemID = id;
    	primaryCat = priCat;
        addToCats(cats);
    	name = n;
        description = desc;
        notes = itemNotes;
        createDate = crDate;
    	dueDateTime = dueDT;
    }
    
    // Setting Values //
    public void setDueDateTime(String ddt) {
    	dueDateTime = ddt;
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
    public void setCompleted(Boolean completed) {
    	isCompleted = completed;
    	if (completed) completedDate = getCurrentDateTime();
    	else completedDate = null;
    }
    public void setCompleted(Integer completed) {
    	// TODO: Remove completedDate if its unchecked?
    	if (completed == 1) {
    		isCompleted = true;
    		completedDate = getCurrentDateTime();
    	} else {
    		isCompleted = false;
    		completedDate = null;
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
    public void setCreateDate(String cd) {
    	// Do some checking here?
    	createDate = cd;
    }
    public void setDescription(String d) {
        description = d;
    }
    public void setName(String n) {
    	name = n;
    }
    public void setID(int id) {
    	itemID = id;
    }
    public void addToCats(String cats) {
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
    public void setNotes(String n) {
    	notes = n;
    }
    // Time & Date
    public Boolean setDueDate(String dd) {
    	Calendar curDateCal = Calendar.getInstance();
    	Boolean success = false;
    	SimpleDateFormat sdfDateTime = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
    	try {
			curDateCal.setTime(sdfDateTime.parse(dueDateTime));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
    	Calendar newDateCal = Calendar.getInstance();
    	SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    	if (dd != null && isDateValid(dd)) {
	    	try {
				newDateCal.setTime(sdfDate.parse(dd));
				Log.v("setDueDate", "New DueDate: " + sdfDateTime.format(newDateCal.getTime()));
				curDateCal.set(newDateCal.get(Calendar.YEAR),  newDateCal.get(Calendar.MONTH), newDateCal.get(Calendar.DAY_OF_MONTH));
				dueDateTime = curDateCal.getTime().toString();
				Log.v("setDueDate", "dueDateTime String: " + dueDateTime);
				hasDueDateBool = true;
				success = true;
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
    	return success;
    }
    public Boolean setDueTime(String dt) {
    	Calendar curTimeCal = Calendar.getInstance();
    	Boolean success = false;
    	SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    	try {
			curTimeCal.setTime(sdfDateTime.parse(dueDateTime));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
    	Calendar newTimeCal = Calendar.getInstance();
    	SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.US);
    	if (dt != null && isTimeValid(dt)) {
	    	try {

				newTimeCal.setTime(sdfTime.parse(dt));
				Log.v("setDueTime", "New DueTime: " + sdfDateTime.format(newTimeCal.getTime()));
				curTimeCal.set(Calendar.HOUR_OF_DAY, newTimeCal.get(Calendar.HOUR_OF_DAY));
				curTimeCal.set(Calendar.MINUTE, newTimeCal.get(Calendar.MINUTE));
				dueDateTime = curTimeCal.getTime().toString();
				Log.v("setDueTime", "dueDateTime String: " + dueDateTime);
				hasDueTimeBool = true;
				success = true;
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
    	return success;
    }
    
    // Get Values //
    public String getName() {
    	return name;
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
    public Boolean isCompleted() {
    	return isCompleted;
    }
    //public int isCompletedInt() {
    //	Integer isCompletedInt = 0;
    //	if (isCompleted) isCompletedInt = 1;
    //	return isCompletedInt;
    //}
    public String getSecondaryCatsString() {
    	String secondaryCatsString = secondaryCats.toArray().toString();
    	return secondaryCatsString;
    }
    public String getDescription() {
        return description;
    }
    public String getNotes() {
    	return notes;
    }
    // Time & Date
    public boolean hasDueDate() {
    	return hasDueDateBool;
    }
    public boolean hasDueTime() {
    	return hasDueTimeBool;
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
    public String getDueTimePretty() {
    	String timeStr = "Set Time";
    	if (hasDueTimeBool && dueDateTime != null) {
    		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.US);
    		timeStr = sdf.format(stringToDate(dueDateTime));
    	}
		return timeStr;
    }
    public String getDueTime() {
    	String timeStr = null;
    	if (hasDueTimeBool) {
    		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
    		timeStr = sdf.format(stringToDate(dueDateTime));
    	}
    	return timeStr;
    }
    public String getDueDatePretty() {
    	String dateStr = "Set Date";
    	Log.v("ListItem.getDueDatePretty", "hasDueDateBool: " + hasDueDateBool + " hasDueTimeBool: " + hasDueTimeBool + " dueDate: " + dueDateTime);
    	if (hasDueDateBool && dueDateTime != null) {
    		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd", Locale.US);
    		dateStr = sdf.format(stringToDate(dueDateTime));
    	}
    	return dateStr;
    }
    public String getDueDate() {
    	String dateStr = null;
    	if (hasDueDateBool) {
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    		dateStr = sdf.format(stringToDate(dueDateTime));
    	}
    	return dateStr;
    }
    
    // Date Methods
    public String dateToString(Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    	String dateStr = sdf.format(date);
    	return dateStr;
    }
    public boolean isDateTimeValid(String dateTimeToValidate) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    	sdf.setLenient(false);
    	
    	try {
    		sdf.parse(dateTimeToValidate);
    		Log.v("DateTime Validator", "DateTime: " + dateTimeToValidate + " is valid.");
    	} catch (ParseException e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    public boolean isDateValid(String dateToValidate) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    	sdf.setLenient(false);
    	
    	try {
    		sdf.parse(dateToValidate);
    		Log.v("Date Validator", "Date: " + dateToValidate + " is valid.");
    	} catch (ParseException e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    public boolean isTimeValid(String timeToValidate) {
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
    	sdf.setLenient(false);
    	
    	try {
    		sdf.parse(timeToValidate);
    		Log.v("Time Validator", "Time: " + timeToValidate + " is valid.");
    	} catch (ParseException e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    // TODO: Set time zone somewhere ?
    public Date stringToDate(String dateStr) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    	Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return date;
    }
    public String dateToStringPretty(Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, hh:mm aa", Locale.US);
    	String dateStr = sdf.format(date);
    	return dateStr;
    }

    // Remove Values
    public void rmCategory(String cat) {
    	secondaryCats.remove(cat);
    }
}
