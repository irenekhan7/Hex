package com.teamhex.cooler.Storage.Classes;

import com.teamhex.cooler.PaletteView;
import com.teamhex.cooler.R;
import com.teamhex.cooler.R.id;
import com.teamhex.cooler.R.layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
 
@SuppressWarnings("unused")
public class PaletteRecordAdapter extends ArrayAdapter<PaletteRecord> {
	// Simple constructor that takes in the context, ID, and palettes
	// Note that ID will be in the sytle of R.layout.listcolor
	public PaletteRecordAdapter(Context _context,
								int _resourceID,
								PaletteRecord[] _palettes) {
		super(_context, _resourceID, _palettes);
		Log.i("TeamHex", "   Super constructor is done.");
		this.context = _context;
	}
	
	/*private view holder class*/
    private class ViewHolder {
        PaletteView paletteView;
        TextView txtDesc;
    }
    
	// Converts a UI View for a particular position
	public View getView(int position,
						View convertView,
						ViewGroup parent) {
		
		
		 ViewHolder holder = null;
	        PaletteRecord rowItem = getItem(position);
	 
	        LayoutInflater mInflater = (LayoutInflater) context
	                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.listcolor, null);
	            holder = new ViewHolder();
	            // Text description off to the right
	            holder.txtDesc = (TextView) convertView.findViewById(R.id.name);
	            
	            // Color visuals
	            holder.paletteView = (PaletteView) convertView.findViewById(R.id.paletteView1);
	            
	            convertView.setTag(holder);
	        } else
	            holder = (ViewHolder) convertView.getTag();
	 
	        holder.txtDesc.setText(rowItem.getName());
	        holder.paletteView.setColorPalette(rowItem);
	       // holder.paletteView.addColor(Color.BLUE);
	       // holder.paletteView.addColor(Color.YELLOW);
	 
	        return convertView;
	}
	
	Context context;
	
}
