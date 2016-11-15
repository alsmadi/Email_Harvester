package com.perpetual_novice.emailharvester.managers;

import java.util.Stack;

import com.perpetual_novice.emailharvester.sql.SQLConnectionManager;
import com.perpetual_novice.emailharvester.util.SiteCrawler;

public class CrawlingManager implements Runnable {
	private AssetManager assets = null;
	private int MAX_THREADS = 0;
	private Stack<Thread> threadStack = new Stack<Thread>();
	private SQLConnectionManager connectionManager = null;

	/** Default constructor
	 * 
	 * @param asset		global AssetManager object
	 * @param threads	maximum number of threads for web crawling
	 * @param connect	global SQLConnectionManager object
	 */
	public CrawlingManager(AssetManager asset, int threads, SQLConnectionManager connect) {
		assets = asset;
		MAX_THREADS = threads;
		connectionManager = connect;
	}
	
	public void run() {
		
		
		int wait = 1000;
		while(threadStack.size() < MAX_THREADS && wait <= 60000) {
			if(assets.countWebsites() > 0 && assets.countLocations() > 0) {
				
				/*
				 * clean the thread stack before adding anything
				 */
				for(int i=0;i < threadStack.size();i++){
					if(!threadStack.get(i).isAlive()){
						threadStack.remove(i);
					}
				}
				
				/*
				 * try to add a new crawler thread
				 */
				try {
					wait = 1000;
					threadStack.push(new Thread(new SiteCrawler(connectionManager, assets)));
					threadStack.peek().start();
					System.out.println("crawler thread added");
				} catch (OutOfMemoryError e) {
					System.out.println("Out of memory: " + e.getMessage() + " --- " + threadStack.size() + " threads remaining");
				}
			} else {
				/*
				 * pause the thread for an incrementally longer time if the assets necessary don't yet exist
				 */
				System.out.println("waiting for websites to search --- websites to search:" + assets.countWebsites() + " locations to search:" + assets.countLocations());
				wait = (int) Math.round(wait * 1.2);
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
				
			}
		}
		
		System.err.println("crawling thread exited. websites to search:" + assets.countWebsites() + " locations to search:" + assets.countLocations() + " wait:" + wait);
		

	}

}
