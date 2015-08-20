package dhanar10.mastersthesis;

import dhanar10.mastersthesis.algorithm.artificialbeecolony.ArtificialBeeColony;
import dhanar10.mastersthesis.algorithm.artificialbeecolony.IOptimizationProblem;
import dhanar10.masterthesis.algorithm.onlinebpropneuralnetwork.OnlineBpropNeuralNetwork;
import dhanar10.mastersthesis.algorithm.irpropplusneuralnetwork.IRpropPlusNeuralNetwork;

public class MastersThesis {
	private double[][] data;

	public static void main(String[] args) {
		MastersThesis mt = new MastersThesis();
		
		double[] result = new double[30];
		
		for (int i = 0; i < result.length; i++)
		{
			//result[i] = mt.experiment02(); // rprop
			result[i] = mt.experiment06(); // bee+rprop
		}
		
		for (int i = 0; i < result.length; i++)
		{
			System.out.println(result[i]);
		}
	}
	
	public MastersThesis() {
		data = Utils.load("data.csv", 1);
		data = Utils.cut(data, new int[] { 1, 4, 8 });
		data = Utils.normalize(data);
	}

	public void experiment00() {
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				System.out.print(data[i][j] + "\t");
			}
			
			System.out.println();
		}
	}
	
	public void experiment01() {
		OnlineBpropNeuralNetwork bprop = new OnlineBpropNeuralNetwork(2, 5, 1);
		bprop.train(data, 0.7, 0.0001, 50000);
		
//		System.out.println();
//		
//		for (int i = 0; i < data.length; i++) {
//			double output[] = bprop.calculate(data[i]);
//			
//			for (int j = 0; j < data[i].length - output.length; j++) {
//				System.out.print(data[i][j] + "\t");
//			}
//			
//			for (int j = 0; j < output.length; j++) {
//				System.out.print(output[j] + (j + 1 != output.length ? "\t" : ""));
//			}
//			
//			System.out.println();
//		}
	}
	
	public double experiment02() {
		IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(2, 5, 1);
		rprop.setTargetMse(0.0001);
		rprop.setMaxEpoch(50000);
		
		while (rprop.canTrain()) {
			rprop.train(data);
			
			System.out.println(rprop.getEpoch() + "\t" + rprop.getMse());
		}
		
//		System.out.println();
//		
//		for (int i = 0; i < data.length; i++) {
//			double output[] = rprop.calculate(data[i]);
//			
//			for (int j = 0; j < data[i].length - output.length; j++) {
//				System.out.print(data[i][j] + "\t");
//			}
//			
//			for (int j = 0; j < output.length; j++) {
//				System.out.print(output[j] + (j + 1 != output.length ? "\t" : ""));
//			}
//			
//			System.out.println();
//		}
		
		return rprop.getMse();
	}
	
	public void experiment03() {
		IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(2, 5, 1);
		rprop.setTargetMse(0.0001);
		rprop.setMaxEpoch(50000);
		
		while (rprop.canTrain()) {
			rprop.train(data);
			
			System.out.println(rprop.getEpoch() + "\t" + rprop.getMse());
		}
		
		OnlineBpropNeuralNetwork bprop = new OnlineBpropNeuralNetwork(2, 5, 1);
		bprop.setWeightInputHidden(rprop.getWeightInputHidden());
		bprop.setWeightHiddenOutput(rprop.getWeightHiddenOutput());
		bprop.train(data, 0.7, 0.0001, 50000);
	}
	
	public void experiment04() {
		OnlineBpropNeuralNetwork bprop = new OnlineBpropNeuralNetwork(2, 5, 1);
		
		bprop.train(data, 0.7, 0.0001, 100000);
		
		IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(2, 5, 1);
		rprop.setWeightInputHidden(bprop.getWeightInputHidden());
		rprop.setWeightHiddenOutput(bprop.getWeightHiddenOutput());
		rprop.setTargetMse(0.0001);
		rprop.setMaxEpoch(100000);
		
		while (rprop.canTrain()) {
			rprop.train(data);
			
			System.out.println(rprop.getEpoch() + "\t" + rprop.getMse());
		}
	}
	
	public void experiment05() {
		IOptimizationProblem problem = new IOptimizationProblem() {
			public int length() {
				return 24;
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
					b[i] = -1;
				}
				
				return b;
			}
			public double getFitness(double x[]) {
				OnlineBpropNeuralNetwork bprop = new OnlineBpropNeuralNetwork(2, 5, 1);
				
				double[][] wInputHidden = bprop.getWeightInputHidden();
				double[][] wHiddenOutput = bprop.getWeightHiddenOutput();
				
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
				
				bprop.setWeightInputHidden(wInputHidden);
				bprop.setWeightHiddenOutput(wHiddenOutput);
				
				double mse = 0;
				
				for (int i = 0; i < data.length; i++) {
					double output[] = bprop.calculate(data[i]);
					double diff = (output[0] - data[i][data[0].length - 1]); // FIXME Hardcoded
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
		abc.optimize(problem, 1000);
		
		OnlineBpropNeuralNetwork bprop = new OnlineBpropNeuralNetwork(2, 5, 1);
		
		double[][] wInputHidden = bprop.getWeightInputHidden();
		double[][] wHiddenOutput = bprop.getWeightHiddenOutput();
		
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
		
		bprop.setWeightInputHidden(wInputHidden);
		bprop.setWeightHiddenOutput(wHiddenOutput);
		bprop.train(data, 0.7, 0.0001, 100000);
	}
	
	public double experiment06() {
		IOptimizationProblem problem = new IOptimizationProblem() {
			public int length() {
				return 24;
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
					double diff = (output[0] - data[i][data[0].length - 1]); // FIXME Hardcoded
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
		IOptimizationProblem problem = new IOptimizationProblem() {
			public int length() {
				return 24;
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
				rprop.setTargetMse(0.0001);
				rprop.setMaxEpoch(100);
				
				while (rprop.canTrain()) {
					rprop.train(data);
				}		
				
				return rprop.getMse();
			}
			public double getOutput(double x[]) {
				return 0;
			}
		};
		
		ArtificialBeeColony abc = new ArtificialBeeColony(5);
		abc.optimize(problem, 10);
		
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
}
