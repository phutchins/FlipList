package com.icanhasnom.fliplist;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

// ToDo
// Make list items clickable to display more info and have edit button
// Ability to assign multiple categories to tasks
// Format list item list display to show tiny date below description to leave more room for actual description

// UI Design
// Make 3 tabs, one for category list, one for task list, one for something else
//   like its done in the tunein android app
// Make buttons smoother
// Add swipe sideways from category to category?
// Third tab could be Item Edit or Add screen. If you select an item it slides over to
//    the right and you can edit. If nothing is selected and you swipe, it lets you add one.

// Settings Design
// Make settings menu -
//    Default category setting
//    Choose what values are displayed for list item on main screen
//    Default list type (grocery, todo, etc...)


// Filter Design (Acts as a categoryList item?)
//   Ability to make custom filters

// Widget Design
// Make widget class that does only one category (for grocery list etc...)

// Types Design
// Make certain list types change the defaults for items added
//   Grocery List type would not have a date by default and no reminders
//   Have it learn common items and group them by section in store


public class FlipList extends Activity {
	public final static String EXTRA_MESSAGE = "com.icanhasnom.FlipList.MESSAGE";
	public static final String TAG = "FlipList";
	
    //ListManager myListMan = new ListManager();
	ListManager myListMan;
	int defaultCatID;
	
    HashMap<String, ListItem> checkListItems = new HashMap<String, ListItem>();
    ItemList currentItemList;
    ListCategory currentCategory;
    ArrayList<ListCategory> catList;
    ArrayList<ListItem> currentListItems;
    
    MyCustomAdapter itemListDataAdapter;
    MyCatSpinnerCustomAdapter catSpinnerDataAdapter;
    
    Spinner catSpinner;
    
    EditText editText;
    DatabaseHandler db;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Fix this, its returning null
        //defaultCatID = db.getDefaultCatID();
        defaultCatID = 1;
        
        db = new DatabaseHandler(this);
        
        if(savedInstanceState != null) {
        	restoreState(savedInstanceState);
        	Log.v("FlipList.onCreate", "Restoring saved instance state");
        } else {
        	// Load saved data from somewhere, maybe SqlLite
        	myListMan = new ListManager(this);
        	Log.v("FlipList.onCreate", "Created new ListManager Object");
        }
        
    	catSpinner = (Spinner) findViewById(R.id.catSpinner);
    	catSpinner.setOnItemSelectedListener(new SpinnerActivity());
    	//selectedCategory = String.valueOf(catSpinner.getSelectedItem());

		currentItemList = myListMan.getItemList(defaultCatID);

    	//itemListDataAdapter = new MyCustomAdapter(this, R.layout.activity_main, currentItemList);
    	//catList = myListMan.getCategoryList();
        //spinnerDataAdapter = (MyCatSpinnerCustomAdapter) new ArrayAdapter<ListCategory>(this, android.R.layout.simple_spinner_item, catList);

		// Do we need this? Might come in handy later...
		updateState();
		
