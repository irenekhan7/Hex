package com.teamhex.cooler.Storage.Classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;

public class HexStorageManager {
	// Constructor
	// Required: String filename, Context context
	// The initial list of PaletteRecords is loaded using the index file
	// Sample usage: 
	// 		new StorageManager(getApplicationContext());
	public HexStorageManager() {
		Log.w("TeamHex", "A StorageManager is being created without a file name or context!");
		Log.w("TeamHex", "Sample usage: new Storagemanager(getApplicationContext());");
	}
	public HexStorageManager(Context _context) { this(_context, "RecordsIndex"); }
	public HexStorageManager(Context _context, String _filename) {
		_filename += ".txt";
		context = _context;
		fileIndexName = _filename;
		
		Log.i("TeamHex", "1. Creating StorageManager using file index " + _filename);

		// Records are stored in a hash table, by name
		records = new HashMap<String, PaletteRecord>();
		record_names = new ArrayList<String>();
		
		// Immediately attempt to load the record names from the file
		// Keep in mind these are viewable in the File Explorer
		// data > data > com.teamhex.cooler > files
		
		// 1. Check if the file exists, and make it if it doesn't
		Log.i("TeamHex", "   Checking if file " + _filename + " exists...");
		if(!fileExists(_filename)) {
			try {
				Log.i("TeamHex", "      File " + _filename + " does not yet exist, attempting to create...");
				OutputStreamWriter osw = getFileWriter(_filename);
				osw.write("");
				osw.close();
				Log.i("TeamHex", "      File " + _filename + " successfully created.");
			}
			catch (IOException e1) {
				Log.e("TeamHex", "      File " + _filename + " could not be created!");
				return;
			}
		}
		else {
			Log.i("TeamHex", "      File " + _filename + " successfully found.");
		}
		Log.i("TeamHex", "   File checks on file " + _filename + " complete.");
		
		// 2. Get a reader to the index file
		Log.i("TeamHex", "2. Creating a reader to the index file");
		BufferedReader br;
		try {
			br = getFileReader(_filename);
		}
		catch (FileNotFoundException e1) {
			Log.i("TeamHex", e1.toString());
			Log.e("TeamHex", "Index file " + _filename + " could not be read: " + e1.toString());
			return;
		}
		
		
		// 3. Read the list of names from the index file
		try {
			Log.i("TeamHex", "3. Loading list of record names.");
			String buffer;
			while((buffer = br.readLine()) != null) {
				Log.i("TeamHex", "   Found record name: " + buffer);
				record_names.add(buffer);
			}
			Log.i("TeamHex", "   Finished finding record names.");
		}
		catch(IOException e1) {
			Log.e("TeamHex", "Error reading list of names from index file (" + _filename + "): " + e1.toString());
			return;
		}
		
		// 4. If there aren't any records, create a default one
		if(record_names.isEmpty()) {
			PaletteRecord empty = new PaletteRecord("America");
			empty.addColor(new ColorRecord("Red", "#ff0000", (float)33.0));
			empty.addColor(new ColorRecord("White", "#ffffff", (float)33.0));
			empty.addColor(new ColorRecord("Blue", "#0000ff", (float)34.0));
			RecordAdd(empty);
		}
		
		Log.i("TeamHex", "Finished making a StorageManager using file index " + _filename);
	}
	
	// Loads the file for the given record 
	public void RecordLoad(String name) {
		// Remember whether the index file should be updated
		Boolean existed = fileExists(name + ".txt");
		
		// Attempt to load the file into the PaletteRecord
		try {
			BufferedReader br = getFileReader(name + ".txt");
			records.put(name, new PaletteRecord(name, br));
			br.close();
			num_loaded += 1;
		}
		catch (IOException e1) {
			Log.e("TeamHex", "Record file " + name + " could not be read: " + e1.toString());
			return;
		}
		
		// Recreate the index file if need be
		if(!existed) remakeFileIndex();
	}
	
	// Loads them all!
	// To do: actually use math..
	public void RecordLoadAll() { RecordLoadNum(9001); }
	
	// Loads the next Num records, limited to max
	public void RecordLoadNum(int num) {
		int i = num_loaded,
			max = Math.min(record_names.size(), num_loaded + num);
		Log.i("TeamHex", "Attempting to load the next " + Integer.toString(num) + " record" + (num == 1 ? "" : "s") + " (out of " + max + ")");
		while(i < max) {
			Log.i("TeamHex", "   Record load " + Integer.toString(i) + ": " + record_names.get(i));
			RecordLoad(record_names.get(i));
			++i;
		}
		num_loaded = max;
	}
	
