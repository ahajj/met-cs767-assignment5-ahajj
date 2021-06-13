package com.metcs767.ahajj;
/**
 * City Factory
 * 
 * Helper class to create cities with sequential, unique ids
 * 
 * @author Andrew
 *
 */
public class CityFactory {

	private static CityFactory cityFactory;
	
	private static Integer curId;
	
	private static final Integer MAX_X_COORD = 100;
	
	private static final Integer MAX_Y_COORD = 100;
	
	private CityFactory()
	{
		curId = 1;
	}
	
	public static CityFactory getCityFactoryInstance() {
		if (cityFactory == null) {
			cityFactory = new CityFactory();
		} 
		
		return cityFactory;
	}
	
	public City buildCityWithRandCoord(String cityName) {
		return(buildCityWithCoord(cityName, generateRandomNumber(MAX_X_COORD), generateRandomNumber(MAX_Y_COORD)));
	}
	
	public City buildCityWithCoord(String cityName, Integer x, Integer y) {
		City cityToGo = new City(cityName, x, y, curId);
		curId++;
		return cityToGo;
	}
	
	public Integer getCurId() {
		return curId;
	}
	
	/**
	 * Function to generate a random number.
	 * @param max the max number to be randomly generated
	 * @return random number up to max
	 */
	private static Integer generateRandomNumber(int max) {
	  return (int) Math.round(Math.random() * (max - 1));
	}
}
