package com.icanhasnom.fliplist;
 
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
 
public class ItemViewFragment extends Fragment {
	private static final int RESULT_OK = -1;

	int fragVal;
	
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
    
    MyCatSpinnerCustomAdapter catSpinnerDataAdapter;
    Spinner catSpinner;
    
    ItemList currentItemList;
    ArrayList<Item> currentListItems;
    
	ArrayList<Category> listList;
	ViewGroup container;
	Activity activity;
	View layoutView;
 
    static ItemViewFragment init(int val) {
        ItemViewFragment truitonFrag = new ItemViewFragment();
        Bundle args = new Bundle();
        args.putInt("val", val);
        truitonFrag.setArguments(args);
        return truitonFrag;
    }
    public void onAttach(Activity a) {
    	super.onAttach(activity);
    	activity = a;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
		prefMan = new ListPreferenceManager(activity);
		myListMan = new ListManager(activity);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_item_list_layout, container, false);
		currentCatID = prefMan.currentCategoryID;
		defaultCatID = prefMan.defaultCatID;
		currentCategory = getCategoryObj(currentCatID);
		Log.v("ItemListActivity.onCreate", "Current category is: " + currentCategory.getName());
    	myItemList = getItemList(currentCatID);
		currentItemList = myListMan.getItemList(defaultCatID);
    	addItemsOnList(myItemList);
        return layoutView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setListAdapter(new ArrayAdapter<String>(getActivity(),
         //       android.R.layout.simple_list_item_1, arr));
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    	itemListDataAdapter = new MyCustomAdapter(activity, R.layout.fragment_item_list_layout, myListItems);
    	ListView listView = (ListView) layoutView.findViewById(R.id.itemList);
    	TextView itemListTitle = (TextView) layoutView.findViewById(R.id.item_view_activity_title);
    	listView.setAdapter(itemListDataAdapter);
    	//itemListTitle.setText(currentCategory.getName());
    	
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			Item item = (Item) parent.getItemAtPosition(position);
    			editListItem(item);
    			Toast.makeText(activity,
    					"Clicked on Row: " + item.getName(), 
    					Toast.LENGTH_LONG).show();
    		}
    	});
    	Button saveItemButton = (Button) layoutView.findViewById(R.id.saveItemButton);
    	saveItemButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			mySaveButtonAction(v);
    		}
    	});
    }
    public void editListItem(Item item) {
		Intent addEditItem = new Intent(activity, AddEditItemActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("item", item);
		addEditItem.putExtras(b);
		this.startActivityForResult(addEditItem, 1);
    }
    public void mySaveButtonAction(View view) {
        EditText editText = (EditText) layoutView.findViewById(R.id.editText);
        String name = editText.getText().toString();
        if (name.isEmpty()) {
 			Toast.makeText(activity,
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
    			LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			convertView = vi.inflate(R.layout.fragment_item_list_view, null);
    	 
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
    public void addItemsOnSpinner() {
        catList = myListMan.getCategories();
        buildIndex(catList);
        catSpinnerDataAdapter = new MyCatSpinnerCustomAdapter(activity, R.layout.activity_main_cat_spinner, catList);
        catSpinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        catSpinner.setAdapter(catSpinnerDataAdapter);
        catSpinner.setOnItemSelectedListener(new SpinnerActivity());
        catSpinner.setSelection(getPosition(currentCategory.getID()));
        catSpinnerDataAdapter.notifyDataSetChanged();
    }

    public class SpinnerActivity extends Activity implements OnItemSelectedListener {
    	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    		Category catSelected = (Category) parent.getItemAtPosition(pos);
    		currentCategory = catSelected;
    		SharedPreferences.Editor prefEditor = mySharedPreferences.edit();
    		prefEditor.putInt("current_category_id", currentCategory.getID());
    		prefEditor.commit();
            Log.v("FlipList.loadPrefs", "(2) Setting currentCategory to " + currentCategory.getName());
    		addItemsOnList();
    	}
    	public void onNothingSelected(AdapterView<?> parent) {
    		// Nothing here yet
    	}
    }
    public void addItemsOnList()  {
    	int catID = currentCategory.getID();
		currentItemList = myListMan.getItemList(catID);
		Log.v("FlipList.addItemsOnList", "catID: " + catID );
		Log.v("FLipList.addItemsonList", "currentItemList: " + currentItemList);
		currentListItems = currentItemList.getListItems();
    	itemListDataAdapter = new MyCustomAdapter(activity, R.layout.activity_item_list_view, currentListItems);
    	ListView listView = (ListView) activity.findViewById(R.id.itemList);
    	listView.setAdapter(itemListDataAdapter);
    	
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			Item item = (Item) parent.getItemAtPosition(position);
    			editListItem(item);
    			Toast.makeText(activity,
    					"Clicked on Row: " + item.getName(), 
    					Toast.LENGTH_LONG).show();
    		}
    	});
    }
    /*
    public void editListItem(Item item) {
		Intent addEditItem = new Intent(this, AddEditItemActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("item", item);
		addEditItem.putExtras(b);
		this.startActivityForResult(addEditItem, 1);
    }
    **/
	public void buildIndex(ArrayList<Category> myCatList) {
		Integer position = 0;
		for (Category myCat : myCatList) {
			myPositionMap.put(myCat.getID(), position);
			position++;
		}
	}
	public int getPosition(int myListCategoryID) {
		int myPosition = myPositionMap.get(myListCategoryID);
		return myPosition;
	}
    private class MyCatSpinnerCustomAdapter extends ArrayAdapter<Category> {
     	 
    	private ArrayList<Category> categoryList;
    	private Activity activity;
    	LayoutInflater inflater;
    	SparseIntArray myPositionMap;
    	 
    	public MyCatSpinnerCustomAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<Category> objects) {
    		super(activitySpinner, textViewResourceId, objects);
    		this.categoryList = (ArrayList<Category>) objects;
    		this.activity = activitySpinner;
    		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		myPositionMap = new SparseIntArray();
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
    		holder.catName.setText(category.getName());
    		holder.catName.setTag(category);
    		return convertView;
    	}
    	 
	}
    
}
