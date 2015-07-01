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
		 //mt.run01();
		 mt.run02();
		 //mt.run03();
	}

	public void run01() {
		IOptimizationProblem problem = new IOptimizationProblem() {
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
				
				return 1 / smse;
			}
			
			public double getOutput(double x[]) {
				return 0;
			}
		};
		
		ArtificialBeeColony abc = new ArtificialBeeColony(5);
		abc.optimize(problem, 30);
		
		System.out.println();
		
		for (int i = 0; i < abc.getBestSolution().length; i++) {
			System.out.println("x[" + i + "]\t= " + (int) abc.getBestSolution()[i]);
		}
	}
	
	public void run02() {
		IOptimizationProblem problem = new IOptimizationProblem() {
			private double data[][] = normalize(load("data.csv", 1));
			
			public int length() {
				return 7;
			}
			
			public double[] upperBound() {
				return new double[] { 1, 1, 1, 1, 1, 1, 1 };
			}
			
			public double[] lowerBound() {
				return new double[] { 0, 0, 0, 0, 0, 0, 0 };
			}
			
			public double getFitness(double x[]) {
				int f = 0;
				
				for (int i = 0; i < x.length; i++) {
					if (x[i] >= 0.5) {
						f++;
					}
				}
				
				if (f == 0) {
					return Double.MIN_VALUE;
				}
				
				double sdata[][] = new double[data.length][f + 1];
				
				for (int i = 0; i < data.length; i++) {
					int k = 0;
					
					for (int j = 0; j < x.length; j++) {
						if (x[j] >= 0.5) {
							sdata[i][k++] = data[i][j + 1];
						}
					}
					
					sdata[i][sdata[i].length - 1] = data[i][8];
				}
				
				double smse = 0;
				
				for (int i = 0; i < 30; i++) {
					IRpropPlusNeuralNetwork irpropplus = new IRpropPlusNeuralNetwork(sdata[0].length - 1, 3, 1);
					irpropplus.setTargetMse(0.0005);
					irpropplus.setMaxEpoch(2000);
					
					while (irpropplus.canTrain()) {
						irpropplus.train(sdata);
					}
					
					smse += irpropplus.getMse();
					
					System.out.print(irpropplus.getMse() < irpropplus.getTargetMse() ? "x" : ".");
				}
				
				smse /= 30;
				
				System.out.println();
				
				return (1 / smse) - (f * 100);
			}
			
			public double getOutput(double x[]) {
				return 0;
			}
		};
		
		ArtificialBeeColony abc = new ArtificialBeeColony(5);
		abc.optimize(problem, 50);
		
		System.out.println();
		
		for (int i = 0; i < abc.getBestSolution().length; i++) {
			System.out.println("x[" + i + "]\t= " + (abc.getBestSolution()[i] >= 0.5 ? 1 : 0));
		}
	}
	
	public void run03() {
		double data[][] = normalize(load("data.csv", 1));
		
//		double sdata[][] = new double[data.length][8];
//		
//		for (int i = 0; i < data.length; i++) {
//			sdata[i][0] = data[i][1];
//			sdata[i][1] = data[i][2];
//			sdata[i][2] = data[i][3];
//			sdata[i][3] = data[i][4];
//			sdata[i][4] = data[i][5];
//			sdata[i][5] = data[i][6];
//			sdata[i][6] = data[i][7];
//			sdata[i][7] = data[i][8];
//		}
		
		double sdata[][] = new double[data.length][4];
		
		for (int i = 0; i < data.length; i++) {
			sdata[i][0] = data[i][1];
			sdata[i][1] = data[i][3];
			sdata[i][2] = data[i][4];
			sdata[i][3] = data[i][8];
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
			
			System.out.println((irpropplus.getMse() < irpropplus.getTargetMse() ? "x" : ".") + "\t" + irpropplus.getMse());
		}
		
		smse /= 30;
		
		System.out.println();
		
		System.out.println(1 / smse);
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
