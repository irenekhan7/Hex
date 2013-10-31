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
	
	static final int EDIT_PALETTE_NAME = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_palette_info);
		
		paletteView = (PaletteView) findViewById(R.id.paletteEditView);
		nameView = (TextView) findViewById(R.id.paletteName);
		
		Intent i = getIntent();
        PaletteRecord palette = (PaletteRecord)i.getSerializableExtra("palette");
        setPaletteRecord(palette);
        
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
	}
	
	PaletteRecord myPaletteRecord;
	String infoString;
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
    	switch(requestCode) {
    		case(EDIT_PALETTE_NAME):
    			Log.i("TeamHex", "The request code is for 'EDIT_PALETTE_NAME'");
    			Bundle extras = data.getExtras();
    			if(extras != null && extras.containsKey("nameNew")) {
        			String nameNew = data.getStringExtra("nameNew");
    				Log.i("TeamHex", "Found a new name: '" + nameNew + "'");
    				myPaletteRecord.setName(nameNew);
    				setPaletteRecord(myPaletteRecord);
    			}
    			else {
    				Log.i("TeamHex", "No new name was given...");
    			}
    		break;
    	}
    }
	/*
	@Override
	public void onBackPressed(){
		
		  finish();
		  super.onBackPressed();
	}*/
    private PaletteRecord record;
}
