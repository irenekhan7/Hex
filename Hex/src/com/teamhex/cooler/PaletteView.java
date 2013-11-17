package com.teamhex.cooler;

import java.util.ArrayList;

import com.teamhex.cooler.Storage.Classes.ColorRecord;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class PaletteView extends View {
	// Initial variables
    Paint paint = new Paint();
    PaletteRecord palette;
    
    GestureDetector mDetector;
    
    // PaletteView Constructors
    public PaletteView(Context context) { super(context); Init();}
    public PaletteView(Context context, AttributeSet attrs) { super(context, attrs); Init();}
    
    
    private void Init()
    {
    	mDetector = new GestureDetector(PaletteView.this.getContext(), new GestureListener());
    }
    
    // Get & Set for ColorScheme
    public void setColorScheme(PaletteRecord setting) { palette = setting; colors = palette.getColors();}
    public PaletteRecord getColorScheme() { return palette; }
    
    ArrayList<ColorRecord> colors;
    public void enableEditing()
    {
    	editing = true;
    	
    	//Copies the colors to avoid editing the color palette directly. 
    	//May not be necessary, commented out for now
    	
    	/*
    	ArrayList<ColorRecord> newColors = new ArrayList<ColorRecord>();
    	for(int i = 0; i < colors.size();i++)
    	{
    		newColors.add(new ColorRecord(colors.get(i)));
    	}
    	colors = newColors;
    	*/
    }
    
    int viewWidth = 0;
    int viewHeight = 0;
    
    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        viewWidth = xNew;
        viewHeight = yNew;
   }

    @Override
    public void onDraw(Canvas canvas) {
    	// Don't draw on a blank palette
    	if(palette == null)
    		return;

    	// If there actually are colors, draw each of them
    	if(colors != null && colors.size() != 0 ) {
			// Each paint starts at the rightmost part of the previous
        	int offset = 0,
        		width;
        	
        	// For each color:
	    	for(int i = 0; i < colors.size(); i++) {

	    		// The paint width is its percentage of the whole
	    		//(Temporarily commented out as we aren't reporting that data)
	    		//ColorRecord current = colors.get(i);
	    		//width = (int) ( ( (Float) Math.max(1, current.getPercentage()) / 100 ) * viewWidth );
	    		
	    		width = (int) (viewWidth / colors.size() );
	    		// Draw the actual color using Android.graphics.Paint
	    		paint.setColor(Color.parseColor(colors.get(i).getHex()));
	    		canvas.drawRect(offset, 0, offset + width, viewHeight, paint);
	    		
	    		// Increase the offset (because width isn't constant)
	    		offset += width;
	    	}
    	}
    	// Otherwise just draw plain white
    	else {
    		paint.setColor(Color.WHITE);
    		canvas.drawRect(0, 0, viewWidth, viewHeight, paint);
    	}

    }
    
    private boolean editing = false; //Is this palette view in edit mode?
    private int indexEditing = 0; //What is the index of the color that is being edited?
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	
    	
    	
        // Let the GestureDetector interpret this event
        boolean result = mDetector.onTouchEvent(event);
        
        if(editing)
    	{
	    	
	    	float percentage = (float)event.getY() / (float)viewHeight;
	    	ColorRecord editing = colors.get(indexEditing);
	    	int color = Color.parseColor(editing.getHex());
	    	editing.setInt(Color.rgb(Color.red(color), Color.green(color),(int)(255 * percentage)));
	    	invalidate();
    	}
    	
        // If the GestureDetector doesn't want this event, do some custom processing.
        // This code just tries to detect when the user is done scrolling by looking
        // for ACTION_UP events.
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // User is done scrolling, it's now safe to do things like autocenter
                result = true;
            }
        }
        return result;
    }
    
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        	
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
        	indexEditing = (int)(((float)e.getX() / (float)viewWidth) * colors.size());
            return true;
        }
    }

}