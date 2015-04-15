package com.stock.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

public class StockListLoader implements Runnable {
	public static final String TAG = "StockLoader";

	public List<StockData> stocks = new ArrayList<StockData>();
	
	public StockListLoader(List<StockData> list) {
		stocks = list;
	}
	
	public StockListLoader(StockData...args) {
		for(StockData stock:args) {
			stocks.add(stock);
		}
	}
	
	@Override
	public void run() {
        Log.i(TAG, "loading stock data...");
		Iterator<StockData> iter = stocks.iterator();
		while(iter.hasNext()) {
			iter.next().load();
		}
        Log.i(TAG, "load stock succeed.");
	}
	
}
