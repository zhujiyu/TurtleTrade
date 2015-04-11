package com.stock.data;

import java.io.IOException;
import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Locale;

import com.stock.source.DataSource;
import com.stock.source.YahooStock;

public class StockData extends PriceBar {
	public static final int CODE   = 1;
	public static final int MARKET = 2;

	private List<PriceBar> bar_list = new ArrayList<PriceBar>();
	private String code = "601857";
	private int market = DataSource.MARKET_SHANGHAI;
	
	public StockData(String code, int market) {
		super();
		this.code = code;
		this.market = market;
	}

	public List<PriceBar> getBarSet() {
		return bar_list;
	}
	
	public int size() {
		return bar_list.size();
	}
	
	public void append(Calendar _end, YahooStock yahoo) 
			throws IOException {
		String cachefile = yahoo.getCacheFile();
		String csvfile = yahoo.getStockFile();
		
		List<List<String>> data = yahoo.get(_end, cachefile);
		if( data == null || data.size() < 2 )
			return;
		
		ArrayList<PriceBar> bars = new ArrayList<PriceBar>();
		this.Parse(data, bars);
		if( bars.size() == 0 )
			return;
		
		Calendar _nstart = bars.get(bars.size()-1).start;
		Calendar _oend_   = bar_list.get(0).start;
		
		if( _oend_.before(_nstart) ) {
			Iterator<PriceBar> iter = bar_list.iterator();
			while( iter.hasNext() ) 
				bars.add(iter.next());
			bar_list = bars;
			
			data = Format(bar_list);
			yahoo.SaveCSVFile(data, csvfile);
		}
	}
	
	public void load() {
		Calendar yesterday = Calendar.getInstance();
		Calendar yearago   = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);
		yearago.add(Calendar.YEAR, -1);
		Calendar _stt = yearago;

		YahooStock yahoo = new YahooStock(code, market);
		String csvfile = yahoo.getStockFile();
		List<List<String>> data = null;
		
