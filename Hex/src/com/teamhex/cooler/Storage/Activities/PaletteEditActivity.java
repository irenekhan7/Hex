package com.teamhex.cooler.Storage.Activities;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.teamhex.cooler.PaletteView;
import com.teamhex.cooler.R;
import com.teamhex.cooler.Storage.Classes.ColorRecord;
import com.teamhex.cooler.Storage.Classes.HexStorageManager;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;

public class PaletteEditActivity extends Activity {

	PaletteView paletteView;
	EditText nameEdit;
	HexStorageManager mHexStorageManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_palette_edit);
		
		paletteView = (PaletteView) findViewById(R.id.palette_view);
		nameEdit = (EditText) findViewById(R.id.editName);
		
		Intent i = getIntent();
        PaletteRecord palette = (PaletteRecord)i.getSerializableExtra("palette");
        setPaletteRecord(palette);
        paletteView.enableEditing();
        
        mHexStorageManager = new HexStorageManager(getApplication());
        mHexStorageManager.RecordLoadAll();
       
        Button saveButton = (Button) findViewById(R.id.button_save);
        saveButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	// Get the user's name input from a standard Android getText()
                	Log.i("TeamHex", "Getting the name to change to..");
                	String nameOld = paletteRecord.getName(),
                		   nameNew = NameFilter(nameEdit.getText().toString());
                	
                	Log.i("TeamHex", "The new name is '" + nameNew + "', from '" + nameOld + "'");
                	if(nameNew != nameOld) {
                		Log.i("TeamHex", "Renaming!");
                		mHexStorageManager.RecordRename(paletteRecord.getName(), nameNew);
                	}
                	
                	Log.i("TeamHex", "Getting new colors from paletteView");
                	ArrayList<ColorRecord> colors = paletteView.getColors();
                	for(int i = 0, len = colors.size(); i < len; ++i)
                		Log.i("TeamHex", "   " + Integer.toString(i) + ": " + colors.get(i).getSaveString());
                	
                	PaletteRecord record = mHexStorageManager.RecordGet(nameNew);
                	record.setColors(colors);
                	mHexStorageManager.RecordSave(record);
                	
                	finish();
                }
            }
        );
        
        Spinner spinner = (Spinner) findViewById(R.id.spinner_edit);
	     // Create an ArrayAdapter using the string array and a default spinner layout
	     ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
	             R.array.edit_options_array, android.R.layout.simple_spinner_item);
	     // Specify the layout to use when the list of choices appears
	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     // Apply the adapter to the spinner
	     spinner.setAdapter(adapter);
	     
	     spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	    	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    	        paletteView.setMode(pos);
	    	    }
	    	    public void onNothingSelected(AdapterView<?> parent) {
	    	    }
	     });
	}
	
	PaletteRecord paletteRecord;
	public void setPaletteRecord(PaletteRecord setting)
	{
		paletteRecord = setting;
		paletteView.setPalette(paletteRecord);
		
		nameEdit.setText(setting.getName());
		setTitle(setting.getName());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scheme_info, menu);
		return true;
	}

	
	// Replaces out any invalid characters that could mess the OS up
	public String NameFilter(String _name) {
		return _name.replace("[ &]", "_");
	}

}
