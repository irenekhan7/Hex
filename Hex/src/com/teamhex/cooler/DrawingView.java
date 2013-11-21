package com.teamhex.cooler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
	
	public enum SelectionType 
	{
		LASSO,
		CROP,
		ALL,
	}
	
	public interface OnSelectionListener {
    	void onSelection();
    }

    private OnSelectionListener onSelectionListener;
    
    public void setOnSelectionListener(OnSelectionListener listener) {
    	onSelectionListener = listener;
    }
	
	//Instance variables
	private SelectionType selectionTYPE;
	private Path drawPath;
	private Bitmap canvasBitmap;
	private boolean touchLift = false;
	private ArrayList<Point> points = new ArrayList<Point>();
	private List<Point> pointsList = new ArrayList<Point>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    int corners = 2;
    boolean square = false;
    int touched = 0;
    Canvas canvas = null;
    
    //The pixels selected
    int[] pixels; 
    
    //Constructor
    public DrawingView(Context context, AttributeSet attrs) { super(context, attrs); Init();}
    public DrawingView(Context context) {
        super(context);
        Init();
    }
    
    private void Init() {
    	drawPath = new Path();
      	paint.setColor(Color.WHITE);
      	paint.setAntiAlias(true);
      	paint.setStrokeWidth(3);
      	paint.setStyle(Paint.Style.STROKE);
      	paint.setStrokeJoin(Paint.Join.ROUND);
      	paint.setStrokeCap(Paint.Cap.ROUND);
    }
   
    private Bitmap b;
    public void setBitmap(Bitmap setting) {
    	b = setting;
    }
    
    //Sets the selection type being used.
    public void setSelectionType(SelectionType type) {
    	selectionTYPE = type;
    	
    	if(type == SelectionType.ALL)
    	{
    		resetSelection();
    		pixels = new int[b.getWidth() * b.getHeight()];
    		b.getPixels(pixels, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
    		onSelectionListener.onSelection();
    		canvas.drawBitmap(b, null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);
    	}
    }
    
    //Get the pixels selected
    public int[] getSelectedPixels() {
    	return pixels;
    }
    
	//size assigned to view
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(canvasBitmap);
	}
	
	//draw the view - will be called after touch event
	@Override
	protected void onDraw(Canvas canvas) {
		//If the bitmap is null, what's to draw anyways?
		if(b == null)
			return;
		
		canvas.drawBitmap(b, null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);
		
		if(selectionTYPE == SelectionType.LASSO) {
		
		canvas.drawPath(drawPath, paint);
		paint.setStrokeWidth(1);
		 
		if(touchLift) {
			Log.i("TeamHex", "About to run the ray casting algorithm.");
			ArrayList<Point> polygonPixels = new ArrayList<Point>();
		  
			//Get total image pixels
			//Get bounding box around points
			//ArrayList<Point> boundingBox = new ArrayList<Point>();
			int left   = points.get(0).x,
				right  = left,
			    top    = points.get(0).y,
			    bottom = top,
			    ax, ay;
			
			int n = points.size();
		  
			// Get top left and bottom right bounding box coordinates
			for(int a = 0; a < n; a++) {
				ax = points.get(a).x;
				ay = points.get(a).y;
				if(ax > right)     
					right = ax;
				if(ax < left) 
					left = ax;
				if(ay > bottom)    
					bottom = ay;
				if(ay < top)  
					top = ay;
			}

			int height = bottom - top;
		  
			Log.i("TeamHex", "The [top, right, bottom, left] coordinates are: " + 
					Integer.toString(top) +
					Integer.toString(right) + 
					Integer.toString(bottom) +
					Integer.toString(left));
		  
		  //Map each line segment in the lasso contour to the horizontal rows that it
		  //passes through. This saves us from performing collision checks on lines that
		  //wouldn't have intersected anyways.
		  @SuppressWarnings("unchecked")
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
		  for (int i = 0; i <= height; i++) {
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
		  
		  pixels = new int[polygonPixels.size()];
		  for(int a = 0; a < polygonPixels.size(); a++)
		  {
			int x = (int)((double)polygonPixels.get(a).x / canvas.getWidth() * b.getWidth());
			int y = (int)((double)polygonPixels.get(a).y / canvas.getHeight() * b.getHeight());
			pixels[a] = b.getPixel(x, y);  
		  }
		  
		  //Allow the user to make another select
		  touchLift = false;
		  
		  //Process the result
		  onSelectionListener.onSelection();
		  
		  //Intent i1 = getIntent();
          //i1.putExtra("com.teamhex.cooler.polygonPixels", pixels);
          //setResult(1000, i1);
          //finish();
		  
		 }
		}
		
		else if(selectionTYPE == SelectionType.CROP)
		{
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
                    //Bitmap subBitmap = Bitmap.createBitmap(b, (int)((double)topLeftX / canvas.getWidth() * b.getWidth()), 
                    //		(int)((double)topLeftY / canvas.getHeight() * b.getHeight()), 
                    //		(int) ((double)width / canvas.getWidth() * b.getWidth()), 
                    //		(int) ((double)height / canvas.getHeight() * b.getHeight()));
                    
                    int bTopLeftX = (int)((double)topLeftX / canvas.getWidth() * b.getWidth());
                    int bTopLeftY = (int)((double)topLeftY / canvas.getHeight() * b.getHeight());
                    int bWidth = (int) ((double)width / canvas.getWidth() * b.getWidth());
                    int bHeight = (int) ((double)height / canvas.getHeight() * b.getHeight());
                    
                    //Try drawing sub bitmap
                    //Clear screen / replace with draw image
                    //WORKS
                    //canvas.drawColor(Color.BLACK);
                    //canvas.drawBitmap(subBitmap, null, new Rect(0, 0, width, height), null);
               
                    //Pass results back to Main activity
                    //Intent d = new Intent(DrawImageActivity.this, MainActivity.class);
            		//ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    //subBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                    //d.putEx-tra("subBitmap", bs.toByteArray());
                	//startActivity(d);
                    //int[] pixels = new int[subBitmap.getWidth() * subBitmap.getHeight()];
        	    	//subBitmap.getPixels(pixels, 0, subBitmap.getWidth(), 0, 0, subBitmap.getWidth(), subBitmap.getHeight());
        			
        	    	//for(int a = 0; a < pixels.length; a++)
        	    	//	System.out.println("PIXEL " + a + ": " + pixels[a] + " ");
        	    	
                    //Intent i1 = getIntent();
                    //ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    //subBitmap.compress(Bitmap.CompressFormat.PNG,  50,  bs);
                    //setResult(RESULT_OK, i1);
                    //i1.putExtra("subBitmap", bs.toByteArray());
                    //finish();
                    
                    pixels = new int[width * height];
                    b.getPixels(pixels, 0, bWidth, bTopLeftX, bTopLeftY, bWidth, bHeight);
                    
	                //Allow the user to make another select
	          		touchLift = false;
	          		
	          		//Process the selection
	          		onSelectionListener.onSelection();
	          		
                    //i1.putExtra("com.teamhex.cooler.polygonPixels", pixels);
                    
              }
            }
	}
	
	public void resetSelection()
	{
		points.clear();
		pointsList.clear();
		drawPath.reset();
		corners = 2;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(selectionTYPE == SelectionType.LASSO)
		{
		 if(touchLift)
			return true; //BUNDLE PIXELS & ANALYZE
		 float touchX = event.getX();
		 float touchY = event.getY();
		 //respond to down, move and up events
		 switch (event.getAction()) {
		 case MotionEvent.ACTION_DOWN:
			 
			 resetSelection(); //Clear the old selection
			 
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
		else if(selectionTYPE == SelectionType.CROP)
		{
			//START CROP
			 if(touchLift)
					return true; //BUNDLE PIXELS & ANALYZE
				 float touchX = event.getX();
				 float touchY = event.getY();
				 //respond to down, move and up events
				 switch (event.getAction()) {
				 case MotionEvent.ACTION_DOWN:
					 if(corners == 0)
					 {
						 resetSelection();
					 }
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
