package com.icanhasnom.fliplist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddEditItemActivity extends Activity {
	Item currentItem;
	ListManager myListMan;
	
    Spinner itemCatSpinner;
    ArrayList<Category> catList;
    MyCatSpinnerCustomAdapter catSpinnerDataAdapter;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit_item);
		myListMan = new ListManager(this);
		Item myItem = null;
		Bundle b = this.getIntent().getExtras();
		if(b != null) {
			myItem = (Item) b.getSerializable("item");
			Log.v("AddEditItemActivity.onCreate", "Getting Serialized Item: " + myItem.getName());
		}
        catSpinnerDataAdapter = new MyCatSpinnerCustomAdapter(this, R.layout.activity_main_cat_spinner, catList);
        catSpinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		editListItem(myItem);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_edit_item, menu);
		return true;
	}
	
    public void editListItem(Item item) {
    	currentItem = item;
    	setContentView(R.layout.activity_add_edit_item);
    	EditText itemIDTv = (EditText) findViewById(R.id.item_edit_id_edittext);
    	EditText itemNameTv = (EditText) findViewById(R.id.item_edit_name_edittext);
    	EditText itemDescTv = (EditText) findViewById(R.id.item_edit_description_edittext);
    	itemCatSpinner = (Spinner) findViewById(R.id.item_edit_category_spinner);
    	EditText itemNotesTv = (EditText) findViewById(R.id.item_edit_notes_edittext);
    	Button editTimeButton = (Button) findViewById(R.id.time_edit_button);
    	Button editDateButton = (Button) findViewById(R.id.date_edit_button);
    	Button noTimeButton = (Button) findViewById(R.id.no_due_time_button);
    	Button noDateButton = (Button) findViewById(R.id.no_due_date_button);
    	
    	itemIDTv.setText(String.valueOf(item.getID()));
    	itemNameTv.setText(item.getName());
    	itemDescTv.setText(item.getDescription());
    	itemNotesTv.setText(item.getNotes());

    	String myDate = item.getDueDate();
    	String myDatePretty = item.getDueDatePretty();
    	String myTime = item.getDueTime();
    	String myTimePretty = item.getDueTimePretty();
    	
    	if (!item.hasDueTime()) noTimeButton.setEnabled(false);
    	if (!item.hasDueDate()) noDateButton.setEnabled(false);

    	editDateButton.setText(myDatePretty);
    	editDateButton.setTag(myDate);
    	editTimeButton.setText(myTimePretty);
    	editTimeButton.setTag(myTime);
    	
        catList = myListMan.getCategoryList();
    	// TODO: Make this into a generic addItemsToSpinner method that I can use with the first spinner also
        ArrayAdapter<Category> itemCatSpinnerDataAdapter = new MyCatSpinnerCustomAdapter(this, R.layout.activity_add_edit_item, catList);
        itemCatSpinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemCatSpinner.setAdapter(itemCatSpinnerDataAdapter);
        
        int myCatID = item.getPrimaryCat();
        Log.v("FlipList.editListItem", "Item Name: " + item.getName() + " Primary Cat: " + myCatID);
        int spinnerPosition = catSpinnerDataAdapter.getPosition(myCatID);
        // TODO: Fix this its returning 0!!! Why? :(
        Log.v("FlipList.editListItem", "spinnerPosition: " + spinnerPosition);
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
    	Category itemCategory = (Category) itemCatSpinner.getItemAtPosition(itemCatSpinner.getSelectedItemPosition());
    	int itemCategoryID = itemCategory.getID();
    	String itemNotes = itemNotesTv.getText().toString();
    	String itemDueTime = (String) itemDueTimeBtn.getTag();
    	String itemDueDate = (String) itemDueDateBtn.getTag();
    	
    	Item myItem = new Item();
    	myItem.setID(itemID);
    	myItem.setName(itemName);
    	myItem.setDescription(itemDesc);
    	myItem.setPrimaryCat(itemCategoryID);
    	myItem.setNotes(itemNotes);
    	myItem.setDueTime(itemDueTime);
    	if (itemDueTime != null && itemDueDate == null) {
    		Calendar cal = Calendar.getInstance();
    		int day = cal.get(Calendar.DAY_OF_MONTH);
    		int month = cal.get(Calendar.MONTH);
    		int year = cal.get(Calendar.YEAR);
    		itemDueDate = year + "-" + month + "-" + day;
    	}
    	myItem.setDueDate(itemDueDate);
    	Log.v("FlipList.itemEditSaveButtonAction", "itemDueTime: " + itemDueTime + " itemDueDate: " + itemDueDate);
    	myItem.setCreateDate(currentItem.getCreateDate());
    	Log.v("FlipList.itemEditSaveButtonAction", "hasDueDate: " + myItem.hasDueDate() + " hasDueTime: " + myItem.hasDueTime());

    	//Set up constructor to be able to take all this stuff
    	//myListMan.updateItem(new ListItem(itemCategoryID, itemID, itemName, itemDesc));
    	myListMan.updateItem(myItem);
    	
		Intent flipList = new Intent(this, FlipList.class);
		this.startActivity(flipList);
    }
	public void itemEditNoDueTimeButtonAction(View view) {
		currentItem.setHasDueTime(false);
		Button dueTimeButton = (Button) findViewById(R.id.time_edit_button);
		Button noDueTimeButton = (Button) findViewById(R.id.no_due_time_button);
		noDueTimeButton.setEnabled(false);
		dueTimeButton.setText("Set Time");
		dueTimeButton.setTag(null);
	}
    public void itemEditNoDueDateButtonAction(View view) {
    	currentItem.setHasDueDate(false);
    	Button dueDateButton = (Button) findViewById(R.id.date_edit_button);
    	Button noDueDateButton = (Button) findViewById(R.id.no_due_date_button);
    	noDueDateButton.setEnabled(false);
    	dueDateButton.setText("Set Date");
    	dueDateButton.setTag(null);
    }
    public void itemEditDeleteButtonAction(View view) {
    	myListMan.deleteItem(currentItem);
		Intent flipList = new Intent(this, FlipList.class);
		this.startActivity(flipList);
    }
    
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Calendar cal = Calendar.getInstance();
			Button setTimeBtn = (Button) getActivity().findViewById(R.id.time_edit_button);
			SimpleDateFormat hmf = new SimpleDateFormat("hh:mm", Locale.US);
			try {
				String setTimeBtnTag = (String) setTimeBtn.getTag();
				if (setTimeBtnTag != null) {
					cal.setTime(hmf.parse(setTimeBtnTag));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
		
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Calendar cal = Calendar.getInstance();
			Button setTimeBtn = (Button) getActivity().findViewById(R.id.time_edit_button);
			Button noTimeBtn = (Button) getActivity().findViewById(R.id.no_due_time_button);
	    	SimpleDateFormat hmaf = new SimpleDateFormat("hh:mm aa", Locale.US);
	    	SimpleDateFormat hmf = new SimpleDateFormat("HH:mm", Locale.US);
	    	String hourMinute = hourOfDay + ":" + minute;
	    	try {
				cal.setTime(hmf.parse(hourMinute));
			} catch (ParseException e) {
				e.printStackTrace();
			}
	    	setTimeBtn.setText(hmaf.format(cal.getTime()));
	    	setTimeBtn.setTag(hourMinute);
	    	noTimeBtn.setEnabled(true);
		}
	}
    
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
    
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			Calendar cal = Calendar.getInstance();
			Button setDateBtn = (Button) getActivity().findViewById(R.id.date_edit_button);
			SimpleDateFormat hmf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			try {
				String setDateBtnTag = (String) setDateBtn.getTag();
				if (setDateBtnTag != null) {
					cal.setTime(hmf.parse(setDateBtnTag));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
		
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			month = month + 1;
			Calendar cal = Calendar.getInstance();
			Button setDateBtn = (Button) getActivity().findViewById(R.id.date_edit_button);
			Button noDateBtn = (Button) getActivity().findViewById(R.id.no_due_date_button);
			SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd", Locale.US);
			SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			Log.v("FlipList.DatePickerFragment", "year: " + year + " month: " + month + " day: " + day);
			String yearMonthDay = year + "-" + month + "-" + day;
			Log.v("FlipList.DatePickerFragment", "yearMonthDay: " + yearMonthDay);
			try {
				cal.setTime(ymd.parse(yearMonthDay));
			} catch (ParseException e) {
				e.printStackTrace();
			}
	    	setDateBtn.setText(sdf.format(cal.getTime()));
			setDateBtn.setTag(yearMonthDay);
			noDateBtn.setEnabled(true);
		}
	}
    
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
    
    private class MyCatSpinnerCustomAdapter extends ArrayAdapter<Category> {
     	 
    	private ArrayList<Category> categoryList;
    	private Activity activity;
    	LayoutInflater inflater;
    	//Map<Integer, Integer> myPositionMap = new HashMap<Integer, Integer>();
    	SparseIntArray myPositionMap;
    	 
    	public MyCatSpinnerCustomAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<Category> objects) {
    		super(activitySpinner, textViewResourceId, objects);
    		this.categoryList = (ArrayList<Category>) objects;
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
    		Category category = categoryList.get(position);
    		
			myPositionMap.put(category.getID(), position);
			Log.v("AddEditItemActivity.catSpinnerCustomAdapter", "Adding CatID: " + category.getID() + " with position " + position);
    		holder.catName.setText(category.getName());
    		holder.catName.setTag(category);
    		return convertView;
    	}
    	 
	}

}
