package com.stock.source;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class YahooStock extends DataSource {
	protected static final String STOCK_URL = "http://table.finance.yahoo.com/table.csv";
	// http://table.finance.yahoo.com/table.csv?a=0&b=1&c=2012&d=3&e=19&f=2012&s=600000.ss
	// http://table.finance.yahoo.com/table.csv?a=0&b=1&c=2014&s=000001.ss

	public YahooStock(String _code, String _market) {
		super(_code, _market);
	}

    public String getDataUrl(Calendar start, Calendar end) {
		String stock_url = String.format(Locale.US,
				"%s?a=%d&b=%d&c=%d&d=%d&e=%d&f=%d&s=%s.%s", STOCK_URL,
				start.get(Calendar.MONTH), start.get(Calendar.DATE), 
				start.get(Calendar.YEAR), 
				end.get(Calendar.MONTH), end.get(Calendar.DATE), 
				end.get(Calendar.YEAR), 
				code, market);
		return stock_url;
    }

    public String getDataUrl(Calendar start) {
		String stock_url = String.format(Locale.US, 
				"%s?a=%d&b=%d&c=%d&s=%s.%s", STOCK_URL,
				start.get(Calendar.MONTH), start.get(Calendar.DATE), 
				start.get(Calendar.YEAR), code, market);
		return stock_url;
    }

	public List<List<String>> get(Calendar start) throws IOException {
		url = getDataUrl(start);
		
		download(url, getStockFilePath());
		LocalData();
		
		return price_list;
	}

	public List<List<String>> get(Calendar start, Calendar end) throws IOException {
		url = getDataUrl(start, end);
		
		String filename = "data/" + market + code + ".csv";
		download(url, filename);
		LocalData();
		
		return price_list;
	}

}
