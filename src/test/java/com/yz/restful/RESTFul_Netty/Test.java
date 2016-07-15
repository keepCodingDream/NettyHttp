package com.yz.restful.RESTFul_Netty;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Test {
	public static void main(String[] args) throws Exception {
		URL targetUrl = new URL("http://localhost:9001/");
		HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
		httpConnection.setDoOutput(true);
		httpConnection.setDoInput(true);
		httpConnection.setUseCaches(false);
		httpConnection.setRequestMethod("POST");
		httpConnection.setRequestProperty("Content-Type", "application/json");
		String input = "{\"deviceId\":\"192.111\"}";
		OutputStream outputStream = httpConnection.getOutputStream();
		outputStream.write(input.getBytes());
		outputStream.flush();
		outputStream.close();
		StringBuffer response=new StringBuffer();
		String line;
		BufferedReader in=new BufferedReader(new InputStreamReader(httpConnection.getInputStream(),"UTF-8"));
		while((line=in.readLine())!=null){
			response.append(line);
		}
		if (httpConnection.getResponseCode() >=300) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ httpConnection.getResponseCode());
			
		}else {
			System.out.println(response.toString());
			System.out.println(httpConnection.getResponseMessage());
		}
	}
	 
}
