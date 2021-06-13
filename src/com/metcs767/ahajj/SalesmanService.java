package com.metcs767.ahajj;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Salesman Service
 * 
 * Service class to perform crossover, mutation, etc
 * 
 * 
 * @author Andrew
 *
 */

public class SalesmanService {
	
	// each city can be represented by 2 bytes (16 bits)
	// this means we have a limitation of 256 cities
	private static final int MAX_BYTES = 2;
	private static final int MAX_BITS = 8 * MAX_BYTES;
	
	// probability of mutation
	private static final int MUTATION_FACTOR = 100000;

	/**
	 * Generate a list of routes for a given set of cities
	 * Number of routes returned is (n-1)! where n is the number of cities (since the starting city is defined)
	 * Has option to include starting city as first and last cities within the routes
	 * 
	 * @param cities map of cities where the key is the city id
	 * @param startingCityId id of the starting city
	 * @param includeStartingCity true if the starting city should added as the first and last cities in the route.  False to leave out
	 * @return a list of all possible routes from the starting city
	 */
	public List<List<Integer>> generateRoutesForStartingLocation(Map<Integer, City> cities, Integer startingCityId, Boolean includeStartingCity) {
		
		// Number of possible routes from the starting location is (n-1)! where n is the number of cities
		Set<Integer> cityIds = cities.keySet();
		
		// remove the starting city from the set
		cityIds.remove(startingCityId);
		List<Integer> citylist = new ArrayList<Integer>();
		citylist.addAll(cityIds);
		// create routes 
		List<List<Integer>> permutationsOfRoutes = generatePerm(citylist); 
		
		// Add the starting city as the first and last cities in the route if needed
		if(includeStartingCity) {
			addStartingCityToStartAndEndOfRoutes(permutationsOfRoutes, startingCityId);
		}
		
		for(int i = 0; i < permutationsOfRoutes.size(); i++) {
			System.out.println(permutationsOfRoutes.get(i));
			System.out.println(binaryStringRepresentationOfRoute(permutationsOfRoutes.get(i)));
		}
		
		return permutationsOfRoutes;
		
	}
	
	/**
	 * Adds a given city to the start and end of each route
	 * Note: Modifies the existing List
	 * 
	 * @param routes various routes
	 * @param startingCityId the id of the starting city
	 */
	public void addStartingCityToStartAndEndOfRoutes(List<List<Integer>> routes, Integer startingCityId) {
		for (int i = 0; i < routes.size(); i++) {
			List<Integer> tempList = routes.get(i);
			tempList.add(0, startingCityId);
			tempList.add(startingCityId);
			routes.set(i, tempList);
		}
	}
	
	/**
	 * Create a child from two parent routes
	 * Applies a crossover to generate the child, and will run through a mutation function
	 * 
	 * @param parentOne first parent to create a child from
	 * @param parentTwo second parent to create a child from
	 * @return child a child of the parents
	 */
	public List<Integer> createChildFromParents(List<Integer> parentOne, List<Integer> parentTwo) {
		
		// first print the parents in binary notation for us to see
		String parentOneStr = binaryStringRepresentationOfRoute(parentOne);
		String parentTwoStr = binaryStringRepresentationOfRoute(parentTwo);
		System.out.println("Parent 1: " + parentOneStr);
		System.out.println("Parent 2: " + parentTwoStr);
		
		// generate a random crossover point, should not be in the starting or ending points (since they are the starting city)
		int crossoverPoint = generateRandomNumberInRange(MAX_BITS, parentOneStr.length()-MAX_BITS);
		
		// once a crossover point is found, then split the parents at that point.
		// Child = PARENT1_CROSSOVER_SECTION + PARENT2_CROSSOVER_SECTION
		String childBinaryRep = parentOneStr.substring(0, crossoverPoint);
		childBinaryRep += parentTwoStr.substring(crossoverPoint);
		
		System.out.println("Child:    " + childBinaryRep);
		
		// pass the created child through a mutator function
		childBinaryRep = mutateRoute(childBinaryRep);

		System.out.println("M Child:  " + childBinaryRep);
		
		// translate that binary string to a list of integers (city ids)
		List<Integer> cityIds = convertBinaryStringToList(childBinaryRep);
		
		System.out.println(cityIds);
		return cityIds;
	}
	
