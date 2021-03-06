package com.icanhasnom.fliplist;

/**
 *
 * @author flip
 */
import java.util.*;
import java.text.*;
import java.io.*;

import android.text.TextUtils;
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
    public String dueDateTime;
    public String createDate;
    public int flist;
    public List<String> categories = new ArrayList<String>();
    public boolean hasDueDateBool = false;
    public boolean hasDueTimeBool = false;
    public String notes = "";
    public boolean isCompleted = false;
    public String completedDate = null;
    
    TimeZone tz = TimeZone.getDefault();
	Calendar myCal = Calendar.getInstance(tz);
    
	SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.US);
	SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	SimpleDateFormat sdfDateTimePretty = new SimpleDateFormat("MMMM dd, hh:mm aa", Locale.US);
	SimpleDateFormat sdfTimePretty = new SimpleDateFormat("hh:mm aa", Locale.US);
	SimpleDateFormat sdfDatePretty = new SimpleDateFormat("MMMM dd", Locale.US);	
    
    // Constructors
    // TODO: Do i need this many?
    public Item() {
    	sdfDateTime.setTimeZone(tz);
    	sdfTime.setTimeZone(tz);
    	sdfDate.setTimeZone(tz);
    	sdfDateTimePretty.setTimeZone(tz);
    	sdfTimePretty.setTimeZone(tz);
    	sdfDatePretty.setTimeZone(tz);
    	
    	dueDateTime = getCurrentDateTime();
	    createDate = getCurrentDateTime();
    }
    public String getCurrentDateTime() {
	    String currentDateTime = sdfDateTime.format(new Date());
	    return currentDateTime;
    }
    public Item(int fl, String n, String desc, String crDate, String due) throws ParseException {
    	name = n;
    	description = desc;
        if (due != null) {
        	dueDateTime = due;
        	hasDueDateBool = true;
        	Log.v("Item Constructor", "dueDateTime: " + due);
        }
    	flist = fl;
	    createDate = getCurrentDateTime();
    	Log.v("Item Constructor", "createDate: " + createDate);

    }
    public Item(int fl, String n, String desc, String due) {
    	name = n;
    	flist = fl;
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
    public Item(int fl, String n, String desc) {
    	name = n;
    	flist = fl;
    	if (desc != null) {
    		description = desc;
    	} else {
    		description = "";
    	}
	    createDate = getCurrentDateTime();
    	dueDateTime = getCurrentDateTime();
    }
    public Item(int fl, String n) {
    	name = n;
    	flist = fl;
	    createDate = getCurrentDateTime();
    	dueDateTime = getCurrentDateTime();
    }
    public Item(int id, int fl, List<String> cat, String n, String desc, String itemNotes, String crDate, String dueDT) throws ParseException {
    	itemID = id;
    	flist = fl;
        setCategories(cat);
    	name = n;
        description = desc;
        notes = itemNotes;
        createDate = crDate;
    	dueDateTime = dueDT;
    }
    
    
    
    // Identity
    public String getName() {
    	return name;
    }
    public int getID() {
    	return itemID;
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
    public void setNotes(String n) {
    	notes = n;
    }
    

    
    
    // Flags
    public void setCompleted(Boolean completed) {
    	isCompleted = completed;
    	if (completed) completedDate = getCurrentDateTime();
    	else completedDate = null;
    	Log.v("Item.setCompleted", "completedDate: " + completedDate);
    }
    public void setCompleted(Integer completed) {
    	// TODO: Remove completedDate if its unchecked?
    	if (completed == 1) {
    		isCompleted = true;
    		completedDate = getCurrentDateTime();
    		Log.v("Item.setCompleted", "Setting " + name + " to completed with completedDate: " + completedDate);
    	} else {
    		isCompleted = false;
    		completedDate = null;
    	}
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
    public Boolean isCompleted() {
    	return isCompleted;
    }
    
    // Membership
    public void setCategory(String cat) {
    	categories.add(cat);
    }
    public void setCategories(List<String> categoryList) {
    	categories = categoryList;
    }
    public void setFlist(int f) {
    	flist = f;
    }
    public int getFlist() {
    	return flist;
    }
    public List<String> getCategories() {
        return categories;
    }
    public String getCategoriesString() {
    	String categoriesString = "";
    	for(int i = 0; i < categories.size(); i++) {
    		categoriesString += categories.get(i);
    		if(i != (categories.size() - 1)) {
    			categoriesString += ",";
    		}
    	}
    	//String categoriesString = TextUtils.join(categories, ',');
    	String categoriesString2 = categories.toArray().toString();
    	Log.v("Item.getCategoriesString()", "categoriesString: " + categoriesString);
    	Log.v("Item.getCategoriesString()", "categoriesString2: " + categoriesString2);
    	
    	return categoriesString;
    }
    
    
    // Time & Date
    public void setDueDateTime(String ddt) {
    	dueDateTime = ddt;
    }
    public void setHasDueDate(Boolean hasDD) {
    	hasDueDateBool = hasDD;
    }
    public Boolean setCompletedDate(String cd) {
    	Boolean success = false;
    	if (cd != null && isDateTimeValid(cd)) {
    		completedDate = cd;
    		success = true;
    		Log.v("Item.setCompletedDate: ", "CompletedDate: " + cd);
    	}
    	return success;
    }
    public void setCreateDate(String cd) {
    	// Do some checking here?
    	createDate = cd;
    }
    public Boolean setDueDate(String dd) {
    	Calendar curDateCal = Calendar.getInstance(tz);
    	Boolean success = false;
    	
    	try {
			curDateCal.setTime(sdfDateTime.parse(dueDateTime));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
    	Calendar newDateCal = Calendar.getInstance(tz);
    	if (dd != null && isDateValid(dd)) {
	    	try {
				newDateCal.setTime(sdfDate.parse(dd));
				Log.v("setDueDate", "New DueDate: " + sdfDateTime.format(newDateCal.getTime()));
				curDateCal.set(newDateCal.get(Calendar.YEAR),  newDateCal.get(Calendar.MONTH), newDateCal.get(Calendar.DAY_OF_MONTH));
				dueDateTime = sdfDateTime.format(newDateCal.getTime());
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
    	Calendar curTimeCal = Calendar.getInstance(tz);
    	Boolean success = false;

    	try {
			curTimeCal.setTime(sdfDateTime.parse(dueDateTime));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
    	Calendar newTimeCal = Calendar.getInstance(tz);
    	if (dt != null && isTimeValid(dt)) {
	    	try {

				newTimeCal.setTime(sdfTime.parse(dt));
				Log.v("setDueTime", "New DueTime: " + sdfDateTime.format(newTimeCal.getTime()));
				curTimeCal.set(Calendar.HOUR_OF_DAY, newTimeCal.get(Calendar.HOUR_OF_DAY));
				curTimeCal.set(Calendar.MINUTE, newTimeCal.get(Calendar.MINUTE));
				dueDateTime = sdfDateTime.format(newTimeCal.getTime());
				Log.v("setDueTime", "dueDateTime String: " + dueDateTime);
				hasDueTimeBool = true;
				success = true;
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
    	return success;
    }
    public String getCreateDate() {
        return createDate;
    }

    
    
    
    
    // Admin & Maintenance
    public void rmCategory(String cat) {
    	categories.remove(cat);
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
    		timeStr = sdfTimePretty.format(stringToDate(dueDateTime));
    	}
		return timeStr;
    }
    public String getDueTime() {
    	String timeStr = null;
    	if (hasDueTimeBool) {
    		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.US);
    		timeStr = sdfTime.format(stringToDate(dueDateTime));
    	}
    	return timeStr;
    }
    public String getDueDatePretty() {
    	String dateStr = "Set Date";
    	Log.v("ListItem.getDueDatePretty", "hasDueDateBool: " + hasDueDateBool + " hasDueTimeBool: " + hasDueTimeBool + " dueDate: " + dueDateTime);
    	if (hasDueDateBool && dueDateTime != null) {
    		dateStr = sdfDatePretty.format(stringToDate(dueDateTime));
    	}
    	return dateStr;
    }
    public String getDueDate() {
    	String dateStr = null;
    	if (hasDueDateBool) {
    		dateStr = sdfDate.format(stringToDate(dueDateTime));
    	}
    	return dateStr;
    }
    public String getCompletedDate() {
    	return completedDate;
    }
    
    // Date Methods
    public String dateToString(Date date) {
    	String dateStr = sdfDateTime.format(date);
    	return dateStr;
    }
    public boolean isDateTimeValid(String dateTimeToValidate) {
    	sdfDateTime.setLenient(false);
    	
    	try {
    		sdfDateTime.parse(dateTimeToValidate);
    		Log.v("DateTime Validator", "DateTime: " + dateTimeToValidate + " is valid.");
    	} catch (ParseException e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    public boolean isDateValid(String dateToValidate) {
    	sdfDate.setLenient(false);
    	try {
    		sdfDate.parse(dateToValidate);
    		Log.v("Date Validator", "Date: " + dateToValidate + " is valid.");
    	} catch (ParseException e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    public boolean isTimeValid(String timeToValidate) {
    	sdfTime.setLenient(false);
    	
    	try {
    		sdfTime.parse(timeToValidate);
    		Log.v("Time Validator", "Time: " + timeToValidate + " is valid.");
    	} catch (ParseException e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    // TODO: Set time zone somewhere ?
    public Date stringToDate(String dateStr) {
    	Date date = null;
		try {
			date = sdfDateTime.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return date;
    }
    public String dateToStringPretty(Date date) {
    	String dateStr = sdfDateTimePretty.format(date);
    	return dateStr;
    }


}
