package com.icanhasnom.fliplist;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

public class AddEditCatActivity extends Activity {
	ListCategory currentCategory;
	MyCatListCustomAdapter catListDataAdapter;

	ListManager myListMan;
	ArrayList<ListCategory> categoryList;
	//DatabaseHandler db;
	
	Spinner typeSpinner;
	MyTypeSpinnerCustomAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit_cat_list);

		//catList = getIntent().getExtras().getStringArray("catList");
		
		myListMan = new ListManager(this);
        categoryList = myListMan.getCategoryList();
		
		addItemsOnEditList();
		
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Set up category list and add an add new category item to the top of the list
		// Create listener that gets category to edit or new category selection
		// Send that selection to the edit layout and display layout
	}
	public void myAddNewCategoryAction(View view) {
    	// Create blank new category to add to the list so there is an option to add new at the top
    	ListCategory newCategory = new ListCategory();
    	newCategory.setIsNew();
    	//newCategory.setName("+ Add New Category");
    	newCategory.setType(myListMan.defaultTypeID);
    	
    	addEditCategory(newCategory);
	}
	public void addEditCategory(ListCategory lc) {
		// TODO: Do I need to pass in the ListCategory? or just use the global currentCategory
		setContentView(R.layout.activity_add_edit_cat);
		boolean isNew = lc.isNew();
		Log.v("addEditCategory", "isNew: " + isNew);
		
		// Get all of the View Objects
		CheckedTextView pageTitle = (CheckedTextView) findViewById(R.id.catNameOrNewCat);
		EditText catName = (EditText) findViewById(R.id.cat_edit_name);
		EditText catDesc = (EditText) findViewById(R.id.cat_edit_desc);
		// Make this catID a hidden text field or something to store the value
		EditText catID = (EditText) findViewById(R.id.cat_edit_id);
		typeSpinner = (Spinner) findViewById(R.id.cat_type_spinner);
		
		// Populate type spinner and select the categories type (will be default if new)
		addTypesToSpinner();
		typeSpinner.setSelection(lc.getType());
		
		// fill out layout variables using the currentCategory object
		if (isNew) {
			pageTitle.setText("New Category");
		} else {
			pageTitle.setText("Edit Category");
			catName.setText(lc.getName());
			catDesc.setText(lc.getDescription());
			//Log.v("addEditCategory", "lc.getID(): " + lc.getID());
			//Log.v("addEditCategory", "currentCategory.getID(): " + currentCategory.getID());
			catID.setText(String.valueOf(lc.getID()));
		}
		currentCategory = lc;
	}
	
    public void addTypesToSpinner() {
        ArrayList<CategoryType> myTypeList = myListMan.getCategoryTypesList();
        typeSpinner = (Spinner) findViewById(R.id.cat_type_spinner);
        ArrayAdapter<CategoryType> myTypeAdapter = new MyTypeSpinnerCustomAdapter(this, R.layout.activity_add_edit_cat, myTypeList);
        myTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(myTypeAdapter);
    }
    
    public class MyTypeSpinnerCustomAdapter extends ArrayAdapter<CategoryType>{
    	private Activity activity;
    	//private Context context;
    	private ArrayList<CategoryType> myTypes;
    	LayoutInflater inflater;
    	
    	public MyTypeSpinnerCustomAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<CategoryType> myTypes) {
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
    	public CategoryType getItem(int position) {
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
    		CategoryType type = myTypes.get(position);
    		holder.typeName.setTextColor(Color.BLACK);
    		holder.typeName.setText(myTypes.get(position).getName());
    		holder.typeName.setTag(type);
    		Log.v("TypeSpinner", myTypes.get(position).getName());
    		return convertView;
    	}
    }
	
    public void addItemsOnEditList() {
    	// Populate the Category List
    	catListDataAdapter = new MyCatListCustomAdapter(this, R.layout.activity_add_edit_cat_list, categoryList);
    	ListView listView = (ListView) findViewById(R.id.itemAddEditList);
    	listView.setAdapter(catListDataAdapter);
    	
    	// Set up the onClick event for each of the list items
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view,
    		int position, long id) {
    			// When clicked, show a toast with the TextView text
    			ListItem item = (ListItem) parent.getItemAtPosition(position);
    			Toast.makeText(getApplicationContext(),
    					"Clicked on Row: " + item.getDescription(), 
    					Toast.LENGTH_LONG).show();
    		}
    	});
    }
    

	
    private class MyCatListCustomAdapter extends ArrayAdapter<ListCategory> {
   	 
    	private ArrayList<ListCategory> categoryList;
    	 
    	public MyCatListCustomAdapter(Context context, int textViewResourceId, ArrayList<ListCategory> categoryList) {
    		super(context, textViewResourceId, categoryList);
    		this.categoryList = new ArrayList<ListCategory>();
    		this.categoryList.addAll(categoryList);
    	}
    	
    	private class ViewHolder {
    		TextView cat_list_text_view;
    	}
    	 
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {

    		ViewHolder holder = null;
    		Log.v("ConvertView", String.valueOf(position));
    	 
    		if (convertView == null) {
    			LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			convertView = vi.inflate(R.layout.add_edit_layout, null);
    	 
    			holder = new ViewHolder();
    			holder.cat_list_text_view = (TextView) convertView.findViewById(R.id.cat_list_text_view);

    			convertView.setTag(holder);
    	 
    			holder.cat_list_text_view.setOnClickListener( new View.OnClickListener() {  
    				public void onClick(View v) {  
    					// Use this onClick to send the user to the edit screen for the clicked category
    					TextView tv = (TextView) v;
    					ListCategory lc = (ListCategory) tv.getTag();
    					
    					// TODO: Probably only need one of these
    					currentCategory = lc;
    					addEditCategory(lc);
    				}  
    			});  
    	   } 
    	   else {
    	    holder = (ViewHolder) convertView.getTag();
    		}
    	 
    		ListCategory category = categoryList.get(position);
    		// TODO: Make a function in ListManager to get and hold the category type names in memory
    		int categoryTypeID = category.getType();
    		//String categoryDescription = category.getDescription();
    		String categoryName = category.getName();
    		String categoryTypeName = myListMan.getCategoryTypeName(categoryTypeID);
    		
    		Log.v("MyCatListCustomAdapter", "categoryTypeID:" + categoryTypeID);
    		Log.v("MyCatListCustomAdapter", "category.getDescription(): " + category.getDescription());
    		Log.v("MyCatListCustomAdapter", "myListMan.getCategoryTypeName(): " + myListMan.getCategoryTypeName(categoryTypeID));
    		
    		holder.cat_list_text_view.setText(categoryName + "(" + categoryTypeName + ")");
    		holder.cat_list_text_view.setTag(category);
    	 
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_edit_cat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: Fix this - read page above to make this go to fliplist activity
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void myDeleteCatButtonAction(View view) {
		boolean success = false;
		EditText editCategoryIdNumber = (EditText) findViewById(R.id.cat_edit_id); 
		int categoryID = Integer.parseInt(editCategoryIdNumber.getText().toString());
		success = myListMan.rmCategory(categoryID);
		
		// TODO: Do something with success to notify user of success or not
		//       Use a toast to display success or failure
		Intent flipList = new Intent(this, FlipList.class);
		this.startActivity(flipList);
	}
    public void mySaveCatButtonAction(View view) {
    	CategoryType categoryType;
    	
    	// Make this a hidden field
    	EditText editCategoryIdNumber = (EditText) findViewById(R.id.cat_edit_id);
    	EditText editCategoryNameText = (EditText) findViewById(R.id.cat_edit_name);
    	String categoryName = editCategoryNameText.getText().toString();
    	
    	EditText editCategoryDescText = (EditText) findViewById(R.id.cat_edit_desc);
    	String categoryDesc = editCategoryDescText.getText().toString();
    	
    	Spinner typeSpinner = (Spinner) findViewById(R.id.cat_type_spinner);
    	
    	// TODO: Fix this
    	int position = typeSpinner.getSelectedItemPosition();
    	categoryType = (CategoryType) typeSpinner.getItemAtPosition(position);
    	int categoryTypeID = categoryType.getID();
    	

    	Log.v("MySaveCatButtonAction", "categoryName: " + categoryName);
    	Log.v("MySaveCatButtonAction", "categoryDesc: " + categoryDesc);
    	Log.v("MySaveCatButtonAction", "categoryTypeID: " + categoryTypeID);
    	
    	ListCategory myNewCat = new ListCategory(categoryName, categoryDesc, categoryTypeID);

    	if (currentCategory.isNew()) {
    		Log.v("AddEditCatActivity", "Adding new category " + currentCategory.getName());
    		myListMan.addCategory(myNewCat);
    	} else {
    		int categoryID = Integer.parseInt(editCategoryIdNumber.getText().toString());
    		myNewCat.setID(categoryID);
        	Log.v("MySaveCatButtonAction", "categoryID: " + categoryID);
    		Log.v("AddEditCatActivity", "Updating category " + currentCategory.getName());
    		myListMan.updateObjCategory(myNewCat);
    	}
    	
		Intent flipList = new Intent(this, FlipList.class);
		//flipList.putExtra("myNewCat", myNewCat);
		this.startActivity(flipList);
    }

}
