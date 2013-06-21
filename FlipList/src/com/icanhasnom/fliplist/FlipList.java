package com.icanhasnom.fliplist;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

// Category Design
//   In addition to having a type, categories should have the ability to choose the behavior when items are checked
//     they could (1) go away immediately (2) stay for X time (3) Disappear after X other items are checked


// Filter Design (Acts as a categoryList item?)
//   Ability to make custom filters

// Widget Design
// Make widget class that does only one category (for grocery list etc...)

// Types Design
// Make certain list types change the defaults for items added
//   Grocery List type would not have a date by default and no reminders
//   Have it learn common items and group them by section in store
//   Password list type - make list passworded and encrypt the list items
//   Songs - have it link you to places to download them
//           maybe have it add list items by using a song finder app

// Caching and Redrawing
// TODO: Only recreate the listmap if we've written, edited or deleted a category, item or type
// TODO: Pull all of the items at one time from the DB into a searchable hash for parsing into itemlists

// BUGS & FIXES
// TODO: Fix all back and "UP" buttons on navigation bar
// TODO: Fix unresponsive category listeners

// General Todo
// TODO: Make cancel button on item edit and category edit (or just use up button?)
// TODO: Make date/time selection on item edit layout
// TODO: Make notes field work
// TODO: 


public class FlipList extends Activity {
	public final static String EXTRA_MESSAGE = "com.icanhasnom.FlipList.MESSAGE";
	public static final String TAG = "FlipList";
	
    //ListManager myListMan = new ListManager();
	ListManager myListMan;
	int defaultCatID;
	
