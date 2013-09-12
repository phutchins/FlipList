package com.icanhasnom.fliplist;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ItemListActivity extends Activity {
	SparseIntArray myPositionMap;
	ListManager myListMan;
	int defaultCatID;
	int currentCatID;
	Category currentCategory;
	ListPreferenceManager prefMan;
	ItemList myItemList;
    ArrayList<Category> catList;
    ArrayList<Item> myListItems;
    MyCustomAdapter itemListDataAdapter;
    SharedPreferences mySharedPreferences;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final ActionBar bar = getActionBar();
		bar.setHomeButtonEnabled(true);
		
        setContentView(R.layout.activity_item_list_layout);
		prefMan = new ListPreferenceManager(getApplicationContext());
		Bundle b = this.getIntent().getExtras();
		if(b != null) {
			currentCatID = (Integer) b.getSerializable("catID");
		}
		myListMan = new ListManager(this);
		currentCategory = getCategoryObj(currentCatID);
		Log.v("ItemListActivity.onCreate", "Current category is: " + currentCategory.getName());
    	myItemList = getItemList(currentCatID);
    	addItemsOnList(myItemList);
	}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 0) {
    	}
    	if (requestCode == 1 && resultCode == RESULT_OK) {
            //currentCategoryID = data.getIntExtra("catID", currentCategoryID);
    	}
    	updateList();
    }
	public Category getCategoryObj(int catID) {
		Category myCat = myListMan.getCategory(catID);
		return myCat;
	}
	public ItemList getItemList(int catID) {
		ItemList itemList = myListMan.getItemList(catID);
		Log.v("ItemListActivity.onCreate", "Got item List for catID: " + catID);
		return itemList;
	}
    public void addItemsOnList(ItemList myItemList)  {
		Log.v("FlipList.addItemsOnList", "catID: " + currentCatID );
		Log.v("FLipList.addItemsonList", "currentItemList: " + myItemList);
		myListItems = myItemList.getListItems();
    	itemListDataAdapter = new MyCustomAdapter(this, R.layout.activity_item_list_layout, myListItems);
    	ListView listView = (ListView) findViewById(R.id.itemList);
    	TextView itemListTitle = (TextView) findViewById(R.id.item_view_activity_title);
    	listView.setAdapter(itemListDataAdapter);
    	itemListTitle.setText(currentCategory.getName());
    	
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			Item item = (Item) parent.getItemAtPosition(position);
    			editListItem(item);
    			Toast.makeText(getApplicationContext(),
    					"Clicked on Row: " + item.getName(), 
    					Toast.LENGTH_LONG).show();
    		}
    	});
    }
    public void editListItem(Item item) {
		Intent addEditItem = new Intent(this, AddEditItemActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("item", item);
		addEditItem.putExtras(b);
		this.startActivityForResult(addEditItem, 1);
    }
    public void mySaveButtonAction(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        String name = editText.getText().toString();
        if (name.isEmpty()) {
 			Toast.makeText(getApplicationContext(),
 					"Please enter an item name!", Toast.LENGTH_LONG).show();
        } else {
 	       int catID = currentCategory.getID();
 	       
 	       // Get the default type of category
 	       //Category myCat = myListMan.getCategory(catID);
 	       // myTypeID = myCat.getType();
 	       //ItemType myType = myListMan.getItemType(myTypeID);
 	       
 	       myListMan.addItem(catID, name);
 	       addItemsOnList(myItemList);
 	       editText.setText("");
        }
        updateList();
     }
    public void updateList() {
    	myItemList = getItemList(currentCatID);
        addItemsOnList(myItemList);
    }
    private class MyCustomAdapter extends ArrayAdapter<Item> {
    	 
    	private ArrayList<Item> itemList;
    	 
    	public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<Item> itemList) {
    		super(context, textViewResourceId, itemList);
    		this.itemList = new ArrayList<Item>();
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
    		if (convertView == null) {
    			LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			convertView = vi.inflate(R.layout.activity_item_list_view, null);
    	 
    			holder = new ViewHolder();
    			holder.itemCheckBox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
    			holder.itemName = (TextView) convertView.findViewById(R.id.itemName);
    			holder.itemInfo = (TextView) convertView.findViewById(R.id.itemInfo);
    			
    			convertView.setTag(holder);
    	 
    			holder.itemCheckBox.setOnClickListener( new View.OnClickListener() {  
    				public void onClick(View v) {  
    					CheckBox cb = (CheckBox) v ;  
    					Item item = (Item) cb.getTag();
    					String itemName = item.getName();
    					if (item.isCompleted()) {
    						cb.setChecked(false);
    						myListMan.unCompleteItem(item);
    					} else {
	    					//Toast.makeText(getApplicationContext(), "Completed: " + itemName, Toast.LENGTH_LONG).show();
	    					myListMan.completeItem(item);
	    					updateList();
    					}
    				}  
    			});  
    		} else {
    		   holder = (ViewHolder) convertView.getTag();
    		}
    		Item item = itemList.get(position);
    		holder.itemName.setText(item.getName());
    		String infoString = "";
    		Boolean showInfo = false;
    		Log.v("ItemListActivity.MyCustomAdapter", "currentCategory.showDueDate(): " + currentCategory.showDueDate());
    		Log.v("ItemListActivity.MyCustomAdapter", "prefMan.showDueDateGlobal: " + prefMan.ShowDueDateGlobal);
    		if (currentCategory.showDueDate() && prefMan.ShowDueDateGlobal) {
    			if (item.hasDueTime() == true) {
    				infoString = "Due: " + item.getDueDatePretty() + " @ " + item.getDueTimePretty();
    				showInfo = true;
    			} else if (item.hasDueDate()) {
    				infoString = "Due: " + item.getDueDatePretty();
    				showInfo = true;
    			}
    		}
	    	if (currentCategory.showDescription() && prefMan.ShowItemDescriptionGlobal) {
	    		if (!item.getDescription().isEmpty()) {
	    			infoString = infoString + " (" + item.getDescription() + ")";
	    			showInfo = true;
	    		}
    		}
	    	if (showInfo) {
	    		holder.itemInfo.setText(infoString);
	    	} else {
	    		holder.itemInfo.setVisibility(View.GONE);
	    	}
	    	if (item.isCompleted) holder.itemCheckBox.setChecked(true); 
    		holder.itemCheckBox.setText("");
    		holder.itemCheckBox.setTag(item);
    		return convertView;
    	} 
	}
    

    public void onBackPressed() {
    	  //Your code here
    	  super.onBackPressed();
    }
}
