package com.icanhasnom.fliplist;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

public class PrefsFragment extends PreferenceFragment implements TabListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
  
		// Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        //final ActionBar actionBar = getActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // For each of the sections in the app, add a tab to the action bar.
        //actionBar.addTab(actionBar.newTab().setText("ListFragment").setTabListener(this));
        //actionBar.addTab(actionBar.newTab().setText(R.string.test_string1).setTabListener(this));
        //actionBar.addTab(actionBar.newTab().setText(R.string.test_string2).setTabListener(this));
        
        populatePreferenceOptions();
	}
	
	public void populatePreferenceOptions() {
		ListPreference defaultCategory = (ListPreference)findPreference("default_category");
		// TODO: Populate these with categories and category ids
        CharSequence[] categoriesList = new String[]{"cat1", "cat2", "cat3"};
        CharSequence[] categoriesListValues = new String[]{"v1", "v2", "v3"};

        defaultCategory.setKey("default_category");
        defaultCategory.setEntries(categoriesList);
        defaultCategory.setEntryValues(categoriesListValues);
        defaultCategory.setDialogTitle("Title Here");
        defaultCategory.setTitle("Title");
        defaultCategory.setSummary("Summary goes here");
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

}