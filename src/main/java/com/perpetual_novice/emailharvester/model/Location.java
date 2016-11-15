package com.perpetual_novice.emailharvester.model;

import java.util.Vector;

import com.perpetual_novice.emailharvester.model.ZipCode;

public class Location {
	private String city = "";
	private String state = "";
	private String stateAbbr = "";
	private int cityId = 0;
	private int stateID;
	private Vector<ZipCode> zipCodes = new Vector<ZipCode>();
	
	/** Default constructor
	 * 
	 * @param c		city name
	 * @param s		state name
	 * @param id	city id
	 */
	public Location(String c, String s, int id) {
		city = c;
		state = s;
		cityId = id;
	}
	
	/** Alternate constructor
	 * 
	 * @param city			city name
	 * @param state			state name
	 * @param shortState	short state name
	 * @param code			zip code
	 */
	public Location(String city, String state, String shortState, ZipCode code) {
		this.city = city;
		this.state = state;
		this.stateAbbr = shortState;
		this.zipCodes.add(code);
		this.stateID = code.state();
		this.cityId = code.city();
	}
	
	/**
	 * 
	 * @return city name
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * 
	 * @param cty	city name
	 */
	public void setCity(String cty) {
		city = cty;
	}
	
	/**
	 * 
	 * @return state name
	 */
	public String getState() {
		return state;
	}
	
	/**
	 * 
	 * @param st	state name
	 */
	public void setState(String st) {
		state = st;
	}
	
	/**
	 * 
	 * @return	short state name
	 */
	public String getStateAbbreviation() {
		return stateAbbr;
	}
	
	/**
	 * 
	 * @param abbr short state name
	 */
	public void setStateAbbreviation(String abbr) {
		stateAbbr = abbr;
	}
	
	/**
	 * 
	 * @return	a vector of ZipCode objects
	 */
	public Vector<ZipCode> getZipCodes() {
		return zipCodes;
	}
	
	/**
	 * 
	 * @param zipCode ZipCode object
	 */
	public void addZipCode(ZipCode zipCode) {
		zipCodes.add(zipCode);
	}
	
	/**
	 * 
	 * @return	city id
	 */
	public int getCityId() {
		return cityId;
	}
	
	/**
	 * 
	 * @param id	city id
	 */
	public void setCityId(int id) {
		cityId = id;
	}
}

