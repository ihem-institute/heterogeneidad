
//import java.io.BufferedReader;populationProp
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class HetIndexMain {

	public static void main(String[] args) {
		// diffMet indicate the number of different percentage of metilation
		// among different genes for tumor/cell sample.
		// the higher diffMet observed, higher is the number of clones
		//that are necessary to explain this number
		// This program search to see if the number of clones can be lower than
		// the diffMet.

		int diffMet = 5; // number of different metilated sites. Maximum is 5

		int maxCells = 3; // maximum number of cells to be tested

		Scanner stdin = new Scanner(System.in).useLocale(Locale.UK); // Create the Scanner.
		System.out
		.println("enter number of metilated sites + metilation index ");

		List<Double> metSet =new ArrayList<Double>();
		double metN = stdin.nextDouble();
		for (int ii = 0; ii < metN; ii++) {
			metSet.add(stdin.nextDouble());
			// parse inputLine however you want, and add to your vector
		}
// llama para generar el set de HetIndex (uno cada 5 valores)
		ArrayList<Double> resultsHI= HetIndex.hetIndex(metSet, maxCells);
		System.out.println("results " + resultsHI);

	}
}
