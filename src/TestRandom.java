

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TestRandom {
	private String name;

	public TestRandom(String string) {
		name = string;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static void main(String[] args) {
		/*
		 * Human mi_humano = new Human(null, null, 10);
		 * System.out.println(mi_humano);
		 * 
		 * TestObjects to = new TestObjects("Luis"); to.setName("Gabriel"); //
		 * to.name = "Luis"; System.out.println("mi nombre " + to.getName());
		 */
		int diffMet = 4;
		int cellNumber = 2;
		Double error1 = 0.0;
		HashMap<String, Double> model = new HashMap<String, Double>();
		HashMap<String, Double> observed = new HashMap<String, Double>();
		//HashMap<Double, Double[]> testSet = new HashMap<Double, Double[]>();
		observed.put("M0", 0.3d);
		observed.put("M1", 0.2d);
		observed.put("M2", 0.5d);
		observed.put("M3", 0.6d);
		// observed.put("M4", 0.1d);
		Double minError = 100000.0;
		//String minMix = null;
		// Iteration over the space of parameters and populations
		// if the number exceeds 10^8 stop
		ArrayList<String> tested = new ArrayList<String>();
		tested.add("");
		//double[] prop = new double[cellNumber];
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
		ArrayList<String> populationSet = populationSet(allPopString,
				cellNumber);
		for (int it = 1; it < 1E4; it++) {
			// here it must call a method that return a set of
			// proportion for each population (mix[])
			
			String mixString = mixTest(cellNumber, tested);
	
			
			String bestPop = "";
			String bestProp = "";
			search: {
				for (String s : populationSet) {

						model = modelEvaluation(s, mixString, cellNumber, diffMet);
						// System.out.println(model);
						error1 = errorEvaluation(observed, model);
						if (error1 < minError) {
							minError = error1;
							bestPop = s;
							bestProp = mixString;

							System.out.println("minerror " + minError);
							System.out.println(model);
							System.out.print(mixString + "  ");
							System.out.println(s);
							if (minError == 0)
								break search;
						}

					}

				}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			/*


			for (int i = 0; i < cellNumber; i++) {
				double value = 0;
				for (int j = 0; j < Math.pow(2, diffMet); j++) {
					int binary = 0;
					int length = Integer.toBinaryString(j).length();
					if (i < length) {
						binary = Integer.valueOf(Integer.toBinaryString(j)
								.substring(length - i - 1, length - i));
					}
					// System.out.println("i " + i + "j " +
					// Integer.toBinaryString(j)
					// + "  binary " + binary);
					value = value + mix[j] * binary;
					// System.out.println("  " + mix[j]);
				}
				model.put("M" + i, value);
			}

			// System.out.println(model);
			// System.out.println(observed);
			// Compare the observed metilation with the calculated
			// using the mix of populations
			error1 = 0.0;
			for (String met : observed.keySet()) {
				// System.out.println("error " + error1);
				error1 = error1
						+ Math.pow((observed.get(met) - model.get(met)), 2);
			}
			testSet.put(error1, mix);
			// System.out.println(Arrays.toString(mix) + "   " + error1);
			
			 * Double sum = 0.0; for (int i = 0; i < mix.length; i++) { sum +=
			 * mix[i]; }
			 
			// System.out.println("error " + error1);
			// System.out.println("sum mix " + sum);
*/
			if (error1 < minError) {
				minError = error1;
				//minMix = mixString;
				System.out.println("minerror " + minError);
				System.out.println(model);
				System.out.println(mixString);

			}
		}
		System.out.println("END");
	}

	public static String mixTest(Integer cellNumber, ArrayList<String> tested) {
		//int maxCell = (int) Math.pow(2, i);
		String mixString = "";
		int srch = 0;
		do {
			Double total = 0.0;
			Double[] mix = new Double[cellNumber];
			for (int k = 0; k < cellNumber; k++) {

				mix[k] = Math.random();
				total = total + mix[k];
			}
			mixString = "";
			for (int k = 0; k < cellNumber; k++) {
				mix[k] = mix[k] / total;
				//System.out.println(mix[k]);
				String str = String.valueOf(Math.round(mix[k] * 100));
				while (str.length() < 2) {
					str = "0" + str;
				}
				mixString = mixString + str;
				//System.out.println(mixString);
			}
			srch = srch +1;
		} while (tested.contains(mixString) || srch < 1E2);
		tested.add(mixString);
		//System.out.println(mixString);
		return mixString;
	}
	public static ArrayList<String> populationSet(String data, int howMany) {
		ArrayList<String> populationSet = new ArrayList<String>();
		populationSet = choose(populationSet, data, howMany,
				new StringBuffer(), 0);
		// populationSet.add(singeSet);
		//System.out.println("PopulationSet" + populationSet);
		return populationSet;

	}
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
	public static HashMap<String, Double> modelEvaluation(String population,
			String mixString, Integer cellNumber, Integer diffMet) {
		ArrayList<Integer> pop = new ArrayList<Integer>();
		double[] prop = new double[cellNumber];
		HashMap<String, Double> model = new HashMap<String, Double>();
		for (int i = 0; i < population.length(); i = i + 2) {
			pop.add(Integer.valueOf(population.substring(i, i + 2)));
		}
		// System.out.println("POPulation  "+pop);
		String propString = mixString;
		while (propString.length() < cellNumber*2) {
			propString = "0" + propString;
		}
		// System.out.println("PropString  "+propString);
		for (int i = 0; i < cellNumber; i=i+2) {
			Integer pp = Integer.valueOf(propString.substring(i, i + 2));
			prop[i/2] = (double) pp / 100;
			// System.out.println("i "+ i +" " +prop[i]);
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
