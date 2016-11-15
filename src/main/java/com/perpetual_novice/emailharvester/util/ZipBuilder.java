package com.perpetual_novice.emailharvester.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.perpetual_novice.emailharvester.managers.AssetManager;
import com.perpetual_novice.emailharvester.model.ZipCode;
import com.perpetual_novice.emailharvester.sql.SQLConnectionManager;

public class ZipBuilder implements Runnable {
	private String SRC_FILE_URI = "";
	private ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	private HashMap<String,Integer> states = new HashMap<String,Integer>();
	private HashMap<String,HashMap<Integer,Integer>> cities = new HashMap<String,HashMap<Integer,Integer>>();
	private HashMap<String,Integer> zipcodes = new HashMap<String,Integer>();
	private SQLConnectionManager connectionManager;
	private File srcFile;
	private AssetManager assets = null;

	public ZipBuilder(SQLConnectionManager manager, String sourceFile, AssetManager locs){
		connectionManager = manager;
		SRC_FILE_URI = sourceFile;
		assets = locs;
	}
	
	public void run() {
		openAndParseFile();
		
		int lastStateID = states.size();
		int lastCityID = cities.size();
		int lastZipCodeID = zipcodes.size();
		
		Iterator<HashMap<String,String>> it = list.iterator();
                int counter=0;
		while(it.hasNext()){
			HashMap<String,String> map = it.next();
			if(!states.containsKey(map.get("State").trim())){
				lastStateID = connectionManager.insertState(counter,map.get("State").trim(), map.get("StateShort").trim());
				states.put(map.get("State").trim(), lastStateID);
                                counter++;
				System.out.println("inserted state: " + map.get("State"));
			}
			if(!cities.containsKey(map.get("City").trim())){
				lastCityID = connectionManager.insertCity(map.get("City").trim(), states.get(map.get("State").trim()));
				// <city, <stateID, cityID>>
				HashMap<Integer, Integer> newCity = new HashMap<Integer, Integer>();
				newCity.put(states.get(map.get("State").trim()), lastCityID);
				cities.put(map.get("City").trim(), newCity);
				
				assets.addLocation(lastCityID, map.get("City").trim(), map.get("State").trim());
			}else{
				int stateID = states.get(map.get("State").trim());
				if(!cities.get(map.get("City").trim()).containsKey(stateID)){
					lastCityID = connectionManager.insertCity(map.get("City").trim(), states.get(map.get("State").trim()));
					cities.get(map.get("City").trim()).put(states.get(map.get("State").trim()),lastCityID);
					
					assets.addLocation(lastCityID, map.get("City").trim(), map.get("State").trim());
				}
			}
			if(!zipcodes.containsKey(map.get("ZipCode").trim())){
				int stateID = states.get(map.get("State").trim());
				int cityID = cities.get(map.get("City").trim()).get(stateID);
				lastZipCodeID = connectionManager.insertZipCode(map.get("ZipCode").trim(), new Float(map.get("Latitude").trim()), new Float(map.get("Longitude").trim()), stateID, cityID);
				zipcodes.put(map.get("ZipCode").trim(), lastZipCodeID);
				
				ZipCode zip = new ZipCode(lastZipCodeID, map.get("ZipCode").trim(), cityID, stateID, new Float(map.get("Latitude").trim()), new Float(map.get("Longitude").trim()));
				assets.addZipForCity(cityID, zip);
			}
		}
		
		assets.finalizeLocations();
	}
	
	private void openAndParseFile(){
		try{
			srcFile = new File(SRC_FILE_URI);
			FileReader reader = new FileReader(srcFile);
			BufferedReader r = new BufferedReader(reader);
			
			String str;
			while((str = r.readLine()) != null){
				HashMap<String,String> inter = new HashMap<String,String>();
				//String[] split = str.split("\\t");
                                String[] split = str.split(",");
				if(split.length >= 2) inter.put("ZipCode", split[1]);
				if(split.length >= 3) inter.put("City", split[2]);
				if(split.length >= 4) inter.put("State", split[3]);
				if(split.length >= 5) inter.put("StateShort", split[4]);
				if(split.length >= 10) inter.put("Latitude", split[9]);
				if(split.length >= 11) inter.put("Longitude", split[10]);
				list.add(inter);
			}
			r.close();
			System.out.println("File parsed.");
		}catch(NullPointerException e){
			System.out.println("File: (" + SRC_FILE_URI + ") was not found.");
		}catch(IOException e){
			System.out.println("Could not read file. Check permissions and try again."+e);
		}
	}
}
