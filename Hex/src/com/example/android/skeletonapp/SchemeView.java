package com.example.android.skeletonapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PaletteView extends View {
    Paint paint = new Paint();

    Palette colorPalette;
    
    public PaletteView(Context context) {
        super(context);
        
    }
    
    public PaletteView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    
    public void setColorPalette(Palette setting)
    {
    	colorPalette = setting;
    }
    
    public Palette getColorPalette()
    {
    	return colorPalette;
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
    	
    	if(colorPalette == null){
    		return;
    	}
    	List<Integer> colors = colorPalette.getColors();
    	
    	int width = 0;
    	if(colors != null && colors.size() != 0 )
    	{
	    	width = viewWidth / colors.size();
	    	
	    	for(int i = 0; i < colors.size(); i++)
	    	{
	    		paint.setColor(colors.get(i));
	    		canvas.drawRect(i * width, 0, i*width+width, viewHeight, paint);
	    	}
    	}
    	else
    	{
    		paint.setColor(Color.WHITE);
    		canvas.drawRect(0, 0, viewWidth, viewHeight, paint);
    	}

    }

}