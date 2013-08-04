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
 
public class CategoryViewFragment extends Fragment {
	MyCatListCustomAdapter catListDataAdapter;
	ArrayList<Category> categoryList;
	ViewGroup container;
	Activity activity;
	ListManager myListMan;
	View layoutView;
	SparseIntArray myPositionMap;
	
	// Tasks for ActivityResult
	static Integer MANAGE_CATEGORIES = 6;
	static Integer ADD_CATEGORY = 5;
	static Integer EDIT_CATEGORY = 4;
 
    static CategoryViewFragment init(int val) {
        CategoryViewFragment truitonList = new CategoryViewFragment();
 
        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt("val", val);
        truitonList.setArguments(args);
 
        return truitonList;
    }
    public void onAttach(Activity a) {
    	super.onAttach(activity);
    	activity = a;
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
        layoutView = inflater.inflate(R.layout.fragment_category_list_layout, container, false);
        addCategoriesOnList();
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
    }
    public void initVars() {
		myListMan = new ListManager(activity);
		categoryList = myListMan.getCategoriesAndFilters();
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
    public void addCategoriesOnList() {
    	// Populate the Category List
    	myPositionMap = buildIndex(categoryList);
    	catListDataAdapter = new MyCatListCustomAdapter(activity, R.layout.fragment_category_list_layout, categoryList);
    	ListView listView = (ListView) layoutView.findViewById(R.id.category_list_list_view);
    	listView.setAdapter(catListDataAdapter);
    	
    	// Set up the onClick event for each of the list items
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			// When clicked, show a toast with the TextView text
    			Category category = (Category) parent.getItemAtPosition(position);
				showList(category.getID());
    		}
    	});
    	listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
    			Log.v("Long clicked", "pos" + " " + position);
    			Category category = (Category) parent.getItemAtPosition(position);
    			editCategory(view, category.getID());
    			return true;
    		}
    	});
    	Button addCategoryButton = (Button) layoutView.findViewById(R.id.category_list_add_button);
    	addCategoryButton.setOnClickListener(new View.OnClickListener() {

    		public void onClick(View v) {
    			addCategoryButtonListener(v);
    		}
    	});
    }

	public SparseIntArray buildIndex(ArrayList<Category> myCatList) {
		SparseIntArray myPositionMap = new SparseIntArray();
		Integer position = 0;
		for (Category myCat : myCatList) {
			myPositionMap.put(myCat.getID(), position);
			position++;
		}
		return myPositionMap;
	}
	public int getPosition(int myListCategoryID, SparseIntArray myPositionMap) {
		int myPosition = myPositionMap.get(myListCategoryID);
		return myPosition;
	}
    public void editCategory(View view, Integer catID) {
		Intent addEditCatActivity = new Intent(view.getContext(), AddEditCatActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("catID", catID);
		b.putSerializable("task", EDIT_CATEGORY);
		addEditCatActivity.putExtras(b);
		this.startActivityForResult(addEditCatActivity, 5);
    }
    public void addCategoryButtonListener(View view) {
		Intent addEditCatActivity = new Intent(view.getContext(), AddEditCatActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("task", ADD_CATEGORY);
		addEditCatActivity.putExtras(b);
		this.startActivityForResult(addEditCatActivity, 5);
    }
    private class MyCatListCustomAdapter extends ArrayAdapter<Category> {
      	 
    	private ArrayList<Category> categoryList;
    	 
    	public MyCatListCustomAdapter(Context context, int textViewResourceId, ArrayList<Category> categoryList) {
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
    		Log.v("ConvertView", String.valueOf(position));
    		Log.v("AddEditCatActivity", "convertView" + convertView);
    	 
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
    					showList(category.getID());
    				}  
    			};
    			holder.category_list_text_view.setOnClickListener( categoryClickListener );  
    	   } 
    	   else {
    	    holder = (ViewHolder) convertView.getTag();
    		}
    	 
    		Category category = categoryList.get(position);
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
    		
    		holder.category_list_text_view.setText(categoryType + " " + categoryName);
    		holder.category_list_text_view_type.setText("(" + categoryTypeName + ")");
    		holder.category_list_text_view.setTag(category);
    		// TODO: Link this to type type editing activity later
    		holder.category_list_text_view_type.setTag(category);
    	 
    		return convertView;
    	}
    	 
	}
 
}