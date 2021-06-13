package com.metcs767.ahajj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TravelingSalesmanMain {

	public static void main(String[] args) {
		
		// First we create some cities for the Traveling Salesman to Visit
		CityFactory factory = CityFactory.getCityFactoryInstance();
		
		// we will store our cities in a map that have the id as a keys
		Map<Integer, City> cityMap = new HashMap<Integer, City>();
		
		City boston = factory.buildCityWithCoord("Boston", 0, 0);
		City london = factory.buildCityWithCoord("London", 0, 5);
		City mumbai = factory.buildCityWithCoord("Mumbai", 4, 6);
		City shanghai = factory.buildCityWithCoord("Shanghai", 7, 0);
		
		cityMap.put(boston.getId(), boston);
		cityMap.put(london.getId(), london);
		cityMap.put(mumbai.getId(), mumbai);
		cityMap.put(shanghai.getId(), shanghai);
		
		// Now lets create parents from the map
		SalesmanService ss = new SalesmanService();
		List<List<Integer>> population = ss.generateRoutesForStartingLocation(cityMap, boston.getId(), true);
		
		// generate a child
		List<Integer> child = ss.createChildFromParents(population.get(1), population.get(2));
		
	}

}
