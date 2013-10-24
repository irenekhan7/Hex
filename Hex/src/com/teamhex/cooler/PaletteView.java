package com.teamhex.cooler;

import java.util.ArrayList;
import com.teamhex.cooler.Storage.Classes.ColorRecord;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PaletteView extends View {
	// Initial variables
    Paint paint = new Paint();
    PaletteRecord palette;
    
    // PaletteView Constructors
    public PaletteView(Context context) { super(context); }
    public PaletteView(Context context, AttributeSet attrs) { super(context, attrs); }
    
    // Get & Set for ColorScheme
    public void setColorScheme(PaletteRecord setting) { palette = setting; }
    public PaletteRecord getColorScheme() { return palette; }
    
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
    	
    	ArrayList<ColorRecord> colors = palette.getColors();
    	
    	// If there actually are colors, draw each of them
    	if(colors != null && colors.size() != 0 ) {
			// Each paint starts at the rightmost part of the previous
        	int offset = 0,
        		width;
        	ColorRecord current;
        	
        	// For each color:
	    	for(int i = 0; i < colors.size(); i++) {
	    		current = colors.get(i);
	    		
	    		// The paint width is its percentage of the whole
	    		width = (int) ( ( (Float) Math.max(1, current.getPercentage()) / 100 ) * viewWidth );
	    		
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

}