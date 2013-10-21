package com.teamhex.cooler.Storage.Activities;

import com.teamhex.cooler.R;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;
import com.teamhex.cooler.Storage.Classes.StorageManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PaletteSaveActivity extends Activity {
	
	private StorageManager storage;
	private PaletteRecord palette;
	
	public PaletteSaveActivity() {}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scheme_list_activity);
		
		Intent i = getIntent();
		
		palette = (PaletteRecord)i.getSerializableExtra("palette");
		
		// Create the initial storage manager
		storage = new StorageManager(getApplicationContext());
		
		// Save the record
		storage.RecordSave(palette);
	}

}
