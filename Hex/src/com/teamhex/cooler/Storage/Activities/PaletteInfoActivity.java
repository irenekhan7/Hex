package com.teamhex.cooler.Storage.Activities;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.teamhex.cooler.PaletteView;
import com.teamhex.cooler.R;
import com.teamhex.cooler.Storage.Classes.ColorRecord;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;

public class PaletteInfoActivity extends Activity {

	PaletteView paletteView;
	TextView nameView;
	
	static final int EDIT_PALETTE_NAME = 14;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TeamHex", "Creating a Palette Info viewer");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_palette_info);
		
		// Fetch the palette and name views
		paletteView = (PaletteView) findViewById(R.id.paletteEditView);
		nameView = (TextView) findViewById(R.id.paletteName);
		
		// Retrieve the palette from serialized data
		Intent i = getIntent();
        setPaletteRecord((PaletteRecord) i.getSerializableExtra("palette"));
        
        // Any later edits will be stored in the changes object
        changes = new ArrayList<String[]>();
        
        // Event: Edit Button Pressed 
        Button editButton = (Button) findViewById(R.id.button_edit);
        editButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	Log.i("TeamHex", "Starting the Edit Palette activity.");
                	
                	Intent i = new Intent(PaletteInfoActivity.this, PaletteEditActivity.class);
                    i.putExtra("palette", myPaletteRecord);
                    startActivityForResult(i, EDIT_PALETTE_NAME);
                    // See onActivityResult for what'll happen next
                }
            }
        );
        
        // Event: Share Button Pressed
        Button shareButton = (Button) findViewById(R.id.button_share);
        shareButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	System.out.println("SHARING\n");
                	
                	Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                	sharingIntent.setType("text/plain");
                	sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Color Scheme");
                	sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, infoString);
                	startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            }
        );

        // Event: Save Button Pressed
        // When the Save Button Pressed event returns, save all and go back to PaletteLibraryActivity
        Button saveButton = (Button) findViewById(R.id.button_save);
        saveButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	System.out.println("SAVING\n");
                	MentionAllChanges();
                	// Store the listing of changes in a result intent
                	Intent resultIntent = new Intent();
                	resultIntent.putExtra("changes", changes);
                	setResult(Activity.RESULT_OK, resultIntent);
                	finish();
                }
            }
        );
	}
	public void setPaletteRecord(PaletteRecord setting)
	{
		Log.i("TeamHex", "Displaying the palette record " + setting.getName());
		myPaletteRecord = setting;
		paletteView.setColorScheme(myPaletteRecord);
		
		ArrayList<ColorRecord> colors = setting.getColors();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(setting.getName());
		sb.append("\n");
		int max = colors.size();
		for(int i = 0; i < max; i++)
		{
			sb.append(colors.get(i).getName());
			sb.append("  ");
			sb.append(colors.get(i).getHex());
			sb.append("\n");
		}
		
		infoString = sb.toString();
		nameView.setText(infoString);
		setTitle(setting.getName());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scheme_info, menu);
		return true;
	}

    // When the Edit Button Pressed event returns, check the data
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.i("TeamHex", "An Edit Palette activity has returned returned: " + Integer.toString(requestCode) + ", " + Integer.toString(resultCode));
    	
    	// A new change must be recorded...
    	String[] changed = new String[3];
    	// ... with the first name of this record
    	changed[0] = myPaletteRecord.getName();
    	
    	boolean has_change = true;
    	
    	switch(requestCode) {
    		// The name was edited
    		case(EDIT_PALETTE_NAME):
    			Log.i("TeamHex", "The request code is for 'EDIT_PALETTE_NAME'");
    			Bundle extras = data.getExtras();
    			// (only if an extra equal to nameNew was returned)
    			if(extras != null && extras.containsKey("nameNew")) {
        			String nameNew = data.getStringExtra("nameNew");
    				Log.i("TeamHex", "Found a new name: '" + nameNew + "'");
    				myPaletteRecord.setName(nameNew);
    				setPaletteRecord(myPaletteRecord);
    				changed[1] = "Name";
    				changed[2] = nameNew;
    			}
    			else {
    				Log.i("TeamHex", "No new name was given...");
    				has_change = false;
    			}
    		break;
    	}
    	// Now that it's all been process, remember to save/send the change
    	if(has_change) {
	    	MentionChange(changed);
	    	changes.add(changed);
    	}
    }
    
    // Helper functions to Log.i change(s)
    private void MentionChange(String[] changed) { MentionChange(changed, "Remember"); }
    private void MentionChange(String[] changed, String prefix) {
    	Log.i("TeamHex", prefix + " to change " + changed[0] + "'s " + changed[1] + " to " + changed[2]);
    }
    private void MentionAllChanges() {
    	for(int i = 0; i < changes.size(); ++i)
    		MentionChange(changes.get(i), "...remembering");
    }
    
	/*
	@Override
	public void onBackPressed(){
		
		  finish();
		  super.onBackPressed();
	}*/
    
    // Listing of changes made (see HexStorageManager::applyChanges)
    private ArrayList<String[]> changes;

	// The PaletteRecord currently being displayed
	PaletteRecord myPaletteRecord;
	
	// The string of the name  and color records, separated by endlines
	String infoString;
}
