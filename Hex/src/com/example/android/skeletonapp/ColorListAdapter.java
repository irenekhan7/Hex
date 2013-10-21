package com.example.android.skeletonapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
 
public class ColorListAdapter extends ArrayAdapter<Palette> {
 
    Context context;
 
    public ColorListAdapter(Context context, int resourceId,
            Palette[] items) {
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
        Palette rowItem = getItem(position);
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listcolor, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.name);
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
}