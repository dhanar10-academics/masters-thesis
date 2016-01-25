package dhanar10.mastersthesis;

import dhanar10.mastersthesis.algorithm.artificialbeecolony.ArtificialBeeColony;
import dhanar10.mastersthesis.algorithm.artificialbeecolony.IOptimizationProblem;
import dhanar10.mastersthesis.algorithm.irpropplusneuralnetwork.IRpropPlusNeuralNetwork;

public class MastersThesis {
	private double[][] data;
	
	public static void main(String[] args) {
		MastersThesis mt = new MastersThesis();
		
		double[] result = new double[30];
		
		for (int i = 0; i < result.length; i++)
		{
			result[i] = mt.experiment01(); 	// RPROP 1
			//result[i] = mt.experiment02(); 	// RPROP 1,4
			//result[i] = mt.experiment03(); 	// RPROP 1,4,6
			//result[i] = mt.experiment04(); 	// RPROP 1,4,6,5
			
			//result[i] = mt.experiment05(); 	// ABC+RPROP 1
			//result[i] = mt.experiment06(); 	// ABC+RPROP 1,4
			//result[i] = mt.experiment07(); 	// ABC+RPROP 1,4,6
			//result[i] = mt.experiment08(); 	// ABC+RPROP 1,4,6,5
		}
		
		for (int i = 0; i < result.length; i++)
		{
			System.out.println(result[i]);
		}
	}
	
	public double experiment01() {
		data = Utils.load("data.csv", 1);
		data = Utils.cut(data, new int[] { 1, 8 });
		data = Utils.normalize(data);
		
		IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(1, 5, 1);
		rprop.setTargetMse(0.0001);
		rprop.setMaxEpoch(50000);
		
		while (rprop.canTrain()) {
			rprop.train(data);
			
			System.out.println(rprop.getEpoch() + "\t" + rprop.getMse());
		}
		
		return rprop.getMse();
	}
	
	public double experiment02() {
		data = Utils.load("data.csv", 1);
		data = Utils.cut(data, new int[] { 1, 4, 8 });
		data = Utils.normalize(data);
		
		IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(2, 5, 1);
		rprop.setTargetMse(0.0001);
		rprop.setMaxEpoch(50000);
		
		while (rprop.canTrain()) {
			rprop.train(data);
			
			System.out.println(rprop.getEpoch() + "\t" + rprop.getMse());
		}
		
		return rprop.getMse();
	}
	
	public double experiment03() {
		data = Utils.load("data.csv", 1);
		data = Utils.cut(data, new int[] { 1, 4, 6, 8 });
		data = Utils.normalize(data);
		
		IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(3, 5, 1);
		rprop.setTargetMse(0.0001);
		rprop.setMaxEpoch(50000);
		
		while (rprop.canTrain()) {
			rprop.train(data);
			
			System.out.println(rprop.getEpoch() + "\t" + rprop.getMse());
		}
		
		return rprop.getMse();
	}
	
	public double experiment04() {
		data = Utils.load("data.csv", 1);
		data = Utils.cut(data, new int[] { 1, 4, 6, 5, 8 });
		data = Utils.normalize(data);
		
		IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(4, 5, 1);
		rprop.setTargetMse(0.0001);
		rprop.setMaxEpoch(50000);
		
		while (rprop.canTrain()) {
			rprop.train(data);
			
			System.out.println(rprop.getEpoch() + "\t" + rprop.getMse());
		}
		
		return rprop.getMse();
	}
	

	
	
