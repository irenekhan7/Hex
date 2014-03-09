/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.teamhex.colorbird.Storage.Activities;

import com.teamhex.colorbird.PaletteView;
//import com.teamhex.colorbird.Menu;
//import com.teamhex.colorbird.Override;
import com.teamhex.colorbird.R;
import com.teamhex.colorbird.Storage.Classes.HexStorageManager;
import com.teamhex.colorbird.Storage.Classes.PaletteRecordAdapter;
//import com.teamhex.colorbird.SuppressLint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


/**
 * This class provides a basic demonstration of how to write an Android
 * activity. Inside of its window, it places a single view: an EditText that
 * displays and edits some internal text.
 */
public class PaletteLibraryActivity extends Activity {
    
    //static final private int BACK_ID = Menu.FIRST;
  //  static final private int CLEAR_ID = Menu.FIRST + 1;
	static final int VIEW_PALETTE_INFO = 7;
	static final int DELETE_PALETTE_RESULT = 10;
  //  private EditText mEditor;
   // private Camera camera;
    
    public PaletteLibraryActivity() {
		//Log.i("TeamHex", "LibraryActivity now being constructed.");
    }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Log.i("TeamHex", "LibraryActivity now running onCreate");
		super.onCreate(savedInstanceState);
		RecreateLibrary();
	}
	
	// Used by onCreate and onResume for the heavy lifting
	void RecreateLibrary() {
		//Log.i("TeamHex", "Recreating library from scratch.");
		setContentView(R.layout.activity_library);
		
		// Create the initial storage manager
		storage = new HexStorageManager(getApplicationContext());
		
		// Load all the records
		storage.RecordLoadAll();
		
		// Display the loaded records
		DisplayLibrary(storage);
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/
	
	// Creates the Android UI stuff
	//@SuppressLint("NewApi")
	public void DisplayLibrary(HexStorageManager storage) {
		//Log.i("TeamHex", "Starting to run DisplayLibrary.");
		
		// The container listView holds the mini-Views from the adapter
		//Log.i("TeamHex", "1. Creating the listView container...");
		container = (ListView) findViewById(R.id.schemeList);
		
		// Make the adapter to generate the mini-Views
		//Log.i("TeamHex", "2. Creating adapter...");
		adapter = new PaletteRecordAdapter(
				this,
				R.layout.listcolor,
				storage.getPalettesArray());
		// Give the adapter to the container
		container.setAdapter(adapter);
		
		//Log.i("TeamHex", "Finished running DisplayLibrary.");
		
		// Create a message handling object as an anonymous class.
        OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	PaletteView s = (PaletteView) v.findViewById(R.id.palette_view);
                Intent i = new Intent(PaletteLibraryActivity.this, PaletteInfoActivity.class);
                i.putExtra("palette", s.getPalette());
                startActivityForResult(i, VIEW_PALETTE_INFO);
            }
        };

        container.setOnItemClickListener(mMessageClickedHandler); 
	}


    // When the Save Button Pressed event returns, check the data
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	//Log.i("TeamHex", "A View Palette activity has returned.");
    	if(resultCode == DELETE_PALETTE_RESULT)
    	{
    		storage.RecordDelete(data.getExtras().getString("name"));
    
    	}
    }
    
	
	// Going back to the library means any changes must be shown; therefore, refresh
    @Override
    protected void onResume() {
        super.onResume();
    	RecreateLibrary();
    }
	
	// StorageManager keeps track of, and manipulates, the library of files
	private HexStorageManager storage;
	
	// The ListView holds the mini-Views from the adapter
	private ListView container;
	
	// PaletteRecordAdapter creates the mini-Views
	private PaletteRecordAdapter adapter;
	
	 /*
	@Override
	    public void onBackPressed() {
	        Intent intent = new Intent(PaletteLibraryActivity.this, MainActivity.class);
	        startActivity(intent);
	        finish();
	    }*/


}
