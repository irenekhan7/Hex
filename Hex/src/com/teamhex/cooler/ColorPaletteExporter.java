package com.teamhex.cooler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.teamhex.cooler.Storage.Classes.ColorRecord;
import com.teamhex.cooler.Storage.Classes.HexStorageManager;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;

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
        file.writeChar(0x0001);

        //Block length/name length
        int n = 3;                                          //Assume RGB model for now, could support others later
        
        int nameLength = name.length() + 1;                 //Includes the terminating '\0' char
        int blockLength = 8 + (n * 4) + nameLength * 2;     //ASE uses wide characters (2 bytes each)

        file.writeInt(blockLength);
        file.writeChar(nameLength);
        
        //Color name
        file.writeChars(name);
        file.writeChar(0x0000);

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
        file.writeChar(0x0000);
    }
    
    public static File exportPaletteASE(int[] palette, String[] names, String filename) throws IOException
    {
        DataOutputStream file = new DataOutputStream(new FileOutputStream(filename));
        
        //Signature (ASEF) and version data (v1.00.00, CS3)
        byte[] header = {0x41, 0x53, 0x45, 0x46, 0x00, 0x01, 0x00, 0x00};
        file.write(header);
        
        int blockCount = palette.length;
        file.writeInt(blockCount);      //ASE is big-endian. Fortunately, DataOutputStream is too.
        
        for (int i = 0; i < blockCount; i++)
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
    
    public static void exportLibraryASE(HexStorageManager storage, String filename) throws IOException
    {
    	PaletteRecord[] palettes = storage.getPalettesArray();
    	DataOutputStream file = new DataOutputStream(new FileOutputStream(filename));
        
        //Signature (ASEF) and version data (v1.00.00, CS3)
        byte[] header = {0x41, 0x53, 0x45, 0x46, 0x00, 0x01, 0x00, 0x00};
        file.write(header);
        
        int blockCount = 0;
        int paletteCount = palettes.length;
        for (int i = 0; i < paletteCount; i++)
        {
        	ArrayList<ColorRecord> colors = palettes[i].getColors();
        	blockCount += colors.size();
        }
        
        for (int i = 0; i < paletteCount; i++)
        {
        	String name = palettes[i].getName();
        	ArrayList<ColorRecord> colors = palettes[i].getColors();
        	
        	//Color group start
            file.writeChar(0xC001);
            
            //Group length/name length
            int nameLength = name.length() + 1;             //Includes the terminating '\0' char
            int groupLength = 2 + nameLength * 2;           //ASE uses wide characters (2 bytes each)
            
            file.writeInt(groupLength);
            file.writeChar(nameLength);

            //Group name
            file.writeChars(name);
            file.writeChar(0x0000);
            
            //Group blocks
            for (int j = 0; j < colors.size(); i++)
            {
            	ColorRecord color = colors.get(j);
                writeASEColorBlock(file, color.getValue(), color.getName());
            }
            
            //Color group end
            file.writeChar(0xC002);
            file.writeInt(0x00000000);
        }
    
        file.writeInt(blockCount);
        
        file.close();
    }
}

