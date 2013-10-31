package com.teamhex.cooler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
//import java.util.List;
//import java.util.Map;
//import java.util.HashMap;
import java.util.Random;

//import android.graphics.Bitmap;
//import android.graphics.Color;

public class ColorPaletteGenerator 
{

	/********************************************************************************************
	 * Begin Color Algorithm Code
	 *******************************************************************************************/
	
	/*private static int dist(int v1, int v2)
	 *		Helper function that returns the squared Euclidean distance between the AR(Alpha-Red)
	 *		and GB(Green-Blue) values of the two selected colors. 
	 *
	 * Parameters:
	 * 		int v1 - the first color to be compared
	 * 		int v2 - the second color to be compared
	 * 
	 * Returns:
	 * 		The squared Euclidean distance between the two colors, using the AR(Alpha-Red) and
	 * 		GB(Green-Blue) values as input
	 */
	private static int dist(int v1, int v2)
	{
		return ((v1 >> 16) - (v2 >> 16)) * ((v1 >> 16) - (v2 >> 16)) + 
			   ((v1 & 0xFFFF) - (v2 & 0xFFFF)) * ((v1 & 0xFFFF) - (v2 & 0xFFFF));
	}
	
	
	//Helper comparator class that compares two integers based on their hue values.
	private static Comparator<Integer> HueComparator = new Comparator<Integer>()
	{
		public double hue(Integer i)
		{
			int r = (i >> 16) & 0xFF;
			int g = (i >> 8) & 0xFF;
			int b = i & 0xFF;
			
			return Math.atan2(1.73205080757 * (g - b), 2 * r - g - b);
		}
		
	    @Override
	    public int compare(Integer i1, Integer i2) 
	    {
	        return (int)(hue(i1) - hue(i2));
	    }
	};
	
	public static class ColorSet
	{
		public ColorSet() { clear(); }
		
		public void add(int pixel) { A += (pixel >> 24); R += ((pixel >> 16) & 0xFF); G += ((pixel >> 8) & 0xFF); B += (pixel & 0xFF); n++; }
		public int mean() { return (n == 0) ? 0 : ((A/n) << 24) | (((R/n) & 0xFF) << 16) | (((G/n) & 0xFF) << 8) | ((B/n) & 0xFF); }
		public void clear() { A = 0; R = 0; G = 0; B = 0; n = 0; }
		
		private int A, R, G, B;
		private int n;
	};
	
	/*public static int[] colorAlgorithm(int[] pixels, int k)
	 *		Computes a color swatch of size <n> based on the input <bitmap> using k-means 
	 *      clustering.
	 * 
	 * Parameters:
	 * 		int[] pixels - the input used to generate the color swatch
	 * 		int k		 - the number of colors to be included in the color swatch
	 * 
	 * Returns:
	 * 		An int array of size <k> that contains the colors in the swatch. Because k-means
	 * 		clustering returns a local (and not a global) minimum, there is no guarantee that
	 * 		the same color swatch will be returned for any two identical inputs.
	 * 
	 * 		If the size of <pixels> is less than <k>, then it is impossible to generate <k>
	 * 		color schemes from the input and null will be returned.
	 */
	public static int[] colorAlgorithm(int[] pixels, int k)
	{
		int n = pixels.length;
		if ((k < n) && (k > 0))
		{
			Random rand = new Random();
			
			int[] means = new int[k];
			
			//Randomly and independently select k unique indices whose points will be the initial
			//values for each of the k means.
			ArrayList<Integer> index = new ArrayList<Integer>();
			index.ensureCapacity(n);
			for (int i = 0; i < n; i++)
			{
				index.add(i);
			}
			Collections.shuffle(index, rand);
			for (int i = 0; i < k; i++)
			{
				means[i] = pixels[index.get(i)];
			}
			index.clear();
			
			//Iterate through the data set. At each iteration, we pair each pixel with the
			//closest mean based on squared Euclidean distance. The set of these pixels forms a
			//Voroni diagram in two dimensions. After we have paired each point with a mean, we
			//then calculate the centroid of each set, which becomes the new mean. We then repeat
			//this process until none of the pixels change their nearest mean between iterations.
			
			ColorSet[] sets = new ColorSet[5];
			for (int i = 0; i < 5; i++)
			{
				sets[i] = new ColorSet();
			}
			
			int maxdist = 0;
			do
			{
				for (int i = 0; i < n; i++)
				{
					int min = 0;
					for (int j = 1; j < k; j++)
					{
						if (dist(pixels[i], means[j]) < dist(pixels[i], means[min]))
						{
							min = j;
						}
					}
					sets[min].add(pixels[i]);
				}
				maxdist = 0;
				for (int i = 0; i < k; i++)
				{
					int mean = sets[i].mean();
					int meandist = dist(means[i], mean);
					if (neandist > maxdist)
					{
						maxdist = meandist;
					}
					means[i] = mean;
					sets[i].clear();
				}
			}
			while (maxdist > 0);
			
			//Because the clusters are randomly generated, the colors may appear in any order.
			//We first sort the colors by hue to make the color scheme appear more presentable
			//to the user.
			Integer[] colors = new Integer[k];
			for (int i = 0; i < k; i++)
			{
				colors[i] = means[i];
			}
			Arrays.sort(colors, HueComparator);
			
			for (int i = 0; i < k; i++)
			{
				means[i] = colors[i];
			}
			return means;
		}
		
		return null;
	}
	
	/********************************************************************************************
	 * End Color Algorithm Code
	 *******************************************************************************************/

	
	/********************************************************************************************
	 * Begin OLD Color Algorithm Code
	 *******************************************************************************************/
	
	//private static int threshold = 5000;	//Similarity threshold. Colors whose squared difference
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
	/*public static int[] colorAlgorithm(Bitmap bitmap, int n)
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
			if (e != null)
			{
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
		}
		
		return colorScheme;
	}*/
	
	/********************************************************************************************
	 * End OLD Color Algorithm Code
	 *******************************************************************************************/
	
	
	/*
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
	
	*/
}
