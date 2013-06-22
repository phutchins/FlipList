package com.icanhasnom.fliplist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
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
// Make type creation tool that lets you change all options and set sizes display characteristics


// Caching and Redrawing
// TODO: Only recreate the listmap if we've written, edited or deleted a category, item or type
// TODO: Pull all of the items at one time from the DB into a searchable hash for parsing into itemlists

// BUGS & FIXES
// TODO: Fix all back and "UP" buttons on navigation bar
// TODO: Fix unresponsive category listeners

// General Todo
// TODO: Make cancel button on item edit and category edit (or just use up button?)
// TODO: Make date/time selection on item edit layout
// TODO: Set input validity check for fields (Date)
// TODO: Make notes field work
// TODO: On category creation, get category settings from type if they exist


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FlipList extends Activity {
	public final static String EXTRA_MESSAGE = "com.icanhasnom.FlipList.MESSAGE";
	public static final String TAG = "FlipList";
	
    //ListManager myListMan = new ListManager();
	ListManager myListMan;
	int defaultCatID;
	
    HashMap<String, ListItem> checkListItems = new HashMap<String, ListItem>();
    ItemList currentItemList;
    ListCategory currentCategory = null;
    ListItem currentItem;
    ArrayList<ListCategory> catList;
    ArrayList<ListItem> currentListItems;
    
    MyCustomAdapter itemListDataAdapter;
    MyCatSpinnerCustomAdapter catSpinnerDataAdapter;
    MyCatSpinnerCustomAdapter itemCatSpinnerDataAdapter;
    
    Spinner catSpinner;
    Spinner itemCatSpinner;
    
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
    		currentItemList = myListMan.getItemList(defaultCatID);
    		currentCategory = myListMan.getCategory(defaultCatID);
        }
        
    	catSpinner = (Spinner) findViewById(R.id.catSpinner);
    	catSpinner.setOnItemSelectedListener(new SpinnerActivity());
    	
		// Do we need this? Might come in handy later...
		updateState();
		
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
    		addItemsOnList();
    	}
    	public void onNothingSelected(AdapterView<?> parent) {
    		// Nothing here yet
    	}
    }
    
    public void mySaveButtonAction(View view) {
       editText = (EditText) findViewById(R.id.editText);
       String name = editText.getText().toString();
       if (name.isEmpty()) {
			Toast.makeText(getApplicationContext(),
					"Please enter an item name!", Toast.LENGTH_LONG).show();
       } else {
	       catSpinner = (Spinner) findViewById(R.id.catSpinner);
	       int position = catSpinner.getSelectedItemPosition();
	       
	       currentCategory = (ListCategory) catSpinner.getItemAtPosition(position);
	       int catID = currentCategory.getID();
	       
	       myListMan.addItem(catID, name);
	
	       addItemsOnList();
	       editText.setText("");
       }
    }
    public void updateState() {
    	// Use this later if needed
    }
    public void addItemsOnSpinner() {
        catList = myListMan.getCategoryList();
        
        catSpinnerDataAdapter = new MyCatSpinnerCustomAdapter(this, R.layout.activity_main_cat_spinner, catList);
        catSpinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        catSpinner.setAdapter(catSpinnerDataAdapter);
        catSpinner.setOnItemSelectedListener(new SpinnerActivity());
        
        // TODO: Get this setting the lastSelected or Current category
        Log.v("FlipList.addItemsOnSpinner", "currentCategory.getID:" + currentCategory.getID());
        catSpinner.setSelection(catSpinnerDataAdapter.getPosition(currentCategory.getID()));
        catSpinnerDataAdapter.notifyDataSetChanged();
    }
    
    public void addItemsOnList()  {
    	int catID = currentCategory.getID();
		currentItemList = myListMan.getItemList(catID);
		currentListItems = currentItemList.getListItems();
    	itemListDataAdapter = new MyCustomAdapter(this, R.layout.list_layout, currentListItems);
    	ListView listView = (ListView) findViewById(R.id.itemList);
    	listView.setAdapter(itemListDataAdapter);
    	
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			ListItem item = (ListItem) parent.getItemAtPosition(position);
    			editListItem(item);
    			Toast.makeText(getApplicationContext(),
    					"Clicked on Row: " + item.getName(), 
    					Toast.LENGTH_LONG).show();
    		}
    	});
    }
    public void editListItem(ListItem item) {
    	currentItem = item;
    	Date dueDateObj = null;
    	setContentView(R.layout.item_edit_layout);
    	EditText itemIDTv = (EditText) findViewById(R.id.item_edit_id_edittext);
    	EditText itemNameTv = (EditText) findViewById(R.id.item_edit_name_edittext);
    	EditText itemDescTv = (EditText) findViewById(R.id.item_edit_description_edittext);
    	itemCatSpinner = (Spinner) findViewById(R.id.item_edit_category_spinner);
    	EditText itemNotesTv = (EditText) findViewById(R.id.item_edit_notes_edittext);
    	Button editTimeButton = (Button) findViewById(R.id.time_edit_button);
    	Button editDateButton = (Button) findViewById(R.id.date_edit_button);
    	
    	itemIDTv.setText(String.valueOf(item.getID()));
    	itemNameTv.setText(item.getName());
    	itemDescTv.setText(item.getDescription());
    	itemNotesTv.setText(item.getNotes());

    	dueDateObj = item.getDueDateObj();

    	String myDate = item.getDueDatePretty();
    	String myTime = item.getDueTimePretty();

    	editDateButton.setText(myDate);
    	editTimeButton.setText(myTime);
    	
        catList = myListMan.getCategoryList();
    	// TODO: Make this into a generic addItemsToSpinner method that I can use with the first spinner also
        ArrayAdapter<ListCategory> itemCatSpinnerDataAdapter = new MyCatSpinnerCustomAdapter(this, R.layout.item_edit_layout, catList);
        itemCatSpinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemCatSpinner.setAdapter(itemCatSpinnerDataAdapter);
        
        int myCatID = item.getPrimaryCat();
        int spinnerPosition = catSpinnerDataAdapter.getPosition(myCatID);
        itemCatSpinner.setSelection(spinnerPosition);

        itemNotesTv.setText(item.getNotes());
    }
    public void itemEditSaveButtonAction(View view) {
    	EditText itemIDTv = (EditText) findViewById(R.id.item_edit_id_edittext);
    	EditText itemNameTv = (EditText) findViewById(R.id.item_edit_name_edittext);
    	EditText itemDescTv = (EditText) findViewById(R.id.item_edit_description_edittext);
    	Spinner itemCatSpinner = (Spinner) findViewById(R.id.item_edit_category_spinner);
    	EditText itemNotesTv = (EditText) findViewById(R.id.item_edit_notes_edittext);
    	Button itemDueTimeBtn = (Button) findViewById(R.id.time_edit_button);
    	Button itemDueDateBtn = (Button) findViewById(R.id.date_edit_button);
    	
    	int itemID = Integer.parseInt(itemIDTv.getText().toString());
    	String itemName = itemNameTv.getText().toString();
    	String itemDesc = itemDescTv.getText().toString();
    	ListCategory itemCategory = (ListCategory) itemCatSpinner.getItemAtPosition(itemCatSpinner.getSelectedItemPosition());
    	int itemCategoryID = itemCategory.getID();
    	String itemNotes = itemNotesTv.getText().toString();
    	String itemDueTime = (String) itemDueTimeBtn.getText();
    	String itemDueDate = (String) itemDueDateBtn.getText();
    	
    	ListItem myItem = new ListItem();
    	myItem.setID(itemID);
    	myItem.setName(itemName);
    	myItem.setDescription(itemDesc);
    	myItem.setPrimaryCat(itemCategoryID);
    	myItem.setNotes(itemNotes);
    	myItem.setDueTime(itemDueTime);
    	myItem.setDueDate(itemDueDate);
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
    	LayoutInflater inflater;
    	//Map<Integer, Integer> myPositionMap = new HashMap<Integer, Integer>();
    	SparseIntArray myPositionMap;
    	 
    	public MyCatSpinnerCustomAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<ListCategory> objects) {
    		super(activitySpinner, textViewResourceId, objects);
    		this.categoryList = (ArrayList<ListCategory>) objects;
    		this.activity = activitySpinner;
    		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		myPositionMap = new SparseIntArray();
    	}
    	public int getPosition(int myListCategoryID) {
    		int myPosition = myPositionMap.get(myListCategoryID);
			return myPosition;
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
    		
			myPositionMap.put(category.getID(), position);
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
		ListCategory catSelectedObj = (ListCategory) catSpinner.getSelectedItem();
    	 
    	public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<ListItem> itemList) {
    		super(context, textViewResourceId, itemList);
    		this.itemList = new ArrayList<ListItem>();
    		this.itemList.addAll(itemList);
    	}
    	 
    	private class ViewHolder {
    		TextView itemName;
    		TextView itemInfo;
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
    			holder.itemInfo = (TextView) convertView.findViewById(R.id.itemInfo);
    			
    			convertView.setTag(holder);
    	 
    			holder.itemCheckBox.setOnClickListener( new View.OnClickListener() {  
    				public void onClick(View v) {  
    					CheckBox cb = (CheckBox) v ;  
    					ListItem item = (ListItem) cb.getTag();
    					Spinner catSpinner = (Spinner) findViewById(R.id.catSpinner);

    					int catSelected = catSelectedObj.getID();
    					//String itemDescription = item.getDescription();
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
    		holder.itemName.setText(item.getName());
    		String infoString = "";
    		if (catSelectedObj.showDueDate()) {
	    		if (item.hasDueDate() == true ) {
	    			infoString = " (" + item.getDueDatePretty() + ")";
	    		}
    		}
	    	if (catSelectedObj.showDescription()) {
	    		infoString = infoString + " (" + item.getDescription() + ")";
    		}
    		holder.itemInfo.setText(infoString);
    		holder.itemCheckBox.setText("");
    		holder.itemCheckBox.setTag(item);
    	 
    		return convertView;
    	 
    	}
    	 
	}
    
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
		
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Button setTimeBtn = (Button) view.findViewById(R.id.time_edit_button);
			
			setTimeBtn.setText(hourOfDay + ":" + minute);
		}
	}
    
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
    
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
		
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			Button setDateBtn = (Button) view.findViewById(R.id.date_edit_button);
			
			setDateBtn.setText(month + ", " + day + " " + year);
		}
	}
    
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
}
