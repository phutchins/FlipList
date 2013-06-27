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
    
    public Item() {
    	dueDateTime = getNowDateTime();
        createDate = new Date().toString();
    }
    public Item(int cat, String n, String desc, String crDate, String due) throws ParseException {
    	name = n;
    	description = desc;
    	Log.v("ListItem", "1) due: " + due + " hasDueDateBool: " + hasDueDateBool);
        if (due != null) {
        	dueDateTime = due;
        	hasDueDateBool = true;
        	Log.v("ListItem.hasDueDateBool", "1) Setting hasDueDateBool to true");
        }
    	primaryCat = cat;
        createDate = new Date().toString();
    	Log.v("ListItem", "1) due: " + due + " hasDueDateBool: " + hasDueDateBool);
    }
    public Item(int cat, String n, String desc, String due) {
    	name = n;
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
    public Item(int cat, String n, String desc) {
    	name = n;
    	primaryCat = cat;
    	if (desc != null) {
    		description = desc;
    	} else {
    		description = "";
    	}
    	createDate = new Date().toString();
    	dueDateTime = getNowDateTime();
    }
    public Item(int cat, String n) {
    	name = n;
    	primaryCat = cat;
    	createDate = new Date().toString();
    	Log.v("ListItem", "getNowDateTime(): " + getNowDateTime());
    	dueDateTime = getNowDateTime();
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
    public String getNowDateTime() {
    	String nowDateTime;
    	SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
    	Calendar cal = Calendar.getInstance();
    	nowDateTime = sdf.format(cal.getTime());
    	return nowDateTime;
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
    	SimpleDateFormat sdfDateTime = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
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
    	SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, hh:mm aa", Locale.US);
    	String dateStr = sdf.format(date);
    	return dateStr;
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
    public Date stringToDate(String dateStr) {
    	SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
    	Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
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