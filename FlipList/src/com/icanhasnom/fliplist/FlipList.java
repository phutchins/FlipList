package com.icanhasnom.fliplist;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
// Make settings menu -
//    Default category setting
//    Choose what values are displayed for list item on main screen
//    Default list type (grocery, todo, etc...)
// Format list item list display to show tiny date below description to leave more room for actual description
// Ability to make custom filters (maybe create as a category
// Ability to assign multiple categories to tasks


public class FlipList extends Activity {
	public final static String EXTRA_MESSAGE = "com.icanhasnom.FlipList.MESSAGE";
	public static final String TAG = "FlipList";
	
    //ListManager myListMan = new ListManager();
	ListManager myListMan;
	int defaultCatID;
	
    HashMap<String, ListItem> checkListItems = new HashMap<String, ListItem>();
    ItemList currentItemList;
    ListCategory selectedCategory;
    ArrayList<ListCategory> catList;
    
    MyCustomAdapter itemListDataAdapter;
    // ArrayAdapter<String> spinnerDataAdapter;
    MyCatSpinnerCustomAdapter catSpinnerDataAdapter;
    Spinner catSpinner;
    ListCategory currentCategory;
    
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
        } else {
        	// Load saved data from somewhere, maybe SqlLite
        	myListMan = new ListManager(this);
        }
        
    	catSpinner = (Spinner) findViewById(R.id.catSpinner);
    	catSpinner.setOnItemSelectedListener(new SpinnerActivity());
    	//selectedCategory = String.valueOf(catSpinner.getSelectedItem());
    	
		try {
			currentItemList = myListMan.getItemList(defaultCatID);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//itemListDataAdapter = new MyCustomAdapter(this, R.layout.activity_main, currentItemList);
    	//catList = myListMan.getCategoryList();
        //spinnerDataAdapter = (MyCatSpinnerCustomAdapter) new ArrayAdapter<ListCategory>(this, android.R.layout.simple_spinner_item, catList);

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
    		// String[] catList = myListMan.getCategoryListStrings();
    		ArrayList<ListCategory> catObjList = myListMan.getCategoryList();
    		// Probably serialize the myListMan object instead? or just fetch categories from the db?
    		addEditCatIntent.putExtra("catObjList", catObjList);
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
    		Log.v("FlipList.SpinnerActivity", "Inside SpinnerActivity 2");
    		addItemsOnList();
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
    
    public void addItemsOnSpinner() {
        catList = myListMan.getCategoryList();
        catSpinnerDataAdapter = new MyCatSpinnerCustomAdapter(this, R.layout.activity_main_cat_spinner, catList);
        // TODO fix this so that clicking the dropdown works again. Maybe try R.layout.
        // catSpinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinnerDataAdapter.setDropDownViewResource(R.layout.activity_main_cat_spinner);
        catSpinner.setAdapter(catSpinnerDataAdapter);
    }
    
    public void addItemsOnList()  {
    	ListCategory selectedCategory;
    	ArrayList<ListItem> itemListArrayList = new ArrayList<ListItem>();
    	// String selectedCategoryString = selectedCategory.getName();
    	// TODO: Fix selected category here? Put some extra logging...
    	int position = catSpinner.getSelectedItemPosition();
    	Log.v("FlipList.addItemsOnList", "position: " + position);
    	// Set this globally using the global spinner listener so we only have to do it once
    	selectedCategory = (ListCategory) catSpinner.getItemAtPosition(position);
    	// Line below works
    	Log.v("FlipList.addItemsOnList", "selectedCategory: " + selectedCategory.getName());
    	int catID = selectedCategory.getID();
    	Log.v("FlipList.addItemsOnList", "catID: " + catID);
    	try {
			ItemList currentItemList = myListMan.getItemList(catID);
			itemListArrayList = currentItemList.getListItems();
			Log.v("FlipList.addItemsOnList", "currentItemList name" + currentItemList.listName);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	itemListDataAdapter = new MyCustomAdapter(this, R.layout.list_layout, itemListArrayList);
    	
    	ListView listView = (ListView) findViewById(R.id.itemList);
    	listView.setAdapter(itemListDataAdapter);
    	
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			// When clicked, show a toast with the TextView text
    			ListItem item = (ListItem) parent.getItemAtPosition(position);
    			Toast.makeText(getApplicationContext(),
    					"Clicked on Row: " + item.getDescription(), 
    					Toast.LENGTH_LONG).show();
    		}
    	});
    }
    
    private class MyCatSpinnerCustomAdapter extends ArrayAdapter<ListCategory> {
      	 
    	private ArrayList<ListCategory> categoryList;
    	//private Activity context;
    	//private Context context;
    	 
    	public MyCatSpinnerCustomAdapter(Context context, int textViewResourceId, ArrayList<ListCategory> categoryList) {
    		super(context, textViewResourceId, categoryList);
    		//this.context = context;
    		this.categoryList = new ArrayList<ListCategory>();
    		this.categoryList.addAll(categoryList);
    	}
    	
    	private class ViewHolder {
    		TextView cat_list_text_view;
    	}
    	
    	public View getDropDownView(int position, View convertView, ViewGroup parent) {
    		return getCustomView(position, convertView, parent);
    	}
    	public View getView(int position, View convertView, ViewGroup parent) {
    		return getCustomView(position, convertView, parent);
    	}
    	 
    	public View getCustomView(int position, View convertView, ViewGroup parent) {

    		ViewHolder holder = null;
    		Log.v("ConvertView", String.valueOf(position));
    	 
    		if (convertView == null) {
    			LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			convertView = vi.inflate(R.layout.activity_main_cat_spinner, null);
    	 
    			holder = new ViewHolder();
    			holder.cat_list_text_view = (TextView) convertView.findViewById(R.id.activity_main_spinner_textview);
    			//holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
    			convertView.setTag(holder);
    	 
    			holder.cat_list_text_view.setOnClickListener( new View.OnClickListener() {  
    				public void onClick(View v) {
    					TextView tv = (TextView) v;
    					ListCategory lc = (ListCategory) tv.getTag();
    					currentCategory = lc;
    					addItemsOnList();
    					Log.v("FlipList.MySpinnerCustomAdapterListener", "Inside adapter listener");
    				}  
    			});  
    		} else {
    			holder = (ViewHolder) convertView.getTag();
    		}
    	 
    		ListCategory category = categoryList.get(position);
    		holder.cat_list_text_view.setText(category.getName());
    		holder.cat_list_text_view.setTag(category);
    		Log.v(TAG, "Category Name: "+ category.getName());
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
    					Toast.makeText(getApplicationContext(), "Completed: " + itemDescription, Toast.LENGTH_LONG).show();
    					myListMan.completeItem(item, catSelected);
    					addItemsOnList();
    				}  
    			});  
    	   } 
    	   else {
    	    holder = (ViewHolder) convertView.getTag();
    		}
    	 
    		ListItem item = itemList.get(position);
    		holder.itemName.setText(item.getName() + " (" +  item.getDueDate() + ")");
    		holder.itemCheckBox.setText("");
    		//holder.name.setChecked(item.isSelected());
    		holder.itemCheckBox.setTag(item);
    	 
    		return convertView;
    	 
    	}
    	 
	}
}