		// Populate the screen
        addItemsOnSpinner();
        addItemsOnList();
    }
    
    public void restoreState(Bundle savedInstanceState) {
    	myListMan = (ListManager) savedInstanceState.getSerializable("ListManager");
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	savedInstanceState.putSerializable("ListManager", myListMan);
    	super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.menu_add_edit_cat:
    		Intent addEditCatIntent = new Intent(this, AddEditCatActivity.class);
    		this.startActivity(addEditCatIntent);
    		break;
    	case R.id.menu_settings:
    		//open settings menu
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    	return true;
    	
    }
    
    public class SpinnerActivity extends Activity implements OnItemSelectedListener {
    	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    		// String itemSelected = (String) parent.getItemAtPosition(pos);
    		Log.v("FlipList.SpinnerActivity", "Inside SpinnerActivity");
    		// Do stuff with clicked category here
    		addItemsOnList();
    		// Refresh spinner here?
    		
    	}
    	public void onNothingSelected(AdapterView<?> parent) {
    		// Nothing here yet
    	}
    }
    
    public void mySaveButtonAction(View view) {
       Date dueDate = new Date();
       editText = (EditText) findViewById(R.id.editText);
       String name = editText.getText().toString();
       //String description = editText.getText().toString();
       String description = "blank";
       
       catSpinner = (Spinner) findViewById(R.id.catSpinner);
       //selectedCategory = String.valueOf(catSpinner.getSelectedItem());
       //ListCategory selectedCategoryObj = (ListCategory) catSpinner.getTag();
       int position = catSpinner.getSelectedItemPosition();
       ListCategory selectedCategory = (ListCategory) catSpinner.getItemAtPosition(position);
       
       Log.v(TAG, "mySaveButtonAction: (below selectedCategory assignment)");
       Log.v(TAG, "Position: " + position);
       Log.v(TAG, "selectedCategoryObj Name: " + selectedCategory.getName());
       
       int catID = selectedCategory.getID();
       ListItem myItem = myListMan.addItem(catID, name, description, dueDate);
       Log.v(TAG, "Added Item: " + myItem.getName());

       addItemsOnList();
       editText.setText("");
    }
    public void updateState() {
    	// Use this later if needed
    }
    public void addItemsOnSpinner() {
    	// TODO: Do we need to set current selected category here?
        catList = myListMan.getCategoryList();
        Resources res = getResources();
        
        catSpinnerDataAdapter = new MyCatSpinnerCustomAdapter(this, R.layout.activity_main_cat_spinner, catList, res);
        catSpinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        catSpinner.setAdapter(catSpinnerDataAdapter);
        catSpinner.setOnItemSelectedListener(new SpinnerActivity());
        
        // Set the current category globally
    	int position = catSpinner.getSelectedItemPosition();
    	currentCategory = (ListCategory) catSpinner.getItemAtPosition(position);
        
        // Run this to update spinner
        catSpinnerDataAdapter.notifyDataSetChanged();
    }
    
    public void addItemsOnList()  {
    	// Line below works
    	Log.v("FlipList.addItemsOnList", "selectedCategory: " + currentCategory.getName());
    	int catID = currentCategory.getID();
    	String catName = currentCategory.getName();
    	Log.v("FlipList.addItemsOnList", "catID: " + catID);

		currentItemList = myListMan.getItemList(catID);
		Log.v("FlipList.addItemsOnList", "catID: " + catID);
		Log.v("FlipList.addItemsOnList", "catName: " + catName);
		Log.v("FlipList.addItemsOnList", "currentItemList: " + currentItemList.listID);
		
		currentListItems = currentItemList.getListItems();

		Log.v("FlipList.addItemsOnList", "currentListItems.size(): " + currentListItems.size());

    	itemListDataAdapter = new MyCustomAdapter(this, R.layout.list_layout, currentListItems);
    	
    	ListView listView = (ListView) findViewById(R.id.itemList);
    	listView.setAdapter(itemListDataAdapter);
    	
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			// When clicked, show a toast with the TextView text
    			ListItem item = (ListItem) parent.getItemAtPosition(position);
    			Log.v("FlipList.addItemsOnList.setOnItemClickListener", "position: " + position + " id " + id);
    			Toast.makeText(getApplicationContext(),
    					"Clicked on Row: " + item.getName(), 
    					Toast.LENGTH_LONG).show();
    		}
    	});
    }
    
    private class MyCatSpinnerCustomAdapter extends ArrayAdapter<ListCategory> {
      	 
    	private ArrayList<ListCategory> categoryList;
    	private Activity activity;
    	// res could be used for populating images
    	public Resources res;
    	LayoutInflater inflater;
    	 
    	public MyCatSpinnerCustomAdapter(FlipList activitySpinner, int textViewResourceId, ArrayList<ListCategory> objects, Resources resLocal) {
    		super(activitySpinner, textViewResourceId, objects);
    		categoryList = (ArrayList<ListCategory>) objects;
    		res = resLocal;
    		activity = activitySpinner;
    		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	private class ViewHolder {
    		TextView catName;
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
    			convertView = inflater.inflate(R.layout.activity_main_cat_spinner, null);
    			holder = new ViewHolder();
    			holder.catName = (TextView) convertView.findViewById(R.id.cat_spinner_text);
    			convertView.setTag(holder);
    		} else {
    			holder = (ViewHolder) convertView.getTag();
    		}
    		ListCategory category = categoryList.get(position);
    		holder.catName.setText(category.getName());
    		holder.catName.setTag(category);
    		return convertView;
    	}
    	 
	}
    
    public class editListItem {
    	// Make this send the item to edit to the edit view/layout
    	public editListItem(ListItem item) {
    		// grab the item to be edited
    	}
    }
    
    private class MyCustomAdapter extends ArrayAdapter<ListItem> {
    	 
    	private ArrayList<ListItem> itemList;
    	 
    	public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<ListItem> itemList) {
    		super(context, textViewResourceId, itemList);
    		this.itemList = new ArrayList<ListItem>();
    		this.itemList.addAll(itemList);
    	}
    	 
    	private class ViewHolder {
    		TextView itemName;
    		CheckBox itemCheckBox;
    	}
    	 
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    	 
    		ViewHolder holder = null;
    		Log.v("ConvertView", String.valueOf(position));
    	 
    		if (convertView == null) {
    			LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			convertView = vi.inflate(R.layout.list_layout, null);
    	 
    			holder = new ViewHolder();
    			holder.itemCheckBox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
    			holder.itemName = (TextView) convertView.findViewById(R.id.itemName);
    			
    			convertView.setTag(holder);
    	 
    			holder.itemCheckBox.setOnClickListener( new View.OnClickListener() {  
    				public void onClick(View v) {  
    					CheckBox cb = (CheckBox) v ;  
    					ListItem item = (ListItem) cb.getTag();
    					Spinner catSpinner = (Spinner) findViewById(R.id.catSpinner);
    					ListCategory catSelectedObj = (ListCategory) catSpinner.getSelectedItem();
    					int catSelected = catSelectedObj.getID();
    					String itemDescription = item.getDescription();
    					String itemName = item.getName();
    					Toast.makeText(getApplicationContext(), "Completed: " + itemName, Toast.LENGTH_LONG).show();
    					myListMan.completeItem(item, catSelected);
    					addItemsOnList();
    				}  
    			});  
    	   } 
    	   else {
    	    holder = (ViewHolder) convertView.getTag();
    		}
    	 
    		ListItem item = itemList.get(position);
    		holder.itemName.setText(item.getName() + " (" +  item.getDueDate() + ")" + "(" + item.getID() + ")");
    		holder.itemCheckBox.setText("");
    		holder.itemCheckBox.setTag(item);
    	 
    		return convertView;
    	 
    	}
    	 
	}
}
