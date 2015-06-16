package dhanar10.mastersthesisexperiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import dhanar10.mastersthesisexperiment.algorithm.artificialbeecolony.ArtificialBeeColony;
import dhanar10.mastersthesisexperiment.algorithm.artificialbeecolony.IOptimizationProblem;
import dhanar10.mastersthesisexperiment.algorithm.rpropneuralnetwork.RpropNeuralNetwork;

public class MastersThesisExperiment {

	public static void main(String[] args) {
		 MastersThesisExperiment mte = new MastersThesisExperiment();
		 mte.run();
	}

	public void run() {
		IOptimizationProblem problem = new IOptimizationProblem() {
			private static final int MAX_TRIAL = 10;
			private static final int HIDDEN_NEURON = 5;
			private static final double TARGET_MSE = 0.001;
			private static final int MAX_EPOCH = 1000;
			
			private double data[][] = normalize(load("data.csv", 1));
			
			public int length() {
				return 1;
			}
			public double[] upperBound() {
				return new double[] { 7 };
			}
			public double[] lowerBound() {
				return new double[] { 1 };
			}
			public double getFitness(double x[]) {
				double sdata[][] = new double[data.length][this.length() + 1];
				
				for (int i = 0; i < x.length; i++) {
					for (int j = i + 1; j < x.length; j++) {
						if (((int) x[i]) == ((int) x[j])) {
							return 0;
						}
					}
				}
				
				for (int i = 0; i < data.length; i++) {
					sdata[i][0] = data[i][(int) x[0]];
					sdata[i][sdata[i].length - 1] = data[i][8];
				}
				
				RpropNeuralNetwork bestRprop = null;
				
				for (int i = 0; i < MAX_TRIAL; i++) {
					RpropNeuralNetwork rprop = new RpropNeuralNetwork(sdata[0].length - 1, HIDDEN_NEURON, 1);
					boolean success = rprop.train(sdata, TARGET_MSE, MAX_EPOCH);
					
					if (bestRprop == null) {
						bestRprop = rprop;
					}
					
					if (rprop.getMse() < bestRprop.getMse()) {
						bestRprop = rprop;
					}
					
					System.out.print(success ? "x" : ".");
				}
				
				System.out.println();
				
				return 1 / bestRprop.getMse();
			}
			public double getOutput(double x[]) {
				return 0;
			}
		};
		
		ArtificialBeeColony abc = new ArtificialBeeColony(5);
		abc.optimize(problem, 10);
		
		System.out.println();
		
		for (int i = 0; i < abc.getBestSolution().length; i++) {
			System.out.println("x[" + i + "]\t= " + (int) abc.getBestSolution()[i]);
		}
	}
	
	private double[][] load(String file, int skip) {
		ArrayList<String> buffer = new ArrayList<String>();
		
		double data[][] = null;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			
			int i = 0;
			
			while ((line = br.readLine()) != null) {
				i++;
				
				if (i <= skip) {
					continue;
				}
				
				buffer.add(line);
			}
			
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		if (buffer.isEmpty()) {
			return null;
		}
		
		for (int i = 0; i < buffer.size(); i++) {
			String split[] = buffer.get(i).split(",");
			
			if (data == null) {
				data = new double[buffer.size()][split.length];
			}
			
			for (int j = 0; j < split.length; j++) {
				data[i][j] = Double.parseDouble(split[j]);
			}
		}
		
		return data;
	}
	
	private double[][] normalize(double data[][]) {
		double min[] = new double[data[0].length];
		double max[] = new double[data[0].length];
		
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				min[j] = min[j] > data[i][j] ? data[i][j] : min[j];
				max[j] = max[j] < data[i][j] ? data[i][j] : max[j];
			}
		}
		
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				data[i][j] = (data[i][j] - min[j]) / (max[j] - min[j]);
			}
		}
		
		return data;
	}
}
