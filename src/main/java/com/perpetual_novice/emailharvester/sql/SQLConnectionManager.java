package com.perpetual_novice.emailharvester.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.perpetual_novice.emailharvester.model.Website;
import com.perpetual_novice.emailharvester.model.ZipCode;

public class SQLConnectionManager {
	private static String CONNECTION_URI = "jdbc:postgresql://";
	private Connection con = null;
	private String desc = "default";
	private Vector<String> keyword = null;
	
	/** publicly used statement */
	public volatile Statement statement = null;
	
	/** Default constructor.
	 * 
	 * @param host			postgresql hostname or address.
	 * @param port			postgresql port.
	 * @param db			postgresql database name.
	 * @param user			postgresql database user name.
	 * @param pass			postgresql database password.
	 * @param description	description for the list being built.
	 * @param create		build database schema.
	 */
	public SQLConnectionManager(String host, String port, String db, String user, String pass, String description, Vector<String> keywords, boolean create) {
		desc = description;
		keyword = keywords;
		
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			con = DriverManager.getConnection(CONNECTION_URI+host+":"+port+"/"+db,user,pass);
			
			System.out.println("connection established ...");
			statement = con.createStatement();
			
			if(create) {

				String sql = "";
				
				// drop everything in the database
				sql = "DROP SCHEMA public CASCADE";
				statement.executeUpdate(sql);
				sql = "CREATE SCHEMA public";
				statement.executeUpdate(sql);
				sql = "GRANT ALL ON SCHEMA public TO postgres";
				statement.executeUpdate(sql);
				sql = "GRANT ALL ON SCHEMA public TO public";
				statement.executeUpdate(sql);
				sql = "COMMENT ON SCHEMA public IS 'standard public schema'";
				statement.executeUpdate(sql);
				
				// create schema from clean database
				sql = "CREATE TABLE cities (name character varying(100),state bigint,id bigint NOT NULL,searched boolean)";
				statement.executeUpdate(sql);
				sql = "CREATE SEQUENCE cities_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1";
				statement.executeUpdate(sql);
				sql = "ALTER SEQUENCE cities_id_seq OWNED BY cities.id";
				statement.executeUpdate(sql);
				sql = "CREATE TABLE dbinfo (sendrate integer NOT NULL,email text,id bigint NOT NULL)";
				statement.executeUpdate(sql);
				sql = "CREATE TABLE emails (domain character varying(75),address character varying(100),id bigint NOT NULL,recipient character varying(75),sent boolean,datesent timestamp without time zone,bounced boolean,subscribed boolean,unsubscribelinkid character varying(39),purpose character varying(100))";
				statement.executeUpdate(sql);
				sql = "CREATE SEQUENCE emails_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1";
				statement.executeUpdate(sql);
				sql = "ALTER SEQUENCE emails_id_seq OWNED BY emails.id";
				statement.executeUpdate(sql);
				sql = "CREATE TABLE emailwebsites (id bigint NOT NULL,email bigint,website bigint)";
				statement.executeUpdate(sql);
				sql = "CREATE SEQUENCE emailwebsites_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1";
				statement.executeUpdate(sql);
				sql = "ALTER SEQUENCE emailwebsites_id_seq OWNED BY emailwebsites.id";
				statement.executeUpdate(sql);
				sql = "CREATE TABLE states (name character varying(100),id bigint NOT NULL,abbreviation character varying(5))";
				statement.executeUpdate(sql);
				sql = "CREATE SEQUENCE states_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1";
				statement.executeUpdate(sql);
				sql = "ALTER SEQUENCE states_id_seq OWNED BY states.id";
				statement.executeUpdate(sql);
				sql = "CREATE TABLE websites (id bigint NOT NULL,url character varying(200),city bigint,domain character varying(100),lastmailed timestamp without time zone,mailcount integer,searched boolean,purpose character varying(100),keywords character varying(300))";
				statement.executeUpdate(sql);
				sql = "CREATE SEQUENCE websites_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1";
				statement.executeUpdate(sql);
				sql = "ALTER SEQUENCE websites_id_seq OWNED BY websites.id";
				statement.executeUpdate(sql);
				sql = "CREATE TABLE zipcodes (state bigint,id bigint NOT NULL,code character varying(20),city bigint,latitude numeric,longitude numeric)";
				statement.executeUpdate(sql);
				sql = "CREATE SEQUENCE zipcodes_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1";
				statement.executeUpdate(sql);
				sql = "ALTER SEQUENCE zipcodes_id_seq OWNED BY zipcodes.id";
				statement.executeUpdate(sql);
				sql = "ALTER TABLE ONLY cities ALTER COLUMN id SET DEFAULT nextval('cities_id_seq'::regclass)";
				statement.executeUpdate(sql);
				sql = "ALTER TABLE ONLY emails ALTER COLUMN id SET DEFAULT nextval('emails_id_seq'::regclass)";
				statement.executeUpdate(sql);
				sql = "ALTER TABLE ONLY emailwebsites ALTER COLUMN id SET DEFAULT nextval('emailwebsites_id_seq'::regclass)";
				statement.executeUpdate(sql);
				sql = "ALTER TABLE ONLY states ALTER COLUMN id SET DEFAULT nextval('states_id_seq'::regclass)";
				statement.executeUpdate(sql);
				sql = "ALTER TABLE ONLY websites ALTER COLUMN id SET DEFAULT nextval('websites_id_seq'::regclass)";
				statement.executeUpdate(sql);
				sql = "ALTER TABLE ONLY zipcodes ALTER COLUMN id SET DEFAULT nextval('zipcodes_id_seq'::regclass)";
				statement.executeUpdate(sql);
				sql = "ALTER TABLE ONLY cities ADD CONSTRAINT cities_pk PRIMARY KEY (id)";
				statement.executeUpdate(sql);
				sql = "ALTER TABLE ONLY dbinfo ADD CONSTRAINT dbinfo_pk PRIMARY KEY (id)";
				statement.executeUpdate(sql);
				sql = "ALTER TABLE ONLY emails ADD CONSTRAINT emails_pk PRIMARY KEY (id)";
				statement.executeUpdate(sql);
				sql = "ALTER TABLE ONLY emailwebsites ADD CONSTRAINT emailwebsites_pk PRIMARY KEY (id)";
				statement.executeUpdate(sql);
				sql = "ALTER TABLE ONLY states ADD CONSTRAINT states_pk PRIMARY KEY (id)";
				statement.executeUpdate(sql);
				sql = "ALTER TABLE ONLY websites ADD CONSTRAINT websites_pk PRIMARY KEY (id)";
				statement.executeUpdate(sql);
				sql = "ALTER TABLE ONLY zipcodes ADD CONSTRAINT zipcodes_pk PRIMARY KEY (id)";
				statement.executeUpdate(sql);
				sql = "CREATE INDEX cities_state ON cities USING btree (state)";
				statement.executeUpdate(sql);
				sql = "CREATE INDEX emailwebsites_email ON emailwebsites USING btree (email)";
				statement.executeUpdate(sql);
				sql = "CREATE INDEX emailwebsites_website ON emailwebsites USING btree (website)";
				statement.executeUpdate(sql);
				sql = "CREATE INDEX websites_city ON websites USING btree (city)";
				statement.executeUpdate(sql);
				sql = "CREATE INDEX zipcodes_city ON zipcodes USING btree (city)";
				statement.executeUpdate(sql);
				sql = "CREATE INDEX zipcodes_state ON zipcodes USING btree (state)";
				statement.executeUpdate(sql);
			}
		}catch (Exception E) {
			E.printStackTrace();
		}
		
