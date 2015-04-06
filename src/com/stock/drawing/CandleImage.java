package com.stock.drawing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.stock.data.PriceBar;
import com.stock.data.StockData;
import com.stock.index.StockIndex;

public class CandleImage {

	private List<PriceBar> bar_list;
	private List<StockIndex> indexes = new ArrayList<StockIndex>();
	
	private int step = 8;
	private int trans = 0;  ///< transform the initialize position of candle image to left 
	private int scoll = 0;  ///< scoll screen
	private float scale = .8f;
	private int background = Color.BLACK;
	
	public CandleImage(StockData data) {
		this.bar_list = data.getBarSet();
	}
	
	public void AddIndex(StockIndex index) {
		index.calcIndex(bar_list);
		indexes.add(index);
	}

	public void setBarWidth(int width) {
		this.step = width;
	}
	
	public void tranCandle(int trans) {
		this.trans = trans;
	}
	
	public void scollCandle(int move) {
		this.scoll = move;
	}
	
	public void Save(String file, int width, int height) {
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		
		if( bar_list.size() > 0 ) {
			Canvas g = new Canvas(bmp);
			this.display(g, new Rect(0, 0, width, height));
			g.save( Canvas.ALL_SAVE_FLAG );//保存
			g.restore();//存储
//			g.dispose();
		}
		
//		try {
//			
//			ImageIO.write(bmp, "jpeg", new File(file));
//		}
//		catch( IOException ex ) {
//			ex.printStackTrace();
//		}
	}
	
	public void display(Canvas g, Rect rect) {
		int divid = Math.round(rect.height() * 0.8f);
		float b = divid * 0.5f, s = b * scale;
		Rect topWindow = new Rect(rect.left, rect.top + Math.round(b - s), rect.width(), Math.round(2 * s));
		Rect btmWindow = new Rect(rect.left, rect.top + divid + 10, rect.width(), rect.height() - divid - 10);
		
		Paint p = new Paint();
		p.setColor(background);
		p.setStyle(Paint.Style.FILL);//设置填满 
		
		g.clipRect(rect);
		g.drawRect(rect, p);

		p.setColor(Color.RED);
		g.drawRect(rect.left + 1, rect.top + 1, rect.width() - 2, divid - 2, p);
		g.drawRect(btmWindow.left + 1, btmWindow.top, btmWindow.width() - 2, 
				btmWindow.height() - 2, p);

		int width = Math.min(rect.width(), rect.width() + (scoll - trans) * step);
		topWindow.right = topWindow.left + width;
		btmWindow.right = btmWindow.left + width;
		
		int count = Math.min(topWindow.width() / step + 1, bar_list.size()), 
				first = Math.max(0, scoll - trans);
		double high = 0, low = 0, maxVolume = 0;
		
		Iterator<PriceBar> iter = bar_list.listIterator(first);
		PriceBar[] bars = new PriceBar[count];

		if( iter.hasNext() ) {
			bars[0] = iter.next();
			high = bars[0].high;
			low = bars[0].low;
			maxVolume = bars[0].volume;
		}
		
		for( int i = 1; i < count && iter.hasNext(); i ++ ) {
			bars[i] = iter.next();
			high = Math.max(bars[i].get(PriceBar.PRICE_HIGH), high);
			low = Math.min(bars[i].get(PriceBar.PRICE_LOW), low);
			maxVolume = Math.max(bars[i].volume, maxVolume);
		}

		drawVolume(g, p, bars, btmWindow, maxVolume);
		drawCandle(g, p, bars, topWindow, high, low);

		int right = topWindow.width() - step / 2;
		double  scale1 = - topWindow.height() / (high - low), 
				scale2 = - btmWindow.height() * 0.8 / maxVolume;
		double  base1 = topWindow.top + topWindow.height() - low * scale1,
				base2 = btmWindow.top + btmWindow.height();
		
		Iterator<StockIndex> _it = indexes.iterator();
		while( _it.hasNext() ) {
			StockIndex index = _it.next();
			
			if( index.getWindowIndex() == StockIndex.WINDOW_TOP )
				index.drawIndex(g, first, count, step, right, scale1, base1);
			else if( index.getWindowIndex() == StockIndex.WINDOW_BOTTOM )
				index.drawIndex(g, first, count, step, right, scale2, base2);
		}
	}
	
	private void drawCandle(Canvas g, Paint p, PriceBar[] bars, Rect rect, double high, double low) {
		int delta = step / 8, bar_width = step * 3 / 4;
		int x = rect.width() - step + delta, height = rect.top + rect.height(),
				middle = rect.width() - step / 2;
		double bhp = rect.height() / (high - low);
		
//		Graphics2D g2d = (Graphics2D)g;
//		g2d.setStroke(new BasicStroke(2));
		
		for( int i = 0; i < bars.length; i ++ ) {
			PriceBar curr = bars[i];
			
			if( curr.get(PriceBar.PRICE_CLOSE) < curr.get(PriceBar.PRICE_OPEN) ) {
				int y = (int) Math.round( (curr.open - low) * bhp );
				int h = (int) Math.round( (curr.close - low) * bhp );

				p.setColor(Color.GREEN);
				p.setStyle(Paint.Style.FILL);
				g.drawRect(x, height - y, bar_width, y - h, p);
			}
			else {
				int y = (int) Math.round( (curr.close - low) * bhp );
				int h = (int) Math.round( (curr.open - low) * bhp );
				
				p.setColor(Color.RED);
				p.setStyle(Paint.Style.STROKE);
				g.drawRect(x, height - y, bar_width, y - h, p);
			}
			
			int y1 = (int) Math.round( (curr.get(PriceBar.PRICE_HIGH) - low) * bhp );
			int y2 = (int) Math.round( (curr.get(PriceBar.PRICE_LOW) - low) * bhp );
			p.setStyle(Paint.Style.STROKE);
			g.drawLine(middle, height - y1, middle, height - y2, p);
			
			x -= step;
			middle -= step;
		}
	}
	
	private void drawVolume(Canvas g, Paint p, PriceBar[] bars, Rect rect, double maxv) {
		int delta = step / 8, bar_width = step * 3 / 4;
		int x = rect.width() - step + delta, bottom = rect.top + rect.height();
		double vhp = rect.height() / maxv * 0.8f;
		
//		((Graphics2D)g).setStroke(new BasicStroke(1));
		
		for( int i = 0; i < bars.length; i ++ ) {
			PriceBar curr = bars[i];
			
			if( curr.close > curr.open )
				p.setColor(Color.RED);
			else
				p.setColor(Color.GREEN);

			int vh =  (int) Math.round( curr.volume * vhp );
			p.setStyle(Paint.Style.FILL);
			g.drawRect(x, bottom - vh, bar_width, vh, p);

			x -= step;
		}
	}
	
}
