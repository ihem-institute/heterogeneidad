import java.util.HashMap;


public class cellmix {

	public static void main(String[] args) {
		int diffMet = 3;
		Double error1 = 0.0;
		HashMap<String, Double> model = new HashMap<String, Double>(); 
		HashMap<String, Double> observed = new HashMap<String, Double>(); 
		HashMap<Double[], Double> testSet = new HashMap<Double[], Double>(); 
		observed.put("M1", 0.75);
		observed.put("M2", 0.30);
		observed.put("M3", 0.10);
		Double[] mix = mixTest(diffMet);
		for (int i=0; i<diffMet; i++){
			double value = 0;
			for (int j =0; j< Math.pow(2, diffMet); j++){

				int binary = Integer.valueOf(Integer.toBinaryString(j).substring(i, i+1));
				value = value + mix[j]*binary;
				}
				model.put("M"+i, value);	
			}
		error1 = 0.0;
		for (String met : observed.keySet()){
			error1 = error1+Math.pow(observed.get(met)- model.get(met),2);
		}
			testSet.put(mix, error1);
			System.out.println(mix.toString() + error1);
		}
		
	public static Double[] mixTest(Integer i){
		int maxCell= (int) Math.pow(2, i);
		Double total = 0.0;
		Double[] mix = new Double[maxCell];
		for (int k = 0; k<maxCell; k++){

			mix[k]= Math.random();
			total = total + mix[k];
		}
		for (int k=0; k<maxCell; k++){
			mix[k]=mix[k]/total;
		}
		return mix;
	}
		
	
	
	//TODO Auto-generated method stub

	}