    HashMap<String, ListItem> checkListItems = new HashMap<String, ListItem>();
    ItemList currentItemList;
    ListCategory currentCategory;
    ListItem currentItem;
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
    		ListCategory itemSelected = (ListCategory) parent.getItemAtPosition(pos);
    		currentCategory = itemSelected;
    		Log.v("FlipList.SpinnerActivity", "itemSelected (cast to ListCategory) name: " + itemSelected.getName());
    		addItemsOnList();
    	}
    	public void onNothingSelected(AdapterView<?> parent) {
    		// Nothing here yet
    	}
    }
    
    public void mySaveButtonAction(View view) {
    	//This is only temporary until I make the DueDate input fields
        Date date = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String dueDate = sdf.format(date);

       // Putting the below in the DB handler so we can just pass objects in and out from the DB layer
       //int intDueDate = (int) dueDate.getTime();

       // Not sure if i should use this way
       //DateFormat myDate = new SimpleDateFormat ("yyy-MM-dd'T'HH:mm:ssZ");
       //String dateStr = myDate.format(dueDate);
       
       editText = (EditText) findViewById(R.id.editText);
       String name = editText.getText().toString();
       if (name == null) {
			Toast.makeText(getApplicationContext(),
					"Please enter an item name!", Toast.LENGTH_LONG).show();
       } else {
	       //String description = editText.getText().toString();
	       String description = "blank";
	       
	       catSpinner = (Spinner) findViewById(R.id.catSpinner);
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
    			// This is run when the item NAME is clicked
    			// We will send the user to item edit layout for this eventually
    			ListItem item = (ListItem) parent.getItemAtPosition(position);
    			Log.v("FlipList.addItemsOnList.setOnItemClickListener", "position: " + position + " id " + id);
    			editListItem(item);
    			Toast.makeText(getApplicationContext(),
    					"Clicked on Row: " + item.getName(), 
    					Toast.LENGTH_LONG).show();
    		}
    	});
    }
    public void editListItem(ListItem item) {
    	currentItem = item;
    	setContentView(R.layout.item_edit_layout);
    	EditText itemIDTv = (EditText) findViewById(R.id.item_edit_id_edittext);
    	EditText itemNameTv = (EditText) findViewById(R.id.item_edit_name_edittext);
    	EditText itemDescTv = (EditText) findViewById(R.id.item_edit_description_edittext);
    	Spinner itemCatSpinner = (Spinner) findViewById(R.id.item_edit_category_spinner);
    	EditText itemNotesTv = (EditText) findViewById(R.id.item_edit_notes_edittext);
    	EditText itemDueTv = (EditText) findViewById(R.id.item_edit_due_edittext);
    	
    	
    	itemIDTv.setText(String.valueOf(item.getID()));
    	itemNameTv.setText(item.getName());
    	itemDescTv.setText(item.getDescription());
    	itemNotesTv.setText(item.getNotes());
    	itemDueTv.setText(item.getDueDate());
    	
    	// TODO: Make this into a generic addItemsToSpinner method that I can use with the first spinner also
        catList = myListMan.getCategoryList();
        Resources res = getResources();
        catSpinnerDataAdapter = new MyCatSpinnerCustomAdapter(this, R.layout.item_edit_layout, catList, res);
        catSpinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemCatSpinner.setAdapter(catSpinnerDataAdapter);
    	//int position = catSpinner.getSelectedItemPosition();
    	//currentCategory = (ListCategory) catSpinner.getItemAtPosition(position);
        // Run this to update spinner
        //catSpinnerDataAdapter.notifyDataSetChanged();
        catSpinner.setSelection(item.getPrimaryCat());
        
        itemNotesTv.setText(item.getNotes());
    }
    public void itemEditSaveButtonAction(View view) {
    	EditText itemIDTv = (EditText) findViewById(R.id.item_edit_id_edittext);
    	EditText itemNameTv = (EditText) findViewById(R.id.item_edit_name_edittext);
    	EditText itemDescTv = (EditText) findViewById(R.id.item_edit_description_edittext);
    	Spinner itemCatSpinner = (Spinner) findViewById(R.id.item_edit_category_spinner);
    	EditText itemNotesTv = (EditText) findViewById(R.id.item_edit_notes_edittext);
    	EditText itemDueTv = (EditText) findViewById(R.id.item_edit_due_edittext);
    	
    	int itemID = Integer.parseInt(itemIDTv.getText().toString());
    	String itemName = itemNameTv.getText().toString();
    	String itemDesc = itemDescTv.getText().toString();
    	//int itemCategoryPosition = itemCatSpinner.getSelectedItemPosition();
    	ListCategory itemCategory = (ListCategory) itemCatSpinner.getItemAtPosition(itemCatSpinner.getSelectedItemPosition());
    	int itemCategoryID = itemCategory.getID();
    	String itemNotes = itemNotesTv.getText().toString();
    	String itemDue = itemDueTv.getText().toString();
    	
    	ListItem myItem = new ListItem();
    	myItem.setID(itemID);
    	myItem.setName(itemName);
    	myItem.setDescription(itemDesc);
    	myItem.setPrimaryCat(itemCategoryID);
    	myItem.setNotes(itemNotes);
    	myItem.setDueDate(itemDue);
    	myItem.setCreateDate(currentItem.getCreateDate());
    	
    	//Set up constructor to be able to take all this stuff
    	//myListMan.updateItem(new ListItem(itemCategoryID, itemID, itemName, itemDesc));
    	myListMan.updateItem(myItem);
    	
		Intent flipList = new Intent(this, FlipList.class);
		this.startActivity(flipList);
    }
    public void itemEditDeleteButtonAction(View view) {
    	myListMan.deleteItem(currentItem);
		Intent flipList = new Intent(this, FlipList.class);
		this.startActivity(flipList);
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
    		ListCategory category = categoryList.get(position);
    		Log.v("FlipList", "getCustomView " + category.name + " visible is " + category.isVisible());

    		if (convertView == null) {
    			convertView = inflater.inflate(R.layout.activity_main_cat_spinner, null);
    			holder = new ViewHolder();
    			holder.catName = (TextView) convertView.findViewById(R.id.cat_spinner_text);
    			convertView.setTag(holder);
        		
        		//Log.v("FlipList", "CatSpinner - Category not visible, set to GONE!");
        		//holder.catName.setVisibility(View.GONE);


    		} else {
    			holder = (ViewHolder) convertView.getTag();
    		}
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
