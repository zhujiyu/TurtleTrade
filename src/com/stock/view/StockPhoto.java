package com.stock.view;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.stock.data.PriceBar;
import com.stock.data.StockData;
import com.stock.index.StockIndex;

public abstract class StockPhoto extends View {

	public StockPhoto(Context context) {
		super(context);
	}

	public StockPhoto(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/** 平移 
	 * 最新一个价格线初始位置，为0时，最新一根价格线出现在图形区域的最右边，
	 * 大于0时，为向左平移的单位数 
	 * */
	public int trans = 0; 
	/**滚动 
	 * 整个股票图形可以向右方滚动 
	 */
	public int scoll = 0;
	/**放缩
	 * 图形中能放进去的价格线数目
	 */
	public int step = 8;
	/**最高价和最低价在图片区域上下方向所占比例
	 */
	public float scale = .8f;

	public boolean volume_visible = true;
	
	public int background = Color.WHITE;
	public boolean bound_visible = true;
	public int bound_color = Color.RED;

	protected int first, count;
	protected double high, low;
	protected double maxVolume;

	public void setBarWidth(int width) {
		this.step = width;
	}
	
	public void tranCandle(int trans) {
		this.trans = trans;
	}
	
	public void scollCandle(int move) {
		this.scoll = move;
	}
	
	private void highLowPrice(PriceBar[] bars) {
		high = bars[first].high;
		low  = bars[first].low;
		maxVolume = bars[first].volume;
		
		for( int i = 1; i < count; i ++ ) {
			PriceBar bar = bars[first + i];
			high = Math.max(bar.get(PriceBar.PRICE_HIGH), high);
			low  = Math.min(bar.get(PriceBar.PRICE_LOW ), low);
			maxVolume = Math.max(bar.volume, maxVolume);
		}
	}
	
	public void display(Canvas canvas, Rect rect, StockData stock) {
		Paint paint = new Paint();
		
		// 上下两个分离的窗口，分割位置
		int divid = Math.round(rect.height() * 0.8f); 
		float b = divid * 0.5f, s = b * scale;
		Rect topWindow = new Rect(rect.left, rect.top + Math.round(b - s), 
				rect.width(), Math.round(2 * s));
		Rect btmWindow = new Rect(rect.left, rect.top + divid + 10, 
				rect.width(), rect.height() - divid - 10);
		
		// 1, 先刷新背景，填充默认背景色
		paint.setColor(background);
		paint.setStyle(Paint.Style.FILL);//设置填满
		canvas.clipRect(rect);
		canvas.drawRect(rect, paint);

		// 2, 绘制上下两个窗口的边框
		if( bound_visible ) {
			paint.setColor(bound_color);
			canvas.drawRect(rect.left + 1, rect.top + 1, rect.width() - 2, 
					divid - 2, paint);
			canvas.drawRect(btmWindow.left + 1, btmWindow.top, btmWindow.width() - 2, 
					btmWindow.height() - 2, paint);
		}
		
		// 3, 计算上下窗口中，显示图形的具体区域
		List<PriceBar> bar_list = stock.getBarSet();
		int width = Math.min(rect.width(), rect.width() + (scoll - trans) * step);
		topWindow.right = topWindow.left + width;
		btmWindow.right = btmWindow.left + width;
		
		// 4, 准备数据
		count = Math.min(topWindow.width() / step + 1, bar_list.size()); 
		first = Math.max(0, scoll - trans);
		PriceBar[] bar_array = new PriceBar[bar_list.size()];
		bar_array = bar_list.toArray(bar_array);
		
		PriceBar[] bars = Arrays.copyOfRange(bar_array, first, first + count);
		highLowPrice(bars);
		
		// 5, 绘制上图中的价格线
//		drawCandle(canvas, paint, bars, topWindow, high, low);
		// 6, 绘制下图中的交易量
//		drawVolume(canvas, paint, bars, btmWindow, maxVolume);
		// 7, 绘制指标，不同指标显示在不同窗口中
//		drawIndex();
		
//		double high = 0, low = 0, maxVolume = 0;
//		Iterator<PriceBar> iter = bar_list.listIterator(first);
//		PriceBar[] bars = new PriceBar[count];
//
//		if( iter.hasNext() ) {
//			bars[0] = iter.next();
//			high = bars[0].high;
//			low = bars[0].low;
//			maxVolume = bars[0].volume;
//		}
//		
//		for( int i = 1; i < count && iter.hasNext(); i ++ ) {
//			bars[i] = iter.next();
//			high = Math.max(bars[i].get(PriceBar.PRICE_HIGH), high);
//			low = Math.min(bars[i].get(PriceBar.PRICE_LOW), low);
//			maxVolume = Math.max(bars[i].volume, maxVolume);
//		}


//		int right = topWindow.width() - step / 2;
//		double  scale1 = - topWindow.height() / (high - low), 
//				scale2 = - btmWindow.height() * 0.8 / maxVolume;
//		double  base1 = topWindow.top + topWindow.height() - low * scale1,
//				base2 = btmWindow.top + btmWindow.height();
//		
//		Iterator<StockIndex> _it = indexes.iterator();
//		while( _it.hasNext() ) {
//			StockIndex index = _it.next();
//			
//			if( index.getWindowIndex() == StockIndex.WINDOW_TOP )
//				index.drawIndex(canvas, first, count, step, right, scale1, base1);
//			else if( index.getWindowIndex() == StockIndex.WINDOW_BOTTOM )
//				index.drawIndex(canvas, first, count, step, right, scale2, base2);
//		}
	}
	
	public void DrawStock() {
		
	}
	
	protected void drawVolume(Canvas _canvas, Paint _paint, PriceBar[] bars, 
			Rect rect, double maxv) {
		int delta = step / 8, bar_width = step * 3 / 4;
		int x = rect.width() - step + delta, bottom = rect.top + rect.height();
		double vhp = rect.height() / maxv * 0.8f;
		
//		((Graphics2D)g).setStroke(new BasicStroke(1));

		_paint.setStyle(Paint.Style.FILL);
		for( int i = 0; i < bars.length; i ++ ) {
			PriceBar curr = bars[i];
			
			if( curr.close > curr.open )
				_paint.setColor(Color.RED);
			else
				_paint.setColor(Color.GREEN);

			int vh =  (int) Math.round( curr.volume * vhp );
			_canvas.drawRect(x, bottom - vh, bar_width, vh, _paint);

			x -= step;
		}
	}
	
}
