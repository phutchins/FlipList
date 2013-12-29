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

public class AddEditFlistActivity extends Activity {
	Menu myMenu;
	Integer currentFlistID;
	Flist currentFlist;
	Integer currentTask;
	public final static int RESULT_DELETED = 3;

	ListManager myListMan;
	ArrayList<Flist> flistList;
	ArrayList<Filter> filterList;
	ArrayList<Category> categoryList;
	ArrayList<ItemType> typeList;
	SparseIntArray myFilterPositionMap;
	SparseIntArray myTypePositionMap;
	SparseIntArray myCategoryPositionMap;
	ActionBar actionBar;
	
	Spinner defaultCategorySpinner;
	Spinner filterSpinner;
	Spinner typeSpinner;
	MyTypeSpinnerCustomAdapter adapter;
	
	// Tasks for ActivityResult
	static Integer MANAGE_FLISTS = 6;
	static Integer ADD_FLIST = 5;
	static Integer EDIT_FLIST = 4;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit_flist);
		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		
		myListMan = new ListManager(this);
        flistList = myListMan.getFlists();
		filterList = myListMan.getFilters();
		categoryList = myListMan.getCategories();
		myFilterPositionMap = new SparseIntArray();
		myFilterPositionMap = buildFilterIndex(filterList);
		myTypePositionMap = new SparseIntArray();
		myTypePositionMap = buildCategoryIndex(categoryList);
		setupActionBar();

		Bundle b = this.getIntent().getExtras();
		if(b != null) {
			currentFlistID = (Integer) b.getSerializable("flistID");
			currentTask = (Integer) b.getSerializable("task");
			if (currentTask == ADD_FLIST) {
				addNewFlist();
			}
			if (currentTask == EDIT_FLIST) {
				currentFlist = myListMan.getFlist(currentFlistID);
				editFlist(currentFlist);
			}
		}
	}
	//public void manageCategories() {
	//	setContentView(R.layout.activity_add_edit_cat_list);
	//	addItemsOnEditList();
	//}
	//public void addNewCategory() {
    //	Category newCategory = new Category();
    //	newCategory.setIsNew();
    //	newCategory.setType(myListMan.defaultTypeID);
    //	currentCategory = newCategory;
    //	addEditCategory(newCategory);
	//}
	public void addNewFlist() {
    	Flist newFlist = new Flist();
    	newFlist.setIsNew();
    	newFlist.setType(myListMan.defaultTypeID);
    	currentFlist = newFlist;
    	editFlist(newFlist);
	}
	public SparseIntArray buildCategoryIndex(ArrayList<Category> myCategoryList) {
		Integer position = 0;
		SparseIntArray myPositionMap = new SparseIntArray();
		for (Category myCategory : myCategoryList) {
			myPositionMap.put(myCategory.getID(), position);
			position++;
		}
		return myPositionMap;
	}
	public int getCategoryPosition(int myCategoryID, SparseIntArray positionMap) {
		int myPosition = positionMap.get(myCategoryID);
		return myPosition;
	}
	public SparseIntArray buildFilterIndex(ArrayList<Filter> myFilterList) {
		Integer position = 0;
		SparseIntArray positionMap = new SparseIntArray();
		for (Filter myFilter : myFilterList) {
			positionMap.put(myFilter.getID(), position);
			position++;
		}
		return positionMap;
	}
	public int getFilterPosition(int myFilterID, SparseIntArray positionMap) {
		int myPosition = myFilterPositionMap.get(myFilterID);
		return myPosition;
	}
	public void editFlist(Flist flist) {
		// TODO: Below menu breaks? Is it needed?

		// TODO: Do I need to pass in the ListCategory? or just use the global currentCategory

		boolean isNew = flist.isNew();
		boolean isVisible;
		Log.v("addEditFlist", "isNew: " + isNew);
    	
    	if (isNew) {
    		isVisible = true;
    	} else {
    		isVisible = flist.isVisible();
    	}
    	Log.v("AddEditFlistActivity.editFlist", "flist.isVisible: " + flist.isVisible());

		// Get all of the View Objects
		EditText flistName = (EditText) findViewById(R.id.flist_edit_name);
		EditText flistDesc = (EditText) findViewById(R.id.flist_edit_desc);
		// Make this catID a hidden text field or something to store the value
		EditText flistID = (EditText) findViewById(R.id.flist_edit_id);
		defaultCategorySpinner = (Spinner) findViewById(R.id.add_edit_flist_default_category_spinner);
		filterSpinner = (Spinner) findViewById(R.id.add_edit_flist_filter_spinner);
		CheckBox isVisibleBox = (CheckBox) findViewById(R.id.add_edit_flist_visible_check_box);
		isVisibleBox.setChecked(isVisible);
		
		// Populate type spinner and select the categories type (will be default if new)
		addCategoriesToSpinner();
		addFiltersToSpinner();
		addTypesToSpinner();
		filterSpinner.setSelection(getFilterPosition(flist.getFilterID(), myFilterPositionMap));
		defaultCategorySpinner.setSelection(flist.getDefaultCategoryID());
		if (isNew) {
		} else {
			flistName.setText(flist.getName());
			flistDesc.setText(flist.getDescription());
			flistID.setText(String.valueOf(flist.getID()));
		}
		currentFlist = flist;
	}
	
    public void addCategoriesToSpinner() {
        ArrayList<Category> myCategoryList = myListMan.getCategories();
        defaultCategorySpinner = (Spinner) findViewById(R.id.add_edit_flist_default_category_spinner);
        ArrayAdapter<Category> myCategorySpinnerAdapter = new MyCategorySpinnerCustomAdapter(this, R.layout.activity_add_edit_flist, myCategoryList);
        myCategorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        defaultCategorySpinner.setAdapter(myCategorySpinnerAdapter);
    }
    public void addTypesToSpinner() {
        ArrayList<ItemType> myTypeList = myListMan.getTypes();
        typeSpinner = (Spinner) findViewById(R.id.add_edit_flist_type_spinner);
        ArrayAdapter<ItemType> myTypeSpinnerAdapter = new MyTypeSpinnerCustomAdapter(this, R.layout.activity_add_edit_flist, myTypeList);
        myTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(myTypeSpinnerAdapter);
    }
    public void addFiltersToSpinner() {
        ArrayList<Filter> myFilterList = myListMan.getFilters();
        filterSpinner = (Spinner) findViewById(R.id.add_edit_flist_filter_spinner);
        ArrayAdapter<Filter> myFilterAdapter = new MyFilterSpinnerCustomAdapter(this, R.layout.activity_add_edit_flist, myFilterList);
        myFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(myFilterAdapter);
    }
    public class MyTypeSpinnerCustomAdapter extends ArrayAdapter<ItemType>{
    	private Activity activity;
    	private ArrayList<ItemType> myTypes;
    	LayoutInflater inflater;
    	
    	public MyTypeSpinnerCustomAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<ItemType> myTypes) {
    		super(activitySpinner, textViewResourceId, myTypes);
    		this.activity = activitySpinner;
    		this.myTypes = myTypes;
    		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	private class ViewHolder {
    		TextView typeName;
    	}
    	public int getCount() {
    		return myTypes.size();
    	}
    	public ItemType getItem(int position) {
    		return myTypes.get(position);
    	}
    	public long getItemId(int position) {
    		return position;
    	}
    	
    	public View getDropDownView(int position, View convertView, ViewGroup parent) {
    		return getCustomView(position, convertView, parent);
    	}
    	public View getView(int position, View convertView, ViewGroup parent) {
    		return getCustomView(position, convertView, parent);
    	}
    	
    	public View getCustomView(int position, View convertView, ViewGroup parent) {
    		ViewHolder holder;
    		if (convertView == null) {
    			convertView = inflater.inflate(R.layout.activity_type_spinner, null);
    			holder = new ViewHolder();
    			holder.typeName = (TextView) convertView.findViewById(R.id.type_spinner_text);
    			convertView.setTag(holder);
    		} else {
    			holder = (ViewHolder) convertView.getTag();
    		}
    		//TextView label = (TextView) convertView.findViewById(R.id.type_spinner_text);
    		ItemType type = myTypes.get(position);
    		holder.typeName.setTextColor(Color.BLACK);
    		holder.typeName.setText(myTypes.get(position).getName());
    		holder.typeName.setTag(type);
    		Log.v("TypeSpinner", myTypes.get(position).getName());
    		return convertView;
    	}
    }
    
    public class MyCategorySpinnerCustomAdapter extends ArrayAdapter<Category>{
    	private Activity activity;
    	private ArrayList<Category> myCategories;
    	LayoutInflater inflater;
    	
    	public MyCategorySpinnerCustomAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<Category> myCategories) {
    		super(activitySpinner, textViewResourceId, myCategories);
    		this.activity = activitySpinner;
    		this.myCategories = myCategories;
    		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	private class ViewHolder {
    		TextView categoryName;
    	}
    	public int getCount() {
    		return myCategories.size();
    	}
    	public Category getItem(int position) {
    		return myCategories.get(position);
    	}
    	public long getItemId(int position) {
    		return position;
    	}
    	
    	public View getDropDownView(int position, View convertView, ViewGroup parent) {
    		return getCustomView(position, convertView, parent);
    	}
    	public View getView(int position, View convertView, ViewGroup parent) {
    		return getCustomView(position, convertView, parent);
    	}
    	
    	public View getCustomView(int position, View convertView, ViewGroup parent) {
    		ViewHolder holder;
    		if (convertView == null) {
    			convertView = inflater.inflate(R.layout.spinner_view, null);
    			holder = new ViewHolder();
    			holder.categoryName = (TextView) convertView.findViewById(R.id.spinner_text);
    			convertView.setTag(holder);
    		} else {
    			holder = (ViewHolder) convertView.getTag();
    		}
    		//TextView label = (TextView) convertView.findViewById(R.id.type_spinner_text);
    		Category category = myCategories.get(position);
    		holder.categoryName.setTextColor(Color.BLACK);
    		holder.categoryName.setText(myCategories.get(position).getName());
    		holder.categoryName.setTag(category);
    		//Log.v("defaultCategorySpinner", myCategories.get(position).getName());
    		return convertView;
    	}
    }
    
    public class MyFilterSpinnerCustomAdapter extends ArrayAdapter<Filter>{
    	private Activity activity;
    	private ArrayList<Filter> myFilters;
    	LayoutInflater inflater;
    	
    	public MyFilterSpinnerCustomAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<Filter> myFilters) {
    		super(activitySpinner, textViewResourceId, myFilters);
    		this.activity = activitySpinner;
    		this.myFilters = myFilters;
    		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	private class ViewHolder {
    		TextView filterName;
    	}
    	public int getCount() {
    		return myFilters.size();
    	}
    	public Filter getItem(int position) {
    		return myFilters.get(position);
    	}
    	public long getFilterId(int position) {
    		return position;
    	}
    	
    	public View getDropDownView(int position, View convertView, ViewGroup parent) {
    		return getCustomView(position, convertView, parent);
    	}
    	public View getView(int position, View convertView, ViewGroup parent) {
    		return getCustomView(position, convertView, parent);
    	}
    	
    	public View getCustomView(int position, View convertView, ViewGroup parent) {
    		ViewHolder holder;
    		if (convertView == null) {
    			convertView = inflater.inflate(R.layout.activity_filter_spinner, null);
    			holder = new ViewHolder();
    			holder.filterName = (TextView) convertView.findViewById(R.id.filter_spinner_text);
    			convertView.setTag(holder);
    		} else {
    			holder = (ViewHolder) convertView.getTag();
    		}
    		//TextView label = (TextView) convertView.findViewById(R.id.type_spinner_text);
    		Filter filter = myFilters.get(position);
    		holder.filterName.setTextColor(Color.BLACK);
    		holder.filterName.setText(myFilters.get(position).getName());
    		holder.filterName.setTag(filter);
    		Log.v("TypeSpinner", myFilters.get(position).getName());
    		return convertView;
    	}
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
		//getMenuInflater().inflate(R.menu.add_edit_cat, myMenu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
    	case R.id.action_save_item:
    		mySaveFlistButtonAction(findViewById(android.R.id.content));
    		break;
    	case R.id.action_delete_item:
    		myDeleteFlistButtonAction(findViewById(android.R.id.content));
    		break;
    	case R.id.action_cancel_item:
    		finish();
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void myDeleteFlistButtonAction(View view) {
		boolean success = false;
		EditText editCategoryIdNumber = (EditText) findViewById(R.id.flist_edit_id); 
		int flistID = Integer.parseInt(editCategoryIdNumber.getText().toString());
		success = myListMan.rmFlist(flistID);
		String toastMessage;
		if (success) {
			toastMessage = "Flist Deleted!";
		} else { 
			toastMessage = "Fail!";
		}
		Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();

		// TODO: return result saying flist deleted and choose a sane default flist in FlipList
		Intent intent = new Intent();
		intent.putExtra("delFlistID", flistID);
		setResult(RESULT_DELETED, intent);
		super.finish();
	}
    public void mySaveFlistButtonAction(View view) {
    	ItemType flistType;
    	
    	EditText editFlistIdNumber = (EditText) findViewById(R.id.flist_edit_id);
    	EditText editFlistNameText = (EditText) findViewById(R.id.flist_edit_name);
    	String flistName = editFlistNameText.getText().toString();
    	
    	EditText editFlistDescText = (EditText) findViewById(R.id.flist_edit_desc);
    	String flistDesc = editFlistDescText.getText().toString();
    	
    	Spinner typeSpinner = (Spinner) findViewById(R.id.add_edit_flist_type_spinner);
    	Spinner filterSpinner = (Spinner) findViewById(R.id.add_edit_flist_filter_spinner);
    	Spinner defaultCategorySpinner = (Spinner) findViewById(R.id.add_edit_flist_default_category_spinner);
    	CheckBox visibleCheckBox = (CheckBox) findViewById(R.id.add_edit_flist_visible_check_box);
    	
    	flistType = (ItemType) typeSpinner.getItemAtPosition(typeSpinner.getSelectedItemPosition());
    	int flistTypeID = flistType.getID();
    	boolean isVisible = visibleCheckBox.isChecked();
    	int isVisibleInt = (isVisible) ? 1 : 0;
    	
    	//int filterID = (Integer) filterSpinner.getItemAtPosition(filterSpinner.getSelectedItemPosition());
    	
    	Flist myFlist = new Flist(flistName, flistDesc, flistTypeID, isVisibleInt);
    	myFlist.setFilterID(filterSpinner.getSelectedItemPosition());
    	myFlist.setType(typeSpinner.getSelectedItemPosition());
    	myFlist.setDefaultCategoryID(defaultCategorySpinner.getSelectedItemPosition());

    	if (currentFlist.isNew()) {
    		int newflistID = myListMan.addFlist(myFlist);
    		myFlist.setID(newflistID);
    		Log.v("AddEditFlistActivity.mySaveFlistButtonAction", "myListMan.addFlist: " + myFlist.getName());
    	} else {
    		int flistID = Integer.parseInt(editFlistIdNumber.getText().toString());
    		myFlist.setID(flistID);
    		myListMan.updateFlist(myFlist);
    	}
    	
    	Intent intent = new Intent();
    	intent.putExtra("flistID", myFlist.getID());
    	setResult(RESULT_OK, intent);
    	finish();
    }

}
