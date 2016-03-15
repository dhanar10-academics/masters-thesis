package dhanar10.mastersthesis;

import dhanar10.mastersthesis.algorithm.artificialbeecolony.ArtificialBeeColony;
import dhanar10.mastersthesis.algorithm.artificialbeecolony.IOptimizationProblem;
import dhanar10.mastersthesis.algorithm.irpropplusneuralnetwork.IRpropPlusNeuralNetwork;
import dhanar10.mastersthesis.algorithm.onlinebpropneuralnetwork.OnlineBpropNeuralNetwork;

public class MastersThesis {
	private static final int HIDDEN_NEURON = 5;
	
	private static final double TARGET_MSE = 0.0001;
	private static final int MAX_EPOCH = 50000;
	private static final double LEARNING_RATE = 0.3;
	
	private static final int COLONY_SIZE = 100;
	private static final int MAXIMUM_CYCLE_NUMBER = 500;
	private static final double UPPER_BOUND = 5;
	private static final double LOWER_BOUND = -5;
	
	private double[][] data;
	
	public static void main(String[] args) {
		MastersThesis mt = new MastersThesis(Integer.parseInt(args[0]));
		
		double[] result = new double[30];
		
		for (int i = 0; i < result.length; i++)
		{
			result[i] = mt.performExperiment(args[1]);
		}
		
		System.out.printf("Variable: %s\n", args[0]);
		System.out.printf("Method: %s\n", args[1]);
		
		for (int i = 0; i < result.length; i++)
		{
			System.out.println(result[i]);
		}
	}
	
	public MastersThesis(int variable) {
		data = Utils.load("data.csv", 1);
		
		// BEGIN FEATURE SELECTION A
		
		switch (variable) {
			case 1:
				data = Utils.cut(data, new int[] { 1, 8 });
				break;
			case 2:
				data = Utils.cut(data, new int[] { 1, 4, 8 });
				break;
			case 3:
				data = Utils.cut(data, new int[] { 1, 4, 6, 8 });
				break;
			case 4:
				data = Utils.cut(data, new int[] { 1, 4, 6, 5, 8 });
				break;
			case 7:
				data = Utils.cut(data, new int[] { 1, 2, 3, 4, 5, 6, 7, 8 }); // no feature selection
				break;
			default:
				System.exit(1);
					
		}

		// END FEATURE SELECTION A
		
		// BEGIN FEATURE SELECTION B
		
		/*switch (variable) {
			case 1:
				data = Utils.cut(data, new int[] { 1, 8 });
				break;
			case 2:
				data = Utils.cut(data, new int[] { 1, 7, 8 });
				break;
			case 3:
				data = Utils.cut(data, new int[] { 1, 7, 3, 8 });
				break;
			case 4:
				data = Utils.cut(data, new int[] { 1, 7, 3, 2, 8 });
				break;
			case 7:
				data = Utils.cut(data, new int[] { 1, 2, 3, 4, 5, 6, 7, 8 }); // no feature selection
				break;
			default:
				System.exit(1);
					
		}*/
		
		// END FEATURE SELECTION B
		
		data = Utils.normalize(data);
	}
	
	public double performExperiment(String method) {
		// BEGIN METHOD
		
		if (method.equals("bprop")) {
			return bprop();
		}
		else if (method.equals("abcbprop")) {
			return abcbprop();
		}
		else if (method.equals("rprop")) {
			return rprop();
		} 
		else if (method.equals("abcrprop")) {
			return abcrprop();
		}
		
		System.exit(2);
		
		return 0;
		
		//END METHOD
	}
	
	public double rprop() {
		IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(data[0].length - 1, HIDDEN_NEURON, 1);
		rprop.setTargetMse(TARGET_MSE);
		rprop.setMaxEpoch(MAX_EPOCH);
		
		while (rprop.canTrain()) {
			rprop.train(data);
			
			System.out.println(rprop.getEpoch() + "\t" + rprop.getMse());
		}
		
		return rprop.getMse();
	}
	
	public double abcrprop() {
		IOptimizationProblem problem = new IOptimizationProblem() {
			public int length() {
				return (data[0].length * (HIDDEN_NEURON + 1)) + (HIDDEN_NEURON + 1);
			}
			public double[] upperBound() {
				double[] b = new double[this.length()];
				
				for (int i = 0; i < b.length; i++) {
					b[i] = UPPER_BOUND;
				}
				
				return b;
			}
			public double[] lowerBound() {
				double[] b = new double[this.length()];
				
				for (int i = 0; i < b.length; i++) {
					b[i] = LOWER_BOUND;
				}
				
				return b;
			}
			public double getFitness(double x[]) {
				IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(data[0].length - 1, HIDDEN_NEURON, 1);
				
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
		
		ArtificialBeeColony abc = new ArtificialBeeColony(COLONY_SIZE);
		abc.optimize(problem, MAXIMUM_CYCLE_NUMBER);
		
		IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(data[0].length - 1, HIDDEN_NEURON, 1);
		
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
		rprop.setTargetMse(TARGET_MSE);
		rprop.setMaxEpoch(MAX_EPOCH);
		
		while (rprop.canTrain()) {
			rprop.train(data);
			
			System.out.println(rprop.getEpoch() + "\t" + rprop.getMse());
		}		
		
		return rprop.getMse();
	}
	
	public double bprop() {
		OnlineBpropNeuralNetwork bprop = new OnlineBpropNeuralNetwork(data[0].length - 1, HIDDEN_NEURON, 1);
		bprop.setTargetMse(TARGET_MSE);
		bprop.setMaxEpoch(MAX_EPOCH);
		bprop.setLearningRate(LEARNING_RATE);
		
		while (bprop.canTrain()) {
			bprop.train(data);
			
			System.out.println(bprop.getEpoch() + "\t" + bprop.getMse());
		}
		
		return bprop.getMse();
	}
	
	public double abcbprop() {
		IOptimizationProblem problem = new IOptimizationProblem() {
			public int length() {
				return (data[0].length * (HIDDEN_NEURON + 1)) + (HIDDEN_NEURON + 1);
			}
			public double[] upperBound() {
				double[] b = new double[this.length()];
				
				for (int i = 0; i < b.length; i++) {
					b[i] = UPPER_BOUND;
				}
				
				return b;
			}
			public double[] lowerBound() {
				double[] b = new double[this.length()];
				
				for (int i = 0; i < b.length; i++) {
					b[i] = LOWER_BOUND;
				}
				
				return b;
			}
			public double getFitness(double x[]) {
				OnlineBpropNeuralNetwork bprop = new OnlineBpropNeuralNetwork(data[0].length - 1, HIDDEN_NEURON, 1);
				
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
		
		ArtificialBeeColony abc = new ArtificialBeeColony(COLONY_SIZE);
		abc.optimize(problem, MAXIMUM_CYCLE_NUMBER);
		
		OnlineBpropNeuralNetwork bprop = new OnlineBpropNeuralNetwork(data[0].length - 1, HIDDEN_NEURON, 1);
		
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
		bprop.setTargetMse(TARGET_MSE);
		bprop.setMaxEpoch(MAX_EPOCH);
		bprop.setLearningRate(LEARNING_RATE);
		
		while (bprop.canTrain()) {
			bprop.train(data);
			
			System.out.println(bprop.getEpoch() + "\t" + bprop.getMse());
		}		
		
		return bprop.getMse();
	}
}
