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

package com.teamhex.cooler.Storage.Activities;


import java.util.Random;


import com.teamhex.cooler.PaletteView;
//import com.teamhex.cooler.Menu;
//import com.teamhex.cooler.Override;
import com.teamhex.cooler.R;
//import com.teamhex.cooler.SuppressLint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import android.graphics.Color;
import com.teamhex.cooler.R;
import com.teamhex.cooler.R.id;
import com.teamhex.cooler.R.layout;
import com.teamhex.cooler.Storage.Classes.PaletteRecordAdapter;
import com.teamhex.cooler.Storage.Classes.StorageManager;

/**
 * This class provides a basic demonstration of how to write an Android
 * activity. Inside of its window, it places a single view: an EditText that
 * displays and edits some internal text.
 */
public class PaletteLibraryActivity extends Activity {
    
    //static final private int BACK_ID = Menu.FIRST;
  //  static final private int CLEAR_ID = Menu.FIRST + 1;

  //  private EditText mEditor;
   // private Camera camera;
    
    public PaletteLibraryActivity() {
    }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.palette_list_activity);
		
		// Create the initial storage manager
		storage = new StorageManager(getApplicationContext());
		
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
	public void DisplayLibrary(StorageManager storage) {
		Log.i("TeamHex", "Starting to run DisplayLibrary.");
		
		// The container listView holds the mini-Views from the adapter
		Log.i("TeamHex", "1. Creating the listView container...");
		container = (ListView) findViewById(R.id.paletteList);
		
		// Make the adapter to generate the mini-Views
		Log.i("TeamHex", "2. Creating adapter...");
		adapter = new PaletteRecordAdapter(
				this,
				R.layout.listcolor,
				storage.getPalettesArray());
		Log.i("TeamHex", "   Adapter created.");
		
		// Give the adapter to the container
		Log.i("TeamHex", "3. Unifying the two...");
		//container.getAlpha();
		Log.i("TeamHex", "Container is not null");
		container.setAdapter(adapter);
		Log.i("TeamHex", "   Container has received the adapter.");
		
		
		Log.i("TeamHex", "Finished running DisplayLibrary.");
		
		// Create a message handling object as an anonymous class.
        OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
            	PaletteView s = (PaletteView) v.findViewById(R.id.paletteView1);
                Intent i = new Intent(PaletteLibraryActivity.this, PaletteInfoActivity.class);
                i.putExtra("palette", s.getColorPalette());
                startActivity(i);
            }
        };

        container.setOnItemClickListener(mMessageClickedHandler); 
	}
	
	// StorageManager keeps track of, and manipulates, the library of files
	private StorageManager storage;
	
	// The ListView holds the mini-Views from the adapter
	private ListView container;
	
	// PaletteRecordAdapter creates the mini-Views
	private PaletteRecordAdapter adapter;

}
