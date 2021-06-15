package com.metcs767.ahajj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TravelingSalesmanMain {

	public static void main(String[] args) {
		
		// First we create some cities for the Traveling Salesman to Visit
		CityFactory factory = CityFactory.getCityFactoryInstance();
		
		// we will store our cities in a map that have the id as a keys
		Map<Integer, City> cityMap = new HashMap<Integer, City>();
		
		// PRESET CITIES AND COORIDNATES
		// FASTEST ROUTE is 0 - 1 - 2 - 3 - 0
		// OR 0 - 3 - 2 - 1 - 0
		City boston = factory.buildCityWithCoord("Boston", 0, 0);
		City london = factory.buildCityWithCoord("London", 0, 5);
		City mumbai = factory.buildCityWithCoord("Mumbai", 4, 6);
		City shanghai = factory.buildCityWithCoord("Shanghai", 7, 0);
		
		cityMap.put(boston.getId(), boston);
		cityMap.put(london.getId(), london);
		cityMap.put(mumbai.getId(), mumbai);
		cityMap.put(shanghai.getId(), shanghai);

		// ALTERNATIVELY, YOU CAN GENERATE A RANDOM DATA SET OF THE PASSED IN LENGTH
		// JUST UNCOMMENT THIS AND COMMENT THE ABOVE SECTION
		/// cityMap = factory.generateAMapOfCities(8);
		
		// Now lets create parents from the map
		SalesmanService ss = new SalesmanService();
		
		// set the max bits based on the number of cities
		ss.setMaxBitsFromNumberOfCities(cityMap.size());
		
		// generate a subset of the population
		List<List<Integer>> parents = ss.generateNumOfRoutesForStartingLocation(4, cityMap, cityMap.get(cityMap.keySet().toArray()[0]).getId(), true);
		
		// get a subset of the population
		// List<List<Integer>> parents = population.subList(2, 5);
		
		// Print out the subset of the population
		for (int i = 0; i < parents.size(); i++) {
			System.out.println("Score for route " + parents.get(i) + " is " + ss.generateRouteScore(parents.get(i), cityMap));
		}
		
		// 4 and 100 is good for 4 cities
		// generate children
		int iterations = 100;
		while (iterations >= 0) {
			parents = ss.selectParentsAndGenerateChildren(parents, cityMap);	
			System.out.println("Next Generation: " + parents);
			iterations--;
		}
		
		System.out.println("Finished!  Final population is: ");
		System.out.println(parents);
		Collection<City> values = cityMap.values();
		System.out.println(values.toString());
		
	}

}
