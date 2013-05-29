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
	
    HashMap<String, ListItem> checkListItems = new HashMap<String, ListItem>();
    ArrayList<ListItem> currentItemList;
    String selectedCategory;
    String[] catList;
    
    MyCustomAdapter itemListDataAdapter;
    ArrayAdapter<String> spinnerDataAdapter;
    Spinner catSpinner;
    
    EditText editText;
    DatabaseHandler db;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        db = new DatabaseHandler(this);
        
        if(savedInstanceState != null) {
        	restoreState(savedInstanceState);
        } else {
        	// Load saved data from somewhere, maybe SqlLite
        	myListMan = new ListManager(this);
        }
        
    	catSpinner = (Spinner) findViewById(R.id.catSpinner);
    	catSpinner.setOnItemSelectedListener(new SpinnerActivity());
    	selectedCategory = String.valueOf(catSpinner.getSelectedItem());
    	
		try {
			currentItemList = myListMan.getItemList("Default");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	itemListDataAdapter = new MyCustomAdapter(this, R.layout.activity_main, currentItemList);
    	catList = myListMan.getCategoryList();
        spinnerDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, catList);

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
    		String[] catList = myListMan.getCategoryList();
    		ArrayList<ListCategory> catObjList = myListMan.getCategoryObjList();
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
    		String itemSelected = (String) parent.getItemAtPosition(pos);
    		addItemsOnList();
    	}
    	public void onNothingSelected(AdapterView<?> parent) {
    		// Nothing here yet
    	}
    }
    
    public void mySaveCatButtonAction(View view) {
    	//Need to find out when an Intent is needed
    	//This uses SaveCatActivity class that is not currently used
    	//Intent intent = new Intent(this, SaveCatActivity.class);
    	EditText editText = (EditText) findViewById(R.id.editText);
    	String message = editText.getText().toString();
    	//intent.putExtra(EXTRA_MESSAGE, message);
        myListMan.addCategory(message);
        addItemsOnSpinner();
        addItemsOnList();
        editText.setText("");
    }
    
    public void mySaveButtonAction(View view) {
       Date dueDate = new Date();
       editText = (EditText) findViewById(R.id.editText);
       String description = editText.getText().toString();
       
       catSpinner = (Spinner) findViewById(R.id.catSpinner);
       selectedCategory = String.valueOf(catSpinner.getSelectedItem());
       
       Log.v(TAG, "mySaveButtonAction: (below selectedCategory assignment)");
       
       ListItem myItem = myListMan.addItem(selectedCategory, description, dueDate);
       db.addItem(myItem);
       addItemsOnList();
       editText.setText("");
    }
    
    public void addItemsOnSpinner() {
        catList = myListMan.getCategoryList();
        spinnerDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, catList);
        spinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinner.setAdapter(spinnerDataAdapter);
    }
    
    public void addItemsOnList()  {

    	selectedCategory = String.valueOf(catSpinner.getSelectedItem());
    	try {
			currentItemList = myListMan.getItemList(selectedCategory);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	itemListDataAdapter = new MyCustomAdapter(this, android.R.layout.simple_spinner_item, currentItemList);
    	
    	ListView listView = (ListView) findViewById(R.id.itemList);
    	listView.setAdapter(itemListDataAdapter);
    	
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
    private class MyCustomAdapter extends ArrayAdapter<ListItem> {
    	 
    	private ArrayList<ListItem> itemList;
    	 
    	public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<ListItem> itemList) {
    		super(context, textViewResourceId, itemList);
    		this.itemList = new ArrayList<ListItem>();
    		this.itemList.addAll(itemList);
    	}
    	 
    	private class ViewHolder {
    		TextView code;
    		CheckBox name;
    	}
    	 
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    	 
    		ViewHolder holder = null;
    		Log.v("ConvertView", String.valueOf(position));
    	 
    		if (convertView == null) {
    			LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			convertView = vi.inflate(R.layout.list_layout, null);
    	 
    			holder = new ViewHolder();
    			holder.code = (TextView) convertView.findViewById(R.id.code);
    			holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
    			convertView.setTag(holder);
    	 
    			holder.name.setOnClickListener( new View.OnClickListener() {  
    				public void onClick(View v) {  
    					CheckBox cb = (CheckBox) v ;  
    					ListItem item = (ListItem) cb.getTag();
    					Spinner catSpinner = (Spinner) findViewById(R.id.catSpinner);
    					String catSelected = String.valueOf(catSpinner.getSelectedItem());
    					String itemDescription = item.getDescription();
    					Toast.makeText(getApplicationContext(), "Completed: " + itemDescription, Toast.LENGTH_LONG).show();
    					//item.setSelected(cb.isChecked());
    					myListMan.completeItem(item, catSelected);
    					addItemsOnList();
    				}  
    			});  
    	   } 
    	   else {
    	    holder = (ViewHolder) convertView.getTag();
    		}
    	 
    		ListItem item = itemList.get(position);
    		holder.code.setText(" (" +  item.getDueDate() + ")");
    		holder.name.setText(item.getDescription());
    		holder.name.setChecked(item.isSelected());
    		holder.name.setTag(item);
    	 
    		return convertView;
    	 
    	}
    	 
	}
}
