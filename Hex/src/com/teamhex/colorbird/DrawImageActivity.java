package com.teamhex.colorbird;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.teamhex.colorbird.R;
import com.teamhex.colorbird.Palette.ColorPaletteGenerator;
import com.teamhex.colorbird.Storage.Activities.PaletteLibraryActivity;
import com.teamhex.colorbird.Storage.Classes.HexStorageManager;
import com.teamhex.colorbird.Storage.Classes.PaletteRecord;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.util.Log;
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
	    
	    Intent i = getIntent();
	    if (i.hasExtra("fileURI"))
	    {
	    	int width = i.getIntExtra("width", 0);
	    	int height = i.getIntExtra("height", 0);
	    	
	    	if ((width != 0) && (height != 0))
	    	{
		    	Uri uri = Uri.parse(i.getStringExtra("fileURI"));
		        loadBitmap(uri.getPath(), width, height);
	    	}
	    	else
	    	{
	    		//Error: width and/or height undefined
	    	}
	    }
	    else if (i.hasExtra("contentURI"))
	    {
	    	Uri uri = Uri.parse(i.getStringExtra("contentURI"));
	    	loadBitmap(uri);
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
		    		    //Log.i("TeamHex", "About to save the palette.");
		    		    savePalette();
		    		    //Log.i("TeamHex", "The save was completed.");
	            	}
	            }
	        }
	    );
	    processTask = new ProcessTask();
	}
	
    public void clearCache()
    {
    	File cacheDir = getCacheDir();
    	File[] fileList = cacheDir.listFiles();
    	
    	for (int i = 0; i < fileList.length; i++)
    	{
    		fileList[i].delete();
    	}
    }
    
    private void loadBitmap(Uri contentURI)
    {
        try 
        {
        	InputStream input = this.getContentResolver().openInputStream(contentURI);
	        Bitmap bitmap = BitmapFactory.decodeStream(input, null, null);
	        input.close();
	        
	        b = bitmap;
		    drawingView.setBitmap(b);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

	private void loadBitmap(String filename, int width, int height)
	{
		System.out.println(filename);
		File file = new File(filename);
	    InputStream ios = null;
	    
	    System.out.println("file = "  + file);
	    
	    byte[] data = new byte[(int)file.length()];
	    try 
	    {
	        ios = new FileInputStream(file);
	        if ( ios.read(data) == -1 ) 
	        {
	            //Error: EOF reached before "data" could be filled
	        }        
	    } 
	    catch (Exception e) {}
	    finally 
	    { 
	        try 
	        {
	            if (ios != null)
	            {
	                ios.close();
	            }
	        } 
	        catch ( IOException e) {}
	    }
	    
	    int[] pixels = new int[width * height];
	    ColorConverter.decodeYUV420SP(pixels, data, width, height);
	    
	    Configuration configuration = getResources().getConfiguration();
	    if (configuration.orientation != Configuration.ORIENTATION_LANDSCAPE)
	    {
	        int[] newPixels = new int[width * height];
	        for (int y = 0; y < height; y++) 
	        {
	            for (int x = 0; x < width; x++)
	            {
	                newPixels[x * height + height - y - 1] = pixels[x + y * width];
	            }
	        }
	        
	        b = Bitmap.createBitmap(newPixels, height, width, Bitmap.Config.ARGB_8888);
	    }
	    else
	    {
	    	b = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
	    }
	    drawingView.setBitmap(b);
	    clearCache();
	}


	private void createPalette()
	{
		//Log.i("TeamHex", "Using the ColorPaletteGenerator.colorAlgorithm to get the [] colors.");
		int pixels[] = drawingView.getSelectedPixels();
	    int[] colors = ColorPaletteGenerator.colorAlgorithm(pixels, 5);
	    
	    if(colors != null)
	    {
	    	// Store the output from colors[] into a new PaletteRecord
		    palette = new PaletteRecord();
		    palette.setName("Untitled Palette");
		    
		    for (int i = 0; i < 5; i++)
		    	palette.addColor(colors[i]);
		    
		    palette.setX11Names();
	    }
	    	
	    //Log.i("TeamHex", "Finixhed adding the colors to a new palette.");
	   
	    
	}

	private void savePalette()
	{
		if (palette != null)
		{
			// Create the initial storage manager
			//Log.i("TeamHex", "Creating a StorageManager to save the palette");
			HexStorageManager storage = new HexStorageManager(getApplicationContext());
			
			// Save the palette record
			storage.RecordAdd(palette);
	
			//Log.i("TeamHex", "The Palette has been saved.");
			
			// Now that it's saved, go to the library activity
	    	Intent intent_new = new Intent(DrawImageActivity.this, PaletteLibraryActivity.class);
	    	startActivity(intent_new);
			finish();
		}
		else
		{
			//Log.i("TeamHex", "savePalette() failed: The palette was null.");
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


