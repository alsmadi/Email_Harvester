package com.perpetual_novice.emailharvester.managers;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

import com.perpetual_novice.emailharvester.managers.AssetManager;
import com.perpetual_novice.emailharvester.util.ETHHostnameVerifier;
import com.perpetual_novice.emailharvester.model.Location;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;

public class WebSearchManager implements Runnable {
	AssetManager assets = null;
	int errorCount = 0;
	private String keywords = "";
	
	/**	Default constructor assigns keywords to search and an asset manager to handle data flow.
	 * 
	 * @param asset		global AssetManager object
	 * @param keywords	vector of keywords to search
	 */
	public WebSearchManager(AssetManager asset, Vector<String> keywords) {
		assets = asset;
		Iterator<String> iter = keywords.iterator();
		while(iter.hasNext()) {
			if(this.keywords.length() > 0) this.keywords += "+";
			this.keywords += iter.next();
		}
	}
	
	
	public void run() {
		/*
		 * maximum allowed requests to google maps is 2500/24hrs
		 * 
		 */
		Stack<String> mapsUrls = new Stack<String>();
		
		while(assets.countLocations() > 0 || !mapsUrls.empty()) {
			String url = null;
			boolean maps = false;
			Location loc = assets.getNextLocation();
			
			if(!mapsUrls.empty()) {
				url = mapsUrls.pop();
				maps = true;
			} else {
				String query = "?q=" + keywords + "+" + loc.getCity().replaceAll(" ", "+") + "+" + loc.getState().replaceAll(" ", "+") + "&gbv=1&um=1&ie=UTF-8&hl=en&sa=N&tab=wl&output=classic&dg=brw";
				url = "https://maps.google.com/maps" + query;
			}
			
			SSLContext context = null;
			try {
				context = SSLContext.getInstance("TLS");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return;
			}
			
			try {
				context.init(null, null, null);
			} catch (KeyManagementException e) {
				e.printStackTrace();
				return;
			}
			
			/*AsyncHttpClient client = new AsyncHttpClient(
                    new AsyncHttpClientConfig.Builder()
                        .setSSLContext(context)
                        .setHostnameVerifier(new ETHHostnameVerifier())
                        .build()
                );*/
			
			AsyncHttpClient client = new AsyncHttpClient();
			
			Response response = null;
			
			try {
				response = client.prepareGet(url).execute().get();
			} catch (InterruptedException e) {
                e.printStackTrace();
                client.close();
                return;
            } catch (ExecutionException e) {
                e.printStackTrace();
                client.close();
                return;
            }
			
			if (response.getStatusCode() / 100 == 2) {
                try {
                    String responseBody = response.getResponseBody();
                    //System.err.println("Successfully downloaded " + url);

					/*
					 * use regular expression to find anything related to http://maps.google.com/maps...
					 * https://maps.google.com/maps?q=realtor+Dayton+New+York&gbv=1&um=1&ie=UTF-8&hl=en&sa=N&tab=wl
					 */
                    
                    if (!maps) {
						Pattern pattern = Pattern.compile("'http[s]?://([a-zA-Z0-9]+\\.)?([a-zA-Z0-9]+)\\.(com|co|org|net)/([a-zA-Z0-9]*)'");
						Matcher matcher = pattern.matcher(responseBody);
						while (matcher.find()) {
							String matched = matcher.group();
							if(!matched.contains("google") || !matched.contains("gstatic")) {
								System.out.println("Website found: " + matched);
								assets.addWebsite(matched.replaceAll("[\"\\\\']", ""), loc);
							}
						}
					} else {
						Pattern pattern = Pattern.compile("href=\"http://maps\\.google\\.com/maps?[.]+\"");
						Matcher matcher = pattern.matcher(responseBody);
						while (matcher.find()) {
							String matched = matcher.group();
							matched = matched.replaceAll("(href=\"|\")", "");
							matched = matched.replaceFirst("http:", "https:");
							if (!mapsUrls.contains(matched)) {
								System.out.println(matched);
								mapsUrls.add(matched);
							}
						}
					}
					client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    client.close();
                    return;
                }
            } else if (response.getStatusCode() == 302) {
            	try{
            		System.err.println(response.getResponseBody());
            	} catch (IOException e) {
            		e.printStackTrace();
            	}
            } else {
                System.err.println("Failure downloading " + url + ": HTTP Status " + response.getStatusCode());
                if(errorCount > 10) {
                	System.exit(1);
                } else {
                	errorCount++;
                }
            }
				
			try {
				// pause the thread before proceeding to limit requests
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO: handle exception
				
				e.printStackTrace();
				return;
			}
			
		}

	}

}
