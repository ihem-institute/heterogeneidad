
//import java.io.BufferedReader;populationProp
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class HetIndex {

	public static void main(String[] args) {
		// diffMet indicate the number of different percentage of metilation
		// among different genes for tumor/cell sample.
		// the higher diffMet observed, higher is the number of clones
		//that are necessary to explain this number
		// This program search to see if the number of clones can be lower than
		// the diffMet.

		int diffMet = 5; // number of different metilated sites. Maximum is 5
		// int cellNumber = 2;// number of expected clones (cannot be larger
		// than 4

		int maxCells = 4; // maximum number of cells to be tested
		double okError = 0.0001;// when reached, the search for better
//		combination of cells and frequency stops
		HashMap<String, Double> model = new HashMap<String, Double>();
		HashMap<String, Double> bestModel = new HashMap<String, Double>();
		HashMap<String, Double> observed = new HashMap<String, Double>();
		// HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		HashMap<Integer, HashMap<Integer, Double>> setHetero = new HashMap<Integer, HashMap<Integer, Double>>();
		Scanner stdin = new Scanner(System.in).useLocale(Locale.UK); // Create the Scanner.
		System.out
				.println("enter number of metilated sites + metilation index ");

		List<Double> metSet = new ArrayList<Double>();
		double metN = stdin.nextDouble();
		for (int ii = 0; ii < metN; ii++) {
			metSet.add(stdin.nextDouble());
		}

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
			for (int cellNumber = 1; cellNumber <= maxCells; cellNumber = cellNumber + 1) {

				double minError = 100000.0;// the minimal difference between
// observed and the model is set to a large number

				int allPop = (int) (Math.pow(2, diffMet)); // 2^3 = 8, 2^4 = 16
// generate a string where each population is coded by a two
// decimal digits (100 different binary number representing 100
// populations with different metilations)
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

				// initialize the error from the observed to the model
				double error1 = 0.0;
/*
Iteration over the space of parameters and populations
Method that return all the possible combination of the
populations in set of size cellNumber. For example if 3
different metilations are evident and we may want to obtain the best 3
populations with the corresponding proportions of each.
Since the metilation site can be off/on, it can be represented by
 a binary number. The possibilities of off/on of three metilation site
in an aploid cell then can be represented by a binary number. For 3 distinct
 metilation sites, then 2^3=8 population are possible and all combinations of 3
 out of the 8 are returned.
 If we want the best result but with only 2 clones, the we
 must screen  for  all possible combination two cell from the 8 population set,
 and search for eacho of them, the best proportion to minimize the difference between
 the model and the observed.
 For diffMet = 4, 16 different populations are possible. We may
select 4, 3 or 2 populations 
For 5 diffMet, 32 different populations are possible. If 5 population
are selected the program may take too long to get an
 answer(32!/(27!)/(5!)combinations are possible)
 */
		ArrayList<String> populationSet = populationSet(allPopString,
						cellNumber);


// for each population, a screening of all combinations of
// proportions are selected
		int delta = 16;// delta is the number of different proportions
				// that are considered for each population
				// in this case, the proportions are evaluated in 16 intervals
				// populationProp method return a set of all possible
				// combinations of proportions that sum 1. 

				ArrayList<String> populationProp = populationProp(delta,
						cellNumber);
//				System.out.println("proportions" + populationProp.size()
//						+ populationProp);

// with the set of all possible combinations of populations
// (populationSet) and
// all possible combinations of proportion that add to 1, the
// predicted metilations
// are calculated and compared with the observed. The minimum is
// stored together with the population and the proportion
				String bestPop = "";
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
		for (int i = 0; i < count; i++) 
			{
			double sumErr = sumErr(setHetero.get(i));
			System.out.println(sumErr);
			}
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