	public double experiment05() {
		data = Utils.load("data.csv", 1);
		data = Utils.cut(data, new int[] { 1, 8 });
		data = Utils.normalize(data);
		
		IOptimizationProblem problem = new IOptimizationProblem() {
			public int length() {
				return 16 + 2;
			}
			public double[] upperBound() {
				double[] b = new double[this.length()];
				
				for (int i = 0; i < b.length; i++) {
					b[i] = 1;
				}
				
				return b;
			}
			public double[] lowerBound() {
				double[] b = new double[this.length()];
				
				for (int i = 0; i < b.length; i++) {
					b[i] = 0;
				}
				
				return b;
			}
			public double getFitness(double x[]) {
				IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(1, 5, 1);
				
				double[][] wInputHidden = rprop.getWeightInputHidden();
				double[][] wHiddenOutput = rprop.getWeightHiddenOutput();
				
				int c = 0;
				
				for (int i = 0; i < wInputHidden.length; i++) {
					for (int j = 0; j < wInputHidden[0].length; j++) {
						wInputHidden[i][j] = x[c++];
					}
				}
				
				for (int i = 0; i < wHiddenOutput.length; i++) {
					for (int j = 0; j < wHiddenOutput[0].length; j++) {
						wHiddenOutput[i][j] = x[c++];
					}
				}
				
				rprop.setWeightInputHidden(wInputHidden);
				rprop.setWeightHiddenOutput(wHiddenOutput);
				
				double mse = 0;
				
				for (int i = 0; i < data.length; i++) {
					double output[] = rprop.calculate(data[i]);
					double diff = (output[0] - data[i][data[0].length - 1]); // FIXME hardcoded output[0]
					mse += diff * diff; 
				}
				
				mse /= data.length;
				
				return 1 / mse;
			}
			public double getOutput(double x[]) {
				return 0;
			}
		};
		
		ArtificialBeeColony abc = new ArtificialBeeColony(10);
		abc.optimize(problem, 500);
		
		IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(1, 5, 1);
		
		double[][] wInputHidden = rprop.getWeightInputHidden();
		double[][] wHiddenOutput = rprop.getWeightHiddenOutput();
		
		int c = 0;
		
		for (int i = 0; i < wInputHidden.length; i++) {
			for (int j = 0; j < wInputHidden[0].length; j++) {
				wInputHidden[i][j] = abc.getBestSolution()[c++];
			}
		}
		
		for (int i = 0; i < wHiddenOutput.length; i++) {
			for (int j = 0; j < wHiddenOutput[0].length; j++) {
				wHiddenOutput[i][j] = abc.getBestSolution()[c++];
			}
		}
		
		rprop.setWeightInputHidden(wInputHidden);
		rprop.setWeightHiddenOutput(wHiddenOutput);
		rprop.setTargetMse(0.0001);
		rprop.setMaxEpoch(50000);
		
		while (rprop.canTrain()) {
			rprop.train(data);
			
			System.out.println(rprop.getEpoch() + "\t" + rprop.getMse());
		}		
		
		return rprop.getMse();
	}
	
