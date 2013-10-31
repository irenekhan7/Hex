package com.teamhex.cooler;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ColorPaletteGenerator extends Activity {
	private ImageView mImageView;
	private Bitmap mImageBitmap;
	private ImageView[] colorBars;

	
	/********************************************************************************************
	 * Begin Color Algorithm Code
	 *******************************************************************************************/
	
	public static class ColorSet
	{
		public ColorSet() { clear(); }
		public ColorSet(int i) { clear(); add(i); }
		private ColorSet(double a, double r, double g, double b, int n) { _A = a; _R = r; _G = g; _B = b; _n = n; }
		
		public void add(int pixel) { _A += (pixel >> 24); _R += ((pixel >> 16) & 0xFF); _G += ((pixel >> 8) & 0xFF); _B += (pixel & 0xFF); _n++; }
		public ColorSet mean() { return new ColorSet(_A/_n, _R/_n, _G/_n, _B/_n, _n); }
		public void clear() { _A = 0; _R = 0; _G = 0; _B = 0; _n = 0; }
		
		public int toInt() { return (((int)_A & 0xFF) << 24) | (((int)_R & 0xFF) << 16) | (((int)_G & 0xFF) << 8) | ((int)_B & 0xFF); }
		
		public static double dist(ColorSet v1, ColorSet v2)
		{
			return (v1._A - v2._A) * (v1._A - v2._A) +
				   (v1._R - v2._R) * (v1._R - v2._R) +
				   (v1._G - v2._G) * (v1._G - v2._G) +
				   (v1._B - v2._B) * (v1._B - v2._B);
		}
		
		public static double dist(int v1, ColorSet v2)
		{
			return ((v1 >> 24) - v2._A) * ((v1 >> 24) - v2._A) +
				   (((v1 >> 16) & 0xFF) - v2._R) * (((v1 >> 16) & 0xFF) - v2._R) +
				   (((v1 >> 8) & 0xFF) - v2._G) * (((v1 >> 8) & 0xFF) - v2._G) +
				   ((v1 & 0xFF) - v2._B) * ((v1 & 0xFF) - v2._B);
		}
		
		public double a() { return _A; }
		public double r() { return _R; }
		public double g() { return _G; }
		public double b() { return _B; }
		public double n() { return _n; }
		
		private double _A, _R, _G, _B;
		private int _n;
	};
	
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
	/*private static double dist(ColorSet v1, ColorSet v2)
	{
		return (v1.a() - v2.a()) * (v1.a() - v2.a()) +
			   (v1.r() - v2.r()) * (v1.r() - v2.r()) +
			   (v1.g() - v2.g()) * (v1.g() - v2.g()) +
			   (v1.b() - v2.b()) * (v1.b() - v2.b());
	}*/
	
	
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
			
			ColorSet[] means = new ColorSet[k];
			for (int i = 0; i < k; i++)
			{
				means[i] = new ColorSet();
			}
			
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
				means[i].clear();
				means[i].add(pixels[index.get(i)]);
			}
			index.clear();
			
			//Iterate through the data set. At each iteration, we pair each pixel with the
			//closest mean based on squared Euclidean distance. The set of these pixels forms a
			//Voroni diagram in two dimensions. After we have paired each point with a mean, we
			//then calculate the centroid of each set, which becomes the new mean. We then repeat
			//this process until none of the pixels change their nearest mean between iterations.
			
			ColorSet[] sets = new ColorSet[k];
			for (int i = 0; i < k; i++)
			{
				sets[i] = new ColorSet();
			}
			
			double maxdist = 0;
			do
			{
				for (int i = 0; i < n; i++)
				{
					int min = 0;
					for (int j = 1; j < k; j++)
					{
						//ColorSet pixel = new ColorSet(pixels[i]);
						if (ColorSet.dist(pixels[i], means[j]) <= ColorSet.dist(pixels[i], means[min]))
						{
							min = j;
						}
					}
					sets[min].add(pixels[i]);
				}
				
				maxdist = 0;
				for (int i = 0; i < k; i++)
				{
					ColorSet mean = sets[i].mean();
					double meandist = ColorSet.dist(means[i], mean);
					if (meandist > maxdist)
					{
						maxdist = meandist;
					}
					means[i] = mean;
					sets[i].clear();
					System.out.println("maxdist = " + maxdist);
				}
			}
			while (maxdist > 25);
			
			//Because the clusters are randomly generated, the colors may appear in any order.
			//We first sort the colors by hue to make the color scheme appear more presentable
			//to the user.
			Integer[] colors = new Integer[k];
			for (int i = 0; i < k; i++)
			{
				colors[i] = means[i].toInt();
			}
			Arrays.sort(colors, HueComparator);
			
			int[] colors2 = new int[k];
			for (int i = 0; i < k; i++)
			{
				colors2[i] = colors[i];
			}
			return colors2;
		}
		
		return null;
	}
	
	/********************************************************************************************
	 * End Color Algorithm Code
	 *******************************************************************************************/

}
