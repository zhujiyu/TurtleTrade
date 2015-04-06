package com.stock.index;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.stock.data.PriceBar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class EnclosureLine extends AverageLine {

	protected List< Double > diffs = new ArrayList<Double>();
	protected List< Double > upline = new ArrayList<Double>();
	protected List< Double > dnline = new ArrayList<Double>();
	
	protected double rate = 0.01;
	protected int upcolor = Color.BLUE;
	protected int dncolor = Color.BLUE;
	
	public EnclosureLine(int terms, int shift, double rate) {
		super(terms, Color.RED);
		this.shift = shift;
		this.rate = rate;
	}
	
	public EnclosureLine(int terms, int color) {
		super(terms, color);
	}

	public void setShift(int s) {
		this.shift = s;
	}

	@Override
	public void drawIndex(Canvas g, int first, int count, int step, int right, 
			double scale, double baseValue) { 
		super.drawIndex(g, first, count, step, right, scale, baseValue);
		
		if( first < 0 || count > upline.size() )
			return;
		Paint p = new Paint();
		p.setColor(upcolor);
		
		draw(g, p, upline, first, count, step, right, scale, baseValue);
		draw(g, p, dnline, first, count, step, right, scale, baseValue);
	}
	
	private void draw(Canvas g, Paint p, 
			List<Double> index, int first, int count, int step, int right, double scale, double baseValue) {
		ListIterator<Double> iter = index.listIterator(index.size() - first);
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

	@Override
	public void calcIndex(List<PriceBar> bars) {
		super.calcIndex(bars);

		double r1 = rate, r2 = 1 - r1;
		int start = 0;
		for( int i = start + shift; i > 0; i -- )
			diffs.add(0.0);
		
		ListIterator<PriceBar> iter = bars.listIterator(bars.size() - start - shift);
		PriceBar bar = iter.previous();
		
		double ma = datas.get(start++);
		double typic = (bar.high + bar.low + bar.close) / 3;
		double diff = Math.abs(typic - ma);
		
		diffs.add(diff);
		upline.add(ma + diff);
		dnline.add(ma - diff);
		
		while( iter.hasPrevious() ) {
			bar = iter.previous();
			typic = (bar.high + bar.low + bar.close) / 3;
			ma = datas.get(start);
			diff = Math.abs(typic - ma) * r1 + diff * r2;
			upline.add(ma + diff);
			dnline.add(ma - diff);
			start++;
		}
	}

}
