package com.teamhex.cooler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ColorPaletteExporter 
{
    //ASE Color Block Format
    //=======================================================
    //NAME:             SIZE            DESCRIPTION
    //Start             (2 bytes)       Color block start (always 00 01)
    //BlockLength       (4 bytes)       Length of block (not including BlockLength or Start)
    //NameLength        (2 bytes)       Length of name in UTF-16 chars
    //ColorName         (variable)      UTF-16 string containing the name of the color
    //Format            (4 bytes)       Either "CMYK", "RGB " (note the space), "LAB ", or "Gray"
    //ColorData         (n*4 bytes)     If CMYK, then n = 4. If RGB or LAB, n = 3. If Gray, n = 1.
    //ColorType         (2 bytes)       0 = Global, 1 = Spot
    private static void writeASEColorBlock(DataOutputStream file, int color, String name) throws IOException
    {
        //Color entry start
        file.write(0x00);
        file.write(0x01);

        //Block length/name length
        int n = 3;                                          //Assume RGB model for now, could support others later
        
        int nameLength = name.length() + 1;                 //Includes the terminating '\0' char
        int blockLength = 8 + (n * 4) + nameLength * 2;     //ASE uses wide characters (2 bytes each)

        file.writeInt(blockLength);
        file.writeChar(nameLength);
        
        //Color name
        file.writeChars(name);
        file.write(0x00);
        file.write(0x00);

        //Color format (only RGB for now)
        byte[] format = {0x52, 0x47, 0x42, 0x20};           //"RGB "
        //byte[] format = {0x43, 0x4D, 0x59, 0x4B};         //"CMYK"
        //byte[] format = {0x4C, 0x41, 0x42, 0x20};         //"LAB "
        //byte[] format = {0x47, 0x72, 0x61, 0x79};         //"Gray"
        
        file.write(format);
        
        //Color data
        float[] colorData = {((float)((color >> 16) & 0xFF))/(float)255.0,       //ASE stores RGB values using single-precision,
                             ((float)((color >> 8) & 0xFF))/(float)255.0,        //IEEE-754 floating point values (which takes
                             ((float)(color & 0xFF))/(float)255.0};              //up twice as much space as necessary)
        
        for (int i = 0; i < n; i++)
        {
            file.writeFloat(colorData[i]);
        }
        
        //Color type (only global = 0 for now)
        file.write(0x00);
        file.write(0x00);
    }
    
    public static File exportASE(int[] palette, String[] names, String filename) throws IOException
    {
        DataOutputStream file = new DataOutputStream(new FileOutputStream(filename));
        
        //Signature (ASEF) and version data (v1.00.00, CS3)
        byte[] header = {0x41, 0x53, 0x45, 0x46, 0x00, 0x01, 0x00, 0x00};
        
        file.write(header);
        
        int paletteSize = palette.length;
    
        file.writeInt(paletteSize);      //ASE is big-endian. Fortunately, DataOutputStream is too.
        
        for (int i = 0; i < paletteSize; i++)
        {
            writeASEColorBlock(file, palette[i], names[i]);
        }
        
        file.close();
        
        File output = new File(filename);
        if (output.exists())
        {
            return output;
        }
        return null;
    }
}

