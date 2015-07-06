package dhanar10.mastersthesis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import dhanar10.mastersthesis.algorithm.artificialbeecolony.ArtificialBeeColony;
import dhanar10.mastersthesis.algorithm.artificialbeecolony.IOptimizationProblem;
import dhanar10.mastersthesis.algorithm.irpropplusneuralnetwork.IRpropPlusNeuralNetwork;

public class MastersThesis {

	public static void main(String[] args) {
		 MastersThesis mt = new MastersThesis();
		 mt.run01();
	}
	
	public void run01() {
		IOptimizationProblem problem = new IOptimizationProblem() {
			private double data[][] = normalize(load("data.csv", 1));
			
			public int length() {
				return 1;
			}
			
			public double[] upperBound() {
				return new double[] { 64 };
			}
			
			public double[] lowerBound() {
				return new double[] { 1 };
			}
			
			public double getFitness(double x[]) {
				int f = 0;
				
				for (int i = 1; i <= 64; i*=2) {
					if ((((int) x[0]) & i) == i) {
						f++;
					}
				}
				
				double sdata[][] = new double[data.length][f + 1];
				
				for (int i = 0; i < data.length; i++) {
					int k = 0;
					
					for (int j = 1; j <= 64; j*=2) {
						if ((((int) x[0]) & j) == j) {
							sdata[i][k++] = data[i][((int) (Math.log(j) / Math.log(2))) + 1];
						}
					}
					
					sdata[i][sdata[i].length - 1] = data[i][8];
				}
				
				double smse = 0;
				
				for (int i = 0; i < 30; i++) {
					IRpropPlusNeuralNetwork irpropplus = new IRpropPlusNeuralNetwork(sdata[0].length - 1, 5, 1);
					irpropplus.setTargetMse(0.0005);
					irpropplus.setMaxEpoch(1000);
					
					while (irpropplus.canTrain()) {
						irpropplus.train(sdata);
					}
					
					smse += irpropplus.getMse();
					
					System.out.print(irpropplus.getMse() < irpropplus.getTargetMse() ? "x" : ".");
				}
				
				smse /= 30;
				
				System.out.println();
				
				return (1 / smse) + ((7 - f) * 50);
			}
			
			public double getOutput(double x[]) {
				return 0;
			}
		};
		
		ArtificialBeeColony abc = new ArtificialBeeColony(5);
		abc.optimize(problem, 50);
		
		System.out.println();
		
		for (int i = 0; i < abc.getBestSolution().length; i++) {
			System.out.println("x[" + i + "]\t= " + abc.getBestSolution()[i]);
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
