package com.metcs767.ahajj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
	//private static final int MAX_BYTES = 2;
	private static int maxBits = 0;
	private static int penaltyBase = 2000;
	
	// probability of mutation
	private static final int MUTATION_FACTOR = 1000;
	
	private List<Double> scoresForIterations = new ArrayList<Double>();

	
	public List<List<Integer>> generateNumOfRoutesForStartingLocation(int numPermutations, Map<Integer, City> cities, Integer startingCityId, Boolean includeStartingCity) {
		
		// Number of possible routes from the starting location is (n-1)! where n is the number of cities
		Set<Integer> cityIds = cities.keySet();
		
		// remove the starting city from the set
		City startingCity = cities.get(startingCityId);
		cityIds.remove(startingCityId);
		List<Integer> citylist = new ArrayList<Integer>();
		citylist.addAll(cityIds);
		cities.put(startingCityId, startingCity);
		// create routes 
		List<List<Integer>> permutationsOfRoutes = new ArrayList<List<Integer>>();
		List<Integer> tempCities = new ArrayList<Integer>();
		for (int i = 0; i < numPermutations; i++) {
			tempCities = citylist;
			Collections.shuffle(tempCities);	
			
			// if the list of routes already contains this route
			// then shuffle and try again
			while (permutationsOfRoutes.contains(tempCities)) {
				Collections.shuffle(tempCities);	
			}
			ArrayList<Integer> tempCit = (ArrayList<Integer>) ((ArrayList<Integer>) tempCities).clone();
			permutationsOfRoutes.add(tempCit);
		}
		
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
		int crossoverPoint = generateRandomNumberInRange(parentOneStr.length()-maxBits, maxBits);
		
		// once a crossover point is found, then split the parents at that point.
		// Child = PARENT1_CROSSOVER_SECTION + PARENT2_CROSSOVER_SECTION
		String childBinaryRep = parentOneStr.substring(0, crossoverPoint);
		childBinaryRep += parentTwoStr.substring(crossoverPoint);
		
		System.out.println("Child:    " + childBinaryRep);
		
		// pass the created child through a mutator function
		childBinaryRep = mutateRouteBySwappingCities(childBinaryRep);

		System.out.println("M Child:  " + childBinaryRep);
		
		// translate that binary string to a list of integers (city ids)
		List<Integer> cityIds = convertBinaryStringToList(childBinaryRep);
		
		System.out.println(cityIds);
		return cityIds;
	}
	
	/**
	 * Mutate a given route by swapping two cities.  There is a defined mutation probability of 1 in 1000
	 * Mutated cities get swapped with a random route
	 * Only the first and the last city will not be mutated
	 * Since that is the starting/ending point
	 * 
	 * @param route binary representation of a route
	 * @return mutatedRoute mutated binary representation of a route
	 */
	private String mutateRouteBySwappingCities(String route) {
		
		List<String> list= new ArrayList<String>();
		int index = 0;
		while (index<route.length()) {
		    list.add(route.substring(index, Math.min(index+maxBits,route.length())));
		    index=index+maxBits;
		}
		
		String mutatedRoute = "";
		boolean mutateBit = false;
		mutatedRoute = route.substring(0, maxBits);
		for (int i = 1; i < list.size()-1; i++) {
			
			// check if random number generated is equal to 10 (1 in MUTATION_FACTOR chance)
			mutateBit = (generateRandomNumberInRange(MUTATION_FACTOR, 0)) == 10 ? true : false;
			
			// flip the bit if it is to be mutated
			if (mutateBit) {
				int locationToSwap = generateRandomNumberInRange(list.size()-2, 1);
				
				// ensure the city isn't swapping itself
				while (locationToSwap == i) {
					locationToSwap = generateRandomNumberInRange(list.size()-2, 1);
				}

				System.out.println("MUTATION: swapping " + i + " with " + locationToSwap);
		        Collections.swap(list, i, locationToSwap);
			}
			
		}
		for (int i = 1; i < list.size(); i++) {
			mutatedRoute += list.get(i);			
		}
		return mutatedRoute;		
	}
	
	/**
	 * Mutate a given route.  There is a define mutation probability (0.001) per bit.  
	 * Mutated bits get flipped (so 0 becomes 1, and 1 becomes 0).
	 * Only the first and the last city will not be mutated
	 * Since that is the starting/ending point
	 * 
	 * @param route binary representation of a route
	 * @return mutatedRoute mutated binary representation of a route
	 */
	private String mutateRoute(String route) {
		
		String mutatedRoute = "";
		boolean mutateBit = false;
		Character c;
		mutatedRoute = route.substring(0, maxBits);
		for (int i = maxBits; i < route.length()-maxBits; i++) {
			
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
		mutatedRoute += route.substring(route.length()-maxBits);
		
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
	public Double generateRouteScore(List<Integer> route, Map<Integer, City> cities) {
		
		Double score = 0d;
		
		// Check if the two lists contain the same elements
		// this will decide if all cities have been visited
		// as well as if there are any fake cities
		boolean validRoute = listEqualsIgnoreOrder(route, cities.keySet());
		
		if (!validRoute) {
			// if the route is not valid then give it a large score
			score += (penaltyBase*maxBits);
		}
		
		// check the milage
		score += calculateMilageInRoute(route, cities);

		// Square the milage to make bigger gaps on similar distances/routes;
		score = Math.pow(score, 2);

		System.out.println(route + " : Score " + score);
		return score;
		
	}
	
	/**
	 * Function to check if two lists contain the same elements
	 * Doesn't take into account duplicates but is okay since our map of cities has unique keys
	 * 
	 * Based on an answer on this stackoverflow
	 * https://stackoverflow.com/questions/1075656/simple-way-to-find-if-two-different-lists-contain-exactly-the-same-elements
	 * 
	 */
	private boolean listEqualsIgnoreOrder(List<Integer> list1, Set<Integer> set) {
	    return new HashSet<>(list1).equals(set);
	}
	
	private Double calculateMilageInRoute(List<Integer> route, Map<Integer, City> cities) {
		
		City first;
		City second;
		Double milage = 0d;
		
		for (int i = 0; i < route.size()-1; i++) {
			
			if (cities.containsKey(route.get(i)) && 
					cities.containsKey(route.get(i+1))) {
				
				first = cities.get(route.get(i));				
				second =cities.get(route.get(i+1));
				
				milage += Math.sqrt((second.getY() - first.getY()) * (second.getY() - first.getY()) 
						+ (second.getX() - first.getX()) * (second.getX() - first.getX()));
				
			} else {
				milage += (penaltyBase*maxBits);
			}
			
		}
		
		return milage;
		
	}
	
	/**
	 * Generates a new population of routes passed in
	 * First, it rates the passed in routes (low milage routes score best)
	 * Then, it assigns a probability to each route and decides which will be parents
	 * Finally, it creates children and returns that
	 * 
	 * @param population current list of routes to be rated and mated
	 * @param cities list of all cities. Used to figure out milage in routes
	 * @param someParentsSurvive if true then 1:4 children will be an elite parent (low milage)
	 * @return new population of routes
	 */
	public List<List<Integer>> selectParentsAndGenerateChildren(List<List<Integer>> population, Map<Integer, City> cities, boolean someParentsSurvive) {
		
		DistributedRandomNumberGenerator drng = new DistributedRandomNumberGenerator();
		
		List<List<Integer>> newRoutes = new ArrayList<List<Integer>>();
		List<List<Integer>> parentRoutes = new ArrayList<List<Integer>>();
		// first rate the current routes
		// 'low' scores should be rated higher as that means the milage was low
		
		Map<Double, List<Integer>> ratingToRoute = new HashMap<Double, List<Integer>>();
		Map<Integer, Double> indexToScore = new HashMap<Integer, Double>();
		Map<Integer, Double> indexToRating = new HashMap<Integer, Double>();
		
		Double sumOfScores = 0d;
		Double curScore = 0d;
		Double factor = 0d;
		Double smallestScore = 0d;
		// calculate the total scores and match score (milage) to route
		for (int i = 0; i < population.size(); i++) {
			curScore = generateRouteScore(population.get(i), cities);
//			indexToScore.put(i, curScore);
			ratingToRoute.put(curScore, population.get(i));
			indexToScore.put(i, curScore);
			
			sumOfScores += curScore;
			factor += (1/curScore);
			
			if (i == 0) {
				smallestScore = curScore;
			}
			else if (smallestScore > curScore) {
				smallestScore = curScore;
			}
		}
		
		scoresForIterations.add(smallestScore);
		
		// finally we need to flip the factor 
		factor = (1/factor);
		

		// now that we have a score per route, we select the parents for the next generation
		// we select the next set of parents using probability.  Low scores have a higher probability 
		// since that means they had low milage
		double distribution = 0d;
		for (int i = 0; i < population.size(); i++) {
			// rating is total scores (milage) - routes score (milage)
			// done this way since low milage rates higher
			distribution = (1/indexToScore.get(i))*factor;
			drng.addNumber(i, distribution);
			indexToRating.put(i, distribution);
		}
		
		System.out.println("Distribution is " + indexToRating);
		
		// now that we have a map of index to rating we can use that to randomly select parents
		for (int i = 0; i < population.size(); i++) {
			
			// index of a route in the population
			int random = drng.getDistributedRandomNumber();
			parentRoutes.add(population.get(random));
		}

		
		// finally, we generate some children from the parents
		// parent pairs are chosen randomly
		int parentOneIndex;
		int parentTwoIndex;
		int numGeneratedChildren = (int) ((someParentsSurvive) ? Math.round(population.size() * 0.75) : population.size());
		for (int i = 0; i < numGeneratedChildren ; i++) {
			parentOneIndex = generateRandomNumberInRange(population.size()-1, 0);
			parentTwoIndex = generateRandomNumberInRange(population.size()-1, 0);

			while (parentOneIndex == parentTwoIndex)
			{
				parentTwoIndex = generateRandomNumberInRange(population.size()-1, 0);
			}
			newRoutes.add(createChildFromParents(parentRoutes.get(parentOneIndex), parentRoutes.get(parentTwoIndex)));
		}
		
		// add in some parents back to the new generation if the flag is set (populations.size() & numGeneratedChildren will be the same if false)
		for (int i = 0; i < (population.size() - numGeneratedChildren); i++) {
			newRoutes.add(parentRoutes.get(generateRandomNumberInRange(population.size()-1, 0)));
			System.out.println("Elite parent " + newRoutes.get(newRoutes.size()-1) + " added");
		}
		
		return newRoutes;
	}
	
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
			routeToGo += String.format("%"+ maxBits + "s", Integer.toBinaryString(route.get(i))).replace(" ", "0");
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
		
		
		for (int i = 0; i < route.length()/maxBits; i++) {
			routeToGo.add(Integer.parseInt(route.substring(i*maxBits, (i+1)*maxBits), 2));
		}
		return routeToGo;
	}
	
	public void setMaxBitsFromNumberOfCities(int numCities) {
		boolean foundPowerOfTwo = false;
		int curPower = 0;
		while(!foundPowerOfTwo) {
			if (Math.pow(2.0d, curPower) >= numCities) {
				maxBits = curPower;
				foundPowerOfTwo = true;
			}
			curPower++;
		}
	}
	
	public void printScoresOfIterations() {
		System.out.println(scoresForIterations);
	}
}
