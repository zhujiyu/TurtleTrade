package com.stock.view;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class StockRowAdapter extends SimpleAdapter {
	private int[] colors = {0x20FF0000, 0x200000FF};
	
	public StockRowAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        int colorPos = position % colors.length;
        view.setBackgroundColor(colors[colorPos]);
//        if( colorPos == 1 )
//        	view.setBackgroundColor(Color.argb(250, 255, 255, 255)); //颜色设置
//        else
//            view.setBackgroundColor(Color.argb(255, 224, 243, 250));//颜色设置
        return view; 
	}
}
