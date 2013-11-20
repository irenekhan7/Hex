package com.teamhex.cooler.Storage.Activities;

import com.teamhex.cooler.R;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;
import com.teamhex.cooler.Storage.Classes.HexStorageManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PaletteSaveActivity extends Activity {
	
	private HexStorageManager storage;
	private PaletteRecord palette;
	
	public PaletteSaveActivity() {}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library);
		
		// The palette has been passed as a serializable extra
		Log.i("TeamHex", "Grabbing a serialized Palette to save");
		Intent intent_old = getIntent();
		palette = (PaletteRecord)intent_old.getSerializableExtra("palette");
		
		// Create the initial storage manager
		Log.i("TeamHex", "Creating a StorageManager to save the palette");
		storage = new HexStorageManager(getApplicationContext());
		
		// Save the palette record
		storage.RecordAdd(palette);

		Log.i("TeamHex", "The Palette has been saved.");
		//finish();
		
		// Now that it's saved, go to the library activity
    	Intent intent_new = new Intent(PaletteSaveActivity.this, PaletteLibraryActivity.class);
    	startActivity(intent_new);
	}

}
