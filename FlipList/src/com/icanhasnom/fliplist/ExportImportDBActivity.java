package com.icanhasnom.fliplist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class ExportImportDBActivity extends Activity implements OnClickListener {


    DatabaseHandler db;
    
    Activity activity;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_import_db);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        activity = this;
        db = new DatabaseHandler(this);
    }
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button1:
			//deleteDB();
			break;
		case R.id.button2:
			db.exportDB();
			break;
		case R.id.button3:
			db.restoreDBSelectFile();
			break;
		}
	}

	/*
	private void deleteDB(){
		boolean result = this.deleteDatabase(DB_NAME);
		if (result==true) {
			 Toast.makeText(this, "DB Deleted!", Toast.LENGTH_LONG).show();
		} 
	}
	*/
	/*
	private void createDB() {
		SQLiteDatabase sampleDB =  this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " +
	                TABLE_NAME +
	                " (LastName VARCHAR, FirstName VARCHAR," +
	                " Rank VARCHAR);");
	        sampleDB.execSQL("INSERT INTO " +
	                TABLE_NAME +
	                " Values ('Kirk','James, T','Captain');");
	        sampleDB.close();
	        sampleDB.getPath();
	        Toast.makeText(this, "DB Created @ "+sampleDB.getPath(), Toast.LENGTH_LONG).show(); 
	}
	 */

	/*
	private void restoreDB() {
		File sd = Environment.getExternalStorageDirectory();
		File data = Environment.getDataDirectory();
		
		try {
			File currentDB = new File(data, currentDBPath);
			File backupDB = new File(sd, backupDBPath);
			
			if (currentDB.exists()) {
				FileChannel src = new FileInputStream(backupDB).getChannel();
				FileChannel dst = new FileOutputStream(currentDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Toast.makeText(this, "Database Restored Successfully", Toast.LENGTH_SHORT).show();
			}
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "Export Failed!", Toast.LENGTH_SHORT).show();
			Toast.makeText(this,  e.toString(),  Toast.LENGTH_LONG).show();
		}
	}
	*/
	

}

