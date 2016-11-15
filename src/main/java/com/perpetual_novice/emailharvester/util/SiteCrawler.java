package com.perpetual_novice.emailharvester.util;

import java.io.IOException;
import java.nio.channels.UnresolvedAddressException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

import com.perpetual_novice.emailharvester.managers.AssetManager;
import com.perpetual_novice.emailharvester.util.ETHHostnameVerifier;
import com.perpetual_novice.emailharvester.model.Website;
import com.perpetual_novice.emailharvester.sql.SQLConnectionManager;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;

public class SiteCrawler implements Runnable {
	AssetManager assets = null;
	SQLConnectionManager manager = null;
	Stack<String> pages = new Stack<String>();
	Vector<String> visited = new Vector<String>();
	Vector<String> emails = new Vector<String>();
	
	/** Default constructor which gives access to sql and data assets.
	 * 
	 * @param manage	global SQLConnectionManager object
	 * @param asset		global AssetManager object
	 */
	public SiteCrawler(SQLConnectionManager manage, AssetManager asset) {
		assets = asset;
		manager = manage;
	}
	
	private void addPage(String page) {
		if(!visited.contains(page) && !pages.contains(page)) {
			//System.out.println("url added: " + page);
			pages.add(page);
		}
	}
	
	
	public void run() {
		while (assets.countWebsites() > 0) {
			try {
				Website site = assets.getNextWebsite();

				pages.empty();
				visited.clear();
				emails.clear();
				
				//manager.searchedWebsite(site);
				String url = site.url();
				Pattern pattern = Pattern.compile("(http|https)://([^/\r\n]+)(/[^\r\n]*)?");
				Pattern email = Pattern.compile("[a-zA-Z0-9]+@(?:[a-zA-Z0-9]\\.)?(?:[a-zA-Z0-9]+\\.(?:com|net|co|org))");

				pages.add(url);
				int completed = 0;
				System.out.println("Beginning crawl for: " + url);

				while (!pages.empty() && completed < 50) {
					completed++;
					String nextUrl = pages.pop();
					System.out.println("crawling " + nextUrl);
					visited.add(nextUrl);
					String protocol = "";
					String domain = "";
					String uri = "";
					Matcher matcher = pattern.matcher(nextUrl);
					if (matcher.find()) {
						protocol = matcher.group(1);
						//System.out.println("protocol: " + protocol);
						domain = matcher.group(2);
						//System.out.println("domain: " + domain);
						uri = matcher.group(3);
						//System.out.println("uri: " + uri);
						//System.out.println("ex: " + matcher.group(3));
					}
					
					/*
					AsyncHttpClient client = null;
					if (protocol.equals("https")) {
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

						client = new AsyncHttpClient(
								new AsyncHttpClientConfig.Builder()
										.setSSLContext(context)
										.setHostnameVerifier(
												new ETHHostnameVerifier())
										.build());
					} else {
						client = new AsyncHttpClient(
								new AsyncHttpClientConfig.Builder().build());
					}*/
					
					AsyncHttpClient client = new AsyncHttpClient();
					Response response = null;
					try {
						response = client.prepareGet(nextUrl).execute().get();
						if (response.getStatusCode() / 100 == 2) {
							try {
								String responseBody = response.getResponseBody();
								//System.out.println("Successfully downloaded " + nextUrl);

								//System.out.println(domain.replaceAll("\\.", "\\."));
								//Pattern link = Pattern.compile("href=\"((http|https)://" + domain.replaceAll(".", "\\.") + "(/[^\\r\\n]*)?)\"");
								Pattern link = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
								Matcher linkMatch = link.matcher(responseBody);

								while (linkMatch.find()) {
									//System.out.println("new link: " + linkMatch.group(1));
									
									String found = linkMatch.group(1).toLowerCase().replaceAll("\"|\'", "");
									//System.out.println(found);
									//found = found.replaceAll("\"", "");
									//found = found.replaceAll("href=", "");
									if (found.matches(".*(contact|about|info).*") && !found.startsWith("/#") && !visited.contains(found) && 
											!found.contains("facebook.com") && !found.contains("reddit.com") && 
											!found.contains("google.com") && !found.contains("css") && 
											!found.contains("jpg") && !found.contains("mail") && 
											!found.contains("js") && !found.contains("png") && 
											!found.contains("mov") && !found.contains("gif") && !found.contains(" ") && !found.contains("javascript")) {
										

										//found = URLEncoder.encode(found, "UTF-8");
										if(found.contains("http") || found.contains("https")) {
											if(found.contains(domain)) {
												addPage(found);
												//pages.add(found);
												//System.out.println("url added: " + found);
											}
										} else {
											if(found.startsWith("/")) {
												found = protocol + "://" + domain + found;
												addPage(found);
												//System.out.println("url added: " + found);
												//pages.add(found);
											} else {
												if(!found.startsWith(".") && !found.startsWith("#")) {
													found = protocol + "://" + domain + "/" + found;
													addPage(found);
													//System.out.println("url added: " + found);
													//pages.add(found);
												}
											}
										}
										/*
										if (!found.contains(domain)) {
											found = domain + found;
										}
										if (!found.contains("http") || !found.contains("https")) {
											found = protocol + "://" + found;
										}
										System.out.println("url added: " + found);
										pages.add(found);*/
									}
								}

								Matcher emailMatch = email.matcher(responseBody);
								while (emailMatch.find()) {
									String match = emailMatch.group().toLowerCase();
									//assets.addEmail(match, site);
									
									if (!emails.contains(match)) {
										//System.out.println("Email found: " + match);
										emails.add(match);
										assets.addEmail(match, site);
									}
								}
								
								
								/*
								Iterator<String> it = emails.iterator();
								while (it.hasNext()) {
									String em = it.next();
									manager.insertEmail(em, url);
								}*/
							} catch (IOException e) {
								e.printStackTrace();
								client.close();
								return;
							}
							client.close();
						} else {
							//System.out.println("Failure downloading " + nextUrl + ": HTTP Status " + response.getStatusCode());
							client.close();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						//client.close();
						//return;
					} catch (ExecutionException e) {
						e.printStackTrace();
						//client.close();
						//return;
					} catch (UnresolvedAddressException e) {
						e.printStackTrace();
					}
				}

			} catch (EmptyStackException e) {

			}
		}

	}

}
