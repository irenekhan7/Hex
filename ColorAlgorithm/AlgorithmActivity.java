package com.teamhex.coloralgorithm;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class AlgorithmActivity extends Activity {
	private ImageView mImageView;
	private Bitmap mImageBitmap;
	private ImageView[] colorBars;

	
	/********************************************************************************************
	 * Begin Color Algorithm Code
	 *******************************************************************************************/
	
	private int threshold = 5000;		//Similarity threshold. Colors whose squared difference
										//with another color in the swatch is less than or equal
										//to this value are not included in the swatch. Currently
										//a fixed value, but should ideally be based on the mean
										//and standard deviation of the color values.
	
	
	/*public int[] colorAlgorithm(Bitmap bitmap, int n):
	 *		Computes a color swatch of size <n> based on the input <bitmap>.
	 * 
	 * Parameters:
	 * 		Bitmap bitmap - the input bitmap image used to generate the color swatch.
	 * 		int n		  - the number of colors to be included in the color swatch.
	 * 
	 * Returns:
	 * 		An int array of size <n> that contains the colors in the swatch. The number of colors
	 *      in the swatch is not necessarily <n>, however (see below).
	 */
	public int[] colorAlgorithm(Bitmap bitmap, int n)
	{
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		int[] colorScheme = new int[n];
		
		//Construct a TreeMap that represents a histogram of the number of times that each color
		//appears in the bitmap.
		TreeMap<Integer, Integer> histogram = new TreeMap<Integer, Integer>();
		for (int i = 0; i < width - 1; i++)
		{
			for (int j = 0; j < height - 1; j++)
			{
				int color = bitmap.getPixel(i, j);
				if (histogram.containsKey(color))
				{
					Integer val = histogram.get(color);
					histogram.put(color, val + 1);
				}
				else
				{
					histogram.put(color, 1);
				}
			}
		}
		
		//Create a reverse sorted histogram such that the keys are now the number of times that a
		//color appears and the values are the colors themselves.
		TreeMap<Integer, Integer> reverse = new TreeMap<Integer, Integer>(Collections.reverseOrder());
		for (Map.Entry<Integer, Integer> entry : histogram.entrySet()) 
		{
			reverse.put(entry.getValue(), entry.getKey());
		}
		
		//Take the <n> highest-frequency colors that are not within <threshold> of each other.
		//Note that if either <threshold> or <n> are too high, then there may not be enough valid
		//colors to include in the swatch. However, because the highest-frequency color is always
		//included, the swatch is guaranteed to have at least one color.
		for (int i = 0; i < n; i++)
		{
			Map.Entry<Integer, Integer> e = reverse.firstEntry();
			colorScheme[i] = e.getValue();
			
			reverse.remove(e.getKey());
			
			for (int j = 0; j < i; j++)
			{
				int r = Color.red(colorScheme[i]) - Color.red(colorScheme[j]);
				int g = Color.green(colorScheme[i]) - Color.green(colorScheme[j]);
				int b = Color.blue(colorScheme[i]) - Color.blue(colorScheme[j]);
				
				if ((r * r + g * g + b * b) < threshold)
				{
					colorScheme[i] = 0;
					i--;
					break;
				}
			}
		}
		
		return colorScheme;
	}
	
	/********************************************************************************************
	 * End Color Algorithm Code
	 *******************************************************************************************/
	
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_algorithm);
		
		DisplayMetrics display = this.getResources().getDisplayMetrics();
		
		int width = display.widthPixels;
		
		mImageBitmap = BitmapFactory.decodeFile("/sdcard/rainbow2.jpg");
		
		int[] colors = colorAlgorithm(mImageBitmap, 5);
		
		mImageView = (ImageView) findViewById(R.id.imageView1);
		mImageView.setImageBitmap(mImageBitmap);
		
		mImageView.setMinimumWidth(width);
		mImageView.setMinimumHeight(mImageBitmap.getHeight() * width/mImageBitmap.getWidth());
		mImageView.setScaleType(ScaleType.FIT_XY);
		
		colorBars = new ImageView[5];
		
		Bitmap scaled = Bitmap.createScaledBitmap(mImageBitmap, width/5, 10000, false);
		
		colorBars[0] = (ImageView) findViewById(R.id.imageView2);
		colorBars[1] = (ImageView) findViewById(R.id.imageView3);
		colorBars[2] = (ImageView) findViewById(R.id.imageView4);
		colorBars[3] = (ImageView) findViewById(R.id.imageView5);
		colorBars[4] = (ImageView) findViewById(R.id.imageView6);
		for (int i = 0; i < 5; i++)
		{
			colorBars[i].setImageBitmap(scaled);
			colorBars[i].setScaleType(ScaleType.FIT_XY);
			colorBars[i].setMaxWidth(width/5);
			colorBars[i].setMinimumHeight(100000);
			colorBars[i].setColorFilter(colors[i]);
		}
	}
}
