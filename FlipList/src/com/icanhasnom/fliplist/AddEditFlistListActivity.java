package com.icanhasnom.fliplist;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AddEditFlistListActivity extends Activity {
	MyCatListCustomAdapter catListDataAdapter;
	Flist currentCategory;
	ArrayList<Flist> categoryList;
	ListManager myListMan;
	ActionBar actionBar;
	
	static Integer ADD_CATEGORY = 5;
	static Integer EDIT_CATEGORY = 4;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		setContentView(R.layout.activity_add_edit_flist_list);
		myListMan = new ListManager(this);
	    categoryList = myListMan.getFlists();
		addCategoriesOnList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_edit_flist_list, menu);
		return true;
	}
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 5 && resultCode == -1) {
    	}
    	// Always refresh the list when coming back from child activity
		refreshList();
    	Log.v("AddEditCatListActivity.onActivityResult", "requestCode: " + requestCode + " resultCode: " + resultCode);
    	super.onActivityResult(requestCode, resultCode, data);
    }
    public void refreshList() {
		myListMan = new ListManager(this);
        categoryList = myListMan.getFlists();
    	addCategoriesOnList();
    }
    public void addCategoriesOnList() {
    	// Populate the Category List
    	catListDataAdapter = new MyCatListCustomAdapter(this, R.layout.activity_add_edit_flist_list, categoryList);
    	ListView listView = (ListView) findViewById(R.id.itemAddEditList);
    	listView.setAdapter(catListDataAdapter);
    	
    	// Set up the onClick event for each of the list items
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view,
    		int position, long id) {
    			// When clicked, show a toast with the TextView text
    			Flist category = (Flist) parent.getItemAtPosition(position);
    			Toast.makeText(getApplicationContext(),
    					"Clicked on Row: " + category.getDescription(), 
    					Toast.LENGTH_LONG).show();
    			editCategory(category);
    		}
    	});
    }
    public void editCategory(Flist category) {
    	Integer catID = category.getID();
		Intent addEditCatActivity = new Intent(this, AddEditFlistActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("catID", catID);
		b.putSerializable("task", EDIT_CATEGORY);
		addEditCatActivity.putExtras(b);
		this.startActivityForResult(addEditCatActivity, 5);
    }
	public void addNewCategory() {
  		Intent addEditCatActivity = new Intent(this, AddEditFlistActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("task", ADD_CATEGORY);
		addEditCatActivity.putExtras(b);
		this.startActivityForResult(addEditCatActivity, 5);
	}
    private class MyCatListCustomAdapter extends ArrayAdapter<Flist> {
      	 
    	private ArrayList<Flist> categoryList;
    	 
    	public MyCatListCustomAdapter(Context context, int textViewResourceId, ArrayList<Flist> categoryList) {
    		super(context, textViewResourceId, categoryList);
    		this.categoryList = new ArrayList<Flist>();
    		this.categoryList.addAll(categoryList);
    	}
    	
    	private class ViewHolder {
    		TextView cat_list_text_view;
    		TextView cat_list_text_view_type;
    	}
    	 
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {

    		ViewHolder holder = null;
    		Log.v("ConvertView", String.valueOf(position));
    		Log.v("AddEditCatActivity", "convertView" + convertView);
    	 
    		if (convertView == null) {
    			LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			// TODO: Should I set null to parent? It errors if I do but I can't call the one below if i do.
    			convertView = vi.inflate(R.layout.add_edit_layout, null);
    	 
    			holder = new ViewHolder();
    			holder.cat_list_text_view = (TextView) convertView.findViewById(R.id.cat_list_text_view);
    			holder.cat_list_text_view_type = (TextView) convertView.findViewById(R.id.cat_list_text_view_type);

    			convertView.setTag(holder);
    			
    			View.OnClickListener categoryClickListener;
    			categoryClickListener = new View.OnClickListener() {  
    				public void onClick(View v) {
    					TextView tv = (TextView) v;
    					Flist category = (Flist) tv.getTag();
    					currentCategory = category;
    					editCategory(category);
    				}  
    			};
    			holder.cat_list_text_view.setOnClickListener( categoryClickListener );  
    			holder.cat_list_text_view_type.setOnClickListener( categoryClickListener );
    	   } 
    	   else {
    	    holder = (ViewHolder) convertView.getTag();
    		}
    		Flist category = categoryList.get(position);
    		// TODO: Make a function in ListManager to get and hold the category type names in memory
    		int categoryTypeID = category.getType();
    		//String categoryDescription = category.getDescription();
    		String categoryName = category.getName();
    		String categoryTypeName = myListMan.getItemTypeName(categoryTypeID);

    		holder.cat_list_text_view.setText(categoryName);
    		holder.cat_list_text_view_type.setText("(" + categoryTypeName + ")");
    		holder.cat_list_text_view.setTag(category);
    		// TODO: Link this to type type editing activity later
    		holder.cat_list_text_view_type.setTag(category);
    		return convertView;
    	}
	}
	public SparseIntArray buildIndex(ArrayList<Flist> myCatList) {
		Integer position = 0;
		SparseIntArray myPositionMap = new SparseIntArray();
		for (Flist myCat : myCatList) {
			myPositionMap.put(myCat.getID(), position);
			position++;
		}
		return myPositionMap;
	}
	public int getPosition(int myListCategoryID, SparseIntArray positionMap) {
		int myPosition = positionMap.get(myListCategoryID);
		return myPosition;
	}
	
	// TODO: Use this to send intent back to fragment to trigger refresh?
	//Intent intent = new Intent();
	//intent.putExtra("catID", myCat.getID());
	//setResult(RESULT_OK, intent);
	//finish();
	
	
}
