
//import java.io.BufferedReader;populationProp
import heterogeneidad.GetValues;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class TestScreen16 {

	public static void main(String[] args) {
		// diffMet indicate the number of different percentage of metilation
		// among different genes for tumor/cell sample.
		// the higher diffMet observed, higher is the number of clone of

		// chromosomes that are necessary to explain this number
		// This program search to see if the number of clones can be lower than
		// the diffMet.
		// getValues();
		int diffMet = 5; // number of different metilated sites. Maximum is 5
		// int cellNumber = 2;// number of expected clones (cannot be larger
		// than 4
		// int improve = 1; // use 0 for fast results. Use 1 to better fir and
		// to
		// decrease the error
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
		// Observed metilation proportions. If less than
		List<Double> metSet = new ArrayList<Double>();
		double metN = stdin.nextDouble();
		for (int ii = 0; ii < metN; ii++) {
			metSet.add(stdin.nextDouble());
			// parse inputLine however you want, and add to your vector
		}
//		System.out.println(metSet);
		int count = 0;
		for (int ii = 0; ii < metSet.size(); ii = ii + 5) {
			observed.put("M0", metSet.get(ii + 0));
			observed.put("M1", metSet.get(ii + 1));
			observed.put("M2", metSet.get(ii + 2));
			observed.put("M3", metSet.get(ii + 3));
			observed.put("M4", metSet.get(ii + 4));
			System.out.println(observed);
			HashMap<Integer, Double> result = new HashMap<Integer, Double>();
			// if (2<4) return;
			/*
			 * to use entering parameters from the console try { int a =
			 * readConsole(); } catch (NumberFormatException | IOException e) {
			 * // TODO Auto-generated catch block e.printStackTrace(); }
			 */
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
//				System.out.println(cellNumber + "  POPULATIONSET"
//						+ populationSet);
// if (cellNumber > 3)return;

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
	//			bestModel 
				search: {
					for (String s : populationSet) {
						for (String p : populationProp) {
							model = modelEvaluation(s, p, cellNumber, diffMet);
							// System.out.println(model);
							error1 = errorEvaluation(observed, model);
							if (error1 > maxError)
								maxError = error1;
							if (error1 < minError) {
								minError = error1;
								bestPop = s;
								bestProp = p;
								bestModel = model;
//								System.out.println("");
//								System.out.println("minerror " + minError);
//								System.out
//										.println("best population " + bestPop);
//								for (int k = 0; k < cellNumber * 2; k = k + 2) {
//									String pop = bestPop.substring(k, k + 2);
//									System.out.println(" "
//											+ Integer.toBinaryString(Integer
//													.valueOf(pop)));
//								}
//
//								System.out.print("proportion ");
//								System.out.print(p + "  ");
//								System.out.println("");
//								for (String key : model.keySet()) {
//									System.out.println(key + " "
//											+ Math.round(model.get(key) * 100)
//											/ 100d + " " + observed.get(key));
//								}
								if (minError < okError)
									break search;
							}

						}

					}
					/*
					 * if (improve == 1) { for (int rest = -1; rest < 2; rest =
					 * rest + 2) { System.out.println("ahora suma " + rest);
					 * ArrayList<double[]> fineScrn = fineScrn(bestPop,
					 * bestProp, cellNumber, populationProp, rest); for (int i =
					 * 0; i < fineScrn.size(); i++) { double[] prSet =
					 * fineScrn.get(i); for (int j = 0; j < cellNumber; j++) {
					 * double pr = prSet[j]; //
					 * System.out.println("propENviada " + pr); } model =
					 * fineModelEvaluation(bestPop, prSet, cellNumber, diffMet);
					 * // System.out.println(model); error1 =
					 * errorEvaluation(observed, model); //
					 * System.out.println("Error1 " + error1); if (error1 <
					 * minError) { minError = error1; System.out.println("");
					 * System.out.println("minerror " + minError);
					 * System.out.println("best population " + bestPop); for
					 * (int k= 0; k< cellNumber*2; k=k+2){ String pop =
					 * bestPop.substring(k, k+2); System.out.println(" "+
					 * Integer.toBinaryString(Integer .valueOf(pop))); }
					 * 
					 * 
					 * System.out.print("proportion "); for (int j = 0; j <
					 * cellNumber; j++) { double pr = prSet[j];
					 * System.out.print(Math.round(pr*100)/100d + " "); }
					 * System.out.println(""); for (String key :
					 * model.keySet()){ System.out.println(key +" "+
					 * Math.round(model.get(key)*100)/100d); }
					 * //System.out.println("best model " + model); ;
					 * 
					 * if (minError == 0) break search; }
					 * 
					 * }
					 * 
					 * }
					 * 
					 * }
					 */
				}
//				System.out.println("cellNumber " + cellNumber + " SS(error) "
//						+ minError);
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
			int heteroIndex = heteroIndex(result);
			if (heteroIndex > result.size()-1) 
			System.out.println("hetero Index " + heteroIndex + " bad relative error " + result.get(heteroIndex-1)/result.get(0));
			else System.out.println("hetero Index " + heteroIndex + " relative error " + result.get(heteroIndex)/result.get(0));
			double area = area(result);
			double sumErr = sumErr(result);
			System.out.println("sumaErrores " + sumErr);
			System.out.println("area " + area );
			System.out.println("END\n");

			setHetero.put(count, result);
			// System.out.println(count + "    " + );
			count = count + 1;

			// System.out.println(setHetero);
		}
		for (int i = 0; i < count; i++) 
			{
			int heteroIndex = heteroIndex(setHetero.get(i));
//			if (heteroIndex > setHetero.get(i).size()-1){ 
//			System.out.println(i + " hetero Index " + heteroIndex + 
//					" bad relative error " + (setHetero.get(i).get(heteroIndex-1)/setHetero.get(i).get(0)) +
//					"\nerror values " + setHetero.get(i));
//			}
//			else{
//			System.out.println(heteroIndex);
			double sumErr = sumErr(setHetero.get(i));
			System.out.println(sumErr);
//			double area = area(setHetero.get(i));
//			System.out.println(area+ "\n");
			}

		

	}

	private static int heteroIndex(HashMap<Integer, Double> setHetero) {
		int index = 0;
		if (setHetero.get(1)<0.004) return index;
		for (int i = 1; i < setHetero.keySet().size(); i++) {
			double delta = (setHetero.get(index)-setHetero.get(i))/setHetero.get(0);
//			System.out.println(i + " delta " + delta);
			if (delta<0.004) continue;
			else index = i;
		}
//		System.out.println(index + " relative error value " + (setHetero.get(index-1)-setHetero.get(index))/setHetero.get(0));	
		if (setHetero.get(index)/setHetero.get(0)> 0.004){
			//System.out.println("bad adjusting  " + setHetero.get(index));
			index = index +1;
		}
		return index;
			
	}
	private static double area(HashMap<Integer, Double> setHetero) {
		double area = 0;
		double delta = 0;
		int cellsMax = setHetero.keySet().size()-1;
//		when the error increases instead of decreasing it takes the error value of the lowest
		for (int i = 1 ; i <= cellsMax; i++){
			if (setHetero.get(i-1)<setHetero.get(i)) setHetero.put(i, setHetero.get(i-1));
		}
		for (int i = 1; i <= cellsMax; i++) {
//			area of a triangle connecting error value in i-1 and i. The triangle's base is always 1
//			plus a rectangle from error value in i to zero with base =1
			double triangle =(setHetero.get(i-1)-setHetero.get(i))/2;
			delta = triangle + setHetero.get(i);
			area = area + delta;
//			System.out.println(i + " delta area " + delta/setHetero.get(0));
		}
//		last triangle between the last error value and the error at 5 that is zero.
		delta = (5-cellsMax)*setHetero.get(cellsMax)/2;
		area = area + delta;
//		divide the area for the initial error. This normalize the areas to the same scale
//		sustract 0.5 that is the area of a sample without heterogeneity 
//		(error at 0 = 1, error at 1 = 0; hence the triangle is 1x1/2=0.5)
//		the 100 factor is just to have more friendly numbers
		area = (area / setHetero.get(0)-0.5)*100;
//		System.out.println("area " + area + " relative area" + area/setHetero.get(0));	
		return area;		
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


	/*
	 * This method generate a set of array of proportions up and down the best
	 * proportions obtained in the first screen, to be combine with the the best
	 * combination of cells generated by the first screen. I switched to a set
	 * of arrays of doubles, to deal with the fact that the proportions
	 * generated would be not simple fractions (0.0 to 0.9) as in the first
	 * screening. It was a mess. If done again, a will use the same as the first
	 * screening but using two digits to represent fractions from 0.00 to 0.99
	 * Notice that the original proportion is decreased by 0.1/cellNumber and
	 * then increased by the combination of proportions that we know add to 0.1.
	 * Then the set of proportions will add to 1. In a second round, the
	 * contrary is done.
	 */
	public static ArrayList<double[]> fineScrn(String bestPop,
			Integer bestProp, Integer cellNumber,
			ArrayList<Integer> populationProp, int rest) {
		ArrayList<double[]> fineScrn = new ArrayList<double[]>();
		double[] bestPropAr = new double[cellNumber];
		double[] newProp = new double[cellNumber];
		// Convert integer to string and add "0" a the beginning if necessary
		String number = Integer.toString(bestProp);
		String propString = number;
		while (propString.length() < cellNumber) {
			propString = "0" + propString;
		}
		// from the string, take each digit and is included in an array of
		// double numbers
		// that will carry the different proportions to be applied to the best
		// set of cells
		for (int l = 0; l < cellNumber; l++) {
			Integer dd = Integer.valueOf(propString.substring(l, l + 1));
			bestPropAr[l] = (double) dd / 10;
		}
		// Convert integer to string and add "0" a the beginning if necessary
		// for each prop. It use the same of populationProp original, but
		// divided by 100 to
		// to generate a finer grid arroun the best values obtained in the firs
		// screen
		for (Integer s : populationProp) {
			searchProp: {
				number = Integer.toString(s);
				propString = number;
				while (propString.length() < cellNumber) {
					propString = "0" + propString;
				}
				for (int l = 0; l < cellNumber; l++) {
					Integer dd = Integer
							.valueOf(propString.substring(l, l + 1));
					double fine = (double) dd / 100;
					newProp[l] = (bestPropAr[l] - rest * 0.1 / cellNumber)
							+ rest * fine;
					if (newProp[l] < 0) {
						System.out.print("Negativa proportion" + newProp[l]);
						// because the sum and rest, negative proportions can be
						// generated
						// in this case, this set of proportions is not added to
						// fineScrn
						break searchProp;
					}
					// System.out.print("NEWPROP" + newProp[l]);

				}
				/*
				 * for (int j = 0; j < cellNumber; j++) { double pr =
				 * newProp[j]; System.out.print(""); System.out.print("newProp "
				 * + pr); System.out.print(""); }
				 */
				// System.out.println("new" + newProp[0]);
				// Using fineScrn.add(newProp) does not work. All proportions
				// are set to the last added!!
				double[] nn = new double[cellNumber];
				for (int i = 0; i < cellNumber; i++) {
					nn[i] = newProp[i];
				}

				fineScrn.add(nn);
			}

			/*
			 * for (int i = 0; i < fineScrn.size(); i++) { double[] prSet =
			 * fineScrn.get(i); for (int j = 0; j < cellNumber; j++) { double pr
			 * = prSet[j]; System.out.print("propBein added " + pr); }
			 * System.out.println(""); }
			 */

		}

		return fineScrn;
	}

	// screening of all combination of different proportions (for the number of
	// different methilations observed
	// that can be applied to a combination of populations from a set of
	// all possible populations (2^diffMet).
	// the number of proportion tested for each population is with a delta of
	// 0.1 (from 0 to 0.9)
	// I think it can be improved to delta of 0.01 easily, but the computation
	// is
	// then heavy.
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
		// populationSet.add(singeSet);
		// System.out.println("PopulationSet" + populationSet);
		return populationSet;

	}

	// this method is the one that generate all the combinations. It calls
	// itself!!
	// it is beyond my understanding
	// n choose k
	private static ArrayList<String> choose(ArrayList<String> populationSet,
			String data, int k, StringBuffer result, int startIndex) {
		// ArrayList<String> populationSet = new ArrayList<String>();
		if (result.length() == 2 * k) {
			// System.out.println(result.toString());
			List<String> myList = new ArrayList<String>(Arrays.asList(result
					.toString()));
			// System.out.println("MYLIST      " + myList);
			populationSet.add(result.toString());
			// System.out.println("vvvvvvvvvvvvvv" + populationSet);
			return populationSet;
		}
		for (int i = startIndex; i < data.length(); i = i + 2) {
			result.append(data.charAt(i));
			result.append(data.charAt(i + 1));
			choose(populationSet, data, k, result, i + 2);
			result.setLength(result.length() - 2);
			// System.out.println("intermedio"+result.toString());
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
		// System.out.println("POPulation  "+pop);
		String propString = proportion;
		while (propString.length() < cellNumber) {
			propString = "0" + propString;
		}
		// System.out.println("PropString  "+propString);
		if (cellNumber == 1) {
			prop[0] = 1;
		} else {
			for (int i = 0; i < propString.length(); i++) {
				Integer pp = Integer.parseInt(propString.substring(i, i + 1),
						16);
				prop[i] = (double) pp / 16;// OJO fijado para hexad
				// System.out.println("i "+ i +" " +prop[i]);
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
				/*
				 * System.out.println("i " + i + "jpop " +
				 * Integer.toBinaryString(j) + "  binary " + binary
				 * +"proportion "+ prop[pr]); //System.out.println("j " +
				 * j+"  "+prop[i]);
				 */
				value = value + prop[pr] * binary;
				pr = pr + 1;
				// System.out.println("  " + mix[j]);
			}
			model.put("M" + i, value);
			// System.out.println("M" + i + " = "+ value);
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
				// System.out.println("proporcion    "+prop[pr]);
				pr = pr + 1;

			}
			model.put("M" + i, value);
			// System.out.println("M" + i + " = "+ value);
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
			// System.out.println("error " + error1);
			error1 = error1 + Math.pow((observed.get(met) - model.get(met)), 2);
		}
		return error1;
	}

}
