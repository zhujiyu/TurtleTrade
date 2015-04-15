package com.stock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.stock.data.PriceBar;

public abstract class StockPhoto extends View {

	public StockPhoto(Context context) {
		super(context);
	}

	public StockPhoto(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected int step = 8;
	protected float scale = .8f;
	protected int background = Color.BLACK;
	
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
