package com.icanhasnom.fliplist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.os.Bundle;
import android.app.ActionBar;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddEditItemActivity extends Activity {
	Item currentItem;
	ListManager myListMan;
    Spinner itemCatSpinner;
    MyCatSpinnerCustomAdapter catSpinnerDataAdapter;
    SparseIntArray myPositionMap;

	ArrayList<Category> categoryList;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit_item);
		final ActionBar bar = getActionBar();
		bar.setHomeButtonEnabled(true);
		myListMan = new ListManager(this);
		categoryList = myListMan.getCategoryList();
		myPositionMap = new SparseIntArray();
		myPositionMap = buildIndex(categoryList);
		
		Bundle b = this.getIntent().getExtras();
		if(b != null) {
			currentItem = (Item) b.getSerializable("item");
		}
		editListItem();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_edit_item, menu);
		return true;
	}
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.action_save_item:
    		itemEditSaveButtonAction(findViewById(android.R.id.content));
    		break;
    	case R.id.action_delete_item:
    		itemEditDeleteButtonAction(findViewById(android.R.id.content));
    		break;
    	case R.id.action_cancel_item:
    		finish();
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    	return true;
    	
    }
	
	public void addCategoriesToSpinner() {
        catSpinnerDataAdapter = new MyCatSpinnerCustomAdapter(this, R.layout.activity_add_edit_item, categoryList);
        catSpinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	itemCatSpinner = (Spinner) findViewById(R.id.item_edit_category_spinner);
        itemCatSpinner.setAdapter(catSpinnerDataAdapter);
	}
	public SparseIntArray buildIndex(ArrayList<Category> myCatList) {
		Integer position = 0;
		SparseIntArray myPositionMap = new SparseIntArray();
		for (Category myCat : myCatList) {
			myPositionMap.put(myCat.getID(), position);
			position++;
		}
		return myPositionMap;
	}
	public int getPosition(int myListCategoryID, SparseIntArray myPositionMap) {
		int myPosition = myPositionMap.get(myListCategoryID);
		return myPosition;
	}
	
    public void editListItem() {
    	EditText itemIDTv = (EditText) findViewById(R.id.item_edit_id_edittext);
    	EditText itemNameTv = (EditText) findViewById(R.id.item_edit_name_edittext);
    	CheckBox itemCompleted = (CheckBox) findViewById(R.id.item_completed_checkbox);
    	EditText itemDescTv = (EditText) findViewById(R.id.item_edit_description_edittext);

    	EditText itemNotesTv = (EditText) findViewById(R.id.item_edit_notes_edittext);
    	Button editTimeButton = (Button) findViewById(R.id.time_edit_button);
    	Button editDateButton = (Button) findViewById(R.id.date_edit_button);
    	Button noTimeButton = (Button) findViewById(R.id.no_due_time_button);
    	Button noDateButton = (Button) findViewById(R.id.no_due_date_button);
    	
    	itemIDTv.setText(String.valueOf(currentItem.getID()));
    	itemNameTv.setText(currentItem.getName());
    	itemDescTv.setText(currentItem.getDescription());
    	itemNotesTv.setText(currentItem.getNotes());

    	String myDate = currentItem.getDueDate();
    	String myDatePretty = currentItem.getDueDatePretty();
    	String myTime = currentItem.getDueTime();
    	String myTimePretty = currentItem.getDueTimePretty();
    	
    	if (!currentItem.hasDueTime()) noTimeButton.setEnabled(false);
    	if (!currentItem.hasDueDate()) noDateButton.setEnabled(false);
    	if (currentItem.isCompleted()) itemCompleted.setChecked(true);

    	editDateButton.setText(myDatePretty);
    	editDateButton.setTag(myDate);
    	editTimeButton.setText(myTimePretty);
    	editTimeButton.setTag(myTime);
        itemNotesTv.setText(currentItem.getNotes());
        
        addCategoriesToSpinner();
        
        int spinnerPosition = getPosition(currentItem.getPrimaryCat(), myPositionMap);
        itemCatSpinner.setSelection(spinnerPosition);
        
		Log.v("AddEditItemActivity.MyCatSpinnerCustomAdapter", "myPosition(0): " + myPositionMap.get(0) + " myPosition(1): " + myPositionMap.get(1) + " myPosition.get(2): " + myPositionMap.get(2));
        Log.v("AddEditItemActivity.editListItem", "****spinnerPosition: " + spinnerPosition);

    	// TODO: Make this into a generic addItemsToSpinner method that I can use with the first spinner also
        //Log.v("AddEditItemActivity.editListItem", "catSpinnerDataAdapter: " + catSpinnerDataAdapter);
        Log.v("AddEditItemActivity.editListItem", "Item Name: " + currentItem.getName() + " Primary Cat: " + currentItem.getPrimaryCat());
        

    }
    public void itemEditSaveButtonAction(View view) {
    	EditText itemIDTv = (EditText) findViewById(R.id.item_edit_id_edittext);
    	EditText itemNameTv = (EditText) findViewById(R.id.item_edit_name_edittext);
    	CheckBox itemCompleted = (CheckBox) findViewById(R.id.item_completed_checkbox);
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
    	myItem.setCompleted(itemCompleted.isChecked());
    	if (itemDueTime != null && itemDueDate == null) {
    		Calendar cal = Calendar.getInstance();
    		int day = cal.get(Calendar.DAY_OF_MONTH);
    		int month = cal.get(Calendar.MONTH);
    		int year = cal.get(Calendar.YEAR);
    		itemDueDate = year + "-" + month + "-" + day;
    	}
    	myItem.setDueDate(itemDueDate);
    	//Log.v("FlipList.itemEditSaveButtonAction", "itemDueTime: " + itemDueTime + " itemDueDate: " + itemDueDate);
    	myItem.setCreateDate(currentItem.getCreateDate());
    	//Log.v("FlipList.itemEditSaveButtonAction", "hasDueDate: " + myItem.hasDueDate() + " hasDueTime: " + myItem.hasDueTime());

    	//Set up constructor to be able to take all this stuff
    	//myListMan.updateItem(new ListItem(itemCategoryID, itemID, itemName, itemDesc));
    	myListMan.updateItem(myItem);
    	
    	Intent intent = new Intent();
    	intent.putExtra("catID", itemCategoryID);
    	setResult(RESULT_OK, intent);
    	super.finish();
    	
		//Intent flipList = new Intent(this, FlipList.class);
		//this.startActivity(flipList);
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
			//Log.v("FlipList.DatePickerFragment", "year: " + year + " month: " + month + " day: " + day);
			String yearMonthDay = year + "-" + month + "-" + day;
			//Log.v("FlipList.DatePickerFragment", "yearMonthDay: " + yearMonthDay);
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

    	 
    	public MyCatSpinnerCustomAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<Category> objects) {
    		super(activitySpinner, textViewResourceId, objects);
    		this.categoryList = (ArrayList<Category>) objects;
    		this.activity = activitySpinner;
    		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		//Log.v("AddEditItemActivity.MyCatSpinnerCustomAdapter", "Initializing myPositionMap");
    		
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
    		

			Log.v("AddEditItemActivity.catSpinnerCustomAdapter", "Adding CatID: " + category.getID() + " with position " + position);
    		holder.catName.setText(category.getName());
    		holder.catName.setTag(category);
    		//Log.v("AddEditItemActivity.catSpinnerCustomAdapter", "System.identifyHashCode(): " + this);
    		//Log.v("AddEditItemActivity.MyCatspinnerCustomAdapter (2)", "myPositionMap: " + myPositionMap.toString());
    		//Log.v("AddEditItemActivity.MyCatSpinnerCustomAdapter", "myPosition(0): " + myPositionMap.get(0) + " myPosition(1): " + myPositionMap.get(1) + " myPosition.get(2): " + myPositionMap.get(2));
    		return convertView;
    	}
    	 
	}

}
