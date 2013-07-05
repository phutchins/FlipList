package com.icanhasnom.fliplist;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
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
 
public class FilterViewFragment extends Fragment {
	int fragVal;
	MyFilterListCustomAdapter filterListDataAdapter;
	ArrayList<Filter> filterList;
	ViewGroup container;
	Activity activity;
	ListManager myListMan;
	View layoutView;
 
    static FilterViewFragment init(int val) {
        FilterViewFragment truitonFrag = new FilterViewFragment();
        Bundle args = new Bundle();
        args.putInt("val", val);
        truitonFrag.setArguments(args);
        return truitonFrag;
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
        fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
		myListMan = new ListManager(activity);
        filterList = myListMan.getFilterList();
    }
 
    /**
     * The Fragment's UI is a simple text view showing its instance number and
     * an associated list.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_filter_list_layout, container, false);
        //View tv = layoutView.findViewById(R.id.text);
        //((TextView) tv).setText("Truiton Fragment #" + fragNum);
        addFiltersOnList();
        return layoutView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setListAdapter(new ArrayAdapter<String>(getActivity(),
        //        android.R.layout.simple_list_item_1, arr));
    }
    public void addFiltersOnList() {
    	// Populate the Category List
    	filterListDataAdapter = new MyFilterListCustomAdapter(activity, R.layout.fragment_filter_list_layout, filterList);
    	ListView listView = (ListView) layoutView.findViewById(R.id.filter_list_list_view);
    	listView.setAdapter(filterListDataAdapter);
    	
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
    private class MyFilterListCustomAdapter extends ArrayAdapter<Filter> {
      	 
    	private ArrayList<Filter> categoryList;
    	 
    	public MyFilterListCustomAdapter(Context context, int textViewResourceId, ArrayList<Filter> categoryList) {
    		super(context, textViewResourceId, categoryList);
    		this.categoryList = new ArrayList<Filter>();
    		this.categoryList.addAll(categoryList);
    	}
    	
    	private class ViewHolder {
    		TextView filter_list_text_view;
    		TextView filter_list_text_view_type;
    	}
    	 
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {

    		ViewHolder holder = null;
    		Log.v("ConvertView", String.valueOf(position));
    		Log.v("AddEditCatActivity", "convertView" + convertView);
    	 
    		if (convertView == null) {
    			LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			// TODO: Should I set null to parent? It errors if I do but I can't call the one below if i do.
    			convertView = vi.inflate(R.layout.fragment_filter_list_view, null);
    	 
    			holder = new ViewHolder();
    			holder.filter_list_text_view = (TextView) convertView.findViewById(R.id.filter_list_text_view);
    			holder.filter_list_text_view_type = (TextView) convertView.findViewById(R.id.filter_list_text_view_type);

    			convertView.setTag(holder);
    			
    			View.OnClickListener categoryClickListener;
    			categoryClickListener = new View.OnClickListener() {  
    				public void onClick(View v) {  
    					// Use this onClick to send the user to the edit screen for the clicked category
    					TextView tv = (TextView) v;
    					Filter myFilter = (Filter) tv.getTag();
    					// TODO: Add method for editing filters
    				}  
    			};
    			// TODO: Fix this!
    			//holder.cat_list_text_view.setOnClickListener( categoryClickListener );  
    	   } 
    	   else {
    	    holder = (ViewHolder) convertView.getTag();
    		}
    	 
    		Filter filter = filterList.get(position);
    		// TODO: Make a function in ListManager to get and hold the category type names in memory
    		int filterID = filter.getID();
    		//String categoryDescription = category.getDescription();
    		String categoryName = filter.getName();
    		//String categoryTypeName = myListMan.getCategoryTypeName(categoryTypeID);
    		
    		//Log.v("MyCatListCustomAdapter", "categoryTypeID:" + filterID);
    		
    		holder.filter_list_text_view.setText(categoryName);
    		holder.filter_list_text_view_type.setText("(" + filterID + ")");
    		holder.filter_list_text_view.setTag(filter);
    		// TODO: Link this to type type editing activity later
    		holder.filter_list_text_view_type.setTag(filter);
    	 
    		return convertView;
    	}
    	 
	}
}