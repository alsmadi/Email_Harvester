package com.perpetual_novice.emailharvester.model;

import java.util.Date;

public class Website {
	private int id;
	private String url;
	private int city;
	private String domain;
	private Date lastmail;
	private int mailcount;
	private String purpose;
	private String keywords;

	/** Default constructor
	 * 
	 * @param id			website id
	 * @param url			website url in form http://www.example.com/
	 * @param city			city id
	 * @param domain		site domain
	 */
	public Website(int id, String url, int city, String domain) {
		this.id = id;
		this.url = url;
		this.city = city;
		this.domain = domain;
	}
	
	public int id() { return this.id; }
	
	public void id(int id) { this.id = id; }
	
	public String url() { return this.url; }
	
	public void url(String url) { this.url = url; }
	
	public int city() { return this.city; }
	
	public void city(int city) { this.city = city; }
	
	public String domain() { return this.domain; }
	
	public void domain(String domain) { this.domain = domain; }
	
	public Date lastmail() { return this.lastmail; }
	
	public void lastmail(Date date) { this.lastmail = date; }
	
	public int mailcount() { return this.mailcount; }
	
	public void addMailCount() { this.mailcount++; }
	
	public String purpose() { return this.purpose; }
	
	public void purpose(String p) { this.purpose = p; }
	
	public String keywords() { return this.keywords; }
	
	public void keywords(String k) { this.keywords = k; }
	
}
