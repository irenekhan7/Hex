package com.teamhex.colorbird;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.teamhex.colorbird.Palette.ColorPaletteGenerator;
import com.teamhex.colorbird.Storage.Activities.PaletteLibraryActivity;
import com.teamhex.colorbird.Storage.Classes.HexStorageManager;
import com.teamhex.colorbird.Storage.Classes.PaletteRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

//import android.util.Log;

public class DrawImageActivity extends Activity implements DrawingView.OnSelectionListener 
{

	Bitmap b = null;
	
	DrawingView drawingView;
	PaletteView previewPalette;
	boolean reverseRotation = false;
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
			reverseRotation = i.getBooleanExtra("reverseRotation", false);
	    	
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
    
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void loadBitmap(Uri contentURI)
    {
        try 
        {
        	InputStream input = this.getContentResolver().openInputStream(contentURI);

        	BitmapFactory.Options options = new BitmapFactory.Options();
        	options.inJustDecodeBounds = true;
        	
        	BitmapFactory.decodeStream(input, null, options);
        	
        	Display d = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        	
        	options.inJustDecodeBounds = false;
        	int version = android.os.Build.VERSION.SDK_INT;
        	
        	/*
        	int inSampleSize = 1;
        	
        	if (version < android.os.Build.VERSION_CODES.HONEYCOMB_MR2)
        	{
        		int width = d.getWidth()/2;
            	int height = d.getHeight()/2;
            	inSampleSize = Math.max(options.outWidth/width, options.outHeight/height);
            	
        	} 
        	else
        	{
        		Point size = new Point();
        		d.getSize(size);
        		int width = size.x/2;
            	int height = size.y/2;
            	inSampleSize = Math.max(options.outWidth/width, options.outHeight/height);
            }*/
        	
        	options = new BitmapFactory.Options();
        	input = this.getContentResolver().openInputStream(contentURI);
        	options.inSampleSize = 4;//inSampleSize;
        
	        Bitmap bitmap = BitmapFactory.decodeStream(input,null, options);
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

	public Bitmap rotateImage(int angle, Bitmap bitmapSrc) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(bitmapSrc, 0, 0,
				bitmapSrc.getWidth(), bitmapSrc.getHeight(), matrix, true);
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

			//vile nexus reverse landscape sensor orientation check
			if(reverseRotation)
				b = rotateImage(270, b);
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


