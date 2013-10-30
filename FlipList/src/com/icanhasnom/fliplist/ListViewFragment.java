package com.icanhasnom.fliplist;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
 
public class ListViewFragment extends Fragment {
	MyFlistListCustomAdapter flistListDataAdapter;
	MyCategoryListCustomAdapter categoryListDataAdapter;
	MyFilterListCustomAdapter filterListDataAdapter;
	ArrayList<Flist> flistList;
	ArrayList<Category> categoryList;
	ArrayList<Filter> filterList;
	ViewGroup container;
	Activity activity;
	ListManager myListMan;
	View layoutView;
	SparseIntArray myPositionMap;
    OnFlistChangedListener mFlistCallback;
    OnFlistSelectedListener sFlistCallback;
    OnCategoryChangedListener mCategoryCallback;
    OnCategorySelectedListener sCategoryCallback;
    OnFilterChangedListener mFilterCallback;
    OnFilterSelectedListener sFilterCallback;
	
	// Tasks for ActivityResult
	static Integer MANAGE = 6;
	static Integer ADD = 5;
	static Integer EDIT = 4;
 
    static ListViewFragment init(int val) {
        ListViewFragment truitonList = new ListViewFragment();
 
        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt("val", val);
        truitonList.setArguments(args);
 
        return truitonList;
    }
    @Override
    public void onAttach(Activity a) {
    	super.onAttach(activity);
    	activity = a;
        try {
            mFlistCallback = (OnFlistChangedListener) activity;
            sFlistCallback = (OnFlistSelectedListener) activity;
            mCategoryCallback = (OnCategoryChangedListener) activity;
            sCategoryCallback = (OnCategorySelectedListener) activity;
            mFilterCallback = (OnFilterChangedListener) activity;
            sFilterCallback = (OnFilterSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCategoryChangedListener");
        }
    }
 
    /**
     * Retrieving this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		myListMan = new ListManager(activity);
        //categoryList = myListMan.getCategoriesAndFilters();
		initVars();
    }
 
    /**
     * The Fragment's UI is a simple text view showing its instance number and
     * an associated list.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_flist_list_layout, container, false);
        addFlistsOnList();
        addCategoriesOnList();
        addFiltersOnList();
        return layoutView;
    }
 
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setListAdapter(new ArrayAdapter<String>(getActivity(),
         //       android.R.layout.simple_list_item_1, arr));
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 5 && resultCode == -1) {
    	}
    	// Always refresh the list when coming back from child activity
		refreshList();
    	Log.v("CategoryViewFragment.onActivityResult", "requestCode: " + requestCode + " resultCode: " + resultCode);
    	super.onActivityResult(requestCode, resultCode, data);
    	mFlistCallback.onFlistChanged(1);
    	mCategoryCallback.onCategoryChanged(1);
    	mFilterCallback.onFilterChanged(1);
    }


    // Container Activity must implement this interface
    public interface OnFlistChangedListener {
        public void onFlistChanged(int catID);
    }
    public interface OnFlistSelectedListener {
    	public void onFlistSelected(int catID);
    }
    public interface OnCategoryChangedListener {
        public void onCategoryChanged(int flistID);
    }
    public interface OnCategorySelectedListener {
    	public void onCategorySelected(int flistID);
    }
    public interface OnFilterChangedListener {
        public void onFilterChanged(int filterID);
    }
    public interface OnFilterSelectedListener {
    	public void onFilterSelected(int filterID);
    }

    public void initVars() {
		myListMan = new ListManager(activity);
		flistList = myListMan.getFlists();
		categoryList = myListMan.getCategories();
		filterList = myListMan.getFilters();
    }
    public void refreshList() {
    	initVars();
    	addCategoriesOnList();
    }
    public void showList(Integer catID) {
		Intent itemListActivity = new Intent(activity, ItemListActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("catID", catID);
		itemListActivity.putExtras(b);
		this.startActivity(itemListActivity);
    }
    
    public void addFlistsOnList() {
    	// Populate the Category List
    	myPositionMap = buildFlistIndex(flistList);
    	flistListDataAdapter = new MyFlistListCustomAdapter(activity, R.layout.fragment_flist_list_layout, flistList);
    	ListView listView = (ListView) layoutView.findViewById(R.id.flist_list_list_view);
    	listView.setAdapter(flistListDataAdapter);
    	
    	// TODO: Are these onclicks used?
    	// Set up the onClick event for each of the list items
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			// When clicked, show a toast with the TextView text
    			Flist flist = (Flist) parent.getItemAtPosition(position);
    			sFlistCallback.onFlistSelected(flist.getID());
				//showList(category.getID());
    		}
    	});
    	listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
    			Log.v("Long clicked", "pos" + " " + position);
    			Flist category = (Flist) parent.getItemAtPosition(position);
    			editCategory(view, category.getID());
    			return true;
    		}
    	});
    	Button addFlistButton = (Button) layoutView.findViewById(R.id.flist_list_add_button);
    	//addFlistButton.setOnClickListener(new View.OnClickListener() {

    	//	public void onClick(View v) {
    	//		addListButtonListener(v);
    	//	}
    	//});
    }
    
    public void addCategoriesOnList() {
    	// Populate the Category List
    	myPositionMap = buildCategoryIndex(categoryList);
    	categoryListDataAdapter = new MyCategoryListCustomAdapter(activity, R.layout.fragment_category_list_layout, categoryList);
    	ListView listView = (ListView) layoutView.findViewById(R.id.flist_category_list_view);
    	listView.setAdapter(categoryListDataAdapter);
    	
    	// TODO: Are these onclicks used?
    	// Set up the onClick event for each of the list items
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			// When clicked, show a toast with the TextView text
    			Flist category = (Flist) parent.getItemAtPosition(position);
    			sCategoryCallback.onCategorySelected(category.getID());
				//showList(category.getID());
    		}
    	});
    	listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
    			Log.v("Long clicked", "pos" + " " + position);
    			Flist category = (Flist) parent.getItemAtPosition(position);
    			editCategory(view, category.getID());
    			return true;
    		}
    	});
    	Button addCategoryButton = (Button) layoutView.findViewById(R.id.category_list_add_button);
    	//addCategoryButton.setOnClickListener(new View.OnClickListener() {

    	//	public void onClick(View v) {
    	//		addCategoryButtonListener(v);
    	//	}
    	//});
    }

    public void addFiltersOnList() {
    	// Populate the Category List
    	myPositionMap = buildFilterIndex(filterList);
    	filterListDataAdapter = new MyFilterListCustomAdapter(activity, R.layout.fragment_filter_list_layout, filterList);
    	ListView listView = (ListView) layoutView.findViewById(R.id.flist_filter_list_view);
    	listView.setAdapter(filterListDataAdapter);
    	
    	// TODO: Are these onclicks used?
    	// Set up the onClick event for each of the list items
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			// When clicked, show a toast with the TextView text
    			Filter filter = (Filter) parent.getItemAtPosition(position);
    			sFilterCallback.onFilterSelected(filter.getID());
				//showList(category.getID());
    		}
    	});
    	listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
    			Log.v("Long clicked", "pos" + " " + position);
    			Filter filter = (Filter) parent.getItemAtPosition(position);
    			editFilter(view, filter.getID());
    			return true;
    		}
    	});
    	//Button addFilterButton = (Button) layoutView.findViewById(R.id.filter_list_add_button);
    	//addFilterButton.setOnClickListener(new View.OnClickListener() {

    	//	public void onClick(View v) {
    	//		addFilterButtonListener(v);
    	//	}
    	//});
    }

	public SparseIntArray buildFlistIndex(ArrayList<Flist> myFlistList) {
		SparseIntArray myPositionMap = new SparseIntArray();
		Integer position = 0;
		for (Flist myCat : myFlistList) {
			myPositionMap.put(myCat.getID(), position);
			position++;
		}
		return myPositionMap;
	}
	public SparseIntArray buildCategoryIndex(ArrayList<Category> myCategoryList) {
		SparseIntArray myPositionMap = new SparseIntArray();
		Integer position = 0;
		for (Category myCat : myCategoryList) {
			myPositionMap.put(myCat.getID(), position);
			position++;
		}
		return myPositionMap;
	}
	public SparseIntArray buildFilterIndex(ArrayList<Filter> myFilterList) {
		SparseIntArray myPositionMap = new SparseIntArray();
		Integer position = 0;
		for (Filter myCat : myFilterList) {
			myPositionMap.put(myCat.getID(), position);
			position++;
		}
		return myPositionMap;
	}
	public int getPosition(int myListCategoryID, SparseIntArray myPositionMap) {
		int myPosition = myPositionMap.get(myListCategoryID);
		return myPosition;
	}
    public void editList(View view, Integer listID) {
		Intent addEditListActivity = new Intent(view.getContext(), AddEditListActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("catID", listID);
		b.putSerializable("task", EDIT);
		addEditListActivity.putExtras(b);
		this.startActivityForResult(addEditListActivity, 5);
    }
    public void editCategory(View view, Integer catID) {
		Intent addEditCategoryActivity = new Intent(view.getContext(), AddEditFlistActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("catID", catID);
		b.putSerializable("task", EDIT);
		addEditCategoryActivity.putExtras(b);
		this.startActivityForResult(addEditCategoryActivity, 5);
    }
    public void editFilter(View view, Integer filterID) {
		Intent addEditFilterActivity = new Intent(view.getContext(), AddEditFilterActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("catID", filterID);
		b.putSerializable("task", EDIT);
		addEditFilterActivity.putExtras(b);
		this.startActivityForResult(addEditFilterActivity, 5);
    }
    public void addListButtonListener(View view) {
		Intent addEditListActivity = new Intent(view.getContext(), AddEditListActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("task", ADD);
		addEditListActivity.putExtras(b);
		this.startActivityForResult(addEditListActivity, 5);
    }
    public void addCategoryButtonListener(View view) {
		Intent addEditCategoryActivity = new Intent(view.getContext(), AddEditFlistActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("task", ADD);
		addEditCategoryActivity.putExtras(b);
		this.startActivityForResult(addEditCategoryActivity, 5);
    }
    //public void addFilterButtonListener(View view) {
	//	Intent addEditFilterActivity = new Intent(view.getContext(), AddEditFilterActivity.class);
	//	Bundle b = new Bundle();
	//	b.putSerializable("task", ADD);
	//	addEditFilterActivity.putExtras(b);
	//	this.startActivityForResult(addEditFilterActivity, 5);
    //}

    private class MyFlistListCustomAdapter extends ArrayAdapter<Flist> {
     	 
    	private ArrayList<Flist> flistList;
    	 
    	public MyFlistListCustomAdapter(Context context, int textViewResourceId, ArrayList<Flist> flistList) {
    		super(context, textViewResourceId, flistList);
    		this.flistList = new ArrayList<Flist>();
    		this.flistList.addAll(flistList);
    	}
    	
    	private class ViewHolder {
    		TextView flist_list_text_view;
    		TextView flist_list_text_view_type;
    	}
    	 
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {

    		ViewHolder holder = null;
    		//Log.v("ConvertView", String.valueOf(position));
    		//Log.v("AddEditCatActivity", "convertView" + convertView);
    	 
    		if (convertView == null) {
    			LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			// TODO: Should I set null to parent? It errors if I do but I can't call the one below if i do.
    			convertView = vi.inflate(R.layout.fragment_flist_list_view, null);
    	 
    			holder = new ViewHolder();
    			holder.flist_list_text_view = (TextView) convertView.findViewById(R.id.flist_list_text_view);
    			holder.flist_list_text_view_type = (TextView) convertView.findViewById(R.id.flist_list_text_view_type);

    			convertView.setTag(holder);
    			
    			View.OnClickListener flistClickListener;
    			flistClickListener = new View.OnClickListener() {  
    				public void onClick(View v) {  
    					TextView tv = (TextView) v;
    					Flist flist = (Flist) tv.getTag();
    					//showList(category.getID());
    					//Log.v("CategoryViewFragment.MyCatListCustomAdapter", "category.getID(): " + category.getID());
    					sFlistCallback.onFlistSelected(flist.getID());
    				}  
    			};
    			holder.flist_list_text_view.setOnClickListener( flistClickListener );  
    	   } 
    	   else {
    	    holder = (ViewHolder) convertView.getTag();
    		}
    	 
    		Flist category = flistList.get(position);
    		// TODO: Make a function in ListManager to get and hold the category type names in memory
    		int categoryTypeID = category.getType();
    		//String categoryDescription = category.getDescription();
    		String categoryName = category.getName();
			String categoryType = "(F)";
    		if (category.getFilterID() == 0) {
    			categoryType = "(C)";
    		}
    		String categoryTypeName = myListMan.getItemTypeName(categoryTypeID);
    		
    		Log.v("MyCatListCustomAdapter", "categoryTypeID:" + categoryTypeID);
    		Log.v("MyCatListCustomAdapter", "category.getDescription(): " + category.getDescription());
    		Log.v("MyCatListCustomAdapter", "myListMan.getCategoryTypeName(): " + myListMan.getItemTypeName(categoryTypeID));
    		
    		holder.flist_list_text_view.setText(categoryType + " " + categoryName);
    		holder.flist_list_text_view_type.setText("(" + categoryTypeName + ")");
    		holder.flist_list_text_view.setTag(category);
    		// TODO: Link this to type type editing activity later
    		holder.flist_list_text_view_type.setTag(category);
    	 
    		return convertView;
    	}
    	 
	}
    private class MyCategoryListCustomAdapter extends ArrayAdapter<Category> {
     	 
    	private ArrayList<Category> categoryList;
    	 
    	public MyCategoryListCustomAdapter(Context context, int textViewResourceId, ArrayList<Category> categoryList) {
    		super(context, textViewResourceId, categoryList);
    		this.categoryList = new ArrayList<Category>();
    		this.categoryList.addAll(categoryList);
    	}
    	
    	private class ViewHolder {
    		TextView category_list_text_view;
    		TextView category_list_text_view_type;
    	}
    	 
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {

    		ViewHolder holder = null;
    		//Log.v("ConvertView", String.valueOf(position));
    		//Log.v("AddEditCatActivity", "convertView" + convertView);
    	 
    		if (convertView == null) {
    			LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			// TODO: Should I set null to parent? It errors if I do but I can't call the one below if i do.
    			convertView = vi.inflate(R.layout.fragment_category_list_view, null);
    	 
    			holder = new ViewHolder();
    			holder.category_list_text_view = (TextView) convertView.findViewById(R.id.category_list_text_view);
    			holder.category_list_text_view_type = (TextView) convertView.findViewById(R.id.category_list_text_view_type);

    			convertView.setTag(holder);
    			
    			View.OnClickListener categoryClickListener;
    			categoryClickListener = new View.OnClickListener() {  
    				public void onClick(View v) {  
    					TextView tv = (TextView) v;
    					Category category = (Category) tv.getTag();
    					//showList(category.getID());
    					//Log.v("CategoryViewFragment.MyCatListCustomAdapter", "category.getID(): " + category.getID());
    					sCategoryCallback.onCategorySelected(category.getID());
    				}  
    			};
    			holder.category_list_text_view.setOnClickListener( categoryClickListener );  
    	   } 
    	   else {
    	    holder = (ViewHolder) convertView.getTag();
    		}
    	 
    		Category category = categoryList.get(position);
    		// TODO: Make a function in ListManager to get and hold the category type names in memory
    		//int categoryTypeID = category.getType();
    		//String categoryDescription = category.getDescription();
    		String categoryName = category.getName();
			String type = "(C)";
    		//if (category.getFilterID() == 0) {
    		//	type = "(C)";
    		//}
    		//String categoryTypeName = myListMan.getItemTypeName(categoryTypeID);
    		
    		//Log.v("MyCatListCustomAdapter", "categoryTypeID:" + categoryTypeID);
    		//Log.v("MyCatListCustomAdapter", "category.getDescription(): " + category.getDescription());
    		//Log.v("MyCatListCustomAdapter", "myListMan.getCategoryTypeName(): " + myListMan.getItemTypeName(categoryTypeID));
    		
    		holder.category_list_text_view.setText(type + " " + categoryName);
    		holder.category_list_text_view_type.setText("");
    		holder.category_list_text_view.setTag(category);
    		// TODO: Link this to type type editing activity later
    		holder.category_list_text_view_type.setTag(category);
    	 
    		return convertView;
    	}
    	 
	}
    private class MyFilterListCustomAdapter extends ArrayAdapter<Filter> {
     	 
    	private ArrayList<Filter> filterList;
    	 
    	public MyFilterListCustomAdapter(Context context, int textViewResourceId, ArrayList<Filter> filterList) {
    		super(context, textViewResourceId, filterList);
    		this.filterList = new ArrayList<Filter>();
    		this.filterList.addAll(filterList);
    	}
    	
    	private class ViewHolder {
    		TextView filter_list_text_view;
    		TextView filter_list_text_view_type;
    	}
    	 
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {

    		ViewHolder holder = null;
    		//Log.v("ConvertView", String.valueOf(position));
    		//Log.v("AddEditCatActivity", "convertView" + convertView);
    	 
    		if (convertView == null) {
    			LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			// TODO: Should I set null to parent? It errors if I do but I can't call the one below if i do.
    			convertView = vi.inflate(R.layout.fragment_filter_list_view, null);
    	 
    			holder = new ViewHolder();
    			holder.filter_list_text_view = (TextView) convertView.findViewById(R.id.filter_list_text_view);
    			holder.filter_list_text_view_type = (TextView) convertView.findViewById(R.id.filter_list_text_view_type);

    			convertView.setTag(holder);
    			
    			View.OnClickListener filterClickListener;
    			filterClickListener = new View.OnClickListener() {  
    				public void onClick(View v) {  
    					TextView tv = (TextView) v;
    					Filter filter = (Filter) tv.getTag();
    					//showList(category.getID());
    					//Log.v("CategoryViewFragment.MyCatListCustomAdapter", "category.getID(): " + category.getID());
    					sFilterCallback.onFilterSelected(filter.getID());
    				}  
    			};
    			holder.filter_list_text_view.setOnClickListener( filterClickListener );  
    	   } 
    	   else {
    	    holder = (ViewHolder) convertView.getTag();
    		}
    	 
    		Filter filter = filterList.get(position);
    		// TODO: Make a function in ListManager to get and hold the category type names in memory
    		//int filterTypeID = filter.getType();
    		//String categoryDescription = category.getDescription();
    		String categoryName = filter.getName();
			String type = "(F)";
    		//if (filter.getFilterID() == 0) {
    		//type = "(F)";
    		//}
    		//String categoryTypeName = myListMan.getItemTypeName(categoryTypeID);
    		
    		//Log.v("MyCatListCustomAdapter", "categoryTypeID:" + categoryTypeID);
    		//Log.v("MyCatListCustomAdapter", "category.getDescription(): " + category.getDescription());
    		//Log.v("MyCatListCustomAdapter", "myListMan.getCategoryTypeName(): " + myListMan.getItemTypeName(categoryTypeID));
    		
    		holder.filter_list_text_view.setText(type + " " + categoryName);
    		holder.filter_list_text_view_type.setText("");
    		holder.filter_list_text_view.setTag(filter);
    		// TODO: Link this to type type editing activity later
    		holder.filter_list_text_view_type.setTag(filter);
    	 
    		return convertView;
    	}
    	 
	}
 
}