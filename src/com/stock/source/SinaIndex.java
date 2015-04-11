package com.stock.source;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SinaIndex extends DataSource {
	public static final String INDEX_URL = "http://hq.sinajs.cn/list=s_";

	public double CurrentPoint;
	public double Range;
	public double Ratio;
	public double volume;
	
	public SinaIndex(String _code, int _market) {
		super(_code, _market);
	}

	public double getNewPrice() {
		try {
			String url = INDEX_URL + (market == MARKET_SHANGHAI ? "sh" : "sz") + code;
			String text = WebData(url);
			this.Parse(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return CurrentPoint;
	}

	// var hq_str_s_sh000001="上证指数,3691.096,9.001,0.24,4089451,50929846";
	public void Parse(String text) {
		Pattern sname  = Pattern.compile("[\\w\\s]+=\"([^,]+),");
		Pattern sprice = Pattern.compile(",(\\d+.\\d+)");
		Pattern svolume = Pattern.compile(",(\\d+)");
		
		Matcher mCells = sname.matcher(text);
		if( mCells.find() ) 
			name = mCells.group(1);
		
		mCells = sprice.matcher(text);
		if( mCells.find() ) 
			CurrentPoint = Double.parseDouble( mCells.group(1) );// 当前点数
		if( mCells.find() ) 
			Range = Double.parseDouble( mCells.group(1) );// 涨跌
		if( mCells.find() ) 
			Ratio = Double.parseDouble( mCells.group(1) ); // 涨跌率

		mCells = svolume.matcher(text);
		// 成交量（手），
		if( mCells.find() ) 
			mCells.group(); 
		// 成交额（万元）；
		if( mCells.find() )
			volume = Double.parseDouble( mCells.group() );
	}

}