	// Adds a record to the listing
	public void RecordAdd(PaletteRecord record) { RecordAdd(record, record.getName()); }
	public void RecordAdd(PaletteRecord record, String name) {
		Log.i("TeamHex", "Adding a new record under name " + name);
		
		//Ensures the user cannot save a file with the same name as another file
		//The user could potentially crash the program using this if they have massive amounts of time, and are jerks.
		String originalName = name;
		int i = 2; 
		while(record_names.contains(name)){
			name = originalName + " ("+i+')';
		}
		records.put(name, record);
		record_names.add(name);
		RecordSave(record, name);
		remakeFileIndex();
	}
	
	// Saves the file for the given record
	public void RecordSave(String name) {
		// Make sure that record name exists
		if(!records.containsKey(name)) {
			Log.w("TeamHex", "Attempting to save a record of name " + name + " that doesn't exist.");
			return;
		}
		RecordSave(records.get(name), name);
	}
	public void RecordSave(PaletteRecord record) { RecordSave(record, record.getName()); }
	public void RecordSave(PaletteRecord record, String name) {
		Log.i("TeamHex", "Saving record under name " + name);
		// Attempt to save the PaletteRecord into the file
		try {
			OutputStreamWriter osw = getFileWriter(name + ".txt");
			osw.write(record.getSaveString());
			osw.close();
		}
		catch (IOException e1) {
			Log.e("TeamHex", "Record file " + name + " could not be saved: " + e1.toString());
		}
	}
	

    /* 
     * Utility functions 
     */

	// fileExists
	// Simply checks whether a file exists
	public Boolean fileExists(String filename) {
		return context.getFileStreamPath(filename).exists();
	}
	
	// fileRename
	// Renames an old file to a new one
	public void fileRename(String nameOld, String nameNew) {
		File fileOld = new File(nameOld),
			 fileNew = new File(nameNew);
		fileOld.renameTo(fileNew);
	}
	
    // getFileReader
    // Simply creates a new BufferedReader
    public BufferedReader getFileReader(String filename) throws FileNotFoundException {
    	FileInputStream fis = context.openFileInput(filename);
    	InputStreamReader isr = new InputStreamReader(fis);
    	BufferedReader br = new BufferedReader(isr);
    	return br;
    }
    
    // getFileWriter
    // Simply creates a new OutputStreamWriter
    public OutputStreamWriter getFileWriter(String filename) throws FileNotFoundException {
    	FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
    	OutputStreamWriter osw = new OutputStreamWriter(fos);
    	return osw;
    }
    
    // remakeFileIndex
    // Outputs the names of each PaletteRecord
    public void remakeFileIndex() {
    	Log.i("TeamHex", "Rebuilding the file index under name " + fileIndexName);
    	// Attempt to remake the file index
    	try {
			OutputStreamWriter osw = getFileWriter(fileIndexName);
			for(int i = 0, len = record_names.size(); i < len; ++i) {
				Log.i("TeamHex", "   ...writing name " + record_names.get(i));
				osw.write(record_names.get(i) + "\n");
			}
			osw.close();
		}
    	// If it fails, who knows?
    	catch(IOException e) {
    		Log.w("TeamHex", "Failed to rebuild file index under name " + fileIndexName);
			e.printStackTrace();
		}
    }
    
	// Gets
	public Context getContext()      { return context; }
	public String getFileIndexName() { return fileIndexName; }
	// Note that this will only get the ones that are loaded
	public PaletteRecord[] getPalettesArray() {
		PaletteRecord[] returner = new PaletteRecord[num_loaded];
		int i = 0;
		for(Entry<String, PaletteRecord> e : records.entrySet()) {
			returner[i] = e.getValue();
			++i;
		}
		return returner;
	}
	
	// Sets
	public void setContext(Context context)            { this.context = context; }
	public void setFileIndexName(String fileIndexName) { this.fileIndexName = fileIndexName; }

	// Privates
	private Context context;					// Passed in from main(activity); used for file loading
	private String fileIndexName; 				// The storage file containing the names of the records  
	private int num_loaded; 					// How many records have been loaded so far
	private ArrayList<String> record_names; 	// The ordered list of record names
	private Map<String, PaletteRecord> records; // The stored records, keyed by name
}