package com.teamhex.colorbird;

import com.teamhex.colorbird.PaletteView;
import com.teamhex.colorbird.R;
import com.teamhex.colorbird.Storage.Classes.PaletteRecord;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
 
public class ColorListAdapter extends ArrayAdapter<PaletteRecord> {
 
    Context context;
 
    public ColorListAdapter(Context context, int resourceId,
            PaletteRecord[] items) {
        super(context, resourceId, items);
        this.context = context;
    }
 
    /*private view holder class*/
    private class ViewHolder {
        PaletteView paletteView;
        TextView txtDesc;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        PaletteRecord rowItem = getItem(position);
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listcolor, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.name);
            holder.paletteView = (PaletteView) convertView.findViewById(R.id.palette_view);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
 
        holder.txtDesc.setText(rowItem.getName());
        holder.paletteView.setPalette(rowItem);
        holder.paletteView.isListItem = true;
       // holder.schemeView.addColor(Color.BLUE);
       // holder.schemeView.addColor(Color.YELLOW);
 
        return convertView;
    }
}