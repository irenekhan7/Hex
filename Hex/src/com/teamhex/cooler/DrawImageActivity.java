package com.teamhex.cooler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class DrawImageActivity extends Activity {

Bitmap b = null;
Canvas canvas = null;

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getIntent().hasExtra("byteArray")) {

        b = BitmapFactory.decodeByteArray(
                getIntent().getByteArrayExtra("byteArray"), 0, getIntent()
                        .getByteArrayExtra("byteArray").length);
    }
    setContentView(new DrawingView(this));
}

class DrawingView extends View {
	
	//Instance variables
	private Path drawPath;
	private Bitmap canvasBitmap;
	private String selectionTYPE = "LASSO";
	private boolean touchLift = false;
	private ArrayList<Point> points = new ArrayList<Point>();
	private List<Point> pointsList = new ArrayList<Point>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    int corners = 2;
    boolean square = false;
    int touched = 0;
    
    //Constructor
    public DrawingView(Context context)
    {
        super(context);
      	drawPath = new Path();
      	paint.setColor(Color.WHITE);
      	paint.setAntiAlias(true);
      	paint.setStrokeWidth(3);
      	paint.setStyle(Paint.Style.STROKE);
      	paint.setStrokeJoin(Paint.Join.ROUND);
      	paint.setStrokeCap(Paint.Cap.ROUND);
    }
   
