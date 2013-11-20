package com.teamhex.cooler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.teamhex.cooler.Storage.Activities.PaletteEditActivity;
import com.teamhex.cooler.Storage.Activities.PaletteInfoActivity;
import com.teamhex.cooler.Storage.Activities.PaletteSaveActivity;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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
<<<<<<< HEAD
	//draw the view - will be called after touch event
	@Override
	protected void onDraw(Canvas canvas) 
	{
		if(selectionTYPE == "LASSO")
		{
         canvas.drawBitmap(b, null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);
		 canvas.drawPath(drawPath, paint);
		 paint.setStrokeWidth(1);
	 	//GET PIXELS FROM POINTS AND SEND TO MAINACTIVITY
		 
		 if(touchLift)
		 {
		  System.out.println("RUN RAY CASTING ALGORITHM HERE");
		  ArrayList<Point> polygonPixels = new ArrayList<Point>();
		  
		  //Get total image pixels
		  //Get bounding box around points
		  //ArrayList<Point> boundingBox = new ArrayList<Point>();
		  int left = points.get(0).x;
		  int right = points.get(0).x;
		  int top = points.get(0).y;
		  int bottom = points.get(0).y;
		  
		  //Get top left and bottom right bounding box coords
		  for(int a = 1; a < points.size(); a++)
		  {
		   if(points.get(a).x > right)
			   right = points.get(a).x;
		   if(points.get(a).x < left)
			   left = points.get(a).x;
		   if(points.get(a).y > bottom)
			   bottom = points.get(a).y;
		   if(points.get(a).y < top)
			   top = points.get(a).y;
		  }
		  
		  int n = points.size();
		  int width = right - left;
		  int height = bottom - top;
		  
		  System.out.println("TOP: " + top + "\nBOTTOM: " + bottom
				  + "\nLEFT: " + left + "\nRIGHT: " + right);
		  //Draw circle for each pixel in the bounding box
		  //WORKS
		  //Add bounding box pixels to arraylist
		  /*for(int x = left; x < right; x++)
		  {
			for(int y = bottom; y < top; y++)
			{
			 boundingBox.add(new Point(x, y));
			//PRINT
		    //canvas.drawCircle((float)x, (float)y, 1, paint); 
			}
		  } 
			  
		  //For each pixel in image, run poly test
		  //If test succeeds, add pixel to polygonPixels
		  for(Point p : boundingBox)
		  {
		   if(pixelInPolygonTest(points.size(), points, p) == 1)
		   {
			//canvas.drawCircle((float)p.x, (float)p.y, 1, paint); 
		    polygonPixels.add(p);
		   }
		  }*/
		  
		  //Map each line segment in the lasso contour to the horizontal rows that it
		  //passes through. This saves us from performing collision checks on lines that
		  //wouldn't have intersected anyways.
		  ArrayList<Integer>[] lineMap = (ArrayList<Integer>[]) new ArrayList[height+1];
		  for (int i = 0; i <= height; i++)
		  {
			  lineMap[i] = new ArrayList<Integer>();
		  }
		  
		  int y1, y2;
		  for (int i = 0; i < n; i++)
		  {
			  if (i != (n - 1))
			  {
				  y1 = points.get(i).y;
				  y2 = points.get(i+1).y;
			  }
			  else
			  {
				  y1 = points.get(n - 1).y;
				  y2 = points.get(0).y;
			  }
			  
			  if (y2 <= y1)
			  {
				  int temp = y1;
				  y1 = y2;
				  y2 = temp;
			  }
			  
			  for (int j = y1; j <= y2; j++)
			  {
				  lineMap[j-top].add(i);
			  }
		  }
		  
		  //Perform intersection checks with the lasso contour and each row inside of the
		  //bounding box. Each row will be split up into intervals. Because the lasso forms
		  //a line loop by its nature, every even-numbered interval will be within the area
		  //bounded by the lasso.
		  for (int i = 0; i <= height; i++)
		  {
			  ArrayList<Double> intersections = new ArrayList<Double>();
			  ArrayList<Integer> lines = lineMap[i];
			  
			  for (int j = 0; j < lines.size(); j++)
			  {
				  int lineID = lines.get(j);
				  Point p1, p2;
				  
				  if (lineID != (n - 1))
				  {
					  p1 = points.get(lineID);
					  p2 = points.get(lineID+1);
				  }
				  else
				  {
					  p1 = points.get(n - 1);
					  p2 = points.get(0);
				  }
				  
				  if (p1.x == p2.x)
				  {
					  intersections.add((double)p1.x);
				  }
				  else if (p1.y == p2.y)
				  {
					  intersections.add((double)p1.x);
					  intersections.add((double)p2.x);
				  }
				  else
				  {
					  double slope = (double)(p2.y - p1.y)/(double)(p2.x - p1.x);
					  double yInt = (double)p2.y - ((double)p2.x * slope);
					  
					  intersections.add(((double)(i + top) - yInt)/slope);
				  }
			  }
			  Collections.sort(intersections);
			  
			  for (int j = 0; j < intersections.size() - 1; j += 2)
			  {
				  int y = i + top;
				  int x1 = (int)Math.floor(intersections.get(j));
				  int x2 = (int)Math.ceil(intersections.get(j+1));
				  
				  for (int k = x1; k <= x2; k++)
				  {
					  polygonPixels.add(new Point(k, y));
				  }
			  }
		  }
		  
		  //Send polygonPixels to MainActivity
		  //Conversion factor?
		  
		  int[] pixels = new int[polygonPixels.size()];
		  for(int a = 0; a < polygonPixels.size(); a++)
		  {
			int x = (int)((double)polygonPixels.get(a).x / canvas.getWidth() * b.getWidth());
			int y = (int)((double)polygonPixels.get(a).y / canvas.getHeight() * b.getHeight());
			pixels[a] = b.getPixel(x, y);  
		  }
		  
		  Intent i1 = getIntent();
          i1.putExtra("com.teamhex.cooler.polygonPixels", pixels);
          setResult(1000, i1);
          finish();
		  
		 }
=======
	
	private class ProcessTask extends AsyncTask {
		@Override
		protected Object doInBackground(Object... arg0) {
			createPalette();
			return null;
>>>>>>> origin/Ian
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


