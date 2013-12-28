package com.icanhasnom.fliplist;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class ListPreferenceManager {
    SharedPreferences mySharedPreferences;
    

	Boolean ShowItemDescriptionGlobal;
	Boolean ShowDueDateGlobal;
	Integer RemoveCompletedItems;
    Integer RemoveCompletedItemsDelay;
    // Category currentCategory;
    private int currentFlistID;
	private int defaultFlistID;
    
    Context context;

    public ListPreferenceManager(Context c) {
    	context = c;
    	//Log.v("ListPreferenceManager.loadPrefs", "context: " + context);
    	loadPrefs();
    }
    private void loadPrefs(){
    	mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        defaultFlistID = Integer.parseInt(mySharedPreferences.getString(context.getString(R.string.default_flist_key), context.getString(R.integer.default_flist_default)));
        //Log.v("FlipList.loadPref", "defaultFlistID: " + defaultFlistID);

        ShowItemDescriptionGlobal = mySharedPreferences.getBoolean(context.getString(R.string.show_description_global_key), context.getResources().getBoolean(R.bool.show_description_global_default));
        ShowDueDateGlobal = mySharedPreferences.getBoolean(context.getString(R.string.show_due_date_global_key), context.getResources().getBoolean(R.bool.show_due_date_global_default));

        // TODO: Create sanity check to ensure that default category exists
        defaultFlistID = mySharedPreferences.getInt("default_flist_id", 0);
        currentFlistID = mySharedPreferences.getInt("current_flist_id", 0);
        //if (currentCategoryID == null) currentCategoryID = defaultCatID;
    }
    public void setDefaultFlistID(int flistID) {
    	defaultFlistID = flistID;
    	SharedPreferences.Editor prefEditor = mySharedPreferences.edit();
    	prefEditor.putInt("default_flist_id", flistID);
    	prefEditor.commit();
    }
    public void setCurrentFlistID(int flistID) {
    	currentFlistID = flistID;
    	SharedPreferences.Editor prefEditor = mySharedPreferences.edit();
    	prefEditor.putInt("current_flist_id", flistID);
    	prefEditor.commit();
    	
    }
    public int getCurrentFlistID() {
    	return currentFlistID;
    }
    public int getDefaultFlistID() {
    	return defaultFlistID;
    }
    /*
     *      mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    		SharedPreferences.Editor prefEditor = mySharedPreferences.edit();
    		prefEditor.putInt("current_category_id", currentFlist.getID());
    		prefEditor.commit();
     */
}


