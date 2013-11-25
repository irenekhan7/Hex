package com.teamhex.cooler;

import java.util.ArrayList;

import com.teamhex.cooler.ColorPaletteModifier.ColorVariable;
import com.teamhex.cooler.DrawingView.SelectionType;
import com.teamhex.cooler.Storage.Classes.ColorRecord;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
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
    
    public interface OnInteractListener{
    	void onInteract();
    }

    private OnInteractListener onInteractListener;
    
    public void setOnInteractListener(OnInteractListener listener) {
    	onInteractListener = listener;
    }
    // Get & Set for ColorPalette
    public void setPalette(PaletteRecord setting) { palette = setting; colors = palette.getColors();invalidate();}
    public PaletteRecord getColorScheme() { return palette; }
    
    
    
    ArrayList<ColorRecord> colors = new ArrayList<ColorRecord>();
    ArrayList<ColorRecord> originalColors;
    public void enableEditing()
    {
    	isEditing = true;
    	
    	//Copies the colors to avoid editing the color palette directly. 
    	//May not be necessary, commented out for now
    	setOriginalColors();
    	
    }
    
    public void setOriginalColors()
    {
    	originalColors = new ArrayList<ColorRecord>();
    	for(int i = 0; i < colors.size();i++)
    	{
    		originalColors.add(new ColorRecord(colors.get(i)));
    	}
    }
    
    public ArrayList<ColorRecord> getColors() { return colors; }
    
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
    
    private boolean isEditing = false; //Is this palette view in edit mode?
    private int indexEditing = 0; //What is the index of the color that is being edited?
    
    public String info = "";
    
    public boolean isListItem = false;
    
   //Send a message to the parent activity that the view has been touched.
    public Boolean fireInteractEvent()
    {
    	if(onInteractListener != null)
    	{
    		onInteractListener.onInteract(); 
    		return true;
    	}
    	return false;
    }
    
    private int mode = 0;
    
    public void setMode(int modeSet)
    {
    	mode = modeSet;
    	setOriginalColors();
	}
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	
    	//If the event is outside of the view, don't handle the event.
    	if(isListItem || event.getY() < 0 || event.getY() > viewHeight || event.getX() < 0 || event.getX() > viewWidth)
    	{
    		return false;
    	}
        // Let the GestureDetector interpret this event
        boolean result = mDetector.onTouchEvent(event);
        
        if(isEditing)
    	{
	    	if(event.getPointerCount() >= 2)
	    	{
	    		for(int i = 0; i < colors.size(); i++)
	    		{
	    			float percentage = (float)event.getY() / (float)viewHeight;
	    	    	ColorRecord editing = colors.get(i);
	    	    	int color = Color.parseColor(editing.getHex());
	    	    	editing.setInt(ColorPaletteModifier.modify(color, percentage,ColorVariable.values()[mode]));
	    	    	invalidate();
	    		}
	    	}
	    	else
	    	{
		    	float percentage = (float)event.getY() / (float)viewHeight;
		    	ColorRecord editing = colors.get(indexEditing);
		    	ColorRecord original = originalColors.get(indexEditing);
		    	int color = Color.parseColor(original.getHex());
		    	editing.setInt(ColorPaletteModifier.modify(color, percentage, ColorVariable.values()[mode]));
		    	invalidate();
	    	}
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
        	if(colors != null)
        	{
	        	indexEditing = (int)(((float)e.getX() / (float)viewWidth) * colors.size());
	        	if(indexEditing < colors.size())
	        	{
		        	info =  colors.get(indexEditing).getName() + "\n" + colors.get(indexEditing).getHex();
		        	return fireInteractEvent() || isEditing;
	        	}
        	}
            return false;
        }
    }

}