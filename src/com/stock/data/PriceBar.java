package com.stock.data;

import java.util.Calendar;
import java.util.Date;

import com.stock.source.DataSource;

public class PriceBar {

	public static final int PRICE_OPEN = 0xf0;
	public static final int PRICE_CLOSE = 0xf1;
	public static final int PRICE_HIGH = 0xf2;
	public static final int PRICE_LOW = 0xf3;
	public static final int VOLUME = 0xf4;

	public static final int PRICE_MIDDLE = 0xf5;  ///< (HIGH + LOW) / 2
	public static final int PRICE_AVERAGE = 0xf6; ///< (HIGH + LOW + OPEN + CLOSE) / 4
	public static final int PRICE_VOLUME = VOLUME;
	
	public static final int START = 0xf7;
	public static final int END = 0xf8;
	public static final int MINUTES = 0xf9;

	
	public double open;
	public double close;
	public double high;
	public double low;
	public double volume;
	
	public Calendar start;
	public int minutes;

	public PriceBar() {
		start = Calendar.getInstance();
//		start.setTime(new Date());
//		start.add(Calendar.YEAR, -1);
	}
	
	public PriceBar(int minutes) {
		start = Calendar.getInstance();
		this.minutes = minutes;
	}
	
	public PriceBar clone() {
		PriceBar bar = new PriceBar();
		
		bar.start = this.start;
		bar.minutes = this.minutes;
		
		bar.close = this.close;
		bar.high = this.high;
		bar.low = this.low;
		bar.open = this.open;
		
		bar.volume = this.volume;
		
		return bar;
	}
	
	public String toString() {
		return DataSource.DATE_FORMAT.format(this.start) + "," + 
				this.open + "," + this.high + "," + this.low + "," + this.close;
	}
	
	public double get(int field) {
		switch(field) {
		case PRICE_OPEN:
			return open;
		case PRICE_CLOSE:
			return close;
		case PRICE_HIGH:
			return high;
		case PRICE_LOW:
			return low;
		case VOLUME:
			return volume;
		case PRICE_MIDDLE:
			return (high + low) / 2;
		case PRICE_AVERAGE:
			return (open + close + high + low) / 4;
		case MINUTES:
			return minutes;
		default:
			break;
		}
		return 0;
	}
	
	public void set(int field, double value) {
		switch(field) {
		case PRICE_OPEN:
			open = value;
			break;
		case PRICE_CLOSE:
			close = value;
			break;
		case PRICE_HIGH:
			high = value;
			break;
		case PRICE_LOW:
			low = value;
			break;
		case VOLUME:
			volume = value;
			break;
		default:
			break;
		}
	}
	
	public Calendar getDate() {
		return this.start;
	}
	
	public void setDate(Date date) {
		this.start.setTime(date);
	}
}

//public PriceBar(Calendar start, int minutes) {
//	start = Calendar.getInstance();
//	this.start = start;
//	this.minutes = minutes;
//}
