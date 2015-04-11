package com.stock.turtle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stock.data.PriceBar;
import com.stock.data.StockData;
import com.stock.index.StockIndex;
import com.stock.index.TurtleIndex;

import com.stock.source.DataSource;
import com.stock.turtle.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


public class MainActivity extends Activity {
	public static final String TAG = "Turtle:MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");

        HandlerThread mHandlerThread = new HandlerThread("MyHandlerThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    protected void onStart() {
    	super.onStart();
        Log.i(TAG, "onStart");
        mHandler.post(loaddata);
    }

    private Handler mHandler;
    private ListView listView;
    
    String[] codes = {"601857", "000001", "601299", "000001"};
    String[] names = {"中国石油", "平安银行", "中国北车", "上证指数"};
    int[] markets = {DataSource.MARKET_SHANGHAI, DataSource.MARKET_SHENZHEN, 
    		DataSource.MARKET_SHANGHAI, DataSource.MARKET_SHANGHAI}; 
    List<Map<String, Object>> stocklist = null;
    
    Runnable loaddata = new Runnable(){
		@Override
		public void run() {
			stocklist = LoadStockList();
			if( stocklist.size() > 0 ) 
				MainActivity.this.runOnUiThread(setAdapter);
		}
    };
    
    Runnable setAdapter = new Runnable(){
		@Override
		public void run() {
			StockRowAdapter adapter = new StockRowAdapter(MainActivity.this, 
	    			stocklist, R.layout.row, //titles, ids);
	                new String[]{"name", "code", "current", "high", "low"},
	                new int[]{R.id.stock_name, R.id.stock_code, R.id.current_price, R.id.high_20days, R.id.low_10days});
	    	listView = (ListView)MainActivity.this.findViewById(R.id.listView1);
	    	listView.setAdapter(adapter);
		}
    };
    
    private Map<String, Object> loadStockInfo(String code, int market,
    		StockIndex buy_index, StockIndex sell_index) {
//		Log.i(TAG, "load " + name + " data.");
		StockData stock = new StockData(code, market);
		stock.load();
		
		List<PriceBar> bars = stock.getBarSet();
    	buy_index.calcIndex(bars);
    	sell_index.calcIndex(bars);

    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("code", code);
    	map.put("current", stock.close);
    	map.put("high", buy_index.get(0));
    	map.put("low", sell_index.get(0));
    	return map;
    }
    
    private List<Map<String, Object>> LoadStockList() {
        Log.i(TAG, "getData");
    	StockIndex buy_index = new TurtleIndex(55, StockIndex.DIRECT_BUY);
    	StockIndex sell_index = new TurtleIndex(20, StockIndex.DIRECT_SELL);
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

    	for( int i = 0; i < 3; i ++ ) {
    		Map<String, Object> map = null;
			map = loadStockInfo(codes[i], markets[i], 
					buy_index, sell_index);
    		map.put("name", names[i]);
    		list.add(map);
    	}
    	
    	return list;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if( id == R.id.menu_settings ) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

//try {
//	StockData stock = new StockData(code, "ss");
//	stock.load();
//	
//	List<PriceBar> bars = stock.getBarSet();
//	buy_index.calcIndex(bars);
//	sell_index.calcIndex(bars);
//
//	Map<String, Object> map = new HashMap<String, Object>();
//	map.put("name", name);
//	map.put("code", code);
//	map.put("high", buy_index.get(0));
//	map.put("low", sell_index.get(0));
//	list.add(map);
//} catch ( Exception ex ) {
//	ex.printStackTrace();
//}

//private void setListAdapter() {
//	SimpleAdapter adapter = new SimpleAdapter(this, stocklist, R.layout.row,
//          new String[]{"name", "code", "current", "high", "low"},
//          new int[]{R.id.stock_name, R.id.stock_code, R.id.current_price, R.id.high_20days, R.id.low_10days});
//	listView = (ListView)findViewById(R.id.listView1);
//	listView.setAdapter(adapter);
//}

//private void LoadStockData(String code, String Market) {
//	StockData stock = new StockData(code, "ss");
//	stock.load();
//	
//	StockIndex index = new TurtleIndex(55, StockIndex.DIRECT_BUY);
//	index.calcIndex(stock.getBarSet());
//	index.get(0);
//}

//for( int i = 0; i < 3; i ++ ) {
//	Log.i(TAG, "load " + names[i] + " data.");
//	StockData stock = new StockData(codes[i], "ss");
//	stock.load();
//	buy_index.calcIndex(stock.getBarSet());
//	sell_index.calcIndex(stock.getBarSet());
//
//	Map<String, Object> map = new HashMap<String, Object>();
//	map.put("name", names[i]);
//	map.put("code", codes[i]);
//	map.put("high", buy_index.get(0));
//	map.put("low", sell_index.get(0));
//	list.add(map);
//}

//Map<String, Object> map = new HashMap<String, Object>();
//map.put("name", "中国石油");
//map.put("code", "601857");
//
//stock = new StockData("601857", "ss");
//buy_index.calcIndex(stock.getBarSet());
//sell_index.calcIndex(stock.getBarSet());
//
//map.put("high", buy_index.get(0));
//map.put("low", sell_index.get(0));
//list.add(map);
//
//map = new HashMap<String, Object>();
//map.put("name", "招商银行");
//map.put("code", "600036");
//
//stock = new StockData("600036", "ss");
//buy_index.calcIndex(stock.getBarSet());
//sell_index.calcIndex(stock.getBarSet());
//
//map.put("high", buy_index.get(0));
//map.put("low", sell_index.get(0));
//list.add(map);
//
//map = new HashMap<String, Object>();
//map.put("name", "中国北车");
//map.put("code", "601299");
//
//stock = new StockData("601299", "ss");
//buy_index.calcIndex(stock.getBarSet());
//sell_index.calcIndex(stock.getBarSet());
//
//map.put("high", buy_index.get(0));
//map.put("low", sell_index.get(0));
//list.add(map);
