

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HetIndex {
	// a single HetIndex is created
	private static HetIndex instance;
	public static HetIndex getInstance() {
		if (instance == null) {
			instance = new HetIndex();
		}
		return instance;
	}


	List<Double> metSet = new ArrayList<Double>();

	public static ArrayList<Double> hetIndex(List<Double> metSet, int maxCells) {
		HashMap<String, Double> observed = new HashMap<String, Double>();
		int diffMet = 5; // number of different metilated sites.  It is set to 5
		// number of combined pure clones (maxCells).  In this algorithm, 5 clones are
		// managed fast enough.  In the old algorithm, 4 was a limit and it was very slow
		// 
		// The method will try to fit the observed data with 1 to 5 clones
		// Each clone is a combination of five 0-or-1 digits.  2^5 clones are possible
		double okError = 0.0001;// when reached, the search for better
		//		combination of cells and frequency stops
		HashMap<String, Double> model = new HashMap<String, Double>();
		HashMap<String, Double> bestModel = new HashMap<String, Double>();
		// HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		HashMap<Integer, HashMap<Integer, Double>> setHetero = new HashMap<Integer, HashMap<Integer, Double>>();

		int allPop = (int) (Math.pow(2, diffMet)); // 2^5 = 32
		// generate a string where each population is coded by a two
		// decimal digits (This will work for diffMet up to 6 2^6 = 64)
		// All numbers are stored as strings with the same number of
		// digits (2)
		String allPopString = "";
		for (int i = 0; i < allPop; i++) {
			String iString = Integer.toString(i);
			if (iString.length() < 2)
				iString = "0" + iString;// add a "0" if it is a single
			// digit
			// number
			allPopString = allPopString + iString;
		}
System.out.println(allPopString);
		
		//Loop taking set of five values from the metSet (methylation set) sent 
		// by the main and loaded in the hetIndex method
		int count = 0;
		for (int ii = 0; ii < metSet.size(); ii = ii + 5) {
			observed.put("M0", metSet.get(ii + 0));
			observed.put("M1", metSet.get(ii + 1));
			observed.put("M2", metSet.get(ii + 2));
			observed.put("M3", metSet.get(ii + 3));
			observed.put("M4", metSet.get(ii + 4));
			System.out.println(observed);
			HashMap<Integer, Double> result = new HashMap<Integer, Double>();
			// Sets of 4, 3, or 2 populations are managed well.
			double maxError = 0.0;
			String bestPop = "";
			//Loop fitting the observed values using 1 to 5 clones
			for (int cellNumber = 1; cellNumber <= maxCells; cellNumber = cellNumber + 1)
			{
				double minError = 100000.0;// the minimal difference between
				// observed and the model is set to a large number
				// initialize the error from the observed to the model
				double error1 = 0.0;
				/*
Iteration over the space of parameters and populations
The population set in this algorithm is constructed choosing the best fit with
one population. Then try the best fit including this clone and some of the other 32 clones.
Then trying the best fit adding a third clone and so on.
At each step, the proportion of each clone is re-tested

				 */
				ArrayList<String> singlePopulationSet = populationSet(allPopString,
						1);
				//bestPop is initially "" and then is replaced by the best combination
				// of 1, 2, 3 .... clones.  The loop add this clones to the possible 32 clones
				// to be tested in all possible proportions to get the best one
				ArrayList<String> populationSet = new ArrayList<String>();
				for (String s : singlePopulationSet){
					populationSet.add(bestPop + s);
				}

				System.out.println(populationSet + "NUEVO" + bestPop);
				// for each population set, a screening of all combinations of
				// proportions are selected
				int delta = 16;// delta is the number of different proportions
				// that are considered for each population
				// in this case, the proportions are evaluated in 16 intervals
				// populationProp method return a set of all possible
				// combinations of proportions that sum 1. 

				ArrayList<String> populationProp = populationProp(delta,
						cellNumber);
				// with the set of all possible combinations of populations
				// (populationSet) and
				// all possible combinations of proportion that add to 1, the
				// predicted methylations
				// are calculated and compared with the observed. The minimum is
				// stored together with the population and the proportion
//				String bestPop = "";
				String bestProp = ""; 
				search: {
					for (String s : populationSet) {
						for (String p : populationProp) {
							model = modelEvaluation(s, p, cellNumber, diffMet);
							error1 = errorEvaluation(observed, model);
							if (error1 > maxError)
								maxError = error1;
							if (error1 < minError) {
								minError = error1;
								bestPop = s;
								bestProp = p;
								bestModel = model;
								if (minError < okError)
									break search;
							}

						}

					}

				}

				System.out.println("\ncell number " + cellNumber);
				System.out.println("minerror " + minError);
				System.out
				.println("best population " + bestPop);
				System.out.print("proportion ");
				System.out.print(bestProp + "  ");
				System.out.println("");
				for (String key : bestModel.keySet()) {
					System.out.println(key + " "
							+ Math.round(bestModel.get(key) * 100)
							/ 100d + " " + observed.get(key));
				}
				result.put(cellNumber, minError);
			}
			result.put(0, maxError);
			System.out.println("\nerrors " + result);
			double sumErr = sumErr(result);
			System.out.println("sumaErrores " + sumErr);
			//System.out.println("area " + area );
			System.out.println("END\n");
			setHetero.put(count, result);
			count = count + 1;

		}
		ArrayList<Double> resultsHI = new ArrayList<Double>();
		for (int i = 0; i < count; i++) 
		{
			Double sumErr = sumErr(setHetero.get(i));
			resultsHI.add(sumErr);
			System.out.println(i +"   "+ sumErr);
		}
		return resultsHI;
	}

	private static double sumErr(HashMap<Integer, Double> setHetero) {
		double sumErr = 0;
		int cellsMax = setHetero.keySet().size()-1;
		//		when the error increases instead of decreasing it takes the error value of the lowest
		for (int i = 1 ; i <= cellsMax; i++){
			if (setHetero.get(i-1)<setHetero.get(i)) setHetero.put(i, setHetero.get(i-1));
		}
		for (int i = 1; i <= cellsMax; i++) {
			sumErr = sumErr + setHetero.get(i);
		}
		return sumErr ;	
	}

	private static void getValues() {
		// TODO Auto-generated method stub
		GetValues.main();
	}

	public static ArrayList<String> populationProp(int delta, int cellNumber) {
		ArrayList<String> populationProp = new ArrayList<String>();
		if (cellNumber == 1) {
			populationProp.add(Integer.toHexString(delta));
			return populationProp;
		}
		for (int i = 0; i < Math.pow(delta, cellNumber); i++) {
			String number = Integer.toHexString(i);

			int sum = 0;
			for (int l = 0; l < number.length(); l++) {
				Integer dd = Integer.parseInt(number.substring(l, l + 1), 16);
				// System.out.println(digitList);
				sum = sum + dd;
				// System.out.println("digit     " +sum);
			}
			// System.out.println("SUMA     " +sum);
			if (sum == delta)
				populationProp.add(Integer.toHexString(i));
		}
		return populationProp;

	}

	// screening of all combination of diffMet populations from a set of
	// all possible populations (2^diffMet).
	// the algorithm used was copied from the internet and adapted to generate
	// combiantions
	// of two digits and to store in an ArrayList as strings. To be honest, I do
	// not how it works
	// but the results are correct.

	public static ArrayList<String> populationSet(String data, int howMany) {
		ArrayList<String> populationSet = new ArrayList<String>();
		populationSet = choose(populationSet, data, howMany,
				new StringBuffer(), 0);
		System.out.println(populationSet);
		return populationSet;

	}

	// this method is the one that generate all the combinations. It calls
	// itself!!
	// it is beyond my understanding
	// n choose k
	private static ArrayList<String> choose(ArrayList<String> populationSet,
			String data, int k, StringBuffer result, int startIndex) {
		if (result.length() == 2 * k) {
			List<String> myList = new ArrayList<String>(Arrays.asList(result
					.toString()));
			populationSet.add(result.toString());
			return populationSet;
		}
		for (int i = startIndex; i < data.length(); i = i + 2) {
			result.append(data.charAt(i));
			result.append(data.charAt(i + 1));
			choose(populationSet, data, k, result, i + 2);
			result.setLength(result.length() - 2);
		}
		return populationSet;
	}

	/*
	 * For the model evalulation, the metilation of each site is calculated
	 * adding the the proportion of each population with the state of metilation
	 * if this particualar site (0 or 1). The state of metilation is extracted
	 * from the binary number representing the population. The proportions is
	 * obtained from the decimal digits cominag as a string
	 */
	public static HashMap<String, Double> modelEvaluation(String population,
			String proportion, Integer cellNumber, Integer diffMet) {
		ArrayList<Integer> pop = new ArrayList<Integer>();
		double[] prop = new double[cellNumber];
		HashMap<String, Double> model = new HashMap<String, Double>();
		for (int i = 0; i < population.length(); i = i + 2) {
			pop.add(Integer.valueOf(population.substring(i, i + 2)));
		}
		String propString = proportion;
		while (propString.length() < cellNumber) {
			propString = "0" + propString;
		}
		if (cellNumber == 1) {
			prop[0] = 1;
		} else {
			for (int i = 0; i < propString.length(); i++) {
				Integer pp = Integer.parseInt(propString.substring(i, i + 1),
						16);
				prop[i] = (double) pp / 16;// OJO fijado para hexad
			}
		}

		for (int i = 0; i < diffMet; i++) {
			double value = 0;
			int pr = 0;
			for (int j : pop) {
				int binary = 0;
				int length = Integer.toBinaryString(j).length();
				if (i < length) {
					binary = Integer.valueOf(Integer.toBinaryString(j)
							.substring(length - i - 1, length - i));
				}

				value = value + prop[pr] * binary;
				pr = pr + 1;
			}
			model.put("M" + i, value);
		}

		return model;
	}

	/*
	 * Same than the modelEvalulation, but modified because the proportions here
	 * comes as a set of doubles
	 */
	public static HashMap<String, Double> fineModelEvaluation(
			String population, double[] prop, Integer cellNumber,
			Integer diffMet) {
		ArrayList<Integer> pop = new ArrayList<Integer>();

		HashMap<String, Double> model = new HashMap<String, Double>();
		for (int i = 0; i < population.length(); i = i + 2) {
			pop.add(Integer.valueOf(population.substring(i, i + 2)));
		}

		for (int i = 0; i < diffMet; i++) {
			double value = 0;
			int pr = 0;
			for (int j : pop) {
				int binary = 0;
				int length = Integer.toBinaryString(j).length();
				if (i < length) {
					binary = Integer.valueOf(Integer.toBinaryString(j)
							.substring(length - i - 1, length - i));
				}
				value = value + prop[pr] * binary;
				pr = pr + 1;

			}
			model.put("M" + i, value);
		}

		return model;
	}

	/*
	 * The difference betweeen the model and the observed metilation is obtenied
	 * as a square of the difference
	 */
	private static double errorEvaluation(HashMap<String, Double> observed,
			HashMap<String, Double> model) {

		double error1 = 0.0;
		for (String met : observed.keySet()) {
			error1 = error1 + Math.pow((observed.get(met) - model.get(met)), 2);
		}
		return error1;
	}

}
