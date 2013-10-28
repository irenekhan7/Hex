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
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawImageActivity extends Activity {

Bitmap b = null;
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

class DrawingView extends SurfaceView {

    private final SurfaceHolder surfaceHolder;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    int corners = 2;
    boolean square = false;
    private List<Point> pointsList = new ArrayList<Point>();

    public DrawingView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        paint.setColor(Color.WHITE);
        paint.setStyle(Style.FILL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (surfaceHolder.getSurface().isValid()) {
            	if(square == true)
            	{
            		square = false;
            		corners = 2;
            		pointsList = new ArrayList<Point>();
            	}
                // Add current touch position to the list of points
            	if(corners > 0)
                 pointsList.add(new Point((int)event.getX(), (int)event.getY()));
                if (corners != 0)
                corners--;
                
                // Get canvas from surface
                Canvas canvas = surfaceHolder.lockCanvas();

                // Clear screen / replace with draw image
                canvas.drawColor(Color.BLACK);

                //Draw image
                //Resources res = getResources();
                //Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
                //canvas = new Canvas(b.copy(Bitmap.Config.ARGB_8888, true));
                canvas.drawBitmap(b, null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);
                // Iterate on the list
                for(int i=0; i<pointsList.size(); i++) {
                    Point current = pointsList.get(i);

                    // Draw points
                    canvas.drawCircle(current.x, current.y, 10, paint);
                    
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
                            Intent i1 = getIntent();
                            ByteArrayOutputStream bs = new ByteArrayOutputStream();
                            subBitmap.compress(Bitmap.CompressFormat.PNG,  50,  bs);
                            i1.putExtra("subBitmap", bs.toByteArray());
                            setResult(RESULT_OK, i1);
                            finish();
                    }
                }

                // Release canvas
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
        return false;
    }

 }
}
