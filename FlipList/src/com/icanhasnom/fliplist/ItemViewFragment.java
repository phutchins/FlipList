package com.icanhasnom.fliplist;
 
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	int defaultFlistID;
	int currentFlistID;
	Flist currentFlist;
	ListPreferenceManager prefMan;
	ItemList myItemList;
    ArrayList<Flist> flistList;
    ArrayList<Item> myListItems;
    MyCustomAdapter itemListDataAdapter;
    SharedPreferences mySharedPreferences;
    
    MyCatSpinnerCustomAdapter flistSpinnerDataAdapter;
    Spinner flistSpinner;
    
    ItemList currentItemList;
    ArrayList<Item> currentListItems;
    
	ArrayList<Flist> listList;
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
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_item_list_layout, container, false);
		initFrag(activity);
		currentItemList = myListMan.getItemList(defaultFlistID);
        return layoutView;
    }
    public void initObjs(Activity myActivity) {
		prefMan = new ListPreferenceManager(myActivity);
		myListMan = new ListManager(myActivity);
    }
    public void initFrag(Activity myActivity) {
    	initObjs(myActivity);
    	myItemList = getItemList(currentFlistID);
		currentFlistID = prefMan.currentFlistID;
		defaultFlistID = prefMan.defaultFlistID;
		//Log.v("ItemViewFragment.initFrag", "currentFlistID: " + currentFlistID);
		currentFlist = getFlistObj(currentFlistID);
		
    	addItemsOnSpinner(myActivity);
    	try {
        	addItemsOnList(myItemList);
    	} catch (Exception e) {
    		Log.v("initFrag", "Exception: " + e.getMessage());
    	}

    }
    public void refreshPage(Activity myActivity) {
    	addItemsOnSpinner(myActivity);
    	addItemsOnList(myItemList);
    }
    public void initCat(Activity myActivity, int fl) {
    	initObjs(myActivity);
    	currentFlist = myListMan.getFlist(fl);
    	myItemList = getItemList(fl);
    	addItemsOnSpinner(myActivity); 
    	addItemsOnList(myItemList);
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
            //currentFlistID = data.getIntExtra("catID", currentFlistID);
    	}
    	updateList(activity);
    }
	public Flist getFlistObj(int catID) {
		Flist myCat = myListMan.getFlist(catID);
		return myCat;
	}
	public ItemList getItemList(int catID) {
		ItemList itemList = myListMan.getItemList(catID);
		//Log.v("ItemListActivity.onCreate", "Got item List for catID: " + catID);
		return itemList;
	}
	// TODO: Add other functions to add filters and categories to the spinner
	// and add another spinner or tab on the fragment to switch between the three
    public void addItemsOnSpinner(Activity myActivity) {
        flistList = myListMan.getFlists();
        //Log.v("ItemViewFragment.addItemsOnSpinner", "layoutView: " + layoutView);
        //Log.v("ItemViewFragment.addItemsOnSpinner", "myActivity: " + myActivity);
        //View myItemView = this.findViewById(R.layout.fragment_item_list_layout);
        //Log.v("ItemViewFragment.addItemsOnSpinner", "myItemView: " + myItemView);
        //Log.v("ItemViewFragment.addItemsOnSpinner", "THIS: " + this);
        //Log.v("ItemViewFragment.addItemsOnSpinner", "R.id.viewpager: " + R.id.pager);
        
        // TODO: Fix this, something here is null

        flistSpinner = (Spinner) layoutView.findViewById(R.id.flist_spinner);
        //catSpinner = (Spinner) myActivity.findViewById(R.id.list_spinner);


        buildIndex(flistList);
        
        //Log.v("ItemViewFragment.addItemsOnSpinner", "myActivity: " + myActivity);
        //Log.v("ItemViewFragment.addItemsOnSpinner", "R.layout.fragment_item_list_layout: " + R.layout.fragment_item_list_layout);
        //Log.v("ItemViewFragment.addItemsOnSpinner", "catList: " + flistList);
        
        flistSpinnerDataAdapter = new MyCatSpinnerCustomAdapter(myActivity, R.layout.fragment_item_list_layout, flistList);
        flistSpinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        //Log.v("ItemViewFragment.addItemsOnSpinner", "catSpinner: " + flistSpinner);
        //Log.v("ItemViewFragment.addItemsOnSpinner", "catSpinnerDataAdapter: " + flistSpinnerDataAdapter);
        
        flistSpinner.setAdapter(flistSpinnerDataAdapter);
        
        //Log.v("ItemViewFragment.addItemsOnSpinner", "currentFlist.getID(): " + currentFlist.getID());
        flistSpinner.setOnItemSelectedListener(new SpinnerActivity());
        flistSpinner.setSelection(getPosition(currentFlist.getID()));
        flistSpinnerDataAdapter.notifyDataSetChanged();
        
        //ArrayList<Filter> myFilterList = myListMan.getFilterList();
        //filterSpinner = (Spinner) findViewById(R.id.category_edit_filter_spinner);
        //ArrayAdapter<Filter> myFilterAdapter = new MyFilterSpinnerCustomAdapter(this, R.layout.activity_add_edit_cat, myFilterList);
        //myFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //filterSpinner.setAdapter(myFilterAdapter);
    }
    public void addItemsOnList(ItemList myItemList)  {
		//Log.v("FlipList.addItemsOnList", "catID: " + currentFlistID );
		//Log.v("FLipList.addItemsonList", "currentItemList: " + myItemList);
		try {
			myListItems = myItemList.getListItems();
		} catch (Exception e) {
			Toast.makeText(activity, "Add a task to get started", Toast.LENGTH_LONG).show();
		}

    	itemListDataAdapter = new MyCustomAdapter(activity, R.layout.fragment_item_list_layout, myListItems);
    	ListView listView = (ListView) layoutView.findViewById(R.id.itemList);
    	//TextView itemListTitle = (TextView) layoutView.findViewById(R.id.item_view_activity_title);
    	listView.setAdapter(itemListDataAdapter);
    	//itemListTitle.setText(currentFlist.getName());
    	
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
 	       int flistID = currentFlist.getID();
 	       
 	       // Get the default type of category
 	       //Category myCat = myListMan.getFlist(catID);
 	       // myTypeID = myCat.getType();
 	       //ItemType myType = myListMan.getItemType(myTypeID);
 	       
 	       myListMan.addItem(flistID, name);
 	       addItemsOnList(myItemList);
 	       editText.setText("");
        }
        updateList(activity);
     }
    public void updateList(Activity myActivity) {
    	myItemList = getItemList(currentFlistID);
    	addItemsOnSpinner(myActivity);
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
	    					updateList(activity);
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
    		//Log.v("ItemListActivity.MyCustomAdapter", "currentFlist.showDueDate(): " + currentFlist.showDueDate());
    		//Log.v("ItemListActivity.MyCustomAdapter", "prefMan.showDueDateGlobal: " + prefMan.ShowDueDateGlobal);
    		if (currentFlist.showDueDate() && prefMan.ShowDueDateGlobal) {
    			if (item.hasDueTime() == true) {
    				infoString = "Due: " + item.getDueDatePretty() + " @ " + item.getDueTimePretty();
    				showInfo = true;
    			} else if (item.hasDueDate()) {
    				infoString = "Due: " + item.getDueDatePretty();
    				showInfo = true;
    			}
    		}
	    	if (currentFlist.showDescription() && prefMan.ShowItemDescriptionGlobal) {
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


    public class SpinnerActivity extends Activity implements OnItemSelectedListener {
    	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    		Flist catSelected = (Flist) parent.getItemAtPosition(pos);
    		currentFlist = catSelected;
        	mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    		SharedPreferences.Editor prefEditor = mySharedPreferences.edit();
    		prefEditor.putInt("current_category_id", currentFlist.getID());
    		prefEditor.commit();
    		addItemsOnList();
    	}
    	public void onNothingSelected(AdapterView<?> parent) {
    		// Nothing here yet
    	}
    }
    public void addItemsOnList()  {
    	int catID = currentFlist.getID();
		currentItemList = myListMan.getItemList(catID);
		//Log.v("ItemViewFragment.addItemsOnList", "catID: " + catID );
		//Log.v("ItemViewFragment.addItemsonList", "currentItemList: " + currentItemList);
		currentListItems = currentItemList.getListItems();
    	itemListDataAdapter = new MyCustomAdapter(activity, R.layout.activity_item_list_view, currentListItems);
    	ListView listView = (ListView) activity.findViewById(R.id.itemList);
    	listView.setAdapter(itemListDataAdapter);
    	
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			Item item = (Item) parent.getItemAtPosition(position);
    			editListItem(item);
    			/*
    			Toast.makeText(activity,
    					"Clicked on Row: " + item.getName(), 
    					Toast.LENGTH_LONG).show();
    					*/
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
	public void buildIndex(ArrayList<Flist> myCatList) {
		Integer position = 0;
		myPositionMap = new SparseIntArray();
		Log.v("ItemViewFragment.buildIndex", "myCatList.size()" + myCatList.size());
		for (Flist myCat : myCatList) {
			//Log.v("ItemViewFragment.buildIndex", "position: " + position);
			//Log.v("ItemViewFragment.buildIndex", "myCat.getName(): " + myCat.getName());
			//Log.v("ItemViewFragment.buildIndex", "myCat.getID(): " + myCat.getID());
			myPositionMap.put(myCat.getID(), position);
			position++;
		}
	}
	public int getPosition(int myListCategoryID) {
		int myPosition = myPositionMap.get(myListCategoryID);
		return myPosition;
	}
    private class MyCatSpinnerCustomAdapter extends ArrayAdapter<Flist> {
     	 
    	private ArrayList<Flist> categoryList;
    	private Activity activity;
    	LayoutInflater inflater;
    	SparseIntArray myPositionMap;
    	 
    	public MyCatSpinnerCustomAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<Flist> objects) {    		
    		super(activitySpinner, textViewResourceId, objects);
    		this.categoryList = (ArrayList<Flist>) objects;
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
    			convertView = inflater.inflate(R.layout.spinner_view, null);
    			holder = new ViewHolder();
    			holder.catName = (TextView) convertView.findViewById(R.id.spinner_text);
    			convertView.setTag(holder);
    		} else {
    			holder = (ViewHolder) convertView.getTag();
    		}
    		Flist category = categoryList.get(position);
    		
			myPositionMap.put(category.getID(), position);
    		holder.catName.setText(category.getName());
    		holder.catName.setTag(category);
    		return convertView;
    	}
    	 
	}
    
}

