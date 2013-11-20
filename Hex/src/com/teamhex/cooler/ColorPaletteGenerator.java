package com.teamhex.cooler;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import com.teamhex.cooler.ColorPaletteModifier;

public class ColorPaletteGenerator {

	/********************************************************************************************
	 * Begin Color Algorithm Code
	 *******************************************************************************************/
	
	//Storage class that represents a set of one or more colors
	public static class ColorSet
	{
		public ColorSet() { clear(); }
		public ColorSet(int i) { set(i); }
		private ColorSet(double a, double r, double g, double b, int n) { _A = a; _R = r; _G = g; _B = b; _n = n; }

		public void set(int i) 
		{ 
			clear(); 
			add(i); 
		}
		
		public void add(int i) 
		{ 
			_A += (i >> 24); 
			_R += ((i >> 16) & 0xFF); 
			_G += ((i >> 8) & 0xFF); 
			_B += (i & 0xFF); 
			_n++;
		}
		
		public ColorSet mean() 
		{ 
			return new ColorSet(_A/_n, _R/_n, _G/_n, _B/_n, _n); 
		}
		public void clear() 
		{ 
			_A = 0; _R = 0; _G = 0; _B = 0; _n = 0; 
		}
		
		public int toInt() 
		{ 
			return (((int)_A & 0xFF) << 24) | 
				   (((int)_R & 0xFF) << 16) | 
				   (((int)_G & 0xFF) << 8)  | 
				    ((int)_B & 0xFF); 
		}
		
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
		
		private double _A, _R, _G, _B;
		private int _n;
	};
	
	
	//Helper comparator class that compares two integers based on their hue values.
	private static Comparator<Integer> ColorComparator = new Comparator<Integer>()
	{
	    @Override
	    public int compare(Integer i1, Integer i2) 
	    {
	    	int hsv1 = ColorPaletteModifier.RGBtoHSV(i1);
	    	int hsv2 = ColorPaletteModifier.RGBtoHSV(i2);
	    	
	    	double c1 = ColorPaletteModifier.getSaturation(hsv1) * ColorPaletteModifier.getValue(hsv2);
	    	double c2 = ColorPaletteModifier.getSaturation(hsv2) * ColorPaletteModifier.getValue(hsv2);
	        return (int)(c1 - c2);
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
			
			
			//k-means++ initialization
			double[] distances = new double[n];
			
			means[0].set(pixels[rand.nextInt(n)]);
			for (int i = 1; i < k; i++)
			{
				double sum = 0;
				for (int j = 0; j < n; j++)
				{
					double mindist = ColorSet.dist(pixels[j], means[0]);
					for (int a = 1; a < i; a++)
					{
						double dist = ColorSet.dist(pixels[j], means[a]);
						if (dist < mindist)
						{
							mindist = dist;
						}
					}
					distances[j] = mindist;
					sum += mindist;
				}
				double p = rand.nextDouble() * sum;
				sum = 0;
				for (int j = 0; j < n; j++)
				{
					sum += distances[j];
					if (sum > p)
					{
						means[i].set(pixels[j]);
						break;
					}
				}
			}
			
			
			//Iterate through the data set. At each iteration, we pair each pixel with the
			//closest mean based on squared Euclidean distance. The set of these pixels forms a
			//Voronoi diagram in two dimensions. After we have paired each point with a mean, we
			//then calculate the centroid of each set, which becomes the new mean. We then repeat
			//this process until none of the pixels change their nearest mean between iterations.
			ColorSet[] sets = new ColorSet[k];
			for (int i = 0; i < k; i++)
			{
				sets[i] = new ColorSet();
			}
			
			double prevdist = 0;
			double maxdist = 0;
			do
			{
				for (int i = 0; i < n; i++)
				{
					int min = 0;
					for (int j = 1; j < k; j++)
					{
						if (ColorSet.dist(pixels[i], means[j]) <= ColorSet.dist(pixels[i], means[min]))
						{
							min = j;
						}
					}
					sets[min].add(pixels[i]);
				}
				
				prevdist = maxdist;
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
				}
			}
			while ((maxdist < prevdist) || (prevdist == 0));
			
			//Because the clusters are randomly generated, the colors may appear in any order.
			//We first sort the colors by hue to make the color scheme appear more presentable
			//to the user.
			
			//TODO: Make this conversion process from int > Integer > int a lot less messy
			Integer[] colors = new Integer[k];
			for (int i = 0; i < k; i++)
			{
				colors[i] = means[i].toInt();
			}
			Arrays.sort(colors, ColorComparator);
			
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
