package com.stock.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stock.data.PriceBar;
import com.stock.data.StockData;
import com.stock.source.DataSource;
import com.stock.turtle.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

public class BrokenLineView extends StockPhoto {

	public List<StockData> stocks = new ArrayList<StockData>();
	
	public BrokenLineView(Context context) {
		super(context);
		stocks.add(new StockData("600036", DataSource.MARKET_SHANGHAI));
	}
	
	public BrokenLineView(Context context, AttributeSet attrs){
		super(context, attrs);
		
        //TypedArray是一个用来存放由context.obtainStyledAttributes获得的属性的数组   
        //在使用完成后，一定要调用recycle方法   
        //属性的名称是styleable中的名称+“_”+属性名称   
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BrokenLineView);   
        String shstocks = array.getString(R.styleable.BrokenLineView_sh_stocks);
        String szstocks = array.getString(R.styleable.BrokenLineView_sz_stocks);
        array.recycle(); //一定要调用，否则这次的设定会对下次的使用造成影响
        
        Pattern pattern = Pattern.compile("([\\d]{6})");
        if( shstocks != null && shstocks.length() > 0 ) {
	        Matcher mCells = pattern.matcher(shstocks);
			while( mCells.find() ) {
				StockData stock = new StockData(mCells.group(), DataSource.MARKET_SHANGHAI);
				stocks.add(stock);
			}
        }

        if( szstocks != null && szstocks.length() > 0 ) {
        	Matcher mCells = pattern.matcher(szstocks);
			while( mCells.find() ) {
				StockData stock = new StockData(mCells.group(), DataSource.MARKET_SHENZHEN);
				stocks.add(stock);
			}
        }
        
        if( stocks.size() == 0 )
    		stocks.add(new StockData("600036", DataSource.MARKET_SHANGHAI));
	}
	
	protected void draw(Canvas canvas, Paint p, PriceBar[] bars, Rect rect) {
		double bhp = rect.height() / (high - low);
		int height = rect.top + rect.height(), middle = rect.width() - step / 2;
		PriceBar prev = bars[0];
		
		for( int i = 1; i < bars.length; i ++ ) {
			PriceBar curr = bars[i];
			int y1 = (int) Math.round( (prev.close - low) * bhp );
			int y2 = (int) Math.round( (curr.close - low) * bhp );
			canvas.drawLine(middle, height - y1, middle, height - y2, p);
			middle -= step;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Iterator<StockData> iter = stocks.iterator();
		while( iter.hasNext() ) {
			StockData stock = iter.next();
			if( stock.getBarSet().size() == 0 )
				continue;
//			DrawBrokenLine(canvas, stock);
		}
	}
}
