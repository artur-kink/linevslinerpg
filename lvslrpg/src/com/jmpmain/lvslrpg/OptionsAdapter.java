package com.jmpmain.lvslrpg;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OptionsAdapter extends ArrayAdapter<String> {

	String[] objectsData;
	
	public OptionsAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);
		objectsData = objects;
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {   // Ordinary view in Spinner, we use android.R.layout.simple_spinner_item
        TextView text = (TextView)super.getView(position, convertView, parent);
        text.setTypeface(MainActivity.pixelFont);
		return text;
    }
	
	@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {   // This view starts when we click the spinner.
        View row = convertView;
        if(row == null)
        {
            LayoutInflater inflater = ((Activity) MainActivity.context).getLayoutInflater();
            row = inflater.inflate(R.layout.spinner_item_layout, parent, false);
        }
        
        ((TextView)row).setTypeface(MainActivity.pixelFont);
        ((TextView)row).setText(objectsData[position]);
        
        return row;
    }
}
