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
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

public class ExportImportDBActivity extends Activity implements OnClickListener {

	private static final String SAMPLE_DB_NAME = "fliplist";
	private static final String SAMPLE_TABLE_NAME = "Info";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_import_db);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
    }
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button1:
			//deleteDB();
			break;
		case R.id.button2:
			exportDB();
			break;
		case R.id.button3:
			//createDB();
			break;
		}
	}
	
	private void deleteDB(){
		boolean result = this.deleteDatabase(SAMPLE_DB_NAME);
		if (result==true) {
			 Toast.makeText(this, "DB Deleted!", Toast.LENGTH_LONG).show();
		} 
	}

	private void createDB() {
		SQLiteDatabase sampleDB =  this.openOrCreateDatabase(SAMPLE_DB_NAME, MODE_PRIVATE, null);
		sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " +
	                SAMPLE_TABLE_NAME +
	                " (LastName VARCHAR, FirstName VARCHAR," +
	                " Rank VARCHAR);");
	        sampleDB.execSQL("INSERT INTO " +
	                SAMPLE_TABLE_NAME +
	                " Values ('Kirk','James, T','Captain');");
	        sampleDB.close();
	        sampleDB.getPath();
	        Toast.makeText(this, "DB Created @ "+sampleDB.getPath(), Toast.LENGTH_LONG).show(); 
	}

	private void exportDB(){
		File sd = Environment.getExternalStorageDirectory();
	      	File data = Environment.getDataDirectory();
	       FileChannel source=null;
	       FileChannel destination=null;
	       String currentDBPath = "/data/"+ "com.icanhasnom.fliplist" +"/databases/"+SAMPLE_DB_NAME;
	       String backupDBPath = "/FlipList/db_export/" + SAMPLE_DB_NAME;
	       
	       Time now = new Time();
	       now.setToNow();
	       
	       SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
	       String currentDateandTime = sdf.format(new Date(0));
	       
	     //File backupDB = new File(sd, backupDBPath);
	       File currentDB = new File(data, currentDBPath);
	       
	       File backupDB = new File(sd, backupDBPath + "-" + "123456");
	       try {
	            source = new FileInputStream(currentDB).getChannel();
	            destination = new FileOutputStream(backupDB).getChannel();
	            destination.transferFrom(source, 0, source.size());
	            source.close();
	            destination.close();
	            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
	        } catch(IOException e) {
	        	e.printStackTrace();
	            Toast.makeText(this, "Export Failed!", Toast.LENGTH_LONG).show();
	            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
	        }
	}
}

