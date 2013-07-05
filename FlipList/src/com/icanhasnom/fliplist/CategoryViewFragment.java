package com.icanhasnom.fliplist;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
        categoryList = myListMan.getCategories();
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
    public void addCategoriesOnList() {
    	// Populate the Category List
    	catListDataAdapter = new MyCatListCustomAdapter(activity, R.layout.fragment_category_list_layout, categoryList);
    	ListView listView = (ListView) layoutView.findViewById(R.id.category_list_list_view);
    	listView.setAdapter(catListDataAdapter);
    	
    	// Set up the onClick event for each of the list items
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view,
    		int position, long id) {
    			// When clicked, show a toast with the TextView text
    			Category category = (Category) parent.getItemAtPosition(position);
    			Toast.makeText(activity,
    					"Clicked on Row: " + category.getDescription(), 
    					Toast.LENGTH_LONG).show();
    			// TODO: Have FlipList launch activity to view items in category
    			//addEditCategory(category);
    		}
    	});
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
    					// Use this onClick to send the user to the edit screen for the clicked category
    					TextView tv = (TextView) v;
    					Category lc = (Category) tv.getTag();
    					Log.v("AddEditCatActivity", "categoryClickListener - made it here");
    					// TODO: Probably only need one of these
    					//currentCategory = lc;
    					//addEditCategory(lc);
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
    		String categoryTypeName = myListMan.getCategoryTypeName(categoryTypeID);
    		
    		Log.v("MyCatListCustomAdapter", "categoryTypeID:" + categoryTypeID);
    		Log.v("MyCatListCustomAdapter", "category.getDescription(): " + category.getDescription());
    		Log.v("MyCatListCustomAdapter", "myListMan.getCategoryTypeName(): " + myListMan.getCategoryTypeName(categoryTypeID));
    		
    		holder.category_list_text_view.setText(categoryName);
    		holder.category_list_text_view_type.setText("(" + categoryTypeName + ")");
    		holder.category_list_text_view.setTag(category);
    		// TODO: Link this to type type editing activity later
    		holder.category_list_text_view_type.setTag(category);
    	 
    		return convertView;
    	}
    	 
	}
 
}