		System.out.println("schema created");
	}

	/**
	 * 
	 * @return		index of cities where key is city id and value is hashmap such that keys: cityname and stateId correspond to string values
	 */
	public synchronized HashMap<Integer,HashMap<String,String>> citiesIndex(){
		HashMap<Integer,HashMap<String,String>> citiesIndex = new HashMap<Integer,HashMap<String,String>>();
		try{
			String query = "SELECT id,name,state FROM cities WHERE searched=false";
			ResultSet set = statement.executeQuery(query);
			while(set.next()){
				if(!citiesIndex.containsKey(set.getInt(1))){
					HashMap<String,String> newCity = new HashMap<String,String>();
					newCity.put("cityname", set.getString(2));
					newCity.put("stateId",Integer.toString(set.getInt(3)));
					citiesIndex.put(set.getInt(1), newCity);
				}else{
					citiesIndex.get(set.getInt(1)).put("cityname", set.getString(2));
					citiesIndex.get(set.getInt(1)).put("stateId",Integer.toString(set.getInt(3)));
				}
			}
			set.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return citiesIndex;
	}

	/**
	 * 
	 * @return		index of states where key is state id and value is hashmap such that keys: name and abbreviation correspond to string values
	 */
	public synchronized HashMap<Integer,HashMap<String,String>> statesIndex(){
		HashMap<Integer,HashMap<String,String>> states = new HashMap<Integer,HashMap<String,String>>();
		try{
			String query = "SELECT id,name,abbreviation FROM states";
			ResultSet set = statement.executeQuery(query);
			while(set.next()){
				HashMap<String,String> info = new HashMap<String,String>();
				info.put("name", set.getString(2));
				info.put("abbreviation", set.getString(3));
				states.put(set.getInt(1), info);
			}
			set.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return states;
	}
	
	/**
	 * 
	 * @return		vector containing ZipCode objects
	 */
	public synchronized Vector<ZipCode> zipVector() {
		Vector<ZipCode> codes = new Vector<ZipCode>();
		try{
			String query = "SELECT id,code,city,state,latitude,longitude FROM zipcodes";
			ResultSet set = statement.executeQuery(query);
			while(set.next()){
				codes.add(new ZipCode(set.getInt(1), set.getString(2), set.getInt(3), set.getInt(4), set.getFloat(5), set.getFloat(6)));
				//zipcodesIndex.put(set.getInt(1), set.getString(2));
			}
			set.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return codes;
	}
	
	/**
	 * 
	 * @param count		website pull limit
	 * @return			Vector of Website objects from database
	 */
	public synchronized Vector<Website> websiteSet(int count){
		Vector<Website> sites = new Vector<Website>();
		try {
			String query = "SELECT id,domain,url,city FROM websites WHERE purpose='" + desc + "' AND keywords='" + keywordsAsString() + "' GROUP BY id HAVING searched=false LIMIT " + count;
			ResultSet set = statement.executeQuery(query);
			while(set.next()){
				sites.add(new Website(set.getInt(1), set.getString(3), set.getInt(4), set.getString(2)));
				//websites.put(set.getString(2),set.getInt(1));
			}
			set.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return sites;
		
	}
	
	/**
	 * 
	 * @param email		email string to insert to database
	 * @param site		Website object to associate to email
	 * @return			success or failure
	 */
	public synchronized boolean insertEmailForWebsite(String email, Website site) {
		try {
			PreparedStatement stmt = con.prepareStatement("SELECT id,count(id) FROM emails GROUP BY id HAVING address = '" + email.trim() + "'");
			ResultSet set = stmt.executeQuery();
			int id = -1;
			if (!set.isBeforeFirst() ) {    
				// email does not exist in db
				set.close();
				id = insertEmail(email);
			} else {
				// email exists
				if(set.next()) id = set.getInt(1);
				set.close();
			}
			
			joinEmailWebsite(id, site);
			
			return true;
		} catch(SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}
	
	/**
	 * 
	 * @param email		email string to insert to database
	 * @return			id of email in database
	 */
	public synchronized int insertEmail(String email) {
		try {

			/*
			PreparedStatement val = con.prepareStatement("SELECT nextval('email_id_seq')");
			ResultSet set = val.executeQuery();
			int ids = 0;
			while(set.next()){
				ids = set.getInt(1);
			}
			val.close();*/
			
			PreparedStatement stmt = con.prepareStatement("INSERT INTO emails (address,recipient,domain,bounced,sent,subscribed,purpose) VALUES (?,?,?,false,false,true,?) RETURNING id");
			//stmt.setInt(1, email.id());
			String[] parts = email.trim().split("@");
			stmt.setString(1, email.trim());
			stmt.setString(2, parts[0]);
			stmt.setString(3, parts[1]);
			stmt.setString(4, desc);
			ResultSet results = stmt.executeQuery();
			int id = 0;
			while(results.next()) {
				id = results.getInt(1);
			}
			results.close();
			stmt.close();
			
			return id;
		} catch(SQLException e) {
			e.printStackTrace();
			System.exit(1);
			
			return 0;
		}
	}
	
	/**
	 * 
	 * @param email		email id in database
	 * @param site		Website object to join
	 */
	public synchronized void joinEmailWebsite(int email, Website site) {
		try {
			PreparedStatement count = con.prepareStatement("SELECT id,count(id) FROM emailwebsites GROUP BY id HAVING email = " + email + " AND website = " + site.id());
			ResultSet set = count.executeQuery();
			if(!set.isBeforeFirst()) {
				// join does not exist
				
				set.close();
				
				PreparedStatement stmt = con.prepareStatement("INSERT INTO emailwebsites (email,website) VALUES (?,?)");
				stmt.setInt(1, email);
				stmt.setInt(2, site.id());
				stmt.executeUpdate();
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * 
	 * @param site		Website object to insert into database
	 * @return			success or failure
	 */
	public synchronized boolean insertWebsite(Website site) {
		try {
			PreparedStatement count = con.prepareStatement("SELECT id,count(id) FROM websites GROUP BY id HAVING url = '" + site.url() + "'");
			ResultSet set = count.executeQuery();
			if(!set.isBeforeFirst()) {
				// join does not exist
				
				set.close();

				
				PreparedStatement stmt = con.prepareStatement("INSERT INTO websites (url,city,domain,searched,purpose,keywords) VALUES (?,?,?,false,?,?) RETURNING id");
				stmt.setString(1, site.url());
				stmt.setInt(2, site.city());
				stmt.setString(3, site.domain());
				stmt.setString(4, desc);
				stmt.setString(5, keywordsAsString());
				ResultSet results = stmt.executeQuery();
				
				results.close();
				stmt.close();
				
				return true;
			} else {
				set.close();
				return true;
			}
		} catch(SQLException e) {
			e.printStackTrace();
			System.exit(1);
			
			return false;
		}
	}
	
	/**	Tests if website has been searched
	 * 
	 * @param site		Website object to test
	 * @return			searched is true
	 */
	public synchronized boolean searchedWebsite(Website site){
		try {
			String query = "UPDATE websites SET searched=true WHERE id=" + site.id();
			ResultSet set = statement.executeQuery(query);
			set.close();
			
			return true;
		} catch(SQLException e) {
			//e.printStackTrace();
			
			return false;
		}
		
	}
	
	/**
	 * 
	 * @param name			state name
	 * @param shortName		state abbreviation
	 * @return				id in database for state
	 */
	public synchronized int insertState(int ID, String name, String shortName){
		try{
			//System.out.println("State id: " + id + " name: " + name + " abbreviation: " + shortName);
			PreparedStatement stmt = con.prepareStatement("INSERT INTO states (name,abbreviation) VALUES (?,?) RETURNING id");
			stmt.setInt(1, ID);
			stmt.setString(1,name);
			stmt.setString(2,shortName);
			ResultSet results = stmt.executeQuery();
			int idt = 0;
			while(results.next()) {
				idt = results.getInt(1);
			}
			results.close();
			stmt.close();
			
			return idt;
		}catch(SQLException e){
			e.printStackTrace();
			System.exit(1);
			
			return 0;
		}
	}
	
	/**
	 * 
	 * @param name			city name
	 * @param stateID		id for state in database
	 * @return				id for city in database
	 */
	public synchronized int insertCity(String name, int stateID){
		try{
			PreparedStatement stmt = con.prepareStatement("INSERT INTO cities (name,state) VALUES (?,?) RETURNING id");
			//stmt.setInt(1, id);
			stmt.setString(1,name);
			stmt.setInt(2, stateID);
			ResultSet results = stmt.executeQuery();
			int idt = 0;
			while(results.next()) {
				idt = results.getInt(1);
			}
			results.close();
			stmt.close();
			
			return idt;
		}catch(SQLException e){
			e.printStackTrace();
			System.exit(1);
			
			return 0;
		}
	}

	/**
	 * 
	 * @param city		id for city in database
	 * @return			has city been searched
	 */
	public synchronized boolean searchedCity(Integer city){
		try {
			String query = "UPDATE cities SET searched=true WHERE id=" + city;
			ResultSet set = statement.executeQuery(query);
			set.close();
			
			return true;
		} catch(SQLException e) {
			//e.printStackTrace();
			
			return false;
		}
		
	}
	
	/**
	 * 
	 * @param name			zip code; 75092
	 * @param latitude		latitude for zip code
	 * @param longitude		longitude for zip code
	 * @param stateID		id in database for state
	 * @param cityID		id in database for city
	 * @return				id in database for zip code
	 */
	public synchronized int insertZipCode(String name, float latitude, float longitude, int stateID, int cityID){
		try{
			//System.out.println("Zip " + id + " code: " + name + " latitude: " + latitude + " longitude: " + longitude + " stateID: " + stateID + " cityID: " + cityID + " added");
			PreparedStatement stmt = con.prepareStatement("INSERT INTO zipcodes (city,code,latitude,longitude,state) VALUES (?,?,?,?,?) RETURNING id");
			stmt.setInt(1, cityID);
			//stmt.setInt(2, id);
			stmt.setString(2,name);
			stmt.setFloat(3, latitude);
			stmt.setFloat(4, longitude);
			stmt.setInt(5, stateID);
			ResultSet results = stmt.executeQuery();
			int idt = 0;
			while(results.next()) {
				idt = results.getInt(1);
			}
			results.close();
			stmt.close();
			
			return idt;
		}catch(SQLException e){
			e.printStackTrace();
			System.exit(1);
			
			return 0;
		}
	}
	
	
	private String keywordsAsString() {
		String out = "";
		Iterator<String> iterator = keyword.iterator();
		while(iterator.hasNext()) {
			if(out.length() > 0) out += " ";
			out += iterator.next();
		}
		return out;
	}
}
