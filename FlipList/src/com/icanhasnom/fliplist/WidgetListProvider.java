package com.icanhasnom.fliplist;

import java.util.ArrayList;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

class WidgetListProvider implements RemoteViewsService.RemoteViewsFactory {
	private ItemList itemList;
	private static String tag="WidgetListProvider";
	private ArrayList<Item> myListItems = new ArrayList<Item>();
	private Context mContext;
	private int mAppWidgetId;
	ListManager myListManager;

	public WidgetListProvider(Context context, Intent intent) {
		mContext = context;
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		updateData();
		Log.d(tag,"Factory created");
	}
	
	private void updateData() {
		myListManager = new ListManager(mContext);
		itemList = myListManager.getItemList(0);
	}

	@Override
	public void onCreate() {
		// In onCreate() you setup any connections / cursors to your data
		// source. Heavy lifting,
		// for example downloading or creating content etc, should be deferred
		// to onDataSetChanged()
		// or getViewAt(). Taking more than 20 seconds in this call will result
		// in an ANR.
		
		//for (int i = 0; i < mCount; i++) {
		//	itemList.add(new Item(i + "!"));
		//}

		// We sleep for 3 seconds here to show how the empty view appears in the
		// interim.
		// The empty view is set in the StackWidgetProvider and should be a
		// sibling of the
		// collection view.
		Log.d(tag,"onCreate called for widget id: " + mAppWidgetId);
		//try {
		//	Thread.sleep(3000);
		//} catch (InterruptedException e) {
		//	e.printStackTrace();
		//}
	}

	@Override
	public void onDestroy() {
		// In onDestroy() you should tear down anything that was setup for your
		// data source,
		// eg. cursors, connections, etc.
		Log.v(tag,"destroy called for widget id: " + mAppWidgetId);
		myListItems.clear();
	}

	@Override
	public int getCount() {
		Log.v(tag,"getCount(): " + itemList.size());
		return itemList.size();
	}

	@Override
	public RemoteViews getViewAt(int position) {
		// position will always range from 0 to getCount() - 1.

		// We construct a remote views item based on our widget item xml file,
		// and set the
		// text based on the position.
		RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.activity_item_list_view);
		Item listItem = itemList.get(position);
		Log.v(tag,"listItem.getName(): " + listItem.getName());
		remoteView.setTextViewText(R.id.itemName, listItem.getName());
		remoteView.setTextViewText(R.id.itemInfo, listItem.getDescription());

		return remoteView;
		
		//if (position % 2 == 0) {
		//	rv.setImageViewResource(R.id.widget_layout, R.drawable.fire);
		//} else {
		//	rv.setImageViewResource(R.id.widget_layout, R.drawable.ente);
		//}

		// rv.setTextViewText(R.id.widget_item,
		// mWidgetItems.get(position).text);

		// Next, we set a fill-intent which will be used to fill-in the pending
		// intent template
		// which is set on the collection view in StackWidgetProvider.
		
		
		//Bundle extras = new Bundle();
		//extras.putInt(StackWidgetProvider.EXTRA_ITEM, position);
		
		//Intent fillInIntent = new Intent();
		//fillInIntent.putExtras(extras);
		//rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

		// You can do heaving lifting in here, synchronously. For example, if
		// you need to
		// process an image, fetch something from the network, etc., it is ok to
		// do it here,
		// synchronously. A loading view will show up in lieu of the actual
		// contents in the
		// interim.
		
		//try {
		//	System.out.println("Loading view " + position);
		//	Thread.sleep(500);
		//} catch (InterruptedException e) {
		//	e.printStackTrace();
		//}

		// Return the remote views object.

	}

	@Override
	public RemoteViews getLoadingView() {
		// You can create a custom loading view (for instance when getViewAt()
		// is slow.) If you
		// return null here, you will get the default loading view.
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onDataSetChanged() {
		// This is triggered when you call AppWidgetManager
		// notifyAppWidgetViewDataChanged
		// on the collection view corresponding to this factory. You can do
		// heaving lifting in
		// here, synchronously. For example, if you need to process an image,
		// fetch something
		// from the network, etc., it is ok to do it here, synchronously. The
		// widget will remain
		// in its current state while work is being done here, so you don't need
		// to worry about
		// locking up the widget.
	}
}
