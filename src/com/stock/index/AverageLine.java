package com.stock.index;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

import com.stock.data.PriceBar;

import android.graphics.Color;

public class AverageLine extends StockIndex {

	public AverageLine(int terms) {
		super(terms, Color.YELLOW);
	}

	public AverageLine(int terms, int color) {
		super(terms, color);
	}

	@Override
	public void calcIndex(List<PriceBar> bars) {
		Queue<Double> prices = new LinkedList<Double>();
		double sump = 0;
		ListIterator<PriceBar> iter = bars.listIterator(bars.size());
		
		while( iter.hasPrevious() ) {
			if( prices.size() >= this.terms ) 
				sump -= prices.poll();

			PriceBar bar = iter.previous();
			double v = bar.get(datatype);
			prices.offer(v);
			
			sump += v;
			this.add(sump / prices.size(), bar.start.getTime());
		}
	}

}
