package com.icanhasnom.fliplist;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesHelper {

	private SharedPreferences sharedPreferences;
	private Editor editor;
	
	public PreferencesHelper(Context context) {
//		this.sharedPreferences = getPreferences(MODE_PRIVATE);    
		this.editor = sharedPreferences.edit(); 
	}
	
	public String GetPreferences(String key, String def) {
	    return sharedPreferences.getString(key, def);
	}
	
	public void SavePreferences(String key, String value) {
		editor.putString(key, value);    
		editor.commit();  
	}
} 