	public double experiment06() {
		data = Utils.load("data.csv", 1);
		data = Utils.cut(data, new int[] { 1, 4, 8 });
		data = Utils.normalize(data);
		
		IOptimizationProblem problem = new IOptimizationProblem() {
			public int length() {
				return 22 + 2;
			}
			public double[] upperBound() {
				double[] b = new double[this.length()];
				
				for (int i = 0; i < b.length; i++) {
					b[i] = 1;
				}
				
				return b;
			}
			public double[] lowerBound() {
				double[] b = new double[this.length()];
				
				for (int i = 0; i < b.length; i++) {
					b[i] = 0;
				}
				
				return b;
			}
			public double getFitness(double x[]) {
				IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(2, 5, 1);
				
				double[][] wInputHidden = rprop.getWeightInputHidden();
				double[][] wHiddenOutput = rprop.getWeightHiddenOutput();
				
				int c = 0;
				
				for (int i = 0; i < wInputHidden.length; i++) {
					for (int j = 0; j < wInputHidden[0].length; j++) {
						wInputHidden[i][j] = x[c++];
					}
				}
				
				for (int i = 0; i < wHiddenOutput.length; i++) {
					for (int j = 0; j < wHiddenOutput[0].length; j++) {
						wHiddenOutput[i][j] = x[c++];
					}
				}
				
				rprop.setWeightInputHidden(wInputHidden);
				rprop.setWeightHiddenOutput(wHiddenOutput);
				
				double mse = 0;
				
				for (int i = 0; i < data.length; i++) {
					double output[] = rprop.calculate(data[i]);
					double diff = (output[0] - data[i][data[0].length - 1]); // FIXME hardcoded output[0]
					mse += diff * diff; 
				}
				
				mse /= data.length;
				
				return 1 / mse;
			}
			public double getOutput(double x[]) {
				return 0;
			}
		};
		
		ArtificialBeeColony abc = new ArtificialBeeColony(10);
		abc.optimize(problem, 500);
		
		IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(2, 5, 1);
		
		double[][] wInputHidden = rprop.getWeightInputHidden();
		double[][] wHiddenOutput = rprop.getWeightHiddenOutput();
		
		int c = 0;
		
		for (int i = 0; i < wInputHidden.length; i++) {
			for (int j = 0; j < wInputHidden[0].length; j++) {
				wInputHidden[i][j] = abc.getBestSolution()[c++];
			}
		}
		
		for (int i = 0; i < wHiddenOutput.length; i++) {
			for (int j = 0; j < wHiddenOutput[0].length; j++) {
				wHiddenOutput[i][j] = abc.getBestSolution()[c++];
			}
		}
		
		rprop.setWeightInputHidden(wInputHidden);
		rprop.setWeightHiddenOutput(wHiddenOutput);
		rprop.setTargetMse(0.0001);
		rprop.setMaxEpoch(50000);
		
		while (rprop.canTrain()) {
			rprop.train(data);
			
			System.out.println(rprop.getEpoch() + "\t" + rprop.getMse());
		}		
		
		return rprop.getMse();
	}
	
