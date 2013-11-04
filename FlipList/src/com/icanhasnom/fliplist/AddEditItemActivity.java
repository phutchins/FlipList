package com.icanhasnom.fliplist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    Spinner categorySpinner;
    Spinner flistSpinner;
    MyCategorySpinnerCustomAdapter categorySpinnerDataAdapter;
    MyFlistSpinnerCustomAdapter flistSpinnerDataAdapter;
    //SparseIntArray myCategoryPositionMap;
    SparseIntArray flistPositionMap;
	SparseIntArray categoryDialogPositionMap;

	ArrayList<Flist> flistList;
	ArrayList<Category> categoryList;
	
	CharSequence[] categoryDialogOptions;
	boolean[] categoryDialogSelections;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit_item);
		final ActionBar bar = getActionBar();
		bar.setHomeButtonEnabled(true);
		myListMan = new ListManager(this);
		categoryList = myListMan.getCategories();
		flistList = myListMan.getFlists();
		//myCategoryPositionMap = new SparseIntArray();
		//myCategoryPositionMap = buildCategorySpinnerIndex(categoryList);
		flistPositionMap = new SparseIntArray();
		flistPositionMap = buildFlistSpinnerIndex(flistList);
		categoryDialogPositionMap = new SparseIntArray();
		categoryDialogPositionMap = buildCategoryDialogIndex(categoryList);
		
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
	/*
	public void addCategoriesToSpinner() {
        categorySpinnerDataAdapter = new MyCategorySpinnerCustomAdapter(this, R.layout.activity_add_edit_item, categoryList);
        categorySpinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	categorySpinner = (Spinner) findViewById(R.id.item_edit_category_spinner);
        categorySpinner.setAdapter(categorySpinnerDataAdapter);
	}
	*/
	public void addFlistsToSpinner() {
        flistSpinnerDataAdapter = new MyFlistSpinnerCustomAdapter(this, R.layout.activity_add_edit_item, flistList);
        flistSpinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	flistSpinner = (Spinner) findViewById(R.id.item_edit_flist_spinner);
        flistSpinner.setAdapter(flistSpinnerDataAdapter);
	}
	/*
	public SparseIntArray buildCategorySpinnerIndex(ArrayList<Category> myCategoryList) {
		Integer position = 0;
		SparseIntArray myCategoryPositionMap = new SparseIntArray();
		for (Category myCategory : myCategoryList) {
			myCategoryPositionMap.put(myCategory.getID(), position);
			position++;
		}
		return myCategoryPositionMap;
	}
	*/
	public SparseIntArray buildFlistSpinnerIndex(ArrayList<Flist> myCatList) {
		Integer position = 0;
		SparseIntArray myFlistPositionMap = new SparseIntArray();
		for (Flist myCat : myCatList) {
			myFlistPositionMap.put(myCat.getID(), position);
			position++;
		}
		return myFlistPositionMap;
	}
	/*
	public int getCategorySpinnerPosition(int myFlistCategoryID, SparseIntArray myPositionMap) {
		int myPosition = myCategoryPositionMap.get(myFlistCategoryID);
		return myPosition;
	}
	*/
	public int getFlistSpinnerPosition(int myListCategoryID, SparseIntArray myPositionMap) {
		int myPosition = flistPositionMap.get(myListCategoryID);
		return myPosition;
	}
    public void editListItem() {
    	EditText itemIDTv = (EditText) findViewById(R.id.item_edit_id_edittext);
    	EditText itemNameTv = (EditText) findViewById(R.id.item_edit_name_edittext);
    	CheckBox itemCompleted = (CheckBox) findViewById(R.id.item_completed_checkbox);
    	EditText itemDescTv = (EditText) findViewById(R.id.item_edit_description_edittext);
        TextView itemCategoryListTv = (TextView) findViewById(R.id.item_edit_category_list);

    	EditText itemNotesTv = (EditText) findViewById(R.id.item_edit_notes_edittext);
    	Button editTimeButton = (Button) findViewById(R.id.time_edit_button);
    	Button editDateButton = (Button) findViewById(R.id.date_edit_button);
    	Button noTimeButton = (Button) findViewById(R.id.no_due_time_button);
    	Button noDateButton = (Button) findViewById(R.id.no_due_date_button);
    	Button categorySelectionButton = (Button) findViewById(R.id.item_edit_category_selection_button);
    	
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
    	categorySelectionButton.setOnClickListener( new CategoryButtonClickHandler() );
    	
        itemNotesTv.setText(currentItem.getNotes());
        
        addFlistsToSpinner();
        List<String> itemCategoryList = currentItem.getCategories();
        String itemCategoryListString = "";
        for (String categoryID : itemCategoryList) {
        	String categoryName = myListMan.getCategory(Integer.valueOf(categoryID)).getName();
        	itemCategoryListString += categoryName + " ";
        }
        
        // Show current category selections
        // Create category selector button that updates the current selections on the way out
        //int categorySpinnerPosition = getCategorySpinnerPosition(currentItem.getCategories(), myCategoryPositionMap);
        //categorySpinner.setSelection(categorySpinnerPosition);
        

        itemCategoryListTv.setText(itemCategoryListString);
        
        int flistSpinnerPosition = getFlistSpinnerPosition(currentItem.getID(), flistPositionMap);
        flistSpinner.setSelection(flistSpinnerPosition);
        
		//Log.v("AddEditItemActivity.MyCatSpinnerCustomAdapter", "myPosition(0): " + myCategoryPositionMap.get(0) + " myPosition(1): " + myCategoryPositionMap.get(1) + " myPosition.get(2): " + myCategoryPositionMap.get(2));
        //Log.v("AddEditItemActivity.editListItem", "****spinnerPosition: " + categorySpinnerPosition);

    	// TODO: Make this into a generic addItemsToSpinner method that I can use with the first spinner also
        //Log.v("AddEditItemActivity.editListItem", "catSpinnerDataAdapter: " + catSpinnerDataAdapter);
        Log.v("AddEditItemActivity.editListItem", "Item Name: " + currentItem.getName() + " Primary Cat: " + currentItem.getFlist());
        

    }
    public SparseIntArray buildCategoryDialogIndex(ArrayList<Category> categoryList) {
    	int position = 0;
    	SparseIntArray categoryMap = new SparseIntArray();
    	for(Category category : categoryList) {
    		categoryMap.put(position, category.getID());
    		position++;
    	}
    	return categoryMap;
    }
    public CharSequence[] buildCategoryDialogOptions(ArrayList<Category> categoryList) {
    	List<String> myOptions = new ArrayList<String>();
    	int i = 0;
    	for (Category category : categoryList) {
    		myOptions.add(category.getName());
    		i++;
    	}
    	final CharSequence[] myOptionsCharSequence = myOptions.toArray(new CharSequence[myOptions.size()]);
    	return myOptionsCharSequence;
    }
    public void initCategoryDialogOptions() {
    	ArrayList<Category> categoryList = myListMan.getCategories();
    	categoryDialogPositionMap = buildCategoryDialogIndex(categoryList);
    	categoryDialogOptions = buildCategoryDialogOptions(categoryList);
    }

    public class CategoryButtonClickHandler implements View.OnClickListener {
    	public void onClick( View view ) {
    		initCategoryDialogOptions();
    		showDialog( 0 );
    	}
    }
	@Override
	protected Dialog onCreateDialog( int id ) {
		return 
				new AlertDialog.Builder( this )
		.setTitle( "Categories" )
		.setMultiChoiceItems( categoryDialogOptions, categoryDialogSelections, new CategoryDialogSelectionClickHandler() )
		.setPositiveButton( "OK", new CategoryDialogButtonClickHandler() )
		.create();
	}
	public class CategoryDialogSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener {
		public void onClick( DialogInterface dialog, int clicked, boolean selected) {
			Log.i("AddEditItemActivity.CategoryDialogSelectionClickHandler", categoryDialogOptions[clicked] + " selected: " + selected);
		}
	}
	public class CategoryDialogButtonClickHandler implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int clicked)  {
			switch( clicked )
			{
			case DialogInterface.BUTTON_POSITIVE:
				printSelectedCategories();
				break;
			}
		}
	}
    protected void printSelectedCategories() {
    	for( int i = 0; i < categoryDialogOptions.length; i++) {
    		Log.i("AddEditItemActivity.printSelectedCategories()", categoryDialogOptions[ i ] + " selected: " + categoryDialogSelections[ i ]);
    	}
    }

    public void itemEditSaveButtonAction(View view) {
    	EditText itemIDTv = (EditText) findViewById(R.id.item_edit_id_edittext);
    	EditText itemNameTv = (EditText) findViewById(R.id.item_edit_name_edittext);
    	CheckBox itemCompleted = (CheckBox) findViewById(R.id.item_completed_checkbox);
    	EditText itemDescTv = (EditText) findViewById(R.id.item_edit_description_edittext);
    	List<String> dialogCategories = new ArrayList<String>();

    	EditText itemNotesTv = (EditText) findViewById(R.id.item_edit_notes_edittext);
    	Button itemDueTimeBtn = (Button) findViewById(R.id.time_edit_button);
    	Button itemDueDateBtn = (Button) findViewById(R.id.date_edit_button);
    	
    	int itemID = Integer.parseInt(itemIDTv.getText().toString());
    	String itemName = itemNameTv.getText().toString();
    	String itemDesc = itemDescTv.getText().toString();
    	Flist itemFlist = (Flist) flistSpinner.getItemAtPosition(flistSpinner.getSelectedItemPosition());

    	int itemFlistID = itemFlist.getID();
    	String itemNotes = itemNotesTv.getText().toString();
    	String itemDueTime = (String) itemDueTimeBtn.getTag();
    	String itemDueDate = (String) itemDueDateBtn.getTag();
    	
    	Item myItem = new Item();
    	myItem.setID(itemID);
    	myItem.setName(itemName);
    	myItem.setDescription(itemDesc);
    	myItem.setFlist(itemFlistID);
    	for (int i = 0; i < categoryDialogOptions.length; i++) {
    		if (categoryDialogSelections[i]) {
    			// Adding empty string to force the int to be a string. Maybe I should create an addCategoryID function
    			// or just handle all categories as int's until we get to the DB layer
    			//myItem.addCategory("" + categoryDialogPositionMap.get(i));
    			dialogCategories.add("" + categoryDialogPositionMap.get(i));
    			Log.v("AddEditItemActivity.itemEditSaveButtonAction", "Adding category " + myListMan.getCategory(categoryDialogPositionMap.get(i)).getName() + " to item " + myItem.getName());
    		}
    	}
    	myItem.setCategories(dialogCategories);
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
    	intent.putExtra("flistID", itemFlistID);
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
    
    private class MyCategorySpinnerCustomAdapter extends ArrayAdapter<Category> {
     	 
    	private ArrayList<Category> categoryList;
    	private Activity activity;
    	LayoutInflater inflater;
    	//Map<Integer, Integer> myPositionMap = new HashMap<Integer, Integer>();

    	 
    	public MyCategorySpinnerCustomAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<Category> objects) {
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
    			convertView = inflater.inflate(R.layout.spinner_view, null);
    			holder = new ViewHolder();
    			holder.catName = (TextView) convertView.findViewById(R.id.spinner_text);
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
    private class MyFlistSpinnerCustomAdapter extends ArrayAdapter<Flist> {
    	 
    	private ArrayList<Flist> flistList;
    	private Activity activity;
    	LayoutInflater inflater;
    	//Map<Integer, Integer> myPositionMap = new HashMap<Integer, Integer>();

    	 
    	public MyFlistSpinnerCustomAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<Flist> objects) {
    		super(activitySpinner, textViewResourceId, objects);
    		this.flistList = (ArrayList<Flist>) objects;
    		this.activity = activitySpinner;
    		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		//Log.v("AddEditItemActivity.MyCatSpinnerCustomAdapter", "Initializing myPositionMap");
    		
    	}

		private class ViewHolder {
    		TextView flistName;
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
    			holder.flistName = (TextView) convertView.findViewById(R.id.spinner_text);
    			convertView.setTag(holder);
    		} else {
    			holder = (ViewHolder) convertView.getTag();
    		}
    		Flist flist = flistList.get(position);
    		

			Log.v("AddEditItemActivity.catSpinnerCustomAdapter", "Adding CatID: " + flist.getID() + " with position " + position);
    		holder.flistName.setText(flist.getName());
    		holder.flistName.setTag(flist);
    		//Log.v("AddEditItemActivity.catSpinnerCustomAdapter", "System.identifyHashCode(): " + this);
    		//Log.v("AddEditItemActivity.MyCatspinnerCustomAdapter (2)", "myPositionMap: " + myPositionMap.toString());
    		//Log.v("AddEditItemActivity.MyCatSpinnerCustomAdapter", "myPosition(0): " + myPositionMap.get(0) + " myPosition(1): " + myPositionMap.get(1) + " myPosition.get(2): " + myPositionMap.get(2));
    		return convertView;
    	}
    	 
	}

}
