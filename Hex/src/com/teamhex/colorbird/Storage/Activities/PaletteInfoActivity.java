package com.teamhex.colorbird.Storage.Activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
//import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.teamhex.colorbird.PaletteView;
import com.teamhex.colorbird.R;
import com.teamhex.colorbird.Palette.ColorPaletteExporter;
import com.teamhex.colorbird.Storage.Classes.ColorRecord;
import com.teamhex.colorbird.Storage.Classes.HexStorageManager;
import com.teamhex.colorbird.Storage.Classes.PaletteRecord;

public class PaletteInfoActivity extends Activity {

	PaletteView paletteView;
	TextView nameView;
	TextView colorInfoView;

	static final int EDIT_PALETTE_NAME = 14;
	// 1. Instantiate an AlertDialog.Builder with its constructor
	AlertDialog.Builder builder;
	AlertDialog areYouSureDialog;
	AlertDialog chooseShareDialog;
	
	String fileTypes[] = {"Text", "ASE (For Photoshop or Illustrator)"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Log.i("TeamHex", "Creating a Palette Info viewer");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_palette_info);
		
		
		//Setup "Are you sure you want to delete?" dialog
		builder = new AlertDialog.Builder(this);
		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setTitle("Are you sure?");

		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   //Log.i("TeamHex", "Starting to delete.");
	               	//MentionAllChanges();
	               	Intent resultIntent = new Intent();
	               	resultIntent.putExtra("name", myPaletteRecord.getName());
	               	setResult(PaletteLibraryActivity.DELETE_PALETTE_RESULT, resultIntent);
	               	finish();
	           }
	       });
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User cancelled the dialog
	           }
	       });
		
		areYouSureDialog = builder.create();
		builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose file type to share");
		builder.setItems(fileTypes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	if(which == 0)
            	{
            		shareTxt();
            	}
            	if(which == 1)
            	{
            		shareASE();
            	}
            }
		});
		
		chooseShareDialog = builder.create();
		
		// Fetch the palette and name views
		paletteView = (PaletteView) findViewById(R.id.palette_view);
		nameView = (TextView) findViewById(R.id.paletteName);
		colorInfoView = (TextView) findViewById(R.id.colorInfo);
		
		paletteView.setOnInteractListener(new PaletteView.OnInteractListener() {
			@Override
			public void onInteract() {
				colorInfoView.setText(paletteView.info);
			}
		});
		
		// Retrieve the palette from serialized data
		Intent i = getIntent();
        setPaletteRecord((PaletteRecord) i.getSerializableExtra("palette"));
        
        // Event: Edit Button Pressed 
        Button editButton = (Button) findViewById(R.id.button_edit);
        editButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	//Log.i("TeamHex", "Starting the Edit Palette activity.");
                	
                	Intent i = new Intent(PaletteInfoActivity.this, PaletteEditActivity.class);
                    i.putExtra("palette", myPaletteRecord);
                    startActivityForResult(i,1);
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
                	chooseShareDialog.show();
                }
            }
        );

        // Event: Save Button Pressed
        // When the Save Button Pressed event returns, save all and go back to PaletteLibraryActivity
        Button saveButton = (Button) findViewById(R.id.button_delete);
        saveButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	
                	areYouSureDialog.show();
                }
            }
        );
	}
	
	public void shareTxt()
	{
		//Log.i("TeamHex", "Sharing .txt file");
    	
    	Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    	sharingIntent.setType("text/plain");
    	sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Color Palette");
    	sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, infoString);
    	startActivity(Intent.createChooser(sharingIntent, "Share text file via"));
	}
	
	public void shareASE()
	{
		
		//Log.i("TeamHex", "Sharing .ase file");
    	
		PaletteRecord p = paletteView.getPalette();
		
		
		ArrayList<ColorRecord> colorRecords = p.getColors();
		
		int colorRecordsLength = colorRecords.size();
		
		String[] names = new String[colorRecordsLength];
		int[] colors = new int[colorRecordsLength];
		
		
		for(int i = 0; i < colorRecordsLength; i++)
		{
			names[i] = colorRecords.get(i).getName();
			colors[i] = colorRecords.get(i).getValue();
		}
		
		
		try {
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File externalRoot = Environment.getExternalStorageDirectory();
			    File temp = new File(externalRoot, p.getName() + ".ase");
			
				File file = ColorPaletteExporter.exportPaletteASE(colors, names, temp.getAbsolutePath().toString());
				
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		    	sharingIntent.setType("application/illustrator");
		    	sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(file));
		    	sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, p.getName() + " ASE Color Palette");
		    	sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
		    			"An ASE file is attached to this email for the color palette "+p.getName()+" .");
		    	startActivity(Intent.createChooser(sharingIntent, "Share ASE file via"));
			}
	    	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    	
	}
	
	public void setPaletteRecord(PaletteRecord setting)
	{
		//Log.i("TeamHex", "Displaying the palette record " + setting.getName());
		myPaletteRecord = setting;
		paletteView.setPalette(myPaletteRecord);
		
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
		nameView.setText(setting.getName());
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
    	
    	//Potentially the user could hit the back button to cancel, and no data would be returned.
    	if(data != null)
    	{
	    	//Log.i("TeamHex", "An Edit Palette activity has returned.");
	    	String nameNew = data.getStringExtra("nameNew");
	    	
	    	 // Load that stuff from memory
	        HexStorageManager mHexStorageManager = new HexStorageManager(getApplication());
	        mHexStorageManager.RecordLoad(nameNew);
	        
	        //Log.i("TEAMHEX:", nameNew);
	        
	        // Set the loaded palette as the current one
	        setPaletteRecord(mHexStorageManager.RecordGet(nameNew));
	        
	        // Change the nameView
	        nameView.setText(nameNew);
    	}
    }

	// Going back means the palette must be reloaded from memory
    @Override
    protected void onResume() {
        super.onResume();
    }
    
	/*
	@Override
	public void onBackPressed(){
		
		  finish();
		  super.onBackPressed();
	}*/

	// The PaletteRecord currently being displayed
	PaletteRecord myPaletteRecord;
	
	// The string of the name  and color records, separated by endlines
	String infoString;
}
