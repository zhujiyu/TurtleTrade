package com.stock.index;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.stock.data.PriceBar;

import android.graphics.Color;

public class TurtleIndex extends StockIndex {

	public TurtleIndex(int terms, int direct) {
		super(terms, Color.YELLOW);
		this.direct = direct;
		
		if( direct == StockIndex.DIRECT_BUY )
			this.datatype = StockIndex.PRICE_HIGH;
		else
			this.datatype = StockIndex.PRICE_LOW;
	}

	public TurtleIndex(int terms, int direct, int color) {
		super(terms, color);
		this.direct = direct;
		
		if( direct == StockIndex.DIRECT_BUY )
			this.datatype = StockIndex.PRICE_HIGH;
		else
			this.datatype = StockIndex.PRICE_LOW;
	}

	@Override
	public void calcIndex(List<PriceBar> bars)  {
		LinkedList<Double> prices = new LinkedList<Double>();
		int len = bars.size();
		
		for( int i = len - 1; i >= 0; --i ) {
			if( prices.size() >= this.terms )
				prices.poll();
			PriceBar bar = bars.get(i);
			
			double v = bar.get(this.datatype), iv = v;
			Iterator<Double> iter = prices.iterator();
			
			if( this.direct == DIRECT_BUY ) {
				while( iter.hasNext() ) 
					iv = Math.max(iv, iter.next());
			}
			else {
				while( iter.hasNext() ) 
					iv = Math.min(iv, iter.next());
			}
			
			this.add(iv, bar.start.getTime());
			prices.offer(v);
		}
	}

}
