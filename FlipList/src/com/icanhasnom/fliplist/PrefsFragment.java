package com.icanhasnom.fliplist;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class PrefsFragment extends PreferenceFragment implements TabListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
  
		// Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        //PreferenceManager.setDefaultValues(this, @, false);
        
        //final ActionBar actionBar = getActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // For each of the sections in the app, add a tab to the action bar.
        //actionBar.addTab(actionBar.newTab().setText("ListFragment").setTabListener(this));
        //actionBar.addTab(actionBar.newTab().setText(R.string.test_string1).setTabListener(this));
        //actionBar.addTab(actionBar.newTab().setText(R.string.test_string2).setTabListener(this));
        
        populatePreferenceOptions();
	}
	
	public void populatePreferenceOptions() {
		createDefaultCategory();
	}
	
	public void createDefaultCategory() {
		ListPreference defaultCategory = (ListPreference)findPreference("default_category");
		ListManager myListMan = new ListManager(getActivity());
		ArrayList<Category> categoryList = myListMan.getCategories();
		List<String> catListStrings = new ArrayList<String>();
		List<String> catListValuesStrings = new ArrayList<String>();
		
		for(Category myCat : categoryList) {
			catListStrings.add(myCat.getName());
			catListValuesStrings.add(String.valueOf(myCat.getID()));
		}
		final CharSequence[] categoriesList = catListStrings.toArray(new CharSequence[catListStrings.size()]);
		final CharSequence[] categoriesListValues = catListValuesStrings.toArray(new CharSequence[catListValuesStrings.size()]);

        defaultCategory.setEntries(categoriesList);
        defaultCategory.setEntryValues(categoriesListValues);
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