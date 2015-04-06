package com.stock.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.ListIterator;

import com.stock.data.PriceBar;
import com.stock.data.StockData;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public abstract class StockIndex {

	public static final int DIRECT_BUY = 0;
	public static final int DIRECT_SELL = 1;
	
	public static final int WINDOW_TOP = 2;
	public static final int WINDOW_BOTTOM = 3;
	public static final int WINDOW_MIDDLE = 4;

	public static final int PRICE_OPEN  = StockData.PRICE_OPEN;
	public static final int PRICE_CLOSE = StockData.PRICE_CLOSE;
	public static final int PRICE_HIGH  = StockData.PRICE_HIGH;
	public static final int PRICE_LOW   = StockData.PRICE_LOW;
	public static final int PRICE_MIDDLE = StockData.PRICE_MIDDLE;   ///< (HIGH + LOW) / 2
	public static final int PRICE_AVERAGE = StockData.PRICE_AVERAGE; ///< (HIGH + LOW + OPEN + CLOSE) / 4
	public static final int PRICE_VOLUME = StockData.PRICE_VOLUME;
	
	protected List<Double> datas = new ArrayList<Double>();
	protected int terms = 10;
	protected int direct = DIRECT_BUY;
	
	protected int shift = 0;  ///< transform index to right
	protected int color = Color.YELLOW;
	protected int window_index = WINDOW_TOP;
	protected int datatype = PRICE_AVERAGE;
	
	public StockIndex(int terms, int color) {
		this.terms = terms;
		this.color = color;
	}

	public double get(int idx) {
		if( datas != null ) {
			idx += shift;
			if( idx >= 0 && idx < datas.size() ) {
				return datas.get(datas.size() - idx - 1);
			}
		}
		return 0;
	}

	public void add(double value, Date time) {
//		indexes.put(time, value);
		datas.add(value);
	}
	
	public void calcIndex(StockData stock) {
		calcIndex(stock.getBarSet());
	}
	
	abstract public void calcIndex(List<PriceBar> bars);
	
	public int getWindowIndex() {
		return window_index;
	}
	
	public void setDirect(int direct) {
		this.direct = direct;
	}
	
	public void setShift(int shift) {
		this.shift = shift;
	}
	
	public void setColor(int c) {
		this.color = c;
	}
	
	public void setWindow(int windowIndex) {
		this.window_index = windowIndex;
	}
	
	public void setDataType(int type) {
		this.datatype = type;
	}
	
	public void drawIndex(Canvas g, int first, int count, int step, int right, 
			double scale, double baseValue) { 
		
		if( first < 0 || count > datas.size() )
			return;
		Paint p = new Paint();
		p.setColor(color);
		
		count = Math.min(datas.size(), count + shift);
		right += this.shift * step; // 不管是否越界，向右移动tran个单位，这样下标就不用移动了
		
		ListIterator<Double> iter = datas.listIterator(datas.size() - first);
		double prev = 0, curr = 0;
		if( iter.hasPrevious() && count -- > 0 ) {
			prev = iter.previous();
			right -= step;
		}
		
		while ( iter.hasPrevious() && count -- > 0 ) {
			curr = iter.previous();
			g.drawLine(right + step, (int)Math.round( prev * scale + baseValue), 
					right, (int)Math.round( curr * scale + baseValue), p);
			prev = curr;
			right -= step;
		}
	}

}
