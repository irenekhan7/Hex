package com.teamhex.cooler;

public class ColorPaletteModifier 
{
	public enum ColorVariable
	{
		HUE,
		SATURATION,
		VALUE,
	}
	
	public static int ColorRGB(int r, int g, int b)
	{
		System.out.println("r = " + r + " g = " + g + " b = " + b);
		return ((0xFF << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
	}
	
	public static int ColorHSV(double h, double s, double v)
	{
		int flag = (Double.isNaN(h) ? 1 : 0);
		
		int v1 = (Double.isNaN(h) ? 0 : (int)(h * 91.0222222));
		int v2 = (int)(s * 255);
		int v3 = (int)(v * 255);
		
		return (((flag & 0x1) << 31) | (v1 & 0xFFFF) << 16) | ((v2 & 0xFF) << 8) | (v3 & 0xFF);
	}
	
	public static double getHue(int hsvColor)
	{
		int flag = (hsvColor >> 31);
		if (flag == 1)
		{
			return Double.NaN;
		}
		else
		{
			int n = hsvColor >> 16;
			return (n * 0.01098632812);
		}
	}
	
	public static double getSaturation(int hsvColor)
	{
		return ((double)((hsvColor >> 8) & 0xFF))/255.0;
	}
	
	public static double getValue(int hsvColor)
	{
		return ((double)(hsvColor & 0xFF))/255.0;
	}
	
	public static int RGBtoHSV(int rgbColor)
	{
		double r = (double)((rgbColor >> 16) & 0xFF)/255.0;
		double g = (double)((rgbColor >> 8) & 0xFF)/255.0; 
		double b = (double)(rgbColor & 0xFF)/255.0;
		
		double max = (r > g) ? ((r > b) ? r : b) : ((g > b) ? g : b);
		double min = (r < g) ? ((r < b) ? r : b) : ((g < b) ? g : b);
		
		double v = max;
		double d = max - min;
		
		if (max == 0)
		{
			return ColorHSV(Double.NaN,0.0,v);
		}
		else
		{
			double h = Double.NaN;
			double s = (max == 0) ? 0 : d/v;
			if (r == max)
			{
				h = 60 * (g - b)/d;
			}
			else if (g == max)
			{
				h = 120 + 60 * (b - r)/d;
			}
			else if (b == max)
			{
				h = 240 + 60 * (r - g)/d;
			}
			return ColorHSV(h,s,v);
		}
	}
	
	public static int HSVtoRGB(int hsvColor)
	{
		double h = getHue(hsvColor)/60.0;
		double s = getSaturation(hsvColor);
		double v = getValue(hsvColor);
		
		System.out.println("h = " + h*60.0 + " s = " + s + " v = " + v);
		
		if (s == 0.0)
		{
			return ColorRGB((int)(v*255.0), (int)(v*255.0), (int)(v*255.0));
		}
		
		double c = s * v;
		double m = v - c;
		double x = c * (1 - Math.abs((h % 2) - 1));

		System.out.println("\n(int)h = " + (int)h + " x = " + x + " c = " + c + " m = " + m + "\n");
		switch ((int)h)
		{
			case 0:
				return ColorRGB((int)((c+m)*255.0),(int)((x+m)*255.0),(int)(m*255.0));
			case 1:
				return ColorRGB((int)((x+m)*255.0),(int)((c+m)*255.0),(int)(m*255.0));
			case 2:
				return ColorRGB((int)(m*255.0),(int)((c+m)*255.0),(int)((x+m)*255.0));
			case 3:
				return ColorRGB((int)(m*255.0),(int)((x+m)*255.0),(int)((c+m)*255.0));
			case 4:
				return ColorRGB((int)((x+m)*255.0),(int)(m*255.0),(int)((c+m)*255.0));
			case 5:
				return ColorRGB((int)((c+m)*255.0),(int)(m*255.0),(int)((x+m)*255.0));
			default:
				return ColorRGB((int)(m*255.0),(int)(m*255.0),(int)(m*255.0));
		}
	}
	
	public static int modify(int rgbColor, double scale, ColorVariable var)
	{
		int hsvColor = RGBtoHSV(rgbColor);
		switch (var)
		{
			case HUE:
			{
				double hue = scale * 360.0;
				while (hue > 360.0)
					hue -= 360.0;
				while (hue < 0)
					hue += 360.0;
				return HSVtoRGB(ColorHSV(hue, getSaturation(hsvColor), getValue(hsvColor)));
			}
			case SATURATION:
			{
				double sat = scale;// * 1.0;
				while (sat > 1.0)
					sat -= 1.0;
				while (sat < 0)
					sat += 1.0;
				return HSVtoRGB(ColorHSV(getHue(hsvColor), sat, getValue(hsvColor)));
			}
			case VALUE:
			{
				double val = scale;// * 1.0;
				while (val > 1.0)
					val -= 1.0;
				while (val < 0)
					val += 1.0;
				return HSVtoRGB(ColorHSV(getHue(hsvColor), getSaturation(hsvColor), val));
			}
			default:
			{
				return 0;
			}
		}
		
	}
}
