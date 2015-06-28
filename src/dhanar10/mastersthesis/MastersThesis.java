package dhanar10.mastersthesis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import dhanar10.mastersthesis.algorithm.artificialbeecolony.ArtificialBeeColony;
import dhanar10.mastersthesis.algorithm.artificialbeecolony.IOptimizationProblem;
import dhanar10.mastersthesis.algorithm.rpropneuralnetwork.RpropNeuralNetwork;

public class MastersThesis {

	public static void main(String[] args) {
		 MastersThesis mte = new MastersThesis();
		 mte.run02();
	}

	public void run01() {
		IOptimizationProblem problem = new IOptimizationProblem() {
			private static final int HIDDEN_NEURON = 15;
			private static final double TARGET_MSE = 0.0001;
			private static final int MAX_EPOCH = 10000;
			
			private double data[][] = normalize(load("data.csv", 1));
			
			public int length() {
				return 3;
			}
			
			public double[] upperBound() {
				return new double[] { 7, 7, 7 };
			}
			
			public double[] lowerBound() {
				return new double[] { 1, 1, 1 };
			}
			
			public double getFitness(double x[]) {
				double sdata[][] = new double[data.length][this.length() + 1];
				
				for (int i = 0; i < x.length; i++) {
					for (int j = i + 1; j < x.length; j++) {
						if (((int) x[i]) == ((int) x[j])) {
							return Double.MIN_VALUE;
						}
					}
				}
				
				for (int i = 0; i < data.length; i++) {
					sdata[i][0] = data[i][(int) x[0]];
					sdata[i][1] = data[i][(int) x[1]];
					sdata[i][2] = data[i][(int) x[2]];
					sdata[i][sdata[i].length - 1] = data[i][8];
				}
				
				double smse = 0;
				
				for (int i = 0; i < 30; i++) {
					RpropNeuralNetwork rprop = new RpropNeuralNetwork(sdata[0].length - 1, HIDDEN_NEURON, 1);
					boolean complete = rprop.train(sdata, TARGET_MSE, MAX_EPOCH);
					
					smse += rprop.getMse();
					
					System.out.print(complete ? "x" : ".");
				}
				
				smse /= 30;
				
				System.out.println();
				
				return 1 / smse;
			}
			
			public double getOutput(double x[]) {
				return 0;
			}
		};
		
		ArtificialBeeColony abc = new ArtificialBeeColony(10);
		abc.optimize(problem, 50);
		
		System.out.println();
		
		for (int i = 0; i < abc.getBestSolution().length; i++) {
			System.out.println("x[" + i + "]\t= " + (int) abc.getBestSolution()[i]);
		}
	}
	
	public void run02() {
		double data[][] = normalize(load("data.csv", 1));
		
		double sdata[][] = new double[data.length][8];
		
		for (int i = 0; i < data.length; i++) {
			sdata[i][0] = data[i][1];
			sdata[i][1] = data[i][2];
			sdata[i][2] = data[i][3];
			sdata[i][3] = data[i][4];
			sdata[i][4] = data[i][5];
			sdata[i][5] = data[i][6];
			sdata[i][6] = data[i][7];
			sdata[i][7] = data[i][8];
		}
		
//		double sdata[][] = new double[data.length][4];
//		
//		for (int i = 0; i < data.length; i++) {
//			sdata[i][0] = data[i][1];
//			sdata[i][1] = data[i][3];
//			sdata[i][2] = data[i][4];
//			sdata[i][3] = data[i][8];
//		}
		
		double smse = 0;
		
		for (int i = 0; i < 30; i++) {
			RpropNeuralNetwork rprop = new RpropNeuralNetwork(sdata[0].length - 1, 2, 1);
			boolean complete = rprop.train(sdata, 0.0001, 10000);
			
			smse += rprop.getMse();
			
			System.out.println(complete ? "x" : "." + "\t" + rprop.getMse());
		}
		
		smse /= 30;
		
		System.out.println();
		
		System.out.println(smse);
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
