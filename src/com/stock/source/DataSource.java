package com.stock.source;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;

public class DataSource {
	public static final String sdpath = Environment.getExternalStorageDirectory().getPath();//"/mnt/sdcard/";
	
	public static final int MARKET_SHANGHAI = 3;
	public static final int MARKET_SHENZHEN = 4;
	
	public static final int SOUREC_LOCAL = 0;
	public static final int SOUREC_YAHOO = 1;
	public static final int SOUREC_SINA  = 2;
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", 
			Locale.CHINA);

	protected String name = "中国石油";
	protected String code = "601857";
	protected int market = MARKET_SHANGHAI;
	protected List< List<String> > price_list = new ArrayList< List<String> >();
	
	public DataSource(String _code, int _market) {
		code = _code;
		market = _market;
	}

	public String getPath() {
		String status = Environment.getExternalStorageState();
		if( !status.equals(Environment.MEDIA_MOUNTED) ) 
			throw new java.lang.SecurityException();
		
		String sDir = sdpath + "/turtletrade";
		File destDir = new File(sDir);
		if( !destDir.exists() ) 
			destDir.mkdirs();
		
		sDir += "/stockdata";
		destDir = new File(sDir);
		if( !destDir.exists() ) 
			destDir.mkdirs();
		
		return sDir;
	}

	public String getCacheFile() {
		String mkt = market == MARKET_SHANGHAI ? "sh" : "sz";
		return getPath() + "/" + mkt + code + ".cache.csv";
	}

	public String getStockFile() {
		String mkt = market == MARKET_SHANGHAI ? "sh" : "sz";
		return getPath() + "/" + mkt + code + ".csv";
	}

	public List<List<String>> LocalData(String csvfile) throws IOException {
		File file = new File(csvfile);
		if( !file.exists() )
			return null;

		FileInputStream fis = new FileInputStream(file);
		InputStreamReader fr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(fr);

    	ParseCSVData(br, price_list);
    	br.close();
    	fis.close();
		return price_list;
	}
	
	protected String WebData(String _url) throws IOException {
		String text = "", temp;
    	HttpURLConnection httpUrl = (HttpURLConnection)new URL(_url).openConnection();
    	
        BufferedInputStream bis = new BufferedInputStream(httpUrl.getInputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(bis));

        try {
        	while( (temp = br.readLine()) != null ) {
        		text += temp;
        	}
        } catch( IOException ex ) {
        	ex.printStackTrace();
        } finally {
        	br.close();
        	bis.close();
        	httpUrl.disconnect();
        }
        
		return text;
	}

	/**下载远程文件并保存到本地  
     * @param remoteFilePath 远程文件路径   
     * @param localFilePath 本地文件路径  
	 * @throws IOException 
     */
    public void download(String remoteFilePath, String localFilePath) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        HttpURLConnection httpUrl = null;
    	
		try {
			URL urlfile = new URL(remoteFilePath);
	    	httpUrl = (HttpURLConnection)urlfile.openConnection();
	        httpUrl.connect();
	        bis = new BufferedInputStream(httpUrl.getInputStream());

	        File file = new File(localFilePath);
	        FileOutputStream fos = new FileOutputStream(file, false);
	        bos = new BufferedOutputStream(fos);

	        int len = 2048;
	        byte[] b = new byte[len];
	        
	        while ((len = bis.read(b)) != -1)
	        {
	            bos.write(b, 0, len);
	        }

	        bos.flush();
		} 
		
		catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        finally
        {
        	if( bos != null )
        		bos.close();
        	if( bis != null )
        		bis.close();
        	if( httpUrl != null )
        		httpUrl.disconnect();
        }
    }

    public void SaveCSVFile(List<List<String>> data, String csvfile) 
    		throws IOException {
        FileWriter fw = new FileWriter(new File(csvfile), false);
        BufferedWriter bow = new BufferedWriter(fw);

    	try {
            Iterator<List<String>> iter = data.iterator();
            while( iter.hasNext() ) {
            	List<String> list = iter.next();
            	String text = "";
            	
            	Iterator<String> _cell = list.iterator();
            	while( _cell.hasNext() ) {
            		text += _cell.next() + ",";
            	}
            	
            	bow.write(text);
            	bow.newLine();
            }
            bow.flush();
		} 
    	
    	catch (IOException e) {
			e.printStackTrace();
		}

        finally
        {
        	if( bow != null )
        		bow.close();
        	if( fw != null )
        		fw.close();
        }
    }
    
	/**解析csv文件 到一个list中 每个单元个为一个String类型记录，每一行为一个list。 再将所有的行放到一个总list中
	 * @param br 
	 * @param price_list
	 * @throws IOException
	 */
	public static void ParseCSVData(BufferedReader br, List<List<String>> price_list) 
			throws IOException {
		String str1 = "(\"[^\"]*(\"{2})*[^\"]*\")*[^,]*,";
		String str2 = "(?sm)\"?([^\"]*(\"{2})*[^\"]*)\"?.*,";
		String str3 = "(?sm)(\"(\"))";
		
		for( String rec = br.readLine(); rec != null; rec = br.readLine() ) {
			Pattern pCells = Pattern.compile(str1);
			Matcher mCells = pCells.matcher(rec);
			List<String> cells = new ArrayList<String>();// 每行记录一个list
			
			// 读取每个单元格
			while (mCells.find()) {
				String str = mCells.group();
				str = str.replaceAll(str2, "$1");
				str = str.replaceAll(str3, "$2");
				cells.add(str);
			}
			
			price_list.add(cells);
		}
	}

	public void print() {
		Iterator< List<String> > iter = price_list.iterator();
		while( iter.hasNext() ) {
			List<String> _data = iter.next();
			System.out.println(_data);
		}
	}

}

//public List<List<String>> LocalData(Calendar start, Calendar end) throws IOException {
//	File file = new File("data/" + code + ".csv");
//	
//	FileInputStream fis = new FileInputStream(file);
//	InputStreamReader fr = new InputStreamReader(fis);
//	BufferedReader br = new BufferedReader(fr);
//
//	ParseCSVData(br, price_list);
//	br.close();
//	fis.close();
//	
//	return price_list;
//}
