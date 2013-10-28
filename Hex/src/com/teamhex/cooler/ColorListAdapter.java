package com.teamhex.cooler;

import com.teamhex.cooler.R;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;
import com.teamhex.cooler.PaletteView;

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
            holder.paletteView = (PaletteView) convertView.findViewById(R.id.paletteEditView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
 
        holder.txtDesc.setText(rowItem.getName());
        holder.paletteView.setColorScheme(rowItem);
       // holder.schemeView.addColor(Color.BLUE);
       // holder.schemeView.addColor(Color.YELLOW);
 
        return convertView;
    }
}