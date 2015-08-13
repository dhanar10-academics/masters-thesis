package dhanar10.mastersthesis;

import dhanar10.masterthesis.algorithm.onlinebpropneuralnetwork.OnlineBpropNeuralNetwork;
import dhanar10.mastersthesis.algorithm.irpropplusneuralnetwork.IRpropPlusNeuralNetwork;

public class MastersThesis {
	private double[][] data;

	public static void main(String[] args) {
		MastersThesis mt = new MastersThesis();
		mt.experiment02();
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
		OnlineBpropNeuralNetwork bprop = new OnlineBpropNeuralNetwork(2, 4, 1);
		bprop.train(data, 0.7, 0.0001, 100000);
		
		System.out.println();
		
		for (int i = 0; i < data.length; i++) {
			double output[] = bprop.calculate(data[i]);
			
//			for (int j = 0; j < data[i].length - output.length; j++) {
//				System.out.print(data[i][j] + "\t");
//			}
			
			for (int j = 0; j < output.length; j++) {
				System.out.print(output[j] + (j + 1 != output.length ? "\t" : ""));
			}
			
			System.out.println();
		}
	}
	
	public void experiment02() {
		IRpropPlusNeuralNetwork rprop = new IRpropPlusNeuralNetwork(2, 4, 1);
		rprop.setTargetMse(0.0001);
		rprop.setMaxEpoch(100000);
		
		while (rprop.canTrain()) {
			rprop.train(data);
			
			System.out.println(rprop.getEpoch() + "\t" + rprop.getMse());
		}
		
		System.out.println();
		
		for (int i = 0; i < data.length; i++) {
			double output[] = rprop.calculate(data[i]);
			
//			for (int j = 0; j < data[i].length - output.length; j++) {
//				System.out.print(data[i][j] + "\t");
//			}
			
			for (int j = 0; j < output.length; j++) {
				System.out.print(output[j] + (j + 1 != output.length ? "\t" : ""));
			}
			
			System.out.println();
		}
	}
}
