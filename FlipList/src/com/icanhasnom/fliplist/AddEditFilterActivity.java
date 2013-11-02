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
	Integer currentFlistID;
	Flist currentFlist;
	Integer currentTask;
	public final static int RESULT_DELETED = 3;

	ListManager myListMan;
	ArrayList<Flist> flistList;
	ArrayList<Filter> filterList;
	ArrayList<ItemType> typeList;
	SparseIntArray myFilterPositionMap;
	SparseIntArray myTypePositionMap;
	ActionBar actionBar;
	
	Spinner typeSpinner;
	Spinner filterSpinner;
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
		typeList = myListMan.getTypes();
		myFilterPositionMap = new SparseIntArray();
		myFilterPositionMap = buildFilterIndex(filterList);
		myTypePositionMap = new SparseIntArray();
		myTypePositionMap = buildTypeIndex(typeList);
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
	//	setContentView(R.layout.activity_add_edit_flist_list);
	//	addItemsOnEditList();
	//}
	//public void addNewFlist() {
    //	Flist newFlist = new Flist();
    //	newFlist.setIsNew();
    //	newFlist.setType(myListMan.defaultTypeID);
    //	currentFlist = newFlist;
    //	addEditFlist(newFlist);
	//}
	public void addNewFlist() {
    	Flist newFlist = new Flist();
    	newFlist.setIsNew();
    	newFlist.setType(myListMan.defaultTypeID);
    	currentFlist = newFlist;
    	editFlist(newFlist);
	}
	public SparseIntArray buildTypeIndex(ArrayList<ItemType> myTypeList) {
		Integer position = 0;
		SparseIntArray myPositionMap = new SparseIntArray();
		for (ItemType myFlist : myTypeList) {
			myPositionMap.put(myFlist.getID(), position);
			position++;
		}
		return myPositionMap;
	}
	public int getTypePosition(int myTypeID, SparseIntArray positionMap) {
		int myPosition = positionMap.get(myTypeID);
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

		// TODO: Do I need to pass in the ListFlist? or just use the global currentFlist

		boolean isNew = flist.isNew();
		boolean isVisible;
		Log.v("addEditFlist", "isNew: " + isNew);
    	
    	if (isNew) {
    		isVisible = true;
    	} else {
    		isVisible = flist.isVisible();
    	}

		// Get all of the View Objects
		EditText flistName = (EditText) findViewById(R.id.flist_edit_name);
		EditText flistDesc = (EditText) findViewById(R.id.flist_edit_desc);
		// Make this flistID a hidden text field or something to store the value
		EditText flistID = (EditText) findViewById(R.id.flist_edit_id);
		//typeSpinner = (Spinner) findViewById(R.id.flist_type_spinner);
		filterSpinner = (Spinner) findViewById(R.id.flist_edit_filter_spinner);
		CheckBox isVisibleBox = (CheckBox) findViewById(R.id.flist_visible_check_box);
		isVisibleBox.setChecked(isVisible);
		
		// Populate type spinner and select the categories type (will be default if new)
		addTypesToSpinner();
		typeSpinner.setSelection(flist.getType());
		addFiltersToSpinner();
		filterSpinner.setSelection(getFilterPosition(flist.getFilterID(), myFilterPositionMap));
		if (isNew) {
		} else {
			flistName.setText(flist.getName());
			flistDesc.setText(flist.getDescription());
			flistID.setText(String.valueOf(flist.getID()));
		}
		currentFlist = flist;
	}
	
    public void addTypesToSpinner() {
        ArrayList<ItemType> myTypeList = myListMan.getTypes();
        //typeSpinner = (Spinner) findViewById(R.id.flist_type_spinner);
        ArrayAdapter<ItemType> myTypeAdapter = new MyTypeSpinnerCustomAdapter(this, R.layout.activity_add_edit_flist, myTypeList);
        myTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(myTypeAdapter);
    }
    public void addFiltersToSpinner() {
        ArrayList<Filter> myFilterList = myListMan.getFilters();
        filterSpinner = (Spinner) findViewById(R.id.flist_edit_filter_spinner);
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
	
    //public void addItemsOnEditList() {
    //	// Populate the Flist List
    //	flistListDataAdapter = new MyCatListCustomAdapter(this, R.layout.activity_add_edit_flist_list, flistegoryList);
    //	ListView listView = (ListView) findViewById(R.id.itemAddEditList);
    //	listView.setAdapter(flistListDataAdapter);
    //	
    //	// Set up the onClick event for each of the list items
    //	listView.setOnItemClickListener(new OnItemClickListener() {
    //		public void onItemClick(AdapterView<?> parent, View view,
    //		int position, long id) {
    //			// When clicked, show a toast with the TextView text
    //			Flist flistegory = (Flist) parent.getItemAtPosition(position);
    //			Toast.makeText(getApplicationContext(),
    //					"Clicked on Row: " + category.getDescription(), 
    //					Toast.LENGTH_LONG).show();
    //			addEditFlist(category);
    //		}
    //	});
    //}
    /*
    private class MyCatListCustomAdapter extends ArrayAdapter<Flist> {
   	 
    	private ArrayList<Flist> categoryList;
    	 
    	public MyCatListCustomAdapter(Context context, int textViewResourceId, ArrayList<Flist> categoryList) {
    		super(context, textViewResourceId, categoryList);
    		this.categoryList = new ArrayList<Flist>();
    		this.categoryList.addAll(categoryList);
    	}
    	
    	private class ViewHolder {
    		TextView cat_list_text_view;
    		TextView cat_list_text_view_type;
    	}
    	 
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {

    		ViewHolder holder = null;
    		Log.v("ConvertView", String.valueOf(position));
    		Log.v("AddEditCatActivity", "convertView" + convertView);
    	 
    		if (convertView == null) {
    			LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			// TODO: Should I set null to parent? It errors if I do but I can't call the one below if i do.
    			convertView = vi.inflate(R.layout.add_edit_layout, null);
    	 
    			holder = new ViewHolder();
    			holder.cat_list_text_view = (TextView) convertView.findViewById(R.id.cat_list_text_view);
    			holder.cat_list_text_view_type = (TextView) convertView.findViewById(R.id.cat_list_text_view_type);

    			convertView.setTag(holder);
    			
    			View.OnClickListener categoryClickListener;
    			categoryClickListener = new View.OnClickListener() {  
    				public void onClick(View v) {  
    					// Use this onClick to send the user to the edit screen for the clicked category
    					TextView tv = (TextView) v;
    					Flist lc = (Flist) tv.getTag();
    					Log.v("AddEditCatActivity", "categoryClickListener - made it here");
    					// TODO: Probably only need one of these
    					currentFlist = lc;
    					//addEditFlist(lc);
    				}  
    			};
    	 
    			holder.cat_list_text_view.setOnClickListener( categoryClickListener );  
    			holder.cat_list_text_view.setOnClickListener( categoryClickListener );
    	   } 
    	   else {
    	    holder = (ViewHolder) convertView.getTag();
    		}
    	 
    		Flist category = categoryList.get(position);
    		// TODO: Make a function in ListManager to get and hold the category type names in memory
    		int categoryTypeID = category.getType();
    		//String categoryDescription = category.getDescription();
    		String categoryName = category.getName();
    		String categoryTypeName = myListMan.getFlistTypeName(categoryTypeID);
    		
    		Log.v("MyCatListCustomAdapter", "categoryTypeID:" + categoryTypeID);
    		Log.v("MyCatListCustomAdapter", "category.getDescription(): " + category.getDescription());
    		Log.v("MyCatListCustomAdapter", "myListMan.getFlistTypeName(): " + myListMan.getFlistTypeName(categoryTypeID));
    		
    		holder.cat_list_text_view.setText(categoryName);
    		holder.cat_list_text_view_type.setText("(" + categoryTypeName + ")");
    		holder.cat_list_text_view.setTag(category);
    		// TODO: Link this to type type editing activity later
    		holder.cat_list_text_view_type.setTag(category);
    	 
    		return convertView;
    	}
    	 
	}
	*/

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
    		mySaveCatButtonAction(findViewById(android.R.id.content));
    		break;
    	case R.id.action_delete_item:
    		myDeleteCatButtonAction(findViewById(android.R.id.content));
    		break;
    	case R.id.action_cancel_item:
    		finish();
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void myDeleteCatButtonAction(View view) {
		boolean success = false;
		EditText editFlistIdNumber = (EditText) findViewById(R.id.flist_edit_id); 
		int flistID = Integer.parseInt(editFlistIdNumber.getText().toString());
		success = myListMan.rmFlist(flistID);
		String toastMessage;
		if (success) {
			toastMessage = "List Deleted!";
		} else { 
			toastMessage = "Fail!";
		}
		Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();

		// TODO: return result saying flist deleted and choose a sane default flist in FlipList
		Intent intent = new Intent();
		intent.putExtra("delCatID", flistID);
		setResult(RESULT_DELETED, intent);
		super.finish();
	}
    public void mySaveCatButtonAction(View view) {
    	ItemType flistType;
    	
    	EditText editFlistIdNumber = (EditText) findViewById(R.id.flist_edit_id);
    	EditText editFlistNameText = (EditText) findViewById(R.id.flist_edit_name);
    	String flistName = editFlistNameText.getText().toString();
    	
    	EditText editFlistDescText = (EditText) findViewById(R.id.flist_edit_desc);
    	String flistDesc = editFlistDescText.getText().toString();
    	
    	//Spinner typeSpinner = (Spinner) findViewById(R.id.flist_type_spinner);
    	Spinner filterSpinner = (Spinner) findViewById(R.id.flist_edit_filter_spinner);
    	CheckBox visibleCheckBox = (CheckBox) findViewById(R.id.flist_visible_check_box);
    	
    	flistType = (ItemType) typeSpinner.getItemAtPosition(typeSpinner.getSelectedItemPosition());
    	int flistTypeID = flistType.getID();
    	boolean isVisible = visibleCheckBox.isChecked();
    	int isVisibleInt = (isVisible) ? 1 : 0;
    	
    	Flist myCat = new Flist(flistName, flistDesc, flistTypeID, isVisibleInt);
    	myCat.setFilterID(filterSpinner.getSelectedItemPosition());

    	if (currentFlist.isNew()) {
    		int newCatID = myListMan.addFlist(myCat);
    		myCat.setID(newCatID);
    	} else {
    		int flistID = Integer.parseInt(editFlistIdNumber.getText().toString());
    		myCat.setID(flistID);
    		myListMan.updateObjFlist(myCat);
    	}
    	
    	Intent intent = new Intent();
    	intent.putExtra("flistID", myCat.getID());
    	setResult(RESULT_OK, intent);
    	finish();
    }

}
