package com.icanhasnom.fliplist;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 18:58 < bankai_> flipture: you need a remoteViewAdapter and a remoteViewService
 * 
19:41 < flipture> bankai_: ok cool. that gives me someting to start with... Do I need a broadcast receiver?
19:42 < bankai_> to catch the update, i believe so yes
19:49 < flipture> bankai_: ok cool. so the widget class will create an intent that calls removeViewService, then that removeViewService has the removeViewAdapter in it?
19:55 < bankai_> Intent listViewIntent = new Intent(mContext,ListViewRVS.class); mainLayout.setRemoteAdapter(R.id.listview, listViewIntent);
19:56 < bankai_> where mainLayout is a RemoteViewService
19:58 < flipture> ok awesome, thanks a ton... I think that will get me going :) I'm finding a few examples but none that quite do it this way...

 * @author flip
 *
 */

public class WidgetProvider extends AppWidgetProvider {
	String tag = "WidgetProvider";
	int currentFlistID;
	int defaultFlistID;
	Flist currentFlist;
	ArrayList<Item> myListItems;
	ListManager myListMan;
	ListPreferenceManager prefMan;
	ItemList myItemList;
	//MyCustomAdapter itemListDataAdapter;
	RelativeLayout widgetLayout;
	ListView layoutView;
	ItemList currentItemList;
	LayoutInflater inflater;
	
	/*
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.v("FlipList.onEnabled", "Widget running onEnabled");
		myContext = context;
		initWidget(context);
	}
	*/
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int N = appWidgetIds.length;
	    Log.v(tag,  "Updating widgets " + Arrays.asList(appWidgetIds));

		/* int[] appWidgetIds holds ids of multiple instances of your widget
		 * meaning you are placing more than one widget on your homescreen
		 */
	    for (int i = 0; i < N; i++) {
	        int appWidgetId = appWidgetIds[i];
	        RemoteViews remoteViews = updateWidgetListView(context, appWidgetIds[i]);

			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	    }
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
    public void initObjs(Context context) {
		prefMan = new ListPreferenceManager(context);
		myListMan = new ListManager(context);
		currentItemList = myListMan.getItemList(defaultFlistID);
    }

	public RemoteViews updateWidgetListView(Context context, int appWidgetId) {
    	Log.v("FlipListWidget.initWidget", "currentFlistID: " + currentFlistID);
    	Log.v("FlipListWidget.initWidget", "defaultFlistID: " + defaultFlistID);
    	
    	initObjs(context);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_layout);
		
    	Intent listViewIntent = new Intent(context,WidgetService.class); 
    	listViewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,  appWidgetId);
    	listViewIntent.setData(Uri.parse(listViewIntent.toUri(Intent.URI_INTENT_SCHEME)));
    	
    	remoteViews.setTextViewText(R.id.widgetHeaderTextView, currentItemList.getName());
    	remoteViews.setRemoteAdapter(R.id.itemList, listViewIntent);
    	
		//remoteViews.setRemoteAdapter(appWidgetId, R.id.itemList, svcIntent);
		remoteViews.setEmptyView(R.id.itemList, R.id.widgetHeaderTextView);
		return remoteViews;
    }
	public Flist getFlistObj(int catID) {
		Flist myCat = myListMan.getFlist(catID);
		return myCat;
	}
}
