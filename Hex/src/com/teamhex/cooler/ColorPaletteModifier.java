package com.teamhex.cooler;

/**
 * public class <b>ColorPaletteModifier</b>
 * <br><br><br>
 * <b>Class Overview</b>
 * <br><br>
 * {@code ColorPaletteModifier} contains static methods for accessing and modifying colors based
 * on their HSV values.
 * <br><br>
 * HSV colors are treated as {@code int} values. The 31st bit in a HSV color is a NaN bit. If it
 * is set to 1, then the hue is not a number. Otherwise, the hue is equal to the value of bits
 * 16-30 multiplied by 360/32768, or 0.01098632812, to fit within the range of [0, 360). Bits 8-15 denote the
 * saturation, and bits 0-7 denote the value. These correspond to values in the range [0, 255), 
 * which are then rescaled to fit in the range of [0, 1). This representation is not as precise
 * as those in {@link java.awt.Color} and {@link android.graphics.Color}, which return arrays of
 * floating-point values, but is more convenient to use and manipulate.
 */
public class ColorPaletteModifier 
{
	public enum ColorVariable
	{
		HUE,
		SATURATION,
		VALUE,
	}
	
	public static int modify(int rgbColor, double scale, ColorVariable var)
	{
		int hsvColor = ColorConverter.RGBtoHSV(rgbColor);
		switch (var)
		{
			case HUE:
			{
				double hue = scale * 360.0;
				while (hue > 360.0)
					hue -= 360.0;
				while (hue < 0)
					hue += 360.0;
				return ColorConverter.HSVtoRGB(ColorConverter.ColorHSV(hue, ColorConverter.getSaturation(hsvColor), ColorConverter.getValue(hsvColor)));
			}
			case SATURATION:
			{
				double sat = scale;// * 1.0;
				while (sat > 1.0)
					sat -= 1.0;
				while (sat < 0)
					sat += 1.0;
				return ColorConverter.HSVtoRGB(ColorConverter.ColorHSV(ColorConverter.getHue(hsvColor), sat, ColorConverter.getValue(hsvColor)));
			}
			case VALUE:
			{
				double val = scale;// * 1.0;
				while (val > 1.0)
					val -= 1.0;
				while (val < 0)
					val += 1.0;
				return ColorConverter.HSVtoRGB(ColorConverter.ColorHSV(ColorConverter.getHue(hsvColor), ColorConverter.getSaturation(hsvColor), val));
			}
			default:
			{
				return 0;
			}
		}
		
	}
}
