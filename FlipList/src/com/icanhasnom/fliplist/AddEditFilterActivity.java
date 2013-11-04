package com.icanhasnom.fliplist;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActionBar;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

public class AddEditFilterActivity extends Activity {
	Menu myMenu;
	Integer currentTask;
	Integer currentFilterID;
	Filter currentFilter;
	public final static int RESULT_DELETED = 3;

	ListManager myListMan;
	ActionBar actionBar;
	
	// Tasks for ActivityResult
	static Integer MANAGE_FILTER = 6;
	static Integer ADD_FILTER = 5;
	static Integer EDIT_FILTER = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit_flist);
		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		
		myListMan = new ListManager(this);

		setupActionBar();

		Bundle b = this.getIntent().getExtras();
		if(b != null) {
			currentFilterID = (Integer) b.getSerializable("flistID");
			currentTask = (Integer) b.getSerializable("task");
			if (currentTask == ADD_FILTER) {
				addNewFilter();
			}
			if (currentTask == EDIT_FILTER) {
				//currentFilter = myListMan.getFilter(currentFilterID);
				//editFilter(currentFilter);
			}
		}
	}
	public void addNewFilter() {
    	Filter newFilter = new Filter();
    	//newFilter.setIsNew();
    	currentFilter = newFilter;
    	editFilter(newFilter);
	}

	public void editFilter(Filter filter) {

	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		myMenu = menu;
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_edit_flist, menu);
		//getMenuInflater().inflate(R.menu.add_edit_flist, myMenu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
    	case R.id.action_save_item:
    		//mySaveCatButtonAction(findViewById(android.R.id.content));
    		break;
    	case R.id.action_delete_item:
    		//myDeleteCatButtonAction(findViewById(android.R.id.content));
    		break;
    	case R.id.action_cancel_item:
    		finish();
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
