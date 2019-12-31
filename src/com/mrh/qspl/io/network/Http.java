package com.mrh.qspl.io.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.mrh.qspl.val.type.TNumber;
import com.mrh.qspl.val.type.TObject;
import com.mrh.qspl.val.type.TString;

public class Http {
	
	public static TObject executeRequest(String targetURL, String urlParameters, String type, String protocol) {
		HttpURLConnection con = null;
		int status = -1;
		try {
			//Create connection
			URL url = new URL(targetURL);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(protocol);//POST / GET
			con.setRequestProperty("Content-Type", type);
			con.setRequestProperty("Content-Length",
			    Integer.toString(urlParameters.getBytes().length));
			con.setRequestProperty("Content-Language", "en-US"); 
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			con.setInstanceFollowRedirects(true);
			con.setUseCaches(false);
			con.setDoOutput(true);
			
			//Send Request
			if(urlParameters.length() > 0) {
				DataOutputStream wr = new DataOutputStream (con.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.close();
			}
			
			//Get Response  
			status = con.getResponseCode();
			boolean didError = status >= 400 && status <= 599;
			InputStream stream;
			if(didError)
				stream = con.getErrorStream();
			else
				stream = con.getInputStream();
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(stream));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			TObject result = new TObject();
			result.set("data", new TString(response.toString()));
			result.set("status", new TNumber(status));
			result.set("successful", new TNumber(didError?0:1));
			result.set("error", new TNumber(0));
			
			rd.close();
			if (con != null)
				con.disconnect();
			return result;
		} 
		catch (Exception e) {
			e.printStackTrace();
			if (con != null)
				con.disconnect();
			TObject result = new TObject();
			result.set("data", new TString(""));
			result.set("status", new TNumber(status));
			result.set("successful", new TNumber(0));
			result.set("error", new TNumber(1));
			return result;
		}
	}
}
