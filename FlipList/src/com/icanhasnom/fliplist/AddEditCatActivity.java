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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

public class AddEditCatActivity extends Activity {
	ListCategory currentCategory;
	MyCatListCustomAdapter catListDataAdapter;
	//ArrayList<ListCategory> categoryList;
	//String[] catList;
	
	// Create some dummy category objects in the list
	ArrayList<ListCategory> categoryList;
	DatabaseHandler db;
	ListManager myListMan = new ListManager(this);
	
	Spinner typeSpinner;
	SpinAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit_cat_list);
		
        db = new DatabaseHandler(this);
		
		//catList = getIntent().getExtras().getStringArray("catList");
		
		//categoryList = new ArrayList<ListCategory>();
		
		categoryList = db.getAllCategories();
		
		//ListCategory listCat1 = new ListCategory("Desc", "Other", "todo");
		//ListCategory listCat2 = new ListCategory("Something", "another one", "Grocery List");
		//categoryList.add(listCat1);
		//categoryList.add(listCat2);
		
		addItemsOnEditList();
		
		// Show the Up button in the action bar.
		setupActionBar();
		// Set up category list and add an add new category item to the top of the list
		// Create listener that gets category to edit or new category selection
		// Send that selection to the edit layout and display layout
	}
	
	public void addEditCategory(ListCategory lc) {
		
		setContentView(R.layout.activity_add_edit_cat);
		
		EditText catName = (EditText) findViewById(R.id.cat_edit_name);
		EditText catDesc = (EditText) findViewById(R.id.cat_edit_desc);
		Spinner catType = (Spinner) findViewById(R.id.cat_type_spinner);
		
		// fill out layout variables using the currentCategory object
		catName.setText(currentCategory.getName());
		catDesc.setText(currentCategory.getDescription());
		
		addTypesToSpinner();
		
		catType.setSelection(currentCategory.getType());
		
		// Get list of types
		// Populate Spinner with types
		
	}
	
    public void addTypesToSpinner() {
        ArrayList<CategoryType> myTypeList = db.getAllTypes();
        typeSpinner = (Spinner) findViewById(R.id.cat_type_spinner);
        ArrayAdapter<CategoryType> myTypeAdapter = new SpinAdapter(this, android.R.layout.simple_spinner_item, myTypeList);
        //ArrayAdapter<CategoryType> myTypeAdapter = new ArrayAdapter<CategoryType>(this, android.R.layout.simple_spinner_item, myTypeList);
        typeSpinner.setAdapter(myTypeAdapter);
        
        //spinnerDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, myTypeList);
        //spinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //catSpinner.setAdapter(spinnerDataAdapter);
    }
    
    public class SpinAdapter extends ArrayAdapter<CategoryType>{
    	private Context context;
    	private ArrayList<CategoryType> myTypes;
    	
    	public SpinAdapter(Context context, int textViewResourceId, ArrayList<CategoryType> myTypes) {
    		super(context, textViewResourceId, myTypes);
    		this.context = context;
    		this.myTypes = myTypes;
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
    	
    	@Override
    	public View getDropDownView(int position, View convertView, ViewGroup parent) {
    		TextView label = new TextView(context);
    		label.setTextColor(Color.BLACK);
    		label.setText(myTypes.get(position).getName());
    		Log.e("TypeSpinner", myTypes.get(position).getName());
    		return label;
    	}
    }
	
    public void addItemsOnEditList() {

    	//selectedCategory = String.valueOf(catSpinner.getSelectedItem());
    	//currentItemList = myListMan.getItemList(selectedCategory);
    	//ListManager myListMan = ListManager.getInstance();
    	
    	catListDataAdapter = new MyCatListCustomAdapter(this, R.layout.activity_add_edit_cat_list, categoryList);
    	
    	// Add an add new category button here
    	
    	ListView listView = (ListView) findViewById(R.id.itemAddEditList);
    	listView.setAdapter(catListDataAdapter);
    	
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
    
    public void mySaveCatButtonAction(View view) {
    	EditText editCategoryNameText = (EditText) findViewById(R.id.cat_edit_name);
    	String categoryName = editCategoryNameText.getText().toString();
    	EditText editCategoryDescText = (EditText) findViewById(R.id.cat_edit_desc);
    	String categoryDesc = editCategoryDescText.getText().toString();
    	Spinner typeSpinner = (Spinner) findViewById(R.id.cat_type_spinner);
    	CategoryType categoryType = (CategoryType) typeSpinner.getTag();
    	int categoryTypeID = categoryType.getID();
    	
    	ListCategory myNewCat = myListMan.addCategory(categoryName, categoryDesc, categoryTypeID);
    	
    	// Maybe find a way to have the newly created category be displayed once you're
    	// sent back to the main screen
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
    		//CheckBox name;
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
    			//holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
    			convertView.setTag(holder);
    	 
    			holder.cat_list_text_view.setOnClickListener( new View.OnClickListener() {  
    				public void onClick(View v) {  
    					//CheckBox cb = (CheckBox) v ;  
    					//ListItem item = (ListItem) cb.getTag();
    					//Spinner catSpinner = (Spinner) findViewById(R.id.catSpinner);
    					//String catSelected = String.valueOf(catSpinner.getSelectedItem());
    					//String itemDescription = item.getDescription();
    					//Toast.makeText(getApplicationContext(), "Completed: " + itemDescription, Toast.LENGTH_LONG).show();
    					//item.setSelected(cb.isChecked());
    					// Use this onClick to send the user to the edit screen for the clicked category
    					TextView tv = (TextView) v;
    					ListCategory lc = (ListCategory) tv.getTag();
    					currentCategory = lc;
    					addEditCategory(lc);
    				}  
    			});  
    	   } 
    	   else {
    	    holder = (ViewHolder) convertView.getTag();
    		}
    	 
    		ListCategory category = categoryList.get(position);
    		holder.cat_list_text_view.setText(category.getDescription() + "(" + category.getType() + ")");
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
