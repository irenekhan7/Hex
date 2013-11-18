package com.teamhex.cooler;

public class ColorPaletteModifier 
{
	private static int ColorRGB(int r, int g, int b)
	{
		return ((0xFF << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
	}
	
	private static int ColorHSV(double h, double s, double v)
	{
		int flag = (Double.isNaN(h) ? 1 : 0);
		
		int v1 = (Double.isNaN(h) ? 0 : (int)(h * 91.0222222));
		int v2 = (int)(s * 255);
		int v3 = (int)(v * 255);
		
		return (((flag & 0x1) << 31) | (v1 & 0xFFFF) << 16) | ((v2 & 0xFF) << 8) | (v3 & 0xFF);
	}
	
	private static double getHue(int hsvColor)
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
	
	private static double getSaturation(int hsvColor)
	{
		return ((double)((hsvColor >> 8) & 0xFF))/255.0;
	}
	
	private static double getValue(int hsvColor)
	{
		return ((double)(hsvColor & 0xFF))/255.0;
	}
	
	private static int RGBtoHSV(int rgbColor)
	{
		int r = ((rgbColor >> 16) & 0xFF);
		int g = ((rgbColor >> 8) & 0xFF); 
		int b = (rgbColor & 0xFF);
		
		int max = (r > g) ? ((r > b) ? r : b) : g;
		int min = (r < g) ? ((r < b) ? r : b) : g;
		
		double v = max;
		double d = max - min;
		
		if (max == 0)
		{
			return ColorHSV(Double.NaN,0.0,v);
		}
		else
		{
			double h = Double.NaN;
			double s = (max == 0) ? 0 : (max - min)/max;
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
	
	private static int HSVtoRGB(int hsvColor)
	{
		double h = getHue(hsvColor)/60.0;
		double s = getSaturation(hsvColor);
		double v = getValue(hsvColor);
		
		if (s == 0.0)
		{
			return ColorRGB(0, 0, 0);
		}
		
		int h2 = (int)h;
		double rem = h - (double)h2;
		
		double a = v * (1.0 - s);
		double b = v * (1.0 - (s * rem));
		double c = v * (1.0 - (s * (1.0 - rem)));
		
		switch (h2)
		{
			case 0:
				return ColorRGB((int)v,(int)c,(int)a);
			case 1:
				return ColorRGB((int)b,(int)v,(int)a);
			case 2:
				return ColorRGB((int)a,(int)v,(int)c);
			case 3:
				return ColorRGB((int)a,(int)b,(int)v);
			case 4:
				return ColorRGB((int)c,(int)a,(int)v);
			case 5:
				return ColorRGB((int)v,(int)a,(int)b);
			default:
				return ColorRGB(0,0,0);
		}
	}
	
	public static int shiftHue(int rgbColor, double offset)
	{
		int hsvColor = RGBtoHSV(rgbColor);
		double hue = getHue(hsvColor);
		
		hue = (1.0 + offset) * hue;
		while (hue < 0)
		{
			hue += 360.0;
		}
		while (hue > 360)
		{
			hue -= 360.0;
		}
		
		return HSVtoRGB(ColorHSV(hue, getSaturation(hsvColor), getValue(hsvColor)));
	}
	
	public static int shiftSaturation(int rgbColor, double offset)
	{
		int hsvColor = RGBtoHSV(rgbColor);
		double sat = getSaturation(hsvColor);
		
		sat = (1.0 + offset) * sat;
		sat = ((sat > 1.0) ? 1.0 : ((sat < 0.0) ? 0 : sat));
		
		return HSVtoRGB(ColorHSV(getHue(hsvColor), sat, getValue(hsvColor)));
	}
	
	public static int shiftValue(int rgbColor, double offset)
	{
		int hsvColor = RGBtoHSV(rgbColor);
		double val = getValue(hsvColor);
		
		val = (1.0 + offset) * val;
		val = ((val > 1.0) ? 1.0 : ((val < 0.0) ? 0 : val));
		
		return HSVtoRGB(ColorHSV(getHue(hsvColor), getSaturation(hsvColor), val));
	}
}
