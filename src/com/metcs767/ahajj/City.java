package com.metcs767.ahajj;

/**
 * City
 * 
 * Represents a City with X & Y coordinates.  ID should be a unique identifier for the city
 * 
 * @author Andrew
 *
 */

public class City {

	private String name;
	private Integer x;
	private Integer y;
	private Integer id;
	
	public City(String name, Integer x, Integer y, Integer id) {
		super();
		this.name = name;
		this.x = x;
		this.y = y;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}
	
	public Integer getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return this.name + " (" + this.id + ") :" + this.x + ", " + this.y + "\n"; 
	}
	
}
