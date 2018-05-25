

import heterogeneidad.GetValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class TestScreen10 {


	public static void main(String[] args) {
		// diffMet indicate the number of different percentage of metilation
		// among different genes for tumor/cell sample.
		// the higher diffMet observed, higher is the number of clone of
		// chromosomes that are necessary to explain this number
		// This program search to see if the number of clones can be lower than
		// the diffMet.
		//getValues();
		int diffMet = 5; // number of different metilated sites. Maximum is 5
		//int cellNumber = 2;// number of expected clones (cannot be larger than 4
		int improve = 0; // use 0 for fast results. Use 1 to better fir and to decrease the error
		HashMap<String, Double> model = new HashMap<String, Double>();
		HashMap<String, Double> observed = new HashMap<String, Double>();
		HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		
		Scanner stdin = new Scanner( System.in ); // Create the Scanner.
		System.out.println("M0 to M4 ");
		// Observed metilation proportions. If less than

		observed.put("M0", stdin.nextDouble());
		observed.put("M1", stdin.nextDouble());
		observed.put("M2", stdin.nextDouble());
		observed.put("M3", stdin.nextDouble());
		observed.put("M4", stdin.nextDouble());
		System.out.println(observed);
		//if (2<4) return;
/*	to use entering parameters from the console	
 * try {
			int a = readConsole();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		// Sets of 4, 3, or 2 populations are managed well.
		for (int cellNumber = 2; cellNumber < 5; cellNumber=cellNumber + 1){

		double minError = 100000.0;// the minimal difference between observed
		// and the model is set to a large number

		int allPop = (int) (Math.pow(2, diffMet)); // 2^3 = 8, 2^4 = 16
		// generate a string where each population is coded by a two decimal
		// digits (100 different binary number representing 100 populations with
		// different metilations)
		// All numbers are stored as strings with the same number of digits (2)
		String allPopString = "";
		for (int i = 0; i < allPop; i++) {
			String iString = Integer.toString(i);
			if (iString.length() < 2)
				iString = "0" + iString;// add a "0" if it is a single digit
										// number
			allPopString = allPopString + iString;
		}

		// initialize the error from the observed to the model
		double error1 = 0.0;
		

		// Iteration over the space of parameters and populations
		// Method that return all the possible combination of the
		// populations in set of size cellNumber. For example if 3 different
		// metilations are evident and we may want to obtain the best 3
		// populations with the corresponding proportions of each.
		// Since the metilation site can be off/on, it can be represented by a
		// binary
		// number. The possibilities of off/on of three metilation site in an
		// aploid cell
		// then can be represented by a binary number. For 3 distinct metilation
		// sites,
		// then 2^3=8 population are possible and all combinations of 3 out of
		// the 8 are returned.
		// If we want the best result but with only 2 clones, the we must screen
		// for
		// all possible combination two cell from the 8 population set, and
		// search for eacho of
		// them, the best proportion to minimize the difference between the
		// model and the observed

		// For diffMet = 4, 16 different populations are possible. We may select
		// 4, 3 or 2 populations
		// For 5 diffMet, 32 different populations are possible. If 5 population
		// are selected
		// the program may take too long to get an
		// answer(32!/(27!)/(5!)combinations are posible)

			
		ArrayList<String> populationSet = populationSet(allPopString,
				cellNumber);
		System.out.println(cellNumber +"  POPULATIONSET" + populationSet);
		//if (cellNumber > 3)return;

		// for each population, a screening of all combinations of
		// proportions are selected
		int delta = 10;// delta is the number of different proportions that are
						// considered for each population
		// in this case, the proportions are evaluated in 0.1 intervals (10
		// intervals from 0 to 1 (from 0.0 to 0.9)
		// populationProp method return a set of all possible combinations of
		// proportions that sum 1. To switch to a different delta, the program
		// needs to be
		// modified to handle numbers that are note decimal. With Java, it would
		// not be
		// very difficult to switch to a base 16. With a little more effort can
		// be changed to
		// delta = 100, but in this case, the proportions must be coded by two
		// digits.
		ArrayList<Integer> populationProp = populationProp(delta, cellNumber);
		System.out.println("proportions" + populationProp.size()
				+ populationProp);
		// with the set of all possible combinations of populations
		// (populationSet) and
		// all possible combinations of proportion that add to 1, the predicted
		// metilations
		// are calculated and compared with the observed. The minimum is stored
		// together with
		// the population and the proportion
		String bestPop = "";
		int bestProp = 0;
		search: {
			for (String s : populationSet) {
				for (int p : populationProp) {
					model = modelEvaluation(s, p, cellNumber, diffMet);
					// System.out.println(model);
					error1 = errorEvaluation(observed, model);
					if (error1 < minError) {
						minError = error1;
						bestPop = s;
						bestProp = p;

						System.out.println("");
						System.out.println("minerror " + minError);
						System.out.println("best population " + bestPop);
						for (int k= 0; k< cellNumber*2; k=k+2){
							String pop = bestPop.substring(k, k+2);
							System.out.println(" "+	Integer.toBinaryString(Integer
									.valueOf(pop)));
						}

						
						System.out.print("proportion ");			
						System.out.print(p + "  ");
						System.out.println("");
						for (String key : model.keySet()){
							System.out.println(key +" "+ Math.round(model.get(key)*100)/100d);
						}
						if (minError == 0)
							break search;
					}

				}

			}
			if (improve == 1) {
				for (int rest = -1; rest < 2; rest = rest + 2) {
					System.out.println("ahora suma " + rest);
					ArrayList<double[]> fineScrn = fineScrn(bestPop, bestProp,
							cellNumber, populationProp, rest);
					for (int i = 0; i < fineScrn.size(); i++) {
						double[] prSet = fineScrn.get(i);
						for (int j = 0; j < cellNumber; j++) {
							double pr = prSet[j];
							// System.out.println("propENviada " + pr);
						}
						model = fineModelEvaluation(bestPop, prSet, cellNumber,
								diffMet);
						// System.out.println(model);
						error1 = errorEvaluation(observed, model);
						// System.out.println("Error1 " + error1);
						if (error1 < minError) {
							minError = error1;
							System.out.println("");
							System.out.println("minerror " + minError);
							System.out.println("best population " + bestPop);
							for (int k= 0; k< cellNumber*2; k=k+2){
								String pop = bestPop.substring(k, k+2);
								System.out.println(" "+	Integer.toBinaryString(Integer
										.valueOf(pop)));
							}

							
							System.out.print("proportion ");			
							for (int j = 0; j < cellNumber; j++) {
								double pr = prSet[j];
								System.out.print(Math.round(pr*100)/100d + " ");
							}
							System.out.println("");
							for (String key : model.keySet()){
								System.out.println(key +" "+ Math.round(model.get(key)*100)/100d);
							}
							//System.out.println("best model " + model);
;

							if (minError == 0)
								break search;
						}

					}

				}

			}
		}
		System.out.println("cellNumber " + cellNumber + " SS(error) " + minError);		
		result.put(cellNumber, minError);
		}
		System.out.println(result);
		System.out.println("END");
	}

	private static void getValues() {
		// TODO Auto-generated method stub
		GetValues.main();
	}

	/*
	 * This method generate a set of array of proportions up and down the best
	 * proportions obtained in the first screen, to be combine with the the best
	 * combination of cells generated by the first screen. I switched to a set of
	 * arrays of doubles, to deal with the fact that the proportions generated
	 * would be not simple fractions (0.0 to 0.9) as in the first screening. It
	 * was a mess. If done again, a will use the same as the first screening but
	 * using two digits to represent fractions from 0.00 to 0.99 Notice that the
	 * original proportion is decreased by 0.1/cellNumber and then increased by
	 * the combination of proportions that we know add to 0.1. Then the set of
	 * proportions will add to 1. In a second round, the contrary is done.
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
		// for each prop.  It use the same of populationProp original, but divided by 100 to 
		// to generate a finer grid arroun the best values obtained in the firs screen
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
						//because the sum and rest, negative proportions can be generated
						//in this case, this set of proportions is not added to fineScrn
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
	// I think it can be improved to delta of 0.01 easily, but the computation is
	// then heavy.
	public static ArrayList<Integer> populationProp(int delta, int cellNumber) {
		ArrayList<Integer> populationProp = new ArrayList<Integer>();
		for (int i = 0; i < Math.pow(delta, cellNumber); i++) {
			String number = Integer.toString(i);

			int sum = 0;
			for (int l = 0; l < number.length(); l++) {
				Integer dd = Integer.valueOf(number.substring(l, l + 1));
				// System.out.println(digitList);
				sum = sum + dd;
				// System.out.println("digit     " +sum);
			}
			// System.out.println("SUMA     " +sum);
			if (sum == 10)
				populationProp.add(i);
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
		//System.out.println("PopulationSet" + populationSet);
		return populationSet;

	}
	// this method is the one that generate all the combinations.  It calls itself!!
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
	/*For the model evalulation, the metilation of each site is calculated adding the 
	 * the proportion of each population with the state of metilation if this particualar 
	 * site (0 or 1).  The state of metilation is extracted from the binary number representing
	 * the population.  The proportions is obtained from the decimal digits cominag as a string
	 * */
	public static HashMap<String, Double> modelEvaluation(String population,
			Integer proportion, Integer cellNumber, Integer diffMet) {
		ArrayList<Integer> pop = new ArrayList<Integer>();
		double[] prop = new double[cellNumber];
		HashMap<String, Double> model = new HashMap<String, Double>();
		for (int i = 0; i < population.length(); i = i + 2) {
			pop.add(Integer.valueOf(population.substring(i, i + 2)));
		}
		// System.out.println("POPulation  "+pop);
		String propString = Integer.toString(proportion);
		while (propString.length() < cellNumber) {
			propString = "0" + propString;
		}
		//if(cellNumber == 4) 

		for (int i = 0; i < propString.length(); i++) {
			Integer pp = Integer.valueOf(propString.substring(i, i + 1));
			prop[i] = (double) pp / 10;
			//if(cellNumber == 4) 
			//System.out.println("i "+ i +" " +prop[i]);
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
	 * comes as a set of doubles*/
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
 * The difference betweeen the model and the observed metilation is obtenied as a square of the difference
 * 
 * */
	private static double errorEvaluation(HashMap<String, Double> observed,
			HashMap<String, Double> model) {

		double error1 = 0.0;
		for (String met : observed.keySet()) {
			// System.out.println("error " + error1);
			error1 = error1 + Math.pow((observed.get(met) - model.get(met)), 2);
		}
		return error1;
	}

/*	public static double[] mixTest(Integer i) {
		int maxCell = (int) Math.pow(2, i);
		double total = 0.0;
		double[] mix = new double[maxCell];
		for (int k = 0; k < maxCell; k++) {

			mix[k] = Math.random();
			total = total + mix[k];
		}
		for (int k = 0; k < maxCell; k++) {
			mix[k] = mix[k] / total;
		}
		return mix;
	}*/

 
		private static Integer readConsole() throws NumberFormatException, IOException{ 
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter String");
        try {
			String s = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.print("Enter Integer:");

            int i = Integer.parseInt(br.readLine());

            System.err.println("Invalid Format!");
        
        return i;
    }
}
	


