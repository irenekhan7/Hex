package com.teamhex.cooler;

import com.teamhex.cooler.Storage.Activities.PaletteSaveActivity;
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

public class DrawImageActivity extends Activity implements DrawingView.OnSelectionListener {

Bitmap b = null;

DrawingView drawingView;
PaletteView previewPalette;

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
    Button saveButton = (Button) findViewById(R.id.save_create_button);
    saveButton.setOnClickListener(
        new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(previewPalette != null)
            	{
            	 	
    		    	// Get auto-generated names for the palette
	    		    Log.i("TeamHex", "Using the X11Helper to generate names for the palette");
	    		    
	    		    //previewPalette.setX11Names(mX11Helper);
	    		    	
	    		    	// Go to the PaletteSaveActivity to save the palette into the library
	    		    Intent intent_save = new Intent(DrawImageActivity.this, PaletteSaveActivity.class);
	    		    intent_save.putExtra("palette", palette);
	    		    startActivity(intent_save);
	    		    System.out.println("SAVED");
            	}
            }
        }
    );
    processTask = new ProcessTask();

}

PaletteRecord palette;

	private void createPalette()
	{
		Log.i("TeamHex", "Using the ColorPaletteGenerator.colorAlgorithm to get the [] colors.");
		int pixels[] = drawingView.getSelectedPixels();
	    int[] colors = ColorPaletteGenerator.colorAlgorithm(pixels, 5);
	    	
	    // Store the output from colors[] into a new PaletteRecord
	    palette = new PaletteRecord();
	    palette.setName("Untitled_Palette");
	    for (int i = 0; i < 5; i++)
	    	palette.addColor(colors[i]);
	    	
	    Log.i("TeamHex", "Finixhed adding the colors to a new palette.");
	   
	    
	}

	
	private class ProcessTask extends AsyncTask<Object, Object, Object> {
		@Override
		protected Object doInBackground(Object... arg0) {
			createPalette();
			return null;

		}
		
		@Override
	    protected void onPostExecute(Object obj) {
	    	 previewPalette.setPalette(palette);
	    	 Log.i("TeamHex:","Setting preview palette");
	    }
	}


	public void saveResult()
	{
	
	}

	ProcessTask processTask;
	@Override
	public void onSelection() {
		processTask.cancel(true);
		processTask = new ProcessTask();
		processTask.execute();
	}
	
} //END ACTIVITY


