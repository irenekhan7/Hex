package com.teamhex.cooler;

import com.teamhex.cooler.Palette.ColorPaletteGenerator;
import com.teamhex.cooler.Storage.Activities.PaletteLibraryActivity;
import com.teamhex.cooler.Storage.Classes.HexStorageManager;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class DrawImageActivity extends Activity implements DrawingView.OnSelectionListener 
{

	Bitmap b = null;
	
	DrawingView drawingView;
	PaletteView previewPalette;
	
	Button saveButton;
	
	PaletteRecord palette = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.activity_palette_create);
	   
	    
	    //Setup the mode select spinner
	    Spinner spinner = (Spinner) findViewById(R.id.spinner_select);
	    // Create an ArrayAdapter using the string array and a default spinner layout
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
	            R.array.select_options_array, android.R.layout.simple_spinner_item);
	    // Specify the layout to use when the list of choices appears
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    // Apply the adapter to the spinner
	    spinner.setAdapter(adapter);
	    
	    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	   	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	   	        drawingView.setSelectionType(DrawingView.SelectionType.values()[pos]);
	   	    }
	   	    public void onNothingSelected(AdapterView<?> parent) {
	   	    }
	    });
	    
	    
	    //Setup the drawing view for making selections
	    drawingView = (DrawingView) findViewById(R.id.drawing_view);
	    drawingView.setOnSelectionListener(this);
	    
	    if (getIntent().hasExtra("byteArray")) {
	
	        b = BitmapFactory.decodeByteArray(
	                getIntent().getByteArrayExtra("byteArray"), 0, getIntent()
	                        .getByteArrayExtra("byteArray").length);
	        drawingView.setBitmap(b);
	    }
	    
	    //Setup the preview palette
	    previewPalette = (PaletteView)findViewById(R.id.preview_palette);
	    
	    
	    //Setup the save button
	 // Event: Edit Button Pressed 
	    saveButton = (Button) findViewById(R.id.save_create_button);
	    saveButton.setEnabled(false);
	    
	    saveButton.setOnClickListener(
	        new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	if(previewPalette != null) {
		    		    Log.i("TeamHex", "About to save the palette.");
		    		    savePalette();
		    		    Log.i("TeamHex", "The save was completed.");
	            	}
	            }
	        }
	    );
	    processTask = new ProcessTask();
	
	}


	private void createPalette()
	{
		Log.i("TeamHex", "Using the ColorPaletteGenerator.colorAlgorithm to get the [] colors.");
		int pixels[] = drawingView.getSelectedPixels();
	    int[] colors = ColorPaletteGenerator.colorAlgorithm(pixels, 5);
	    
	    if(colors != null)
	    {
	    	// Store the output from colors[] into a new PaletteRecord
		    palette = new PaletteRecord();
		    palette.setName("Untitled_Palette");
		    
		    for (int i = 0; i < 5; i++)
		    	palette.addColor(colors[i]);
		    
		    palette.setX11Names();
	    }
	    	
	    Log.i("TeamHex", "Finixhed adding the colors to a new palette.");
	   
	    
	}

	private void savePalette()
	{
		if (palette != null)
		{
			// Create the initial storage manager
			Log.i("TeamHex", "Creating a StorageManager to save the palette");
			HexStorageManager storage = new HexStorageManager(getApplicationContext());
			
			// Save the palette record
			storage.RecordAdd(palette);
	
			Log.i("TeamHex", "The Palette has been saved.");
			
			// Now that it's saved, go to the library activity
	    	Intent intent_new = new Intent(DrawImageActivity.this, PaletteLibraryActivity.class);
	    	startActivity(intent_new);
			finish();
		}
		else
		{
			Log.i("TeamHex", "savePalette() failed: The palette was null.");
		}
	}
	
	private class ProcessTask extends AsyncTask<Object, Object, Object> {
		@Override
		protected Object doInBackground(Object... arg0) {
			createPalette();
			return null;

		}
		
		@Override
	    protected void onPostExecute(Object obj) 
		{
			if (palette != null)                               
			{
				previewPalette.setPalette(palette);
		    	saveButton.setEnabled(true);
			}
	    }
	}

	ProcessTask processTask;
	@Override
	public void onSelection() {
		saveButton.setEnabled(false);
		processTask.cancel(true);
		processTask = new ProcessTask();
		processTask.execute();
	}
	
} //END ACTIVITY


