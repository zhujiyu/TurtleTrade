package com.stock.source;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**雅虎股票查询接口
 * <br>
 * 沪市股票的市场代码为 ss <br>
 * 深市股票的市场代码为 sz <br>
 * 参考文献：<br>
 * 		http://wenku.baidu.com/view/8907c542b307e87101f6962b.html
 * @author jiyu
 */
public class YahooStock extends DataSource {
	protected static final String STOCK_URL = "http://table.finance.yahoo.com/table.csv";
	// http://table.finance.yahoo.com/table.csv?a=0&b=1&c=2012&d=3&e=19&f=2012&s=600000.ss
	// http://table.finance.yahoo.com/table.csv?a=0&b=1&c=2014&s=000001.ss

	public YahooStock(String _code, int _market) {
		super(_code, _market);
	}

    public String getDataUrl(Calendar start, Calendar end) {
		String stock_url = String.format(Locale.US,
				"%s?a=%d&b=%d&c=%d&d=%d&e=%d&f=%d&s=%s.%s", STOCK_URL,
				start.get(Calendar.MONTH), start.get(Calendar.DATE), start.get(Calendar.YEAR), 
				end.get(Calendar.MONTH), end.get(Calendar.DATE), end.get(Calendar.YEAR), 
				code, market == MARKET_SHANGHAI ? "ss" : "sz");
		return stock_url;
    }

    public String getDataUrl(Calendar start) {
		String stock_url = String.format(Locale.US, 
				"%s?a=%d&b=%d&c=%d&s=%s.%s", STOCK_URL,
				start.get(Calendar.MONTH), start.get(Calendar.DATE), start.get(Calendar.YEAR), 
				code, market == MARKET_SHANGHAI ? "ss" : "sz");
		return stock_url;
    }

	public List<List<String>> get(Calendar start, String csvfile) throws IOException {
//		url = getDataUrl(start);
//		String cachefile = this.getCacheFile();
		download(getDataUrl(start), csvfile);
		return LocalData(csvfile);
	}
}

//public List<List<String>> get(Calendar start, Calendar end) throws IOException {
////	url = getDataUrl(start, end);
////	String filename = "data/" + market + code + ".csv";
//	String cachefile = this.getCacheFile();
//	download(getDataUrl(start, end), cachefile);
//	return LocalData(cachefile);
//}
