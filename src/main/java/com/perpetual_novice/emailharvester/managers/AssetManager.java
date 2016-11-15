package com.perpetual_novice.emailharvester.managers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.perpetual_novice.emailharvester.model.Location;
import com.perpetual_novice.emailharvester.model.Website;
import com.perpetual_novice.emailharvester.model.ZipCode;
import com.perpetual_novice.emailharvester.sql.SQLConnectionManager;

public class AssetManager {
	private HashMap<Integer,Location> locations = new HashMap<Integer,Location>();
	private Stack<Website> websites = new Stack<Website>();
	private SQLConnectionManager manager = null;
	
	/**	Default constructor
	 * 
	 * @param man		configured SQLConnectionManager
	 */
	public AssetManager(SQLConnectionManager man) {
		manager = man;
	}
	
	/**
	 * 
	 * @return		the size of the locations to search map
	 */
	public synchronized Integer countLocations() {
		return locations.size();
	}
	
	/**
	 * 
	 * @return		all locations to search
	 */
	public synchronized HashMap<Integer,Location> allLocations() {
		return locations;
	}
	
	/**
	 * 
	 * @return		next random location from map
	 */
	public synchronized Location getNextLocation() {
		
		if(locations.size() > 0) {
			Random generator = new Random();
			Object[] keys = locations.keySet().toArray();
			int next = generator.nextInt(keys.length);
			Location loc = locations.get(keys[next]);
			
			locations.remove(keys[next]);
			
			return loc;
		}else return null;
	}
	
	public synchronized void addLocation(Integer cityId, String city, String state) {
		locations.put(cityId, new Location(city, state, cityId));
	}
	
	public synchronized void addZipForCity(Integer cityId, ZipCode zip) {
		locations.get(cityId).addZipCode(zip);
	}
	
	public void finalizeLocations() {
	}
	
	public void innitFromDB() {
		
		Map<Integer,HashMap<String,String>> cities = manager.citiesIndex();
		Map<Integer,HashMap<String,String>> states = manager.statesIndex();
		
		Vector<ZipCode> zips = manager.zipVector();
		
		System.out.println("Initializing from database ...");
		
		Iterator<ZipCode> its = zips.iterator();
		while(its.hasNext()) {
			ZipCode zip = its.next();
			
			if(cities.containsKey(zip.city())) {
				if(locations.containsKey(zip.city())) {
					locations.get(zip.city()).addZipCode(zip);
				} else {
					locations.put(zip.city(), new Location(cities.get(zip.city()).get("cityname"), states.get(zip.state()).get("name"), states.get(zip.state()).get("abbreviation"), zip));
				}
			}
		}
		
		System.out.println("cities array prepared - " + cities.size());
		
		websites.addAll(manager.websiteSet(50));
	}
	
	public synchronized void addEmail(String email, Website site) {
		if (!email.matches("[Ww]ebmaster@.*")) {
			System.out.println("Email found: " + email);
			
			manager.insertEmailForWebsite(email,site);
		}
	}
	
	private int nextWebsiteID() {
		return websites.size() + 1;
	}
	
	public synchronized void addWebsite(String url, Location city) {

		String domain = "";
		Pattern pattern = Pattern.compile("(http|https)://([^/\r\n]+)(/[^\r\n]*)?");
		Matcher matcher = pattern.matcher(url);
		if(matcher.find()) {
			domain = matcher.group(1);
		}
		Website site = new Website(nextWebsiteID(), url, city.getCityId(), domain);
		manager.insertWebsite(site);
		manager.searchedCity(city.getCityId());
	}
	
	public synchronized Website getNextWebsite() {
		refreshWebsiteStack();
		Website site = websites.pop();
		manager.searchedWebsite(site);
		return site;
	}
	
	public synchronized int countWebsites() {
		refreshWebsiteStack();
		return websites.size();
	}
	
	public synchronized void refreshWebsiteStack() {
		if(websites.size() <= 0) {
			websites.addAll(manager.websiteSet(40));
		}
	}
}