	//size assigned to view
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(canvasBitmap);
	}

	int pixelInPolygonTest(int numVertices, ArrayList<Point> points, Point testPoint)
	{
	  int i, j, c = 0;
	  for (i = 0, j = numVertices-1; i < numVertices; j = i++) 
	  {
	    if ( ((points.get(i).y > testPoint.y) != (points.get(j).y > testPoint.y)) &&
	     (testPoint.x < (points.get(j).x - points.get(i).x) * (testPoint.y - points.get(i).y) / (points.get(j).y - points.get(i).y) + points.get(i).x) )
	       c = 1;
	  }
	  return c;
	}
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
		  ArrayList<Point> boundingBox = new ArrayList<Point>();
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
		   if(points.get(a).y > top)
			   top = points.get(a).y;
		   if(points.get(a).y < bottom)
			   bottom = points.get(a).y;
		  }
		  
		  System.out.println("TOP: " + top + "\nBOTTOM: " + bottom
				  + "\nLEFT: " + left + "\nRIGHT: " + right);
		  //Draw circle for each pixel in the bounding box
		  //WORKS
		  //Add bounding box pixels to arraylist
		  for(int x = left; x < right; x++)
		  {
			for(int y = bottom; y < top; y++)
			 boundingBox.add(new Point(x, y));
			//PRINT
		    //canvas.drawCircle((float)x, (float)y, 1, paint); 
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
          i1.putExtra("polygonPixels", pixels);
          setResult(RESULT_OK, i1);
          finish();
		  
		 }
		}
		
		else if(selectionTYPE == "CROP")
		{
			//Draw image
            canvas.drawBitmap(b, null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);
            
            //Traverse points
            for(int i=0; i<pointsList.size(); i++) 
            {
                Point current = pointsList.get(i);

                // Draw points
                canvas.drawCircle(current.x, current.y, 10, paint);   
            }
            
            Point p1 = null;
            Point p2 = null;
            //Draw square if there are 2 corners
            if(corners == 0)
            {
            		p1 = new Point(pointsList.get(0).x, pointsList.get(1).y);
            		p2 = new Point(pointsList.get(1).x, pointsList.get(0).y);
            		if(pointsList.contains(p1) == false)
            		{
            		 pointsList.add(p1);
            		 System.out.println("ADDED P1");
            		}
            		if(pointsList.contains(p2) == false)
            		{
            		 pointsList.add(p2);
            		 System.out.println("ADDED P2");
            		}
            		
            		//Draw square
                    Point o1 = pointsList.get(0);
                    Point o2 = pointsList.get(1);
                    canvas.drawLine(o1.x, o1.y, p1.x, p1.y, paint);
                    canvas.drawLine(o1.x, o1.y, p2.x, p2.y, paint);
                    canvas.drawLine(o2.x, o2.y, p1.x, p1.y, paint);
                    canvas.drawLine(o2.x, o2.y, p2.x, p2.y, paint);
                    square = true;
                    
                  //Find top left point
                    int topLeftX;
                    int topLeftY;
                    
                    if(o1.x > o2.x)
                    	topLeftX = o2.x;
                    else
                    	topLeftX = o1.x;
                    if(o1.y > o2.y)
                    	topLeftY = o2.y;
                    else
                    	topLeftY = o1.y;
                    
                    int width = Math.abs(o2.x - o1.x);
                    int height = Math.abs(o2.y - o1.y);
                    
                    System.out.println("BITMAP WIDTH: " + b.getWidth());
                    System.out.println("TOP LEFT X: " + (int)((double)topLeftX / canvas.getWidth() * b.getWidth()));
                    System.out.println("TOP LEFT Y: " + (int)((double)topLeftY / canvas.getHeight() * b.getHeight()));
                    System.out.println("WIDTH: " + ((int) ((double)width / canvas.getWidth() * b.getWidth())));
                    System.out.println("HEIGHT: " + ((int) ((double)height / canvas.getHeight() * b.getHeight())));
                    System.out.println("WIDTH ONLY: " + width);
                    System.out.println("HEIGHT ONLY: " + height);
                    
                    //Create sub bitmap from user selection
                    Bitmap subBitmap = Bitmap.createBitmap(b, (int)((double)topLeftX / canvas.getWidth() * b.getWidth()), 
                    		(int)((double)topLeftY / canvas.getHeight() * b.getHeight()), 
                    		(int) ((double)width / canvas.getWidth() * b.getWidth()), 
                    		(int) ((double)height / canvas.getHeight() * b.getHeight()));
                    
                    //Try drawing sub bitmap
                    //Clear screen / replace with draw image
                    //WORKS
                    //canvas.drawColor(Color.BLACK);
                    //canvas.drawBitmap(subBitmap, null, new Rect(0, 0, width, height), null);
               
                    //Pass results back to Main activity
                    //Intent d = new Intent(DrawImageActivity.this, MainActivity.class);
            		//ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    //subBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                    //d.putExtra("subBitmap", bs.toByteArray());
                	//startActivity(d);
                    //int[] pixels = new int[subBitmap.getWidth() * subBitmap.getHeight()];
        	    	//subBitmap.getPixels(pixels, 0, subBitmap.getWidth(), 0, 0, subBitmap.getWidth(), subBitmap.getHeight());
        			
        	    	//for(int a = 0; a < pixels.length; a++)
        	    	//	System.out.println("PIXEL " + a + ": " + pixels[a] + " ");
        	    	
                    Intent i1 = getIntent();
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    subBitmap.compress(Bitmap.CompressFormat.PNG,  50,  bs);
                    i1.putExtra("subBitmap", bs.toByteArray());
                    setResult(RESULT_OK, i1);
                    finish();
              }
            }
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(selectionTYPE == "LASSO")
		{
		 if(touchLift)
			return true; //BUNDLE PIXELS & ANALYZE
		 float touchX = event.getX();
		 float touchY = event.getY();
		 //respond to down, move and up events
		 switch (event.getAction()) {
		 case MotionEvent.ACTION_DOWN:
			 points.add(new Point((int)touchX, (int)touchY));
			 drawPath.moveTo(touchX, touchY);
			 break;
		 case MotionEvent.ACTION_MOVE:
			 points.add(new Point((int)touchX, (int)touchY));
			 //drawPath.cubicTo((float)(points.get(points.size()-1).x)/2, points.get(points.size()-1).y, touchX, touchY);
			 float x1 = points.get(points.size()-1).x;
	 		 float y1 = points.get(points.size()-1).y;
	 		 float x3 = touchX;
	 		 float y3 = touchY;
	 		 float x2 = (x1 + x3) / 2;
	 		 float y2 = (y1 + y3) / 2;
	 			
	 		 //drawPath.moveTo(x1, y1);
	 		 drawPath.cubicTo(x1, y1, x2, y2, x3, y3);
			 
			 
			 break;
	 	 case MotionEvent.ACTION_UP:
	 		 points.add(new Point((int)touchX, (int)touchY));
			 drawPath.quadTo(points.get(points.size()-1).x, points.get(points.size()-1).y, touchX, touchY);

			 x3 = points.get(0).x;
	 		 y3 = points.get(0).y;
	 		 x1 = touchX;
	 		 y1 = touchY;
	 		 x2 = (x1 + x3) / 2;
	 		 y2 = (y1 + y3) / 2;
	 			
	 		 //drawPath.moveTo(x1, y1);
	 		 drawPath.cubicTo(x1, y1, x2, y2, x3, y3);
	 		 
	 		 touchLift = true;
	 		  canvas.drawPath(drawPath, paint);
	 		 if(!touchLift)
	 		  drawPath.reset();
	 		 break;
	 	 default:
	 		 return false;
		 }
		}
		else if(selectionTYPE == "CROP")
		{
			//START CROP
			 if(touchLift)
					return true; //BUNDLE PIXELS & ANALYZE
				 float touchX = event.getX();
				 float touchY = event.getY();
				 //respond to down, move and up events
				 switch (event.getAction()) {
				 case MotionEvent.ACTION_DOWN:
					 if(corners > 0)
					  pointsList.add(new Point((int)touchX, (int)touchY));
					 if (corners != 0)
					 corners--;
					 break;
				 
				
			 	 default:
			 		 return false;
				 }
			//END CROP
		}
		//redraw
		invalidate();
		return true;

	}
	


 } //END CLASS
} //END ACTIVITY
