package com.icanhasnom.fliplist;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseIntArray;
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

// Currently Working On
//   Getting onclick for category view fragment to switch fragment to item view fragment
//   Enabling item view fragment update when fragemnt becomes active (as in after clicking on category, or selecting a drop down item)
//   Enabling item view fragment spinner updating
//   Setting up an init and refresh method for erach fragment and action

// ToDo Next
// Ability to assign multiple categories to tasks
// TODO: Format list item list display to show tiny date below description to leave more room for actual description
// TODO: Fix button layout on Category Edit/Add page
// TODO: Set types to be assigned to ITEM - Determined by default type of category
// TODO: Make filter types for category (first one will display all, and have None option)
// TODO: Have current category displayed when returning to list view
// TODO: Add completed field to item and isChecked so it knows to be checked if its completed

// UI Design
// Make 3 tabs, one for category list, one for task list, one for something else
//   like its done in the tunein android app (use actionBar.addTab()), example in PrefsFragment
// Make buttons smoother
// Add swipe sideways from category to category?
// Third tab could be Item Edit or Add screen. If you select an item it slides over to
//    the right and you can edit. If nothing is selected and you swipe, it lets you add one.
// Have save icon on the action bar, as well as the menu items in a 3dot drop down

// List Design
//   Lists would apply filters. If lists have only one category, an item could be quick added and apply the
//   default type for that category.

// Category Design
//   In addition to having a type, categories should have the ability to choose the behavior when items are checked
//     they could (1) go away immediately (2) stay for X time (3) Disappear after X other items are checked
//   Items would be created into uncategorized by default

// Item Edit Design
//    Let the user add attributes one at a time (name is always there) like description, notes, etc...
//    The type of item determines the attributes that are able to be added, or additional to the standard attributes

// Filter Design (Acts as a categoryList item?)
//   Ability to make custom filters

// Sorting Lists (change categories to lists and have categories be adable to lists)
//   Or should sorting just be added to the filter?

// Widget Design
// Make widget class that does only one category (for grocery list etc...)

// Types Design
// Make certain list types change the defaults for items added
//   Grocery List type would not have a date by default and no reminders
//   Have it learn common items and group them by section in store
//   Password list type - make list passworded and encrypt the list items
//   Songs - have it link you to places to download them
//           maybe have it add list items by using a song finder app
// Make type creation tool that lets you change all options and set sizes display characteristics

// Event Logging
//   Where should i log events? In the item itself? Or have a log?

// Caching and Redrawing
// TODO: Only recreate the listmap if we've written, edited or deleted a category, item or type
// TODO: Pull all of the items at one time from the DB into a searchable hash for parsing into itemlists

// Completed Items
// TODO: Add archive time? Might start taking a long time to load all items if we dont' move them somewhere else... 

// UI:
// TODO: Move the categories drop down to the action bar

// BUGS & FIXES
// TODO: Sometimes click listener for categories only works on description

