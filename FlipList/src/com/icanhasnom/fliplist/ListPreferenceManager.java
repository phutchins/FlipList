package com.icanhasnom.fliplist;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class ListPreferenceManager {
    SharedPreferences mySharedPreferences;
    
	int defaultCatID;
	Boolean ShowItemDescriptionGlobal;
	Boolean ShowDueDateGlobal;
	Integer RemoveCompletedItems;
    Integer RemoveCompletedItemsDelay;
    // Category currentCategory;
    Integer currentCategoryID;
    
    Context context;

    public ListPreferenceManager(Context c) {
    	context = c;
    	Log.v("ListPreferenceManager.loadPrefs", "context: " + context);
    	loadPrefs();
    }
    private void loadPrefs(){
    	mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        defaultCatID = Integer.parseInt(mySharedPreferences.getString(context.getString(R.string.default_category_key), context.getString(R.integer.default_category_default)));
        Log.v("FlipList.loadPref", "defaultCatID: " + defaultCatID);

        ShowItemDescriptionGlobal = mySharedPreferences.getBoolean(context.getString(R.string.show_description_global_key), context.getResources().getBoolean(R.bool.show_description_global_default));
        ShowDueDateGlobal = mySharedPreferences.getBoolean(context.getString(R.string.show_due_date_global_key), context.getResources().getBoolean(R.bool.show_due_date_global_default));

        // TODO: Create sanity check to ensure that default category exists
        defaultCatID = mySharedPreferences.getInt("current_category_id", 0);
        //if (currentCategoryID == null) currentCategoryID = defaultCatID;
    }
}


