package com.stock.turtle;

import com.stock.data.StockListLoader;
import com.stock.view.BrokenLineView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class BrokenLineActivity extends Activity {
	public static final String TAG = "Turtle:MainActivity";
	private Handler mHandler;
	private BrokenLineView brokenline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_activity);
        Log.i(TAG, "onCreate");

        HandlerThread mHandlerThread = new HandlerThread("LoadStockData");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        
        brokenline = (BrokenLineView) findViewById(R.id.brokenLineView1);
    }

    @Override
    protected void onStart() {
    	super.onStart();
        Log.i(TAG, "onStart");
        
        mHandler.post(new StockListLoader(brokenline.stocks));
    }


}
