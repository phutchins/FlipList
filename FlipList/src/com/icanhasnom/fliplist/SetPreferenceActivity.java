package com.icanhasnom.fliplist;

import android.app.Activity;
import android.os.Bundle;

//Settings Design
//Make settings menu -
// Category Settings
//   Default category setting
//   Default list type (grocery, todo, etc...)
// Notification Settings
//   Default notification sound
//   All notifications on/off

public class SetPreferenceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
  
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
 	}

}