// General Todo
// TODO: Show tiny category default type on main dropdown on right (just use a different layout, and maybe adapter)
// TODO: Set up defaults for item creation depending on category type (i.e. todo would have a due date default to a week away)
// TODO: Should I allow creation of items in groups with certain filters or types? Like next 7 days, it would not necessarily show all items in that category
// TODO: Rename main category to List. (i.e. manage lists, not categories)
// TODO: Add ability to add items to multiple categories. It will default to default. Must always be in one category. (default undeletable)
// TODO: Turn Due Date RED if its past current date
// TODO: Make each field on edit screen show up only if assigned & create + button to add each one
// TODO: Break preferences out to its own class (PreferencesHelper?)
// TODO: Move database clear on upgrade to a function and only do this once we have a DB upgrade function set up also


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FlipList extends FragmentActivity implements 
ListViewFragment.OnCategoryChangedListener, 
ListViewFragment.OnCategorySelectedListener,
ListViewFragment.OnFlistChangedListener,
ListViewFragment.OnFlistSelectedListener,
ListViewFragment.OnFilterChangedListener,
ListViewFragment.OnFilterSelectedListener {
	public final static String EXTRA_MESSAGE = "com.icanhasnom.FlipList.MESSAGE";
	public static final String TAG = "FlipList";
	public final static int RESULT_DELETED = 3;
	
	ListManager myListMan;
	int defaultCatID;
	
	Boolean prefShowItemDescriptionGlobal;
	Boolean prefShowDueDateGlobal;
	Integer prefRemoveCompletedItems;
    Integer prefRemoveCompletedItemsDelay;
    
    HashMap<String, Item> checkListItems = new HashMap<String, Item>();

    Flist currentFlist;
    Integer currentFlistID;
    Item currentItem;
    ArrayList<Flist> catList;


	SparseIntArray myPositionMap;
    
    //MyCustomAdapter itemListDataAdapter;
    //MyCatSpinnerCustomAdapter catSpinnerDataAdapter;
    SharedPreferences mySharedPreferences;
    
    //Spinner catSpinner;
    //Spinner itemCatSpinner;
    
    EditText editText;
    DatabaseHandler db;
    
    MyFragmentPagerAdapter mAdapter;
    ViewPager mPager;
    
    ListViewFragment categoryFragment;
    ItemViewFragment itemFragment;
    UtilViewFragment utilFragment;
    
    static final Integer ITEMS = 3;
    
	// Tasks for ActivityResult
	static Integer MANAGE_CATEGORIES = 6;
	static Integer ADD_CATEGORY = 5;
	static Integer EDIT_CATEGORY = 4;
	
	static Integer CATEGORY_VIEW_FRAGMENT = 0;
	static Integer ITEM_VIEW_FRAGMENT = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        db = new DatabaseHandler(this);
		myListMan = new ListManager(this);
		
		init(savedInstanceState);
        
        setContentView(R.layout.fragment_pager);
        
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        Fragment testFrag = mAdapter.getItem(ITEM_VIEW_FRAGMENT);
        Log.v("FlipList.onCreate", "testFrag: " + testFrag);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        
		//loadPref();
        Log.v("FlipList.onCreate", "defaultCatID: " + defaultCatID);
		Log.v("FlipList.onCreate", "2) this: " + this);
		Log.v("FlipList.onCreate", "Fragment Tag (2): " + getFragmentTag(2));
		
        // Watch for button clicks.
		/*
        Button button = (Button)findViewById(R.id.goto_first);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(0);
            }
        });
        button = (Button)findViewById(R.id.goto_last);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(NUM_ITEMS-1);
            }
        });
        */
    }
    public void init(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
        	restoreState(savedInstanceState);
        	Log.v("FlipList.onCreate", "Restoring saved instance state");
        } else {
        	loadPref();
        }
    }
    
    private String getFragmentTag(int pos){
        return "android:switcher:"+R.id.pager+":"+pos;
    }

    public static class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
    	private Map<Integer, Fragment> mPageReferenceMap = new HashMap<Integer, Fragment>();
		public MyFragmentPagerAdapter(android.support.v4.app.FragmentManager fragmentManager) {
            super(fragmentManager);
        }
 
        @Override
        public int getCount() {
            return ITEMS;
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
            case 0:
            	Fragment categoryViewFragment = ListViewFragment.init(position);
            	mPageReferenceMap.put(Integer.valueOf(position), categoryViewFragment);
                return categoryViewFragment;
            case 1:
            	Fragment itemViewFragment = ItemViewFragment.init(position);
            	mPageReferenceMap.put(Integer.valueOf(position), itemViewFragment);
                return itemViewFragment;
            case 2:
            	Fragment utilViewFragment = UtilViewFragment.init(position);
            	mPageReferenceMap.put(Integer.valueOf(position),  utilViewFragment);
            	return utilViewFragment;
            default:
            	ListViewFragment defCategoryViewFragment = ListViewFragment.init(position);
            	mPageReferenceMap.put(Integer.valueOf(position), defCategoryViewFragment);
                return defCategoryViewFragment;
            }
        }
        @SuppressWarnings("deprecation")
		@Override
        public void destroyItem(View container, int position, Object object) {
        	super.destroyItem(container,  position,  object);
        	mPageReferenceMap.remove(Integer.valueOf(position));
        	
        }
        public Fragment getFragment(int key) {
        	return mPageReferenceMap.get(key);
        }
    }
    public void setCurrentPagerItem(int item) {
    	mPager.setCurrentItem(item);
    }
    public void restoreState(Bundle savedInstanceState) {
    	myListMan = (ListManager) savedInstanceState.getSerializable("ListManager");
    	Log.v("FlipList.restoreState", "Restored ListManager!");
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
    		Intent addEditCatIntent = new Intent(this, AddEditFlistListActivity.class);
    		Bundle b = new Bundle();
    		b.putSerializable("task", MANAGE_CATEGORIES);
    		addEditCatIntent.putExtras(b);
    		this.startActivityForResult(addEditCatIntent, 6);
    		break;
    	case R.id.menu_settings:
    		 Intent intent = new Intent();
    		 intent.setClass(FlipList.this, SetPreferenceActivity.class);
    	     startActivityForResult(intent, 0);
    		break;
    	case R.id.export_import_db:
    		Intent exportImportIntent = new Intent();
    		exportImportIntent.setClass(FlipList.this, ExportImportDBActivity.class);
    		startActivityForResult(exportImportIntent, 9);
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    	return true;
    	
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v("FlipList.onActivityResult", "requestCode: " + requestCode + " resultCode: " + resultCode);
    	if (requestCode == 0) {
        	loadPref();
    	}
    	if (requestCode == 1 && resultCode == RESULT_OK) {
            currentFlistID = data.getIntExtra("flistID", currentFlistID);
            currentFlist = myListMan.getFlist(currentFlistID);
            
    		ListViewFragment listViewFragment = (ListViewFragment) mAdapter.getItem(0);
    		listViewFragment.refreshList();
    		mAdapter.notifyDataSetChanged();
    		
    		ItemViewFragment itemViewFragment = (ItemViewFragment) mAdapter.getItem(1);
    		itemViewFragment.refreshPage(this);
    		
            Log.v("FlipList.onActivityResult", "Got Result Code -1, currentCategoryID: " + currentFlistID + " currentCategory.getName(): " + currentFlist.getName());
    	} else if (requestCode == 1 && resultCode == RESULT_DELETED) {
    		loadPref();
    	} else if (requestCode == 1 && resultCode == RESULT_CANCELED) {
    		// Do nothing
    	}
    	if (requestCode == 5) {
    		// Refresh content in CategoryViewFragment
    		ListViewFragment listViewFragment = (ListViewFragment) mAdapter.getItem(0);
    		listViewFragment.refreshList();
    		
    		ItemViewFragment itemViewFragment = (ItemViewFragment) mAdapter.getItem(1);
    		itemViewFragment.refreshPage(this);
    		
    		mAdapter.notifyDataSetChanged();
    	}
    	//addItemsOnSpinner();
    	// TODO: Do i need this?
    	//addItemsOnList();
    	super.onActivityResult(requestCode, resultCode, data);
    }
    public void onFlistSelected(int selectedFlist) {
    	Log.v("FlipList.onFlistSelected", "selectedFlist: " + selectedFlist);
    	try {
    		setCurrentPagerItem(ITEM_VIEW_FRAGMENT);
        	Log.v("FlipList.onCategorySelected", "itemFragment: " + itemFragment);
        	mAdapter.notifyDataSetChanged();
        	ItemViewFragment ivf = (ItemViewFragment) ((MyFragmentPagerAdapter)mPager.getAdapter()).getFragment(ITEM_VIEW_FRAGMENT);
        	ivf.initCat(FlipList.this, selectedFlist);
    	} catch (Exception e) {
    		e.printStackTrace();
    		Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
    	}
    }
    public void onCategorySelected(int selectedCat) {
    	Log.v("FlipList.onCategorySelected", "selectedCat: " + selectedCat);
    	try {
    		setCurrentPagerItem(ITEM_VIEW_FRAGMENT);
        	Log.v("FlipList.onCategorySelected", "itemFragment: " + itemFragment);
        	mAdapter.notifyDataSetChanged();
        	ItemViewFragment ivf = (ItemViewFragment) ((MyFragmentPagerAdapter)mPager.getAdapter()).getFragment(ITEM_VIEW_FRAGMENT);
        	ivf.initCat(FlipList.this, selectedCat);
    	} catch (Exception e) {
    		e.printStackTrace();
    		Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
    	}
    }
    public void onFilterSelected(int selectedFilter) {
    	Log.v("FlipList.onFilterSelected", "selectedFilter: " + selectedFilter);
    	try {
    		setCurrentPagerItem(ITEM_VIEW_FRAGMENT);
        	Log.v("FlipList.onFilterSelected", "itemFragment: " + itemFragment);
        	mAdapter.notifyDataSetChanged();
        	ItemViewFragment ivf = (ItemViewFragment) ((MyFragmentPagerAdapter)mPager.getAdapter()).getFragment(ITEM_VIEW_FRAGMENT);
        	ivf.initCat(FlipList.this, selectedFilter);
    	} catch (Exception e) {
    		e.printStackTrace();
    		Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
    	}
    }
    public void onFlistChanged(int position) {
    	
    }
    public void onCategoryChanged(int position) {
        // The user selected the headline of an article from the HeadlinesFragment
        // Do something here to display that article
/*
        ItemViewFragment articleFrag = (ItemViewFragment) getSupportFragmentManager().findFragmentById(R.id.item_view_fragment);

        if (articleFrag != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            articleFrag.updateArticleView(position);
        } else {
            // Otherwise, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            ArticleFragment newFragment = new ArticleFragment();
            Bundle args = new Bundle();
            args.putInt(ArticleFragment.ARG_POSITION, position);
            newFragment.setArguments(args);
        
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
        **/
    }

    public void onFilterChanged(int position) {
    	
    }
    private void loadPref(){
    	mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        defaultCatID = Integer.parseInt(mySharedPreferences.getString(getString(R.string.default_flist_key), getString(R.integer.default_flist_default)));
        Log.v("FlipList.loadPref", "this:  " + this);
        Log.v("FlipList.loadPref", "Loaded Pref from mySharedPreferences, defaultCatID: " + defaultCatID);

        prefShowItemDescriptionGlobal = mySharedPreferences.getBoolean(getString(R.string.show_description_global_key), getResources().getBoolean(R.bool.show_description_global_default));
        prefShowDueDateGlobal = mySharedPreferences.getBoolean(getString(R.string.show_due_date_global_key), getResources().getBoolean(R.bool.show_due_date_global_default));

        // TODO: Create sanity check to ensure that default category exists
        //defaultCatID = mySharedPreferences.getInt("current_category_id", 0);
        if (currentFlistID == null) currentFlistID = defaultCatID;
        currentFlist = myListMan.getFlist(currentFlistID);
        Log.v("FlipList.loadPrefs", "(1) Setting currentCategory to " + currentFlist.getName());
    }
    /**
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
    
*/


    /**
    public void addItemsOnList()  {
    	int catID = currentCategory.getID();
		currentItemList = myListMan.getItemList(catID);
		Log.v("FlipList.addItemsOnList", "catID: " + catID );
		Log.v("FLipList.addItemsonList", "currentItemList: " + currentItemList);
		currentListItems = currentItemList.getListItems();
    	itemListDataAdapter = new MyCustomAdapter(this, R.layout.activity_item_list_view, currentListItems);
    	ListView listView = (ListView) findViewById(R.id.itemList);
    	listView.setAdapter(itemListDataAdapter);
    	
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
    */

/**
    
    private class MyCustomAdapter extends ArrayAdapter<Item> {
    	 
    	private ArrayList<Item> itemList;
		//Category catSelectedObj = (Category) catSpinner.getSelectedItem();
    	 
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
	    					addItemsOnList();
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
    		if (catSelectedObj.showDueDate() && prefShowDueDateGlobal) {
    			if (item.hasDueTime() == true) {
    				infoString = "Due: " + item.getDueDatePretty() + " @ " + item.getDueTimePretty();
    				showInfo = true;
    			} else if (item.hasDueDate()) {
    				infoString = "Due: " + item.getDueDatePretty();
    				showInfo = true;
    			}
    		}
	    	if (catSelectedObj.showDescription() && prefShowItemDescriptionGlobal) {
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
    */

    public void onBackPressed() {
    	  //Your code here
    	  super.onBackPressed();
    }

}