		// first read local file.
		try {
			data = yahoo.LocalData(csvfile);
			if( data != null ) {
				this.Parse(data, bar_list);
				if( bar_list != null && bar_list.size() > 0 ) {
					_stt = bar_list.get(bar_list.size() - 1).start;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// download from yahoo api
		// if there is't local data or start date after a year age
		try {
			if( data == null || _stt.after(yearago) ) {
				data = yahoo.get(yearago, csvfile);
				if( data != null ) {
					this.Parse(data, bar_list);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// download data from yahoo api
		// if there is't recent some days data.
		try {
			if( bar_list != null && bar_list.size() > 0 ) {
				Calendar _end = bar_list.get(0).start;
				if( _end.before(yesterday) ) { 
					_end.add(Calendar.DATE, 1);
					append(_end, yahoo);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// load today's data from sina api.
//		SinaStock sina = new SinaStock(code, market);
//		PriceBar bar = sina.getNewPrice();
//		bar_list.add(bar);
		
		this.Statistic();
	}
	
	public List<List<String>> Format(List<PriceBar> bars) {
		List<List<String>> data = new ArrayList<List<String>>();
		List<String> head = new ArrayList<String>();
		int[] fields = {START, PRICE_OPEN, PRICE_CLOSE, PRICE_HIGH, PRICE_LOW, VOLUME};
		String[] heads = {"DATE", "OPEN", "CLOSE", "HIGH", "LOW", "VOLUME"};
		
		for( int i = 0; i < fields.length; i ++ ) {
			head.add(heads[i]);
		}
		data.add(head);
		
		Iterator<PriceBar> iter = bars.iterator();
		while( iter.hasNext() ) {
			PriceBar bar = iter.next();
			List<String> row = new ArrayList<String>();

			String date = DataSource.DATE_FORMAT.format(bar.start.getTime());
			row.add(date);
			
			for( int i = 1; i < fields.length; i ++ ) {
				double d = bar.get(fields[i]);
				String s = Double.toString(d);
				row.add(s);
			}
			
			data.add(row);
		}
		
		return data;
	}
	
	protected void Parse(List<List<String>> data, List<PriceBar> bars) {
		int[] fields = null;
		Iterator<List<String>> rowIter = data.iterator();
		
		if( rowIter.hasNext() ) {
			List<String> rec = rowIter.next();
			fields = new int[rec.size()];
			Iterator<String> cellIter = rec.iterator();
			
			for(int i = 0; cellIter.hasNext(); i ++ ) {
				String cell = cellIter.next().toUpperCase(Locale.CHINA);
				
				if( cell.equals("OPEN") )
					fields[i] = PriceBar.PRICE_OPEN;
				else if( cell.equals("CLOSE") )
					fields[i] = PriceBar.PRICE_CLOSE;
				else if( cell.equals("HIGH") )
					fields[i] = PriceBar.PRICE_HIGH;
				else if( cell.equals("LOW") )
					fields[i] = PriceBar.PRICE_LOW;
				else if( cell.equals("VOLUME") )
					fields[i] = PriceBar.VOLUME;
				else if( cell.equals("DATE") )
					fields[i] = PriceBar.START;
			}
		} // end of if
		
		while( rowIter.hasNext() ) {
			PriceBar bar = new PriceBar(4*60);
			List<String> rec = rowIter.next();
			Iterator<String> cellIter = rec.iterator();
			
			for(int i = 0; cellIter.hasNext(); i ++ ) {
				String cell = cellIter.next();
				try {
					if( fields[i] == PriceBar.START ) {
						Date date = DataSource.DATE_FORMAT.parse(cell);
//						bar.start = Calendar.getInstance();
						bar.start.setTime(date);
					}
//					else if( fields[i] == PriceBar.VOLUME ) {
//						bar.set(fields[i], Double.parseDouble(cell));
//					}
					else {
						bar.set(fields[i], Double.parseDouble(cell));
					}
				}
				catch( ParseException ex ) {
					ex.printStackTrace();
				}
				catch( Exception ex ) {
					ex.printStackTrace();
				}
			}

			bars.add(bar);
		} // end of while
		
//		this.Statistic();
	}// end of parse
	
	protected void Statistic() {
		Iterator<PriceBar> iter = bar_list.iterator();
		PriceBar bar = null;
		
		if( iter.hasNext() ) {
			bar = iter.next();
			this.close = bar.close;
			this.high = bar.high;
			this.low = bar.low;
			this.volume = bar.volume;
		}
		
		while( iter.hasNext() ) {
			bar = iter.next();
			this.high = this.high > bar.high ? this.high : bar.high;
			this.low = this.low < bar.low ? this.low : bar.low;
			this.volume += bar.volume;
		}
		
		if( bar != null ) {
			this.open  = bar. open;
			this.start = bar.start;
			this.minutes = bar.minutes * bar_list.size();
		}
	}
	
	/**
	 * 取价格对数
	 */
	public void log() {
		Iterator< PriceBar > iter = bar_list.iterator();
		PriceBar bar = null;
		
		while( iter.hasNext() ) {
			bar = iter.next();
			bar.high  = Math.log(bar.high );
			bar.low   = Math.log(bar.low  );
			bar.open  = Math.log(bar.open );
			bar.close = Math.log(bar.close);
		}

		this.high  = Math.log(this.high );
		this.low   = Math.log(this.low  );
		this.open  = Math.log(this.open );
		this.close = Math.log(this.close);
	}
	
	private PriceBar NewWeekBar(PriceBar dbar) {
		PriceBar wbar = new PriceBar(1200);
		wbar.start = dbar.start;
		
		wbar.high = dbar.high;
		wbar.low = dbar.low;
		wbar.open = dbar.open;
		wbar.close = dbar.close;
		
		wbar.volume = dbar.volume;
		
		return wbar;
	}
	
	private void UpdateWeekBar(PriceBar wbar, PriceBar dbar) {
		wbar.high = wbar.high > dbar.high ? wbar.high : dbar.high;
		wbar.low = dbar.low > wbar.low ? wbar.low : dbar.low;
		wbar.close = dbar.close;
		wbar.volume += dbar.volume;
	}
	
	public StockData ToWeekBars() {
		StockData weekbars = new StockData(this.code, this.market);
		List<PriceBar> wbar_list = new ArrayList<PriceBar>();
		ListIterator<PriceBar> diter = bar_list.listIterator(bar_list.size());

//		Calendar start_time = new GregorianCalendar();
		PriceBar wbar = new PriceBar(1200), dbar;
		
		if( diter.hasPrevious() ) {
			dbar = diter.previous();
			wbar = NewWeekBar(dbar);
		}
		
		while( diter.hasPrevious() ) {
			dbar = diter.previous();
			int w = dbar.start.get(Calendar.DAY_OF_WEEK);
			
//			start_time.setTime(dbar.start);
//			int w = start_time.get(Calendar.DAY_OF_WEEK);
			
			if( w == Calendar.MONDAY )
			{
				wbar_list.add(wbar);
				wbar = NewWeekBar(dbar);
			}
			else
				UpdateWeekBar(wbar, dbar);
		}
		wbar_list.add(wbar);
		
		ListIterator<PriceBar> witer = wbar_list.listIterator(wbar_list.size());
		while( witer.hasPrevious() ) {
			wbar = witer.previous();
			weekbars.bar_list.add(wbar);
		}
		weekbars.Statistic();
		
		return weekbars;
	}
}

//public PriceBar todayPrice() {
//	SinaStock sina = new SinaStock(code, market);
//	return sina.getNewPrice();
//}

//public String getInfo(int field) {
//	switch(field) {
//	case CODE:
//		return this.code;
//	case MARKET:
//		return this.market;
//	}
//	return null;
//}

//public void print() {
//	datasource.print();
//}

//public void load(Date start, Date end) 
//{
//	Calendar start_time = new GregorianCalendar();
//	start_time.setTime(start);
//	Calendar end_date = new GregorianCalendar();
//	end_date.setTime(end);
//	
//	try {
//		List<List<String>> data = datasource.LocalData();
//		this.Parse(data);
//	} catch (IOException e) {
////		String url = ((WebSource)datasource).getUrl();
////		System.out.println("download data from " + url + " failed.");
//		e.printStackTrace();
//	}
//}

//public void load(Date start, int source) 
//{
//	if( source == DataSource.SOUREC_LOCAL )
//		datasource = new DataSource(this.code, this.market);
//	else if( source == DataSource.SOUREC_YAHOO )
//		datasource = new YahooStock(this.code, this.market);
//	else if( source == DataSource.SOUREC_SINA )
//		datasource = new SinaStock(this.code, this.market);
//	this.load(start, new Date());
//}