	/**
	 * Mutate a given route.  There is a define mutation probability (0.001) per bit.  
	 * Mutated bits get flipped (so 0 becomes 1, and 1 becomes 0).
	 * 
	 * @param route binary representation of a route
	 * @return mutatedRoute mutated binary representation of a route
	 */
	private String mutateRoute(String route) {
		
		String mutatedRoute = "";
		boolean mutateBit = false;
		Character c;
		for (int i = 0; i < route.length(); i++) {
			
			// check if random number generated is equal to 10 (1 in MUTATION_FACTOR chance)
			mutateBit = (generateRandomNumberInRange(MUTATION_FACTOR, 0)) == 10 ? true : false;
			
			// pull the current character from the string
			c = route.charAt(i);
			
			// flip the bit if it is to be mutated
			if (mutateBit) {
				c = (c.equals('1') ? '0' : '1');
			}
			
			mutatedRoute += c;
		}
		
		return mutatedRoute;		
	}
	
	/**
	 * Generate a score for a given route.  Scores are factor in: 
	 * 1. Milage
	 * 2. Are all cities visited?
	 * 3. Are there any fake cities? (caused by mutation or crossover)
	 * 
	 * Low scores are good in this case since we want the lowest milage
	 * 
	 * @param route
	 * @param cities
	 * @return
	 */
//	public Integer generateRouteScore(List<Integer> route, Map<Integer, City> cities) {
//		
//		Integer score 0;
//		
//		
//	
//		
//		return null;
//		
//	}
//	
	
	/**
	 * Function to generate a random number in a given range.
	 * @param max the max number to be randomly generated
	 * @param min the min number to be randomly generated
	 * @return random number between min and max
	 */
	private static Integer generateRandomNumberInRange(int max, int min) {
	  return (int) Math.round(Math.random() * (max - min) + min);
	}
	
	/**
	 * Generates a binary string from a list of integers
	 * @param route a list of city ids
	 * @return routeToGo a binary string representation
	 */
	public String binaryStringRepresentationOfRoute(List<Integer> route) {
		String routeToGo = "";
		
		for (int i = 0; i < route.size(); i++) {
			routeToGo += String.format("%"+ MAX_BITS + "s", Integer.toBinaryString(route.get(i))).replace(" ", "0");
		}
		return routeToGo;
	}
	
	/**
	 * Generates a list of city ids from a binary string representation of a route
	 * @param route binary representation of a route
	 * @return routeToGo a list of city Ids
	 */
	public List<Integer> convertBinaryStringToList(String route) {
		List<Integer> routeToGo = new ArrayList<Integer>();
		
		
		for (int i = 0; i < route.length()/MAX_BITS; i++) {
			routeToGo.add(Integer.parseInt(route.substring(i*MAX_BITS, (i+1)*MAX_BITS), 2));
		}
		return routeToGo;
	}
	
	/**
	 * COPIED FROM STACKOVERFLOW
	 * Author: DaveFar
	 * Source:
	 * https://stackoverflow.com/questions/10305153/generating-all-possible-permutations-of-a-list-recursively
	 * 
	 * Function to aide with pulling together all permutations of a list
	 * Utilized here as the purpose of this assignment is not to come up with an algorithm to get all permutations
	 * 
	 * @param original list of cities
	 * @return all posibile permutations of that list 
	 */
	public <E> List<List<E>> generatePerm(List<E> original) {
		if (original.isEmpty()) {
			List<List<E>> result = new ArrayList<>(); 
			result.add(new ArrayList<>()); 
			return result; 
		}
		E firstElement = original.remove(0);
		List<List<E>> returnValue = new ArrayList<>();
		List<List<E>> permutations = generatePerm(original);
		for (List<E> smallerPermutated : permutations) {
			for (int index=0; index <= smallerPermutated.size(); index++) {
				List<E> temp = new ArrayList<>(smallerPermutated);
				temp.add(index, firstElement);
				returnValue.add(temp);
			}
		}
		return returnValue;
	}
}