	public double experiment07() {
		data = Utils.load("data.csv", 1);
		data = Utils.cut(data, new int[] { 1, 4, 6, 8 });
		data = Utils.normalize(data);
		
		IOptimizationProblem problem = new IOptimizationProblem() {
			public int length() {
				return 28 + 2;
			}
			public double[] upperBound() {
				double[] b = new double[this.length()];
				
				for (int i = 0; i < b.length; i++) {
					b[i] = 1;
				}
				
				return b;
			}
			public double[] lowerBound() {
				double[] b = new double[this.length()];
				
				for (int i = 0; i < b.length; i++) {
					b[i] = 0;
				}
				
				return b;
			}
			public double getFitness(double x[]) {
				IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(3, 5, 1);
				
				double[][] wInputHidden = rprop.getWeightInputHidden();
				double[][] wHiddenOutput = rprop.getWeightHiddenOutput();
				
				int c = 0;
				
				for (int i = 0; i < wInputHidden.length; i++) {
					for (int j = 0; j < wInputHidden[0].length; j++) {
						wInputHidden[i][j] = x[c++];
					}
				}
				
				for (int i = 0; i < wHiddenOutput.length; i++) {
					for (int j = 0; j < wHiddenOutput[0].length; j++) {
						wHiddenOutput[i][j] = x[c++];
					}
				}
				
				rprop.setWeightInputHidden(wInputHidden);
				rprop.setWeightHiddenOutput(wHiddenOutput);
				
				double mse = 0;
				
				for (int i = 0; i < data.length; i++) {
					double output[] = rprop.calculate(data[i]);
					double diff = (output[0] - data[i][data[0].length - 1]); // FIXME hardcoded output[0]
					mse += diff * diff; 
				}
				
				mse /= data.length;
				
				return 1 / mse;
			}
			public double getOutput(double x[]) {
				return 0;
			}
		};
		
		ArtificialBeeColony abc = new ArtificialBeeColony(10);
		abc.optimize(problem, 500);
		
		IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(3, 5, 1);
		
		double[][] wInputHidden = rprop.getWeightInputHidden();
		double[][] wHiddenOutput = rprop.getWeightHiddenOutput();
		
		int c = 0;
		
		for (int i = 0; i < wInputHidden.length; i++) {
			for (int j = 0; j < wInputHidden[0].length; j++) {
				wInputHidden[i][j] = abc.getBestSolution()[c++];
			}
		}
		
		for (int i = 0; i < wHiddenOutput.length; i++) {
			for (int j = 0; j < wHiddenOutput[0].length; j++) {
				wHiddenOutput[i][j] = abc.getBestSolution()[c++];
			}
		}
		
		rprop.setWeightInputHidden(wInputHidden);
		rprop.setWeightHiddenOutput(wHiddenOutput);
		rprop.setTargetMse(0.0001);
		rprop.setMaxEpoch(50000);
		
		while (rprop.canTrain()) {
			rprop.train(data);
			
			System.out.println(rprop.getEpoch() + "\t" + rprop.getMse());
		}		
		
		return rprop.getMse();
	}
	
	
	public double experiment08() {
		data = Utils.load("data.csv", 1);
		data = Utils.cut(data, new int[] { 1, 4, 6, 5, 8 });
		data = Utils.normalize(data);
		
		IOptimizationProblem problem = new IOptimizationProblem() {
			public int length() {
				return 34 + 2;
			}
			public double[] upperBound() {
				double[] b = new double[this.length()];
				
				for (int i = 0; i < b.length; i++) {
					b[i] = 1;
				}
				
				return b;
			}
			public double[] lowerBound() {
				double[] b = new double[this.length()];
				
				for (int i = 0; i < b.length; i++) {
					b[i] = 0;
				}
				
				return b;
			}
			public double getFitness(double x[]) {
				IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(4, 5, 1);
				
				double[][] wInputHidden = rprop.getWeightInputHidden();
				double[][] wHiddenOutput = rprop.getWeightHiddenOutput();
				
				int c = 0;
				
				for (int i = 0; i < wInputHidden.length; i++) {
					for (int j = 0; j < wInputHidden[0].length; j++) {
						wInputHidden[i][j] = x[c++];
					}
				}
				
				for (int i = 0; i < wHiddenOutput.length; i++) {
					for (int j = 0; j < wHiddenOutput[0].length; j++) {
						wHiddenOutput[i][j] = x[c++];
					}
				}
				
				rprop.setWeightInputHidden(wInputHidden);
				rprop.setWeightHiddenOutput(wHiddenOutput);
				
				double mse = 0;
				
				for (int i = 0; i < data.length; i++) {
					double output[] = rprop.calculate(data[i]);
					double diff = (output[0] - data[i][data[0].length - 1]); // FIXME hardcoded output[0]
					mse += diff * diff; 
				}
				
				mse /= data.length;
				
				return 1 / mse;
			}
			public double getOutput(double x[]) {
				return 0;
			}
		};
		
		ArtificialBeeColony abc = new ArtificialBeeColony(10);
		abc.optimize(problem, 500);
		
		IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(4, 5, 1);
		
		double[][] wInputHidden = rprop.getWeightInputHidden();
		double[][] wHiddenOutput = rprop.getWeightHiddenOutput();
		
		int c = 0;
		
		for (int i = 0; i < wInputHidden.length; i++) {
			for (int j = 0; j < wInputHidden[0].length; j++) {
				wInputHidden[i][j] = abc.getBestSolution()[c++];
			}
		}
		
		for (int i = 0; i < wHiddenOutput.length; i++) {
			for (int j = 0; j < wHiddenOutput[0].length; j++) {
				wHiddenOutput[i][j] = abc.getBestSolution()[c++];
			}
		}
		
		rprop.setWeightInputHidden(wInputHidden);
		rprop.setWeightHiddenOutput(wHiddenOutput);
		rprop.setTargetMse(0.0001);
		rprop.setMaxEpoch(50000);
		
		while (rprop.canTrain()) {
			rprop.train(data);
			
			System.out.println(rprop.getEpoch() + "\t" + rprop.getMse());
		}		
		
		return rprop.getMse();
	}
}
