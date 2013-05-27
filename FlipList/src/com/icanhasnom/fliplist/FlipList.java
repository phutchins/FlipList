package com.icanhasnom.fliplist;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
    
    //Spinner catSpinner;
    //ArrayAdapter<String> spinnerDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, catList);
    //MyCustomAdapter listDataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, catList);
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(savedInstanceState != null) {
        	restoreState(savedInstanceState);
        } else {
        	// Load saved data from somewhere, maybe SqlLite
        	myListMan = new ListManager();
        }
        
    	catSpinner = (Spinner) findViewById(R.id.catSpinner);
    	catSpinner.setOnItemSelectedListener(new SpinnerActivity());
    	
    	selectedCategory = String.valueOf(catSpinner.getSelectedItem());
    	ArrayList<ListItem> currentItemList = myListMan.getItemList("Default");
    	
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
    
    //@Override
    //public void onRestoreInstanceState(Bundle savedInstanceState) {
    //	super.onRestoreInstanceState(savedInstanceState);
    //	this.myListMan = (ListManager) savedInstanceState.getSerializable("ListManager");
    //	
    //}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
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
       
       myListMan.addItem(selectedCategory, description, dueDate);
       addItemsOnList();
       editText.setText("");
    }
    
    public void addItemsOnSpinner() {
        catList = myListMan.getCategoryList();
        spinnerDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, catList);
        spinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinner.setAdapter(spinnerDataAdapter);
    }
    
    public void addItemsOnList() {
    	//Spinner catSpinner = (Spinner) findViewById(R.id.catSpinner);
    	selectedCategory = String.valueOf(catSpinner.getSelectedItem());
    	currentItemList = myListMan.getItemList(selectedCategory);
    	itemListDataAdapter = new MyCustomAdapter(this, android.R.layout.simple_spinner_item, currentItemList);
    	//ArrayList<ItemList> currentItemList = myListMan.getItemList(selectedCategory);
    	
    	
        //Iterator<ListItem> listItem = currentItemList.iterator();
        //Object[] listItemObjects = currentItemList.toArray();
        //String[] listItemArray = Arrays.copyOf(listItemObjects,  listItemObjects.length, String[].class);
    	
    	
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
