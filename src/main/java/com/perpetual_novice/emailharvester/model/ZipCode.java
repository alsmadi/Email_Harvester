package com.perpetual_novice.emailharvester.model;

public class ZipCode {
	private int id;
	private String code;
	private int city;
	private int state;
	private float latitude;
	private float longitude;
	
	/** Default constructor
	 * 
	 * @param id			zip code id
	 * @param code			zip code with form 75092
	 * @param city			city id
	 * @param state			state id
	 */
	public ZipCode(int id, String code, int city, int state) {
		this.id = id;
		this.code = code;
		this.city = city;
		this.state = state;
	}
	
	/**	Alternate constructor
	 * 
	 * @param id			zip code id
	 * @param code			zip code with form 75092
	 * @param city			city id
	 * @param state			state id
	 * @param latitude		latitude for zip
	 * @param longitude		longitude for zip
	 */
	public ZipCode(int id, String code, int city, int state, float latitude, float longitude) {
		this.id = id;
		this.code = code;
		this.city = city;
		this.state = state;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public int id() { return this.id; }
	
	public void id(int id) { this.id = id; }
	
	public String code() { return this.code; }
	
	public void code(String code) { this.code = code; }
	
	public int city() { return this.city; }
	
	public void city(int city) { this.city = city; }
	
	public int state() { return this.state; }
	
	public void state(int state) { this.state = state; }
	
	public float latitude() { return this.latitude; }
	
	public void latitude(float lat) { this.latitude = lat; }
	
	public float longitude() { return this.longitude; }
	
	public void longitude(float lon) { this.longitude = lon; }
}
