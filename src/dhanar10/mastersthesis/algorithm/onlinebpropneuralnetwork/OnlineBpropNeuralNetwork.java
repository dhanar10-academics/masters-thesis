package dhanar10.mastersthesis.algorithm.onlinebpropneuralnetwork;

public class OnlineBpropNeuralNetwork {
	private double learningRate = 0.7;
	private double targetMse = 0.0001;
	private int maxEpoch = 1000;
	
	private double mse = Double.MAX_VALUE;
	private int epoch = 0;
	
	private double yInput[];
	private double yHidden[];
	private double yOutput[];
	
	private double wInputHidden[][];
	private double wHiddenOutput[][];

	public static void main(String[] args) {
		double data[][] = {{0, 0, 0}, {0, 1, 1}, {1, 0, 1}, {1, 1, 0}}; // XOR
		
		OnlineBpropNeuralNetwork bprop = new OnlineBpropNeuralNetwork(2, 4, 1);
		//boolean success = bprop.train(data, 0.7, 0.001, 10000); // FIXME
		
		System.out.println();
		
		for (int i = 0; i < data.length; i++) {
			double output[] = bprop.calculate(data[i]);
			
			for (int j = 0; j < data[i].length - output.length; j++) {
				System.out.printf("%.2f%s", data[i][j], "\t");
			}
			
			for (int j = 0; j < output.length; j++) {
				System.out.printf("%.2f%s", output[j], j + 1 != output.length ? "\t" : "");
			}
			
			System.out.println();
		}
		
		//System.exit(success ? 0 : 1); // FIXME
	}
	
	public OnlineBpropNeuralNetwork(int input, int hidden, int output) {
		yInput = new double[input + 1];
		yHidden = new double[hidden + 1];
		yOutput = new double[output];
		
		wInputHidden = new double[yInput.length][yHidden.length];
		wHiddenOutput = new double[yHidden.length][yOutput.length];
		
		for (int i = 0; i < wInputHidden.length; i++) {
			for (int j = 0; j < wInputHidden[0].length; j++) {
				wInputHidden[i][j] = Math.random() /** 2 - 1*/;
			}
		}
		
		for (int i = 0; i < wHiddenOutput.length; i++) {
			for (int j = 0; j < wHiddenOutput[0].length; j++) {
				wHiddenOutput[i][j] = Math.random() /** 2 - 1*/;
			}
		}
	}
	
	public boolean train(double data[][]) {
		if (!canTrain()) {
			return false;
		}
		
		mse = 0;
		
		epoch++;
		
		for (double[] d : data) {
			double yTarget[] = new double[yOutput.length];
			
			double eHidden[] = new double[yHidden.length];
			double eOutput[] = new double[yOutput.length];
			
			for (int i = 0; i < d.length; i++) {
				if (i < yInput.length - 1) {
					yInput[i] = d[i];
				}
				else {
					yTarget[i - (yInput.length - 1)] = d[i];
				}
			}
			
			yInput[yInput.length - 1] = 1;
			
			for (int i = 0; i < yHidden.length - 1; i++) {
				yHidden[i] = 0;
				
				for (int j = 0; j < yInput.length; j++) {
					yHidden[i] += yInput[j] * wInputHidden[j][i];
				}
				
				yHidden[i] = sigmoid(yHidden[i]);
			}
			
			yHidden[yHidden.length - 1] = 1;
			
			for (int i = 0; i < yOutput.length; i++) {
				yOutput[i] = 0;
				
				for (int j = 0; j < yHidden.length; j++) {
					yOutput[i] += yHidden[j] * wHiddenOutput[j][i];
				}
				
				yOutput[i] = sigmoid(yOutput[i]);
			}
			
			for (int i = 0; i < yOutput.length; i++) {
				eOutput[i] = (yTarget[i] - yOutput[i]) * dsigmoid(yOutput[i]);
			}
			
			for (int i = 0; i < yHidden.length; i++) {
				for (int j = 0; j < yOutput.length; j++) {
					eHidden[i] += eOutput[j] * wHiddenOutput[i][j];
				}
				
				eHidden[i] *= dsigmoid(yHidden[i]);
			}
			
			for (int j = 0; j < yHidden.length; j++) {
				for (int k = 0; k < yInput.length; k++) {
					wInputHidden[k][j] += learningRate * eHidden[j] * yInput[k];
				}
			}
			
			for (int j = 0; j < yOutput.length; j++) {
				for (int k = 0; k < yHidden.length; k++) {
					wHiddenOutput[k][j] += learningRate * eOutput[j] * yHidden[k];
				}
			}
			
			for (int j = 0; j < yOutput.length; j++) {
				mse += (yTarget[j] - yOutput[j]) * (yTarget[j] - yOutput[j]);
			}
		}
		
		mse /= data.length * data[0].length;
		
		//System.out.println(epoch + "\t" + mse);
		
		return true;
	}
	
	public double[] calculate(double input[]) {
		for (int i = 0; i < yInput.length - 1; i++) {
			yInput[i] = input[i];
		}
		
		yInput[yInput.length - 1] = 1;
		
		for (int i = 0; i < yHidden.length - 1; i++) {
			yHidden[i] = 0;
			
			for (int j = 0; j < yInput.length; j++) {
				yHidden[i] += yInput[j] * wInputHidden[j][i];
			}
			
			yHidden[i] = sigmoid(yHidden[i]);
		}
		
		yHidden[yHidden.length - 1] = 1;
		
		for (int i = 0; i < yOutput.length; i++) {
			yOutput[i] = 0;
			
			for (int j = 0; j < yHidden.length; j++) {
				yOutput[i] += yHidden[j] * wHiddenOutput[j][i];
			}
			
			yOutput[i] = sigmoid(yOutput[i]);
		}
		
		return yOutput;
	}
	
	public boolean canTrain() {
		return !(mse < targetMse || epoch == maxEpoch);
	}
	
	public double getLearningRate() {
		return learningRate;
	}
	
	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}
	
	public double getTargetMse() {
		return targetMse;
	}
	
	public void setTargetMse(double targetMse) {
		this.targetMse = targetMse;
	}
	
	public double getMaxEpoch() {
		return maxEpoch;
	}
	
	public void setMaxEpoch(int maxEpoch) {
		this.maxEpoch = maxEpoch;
	}
	
	public double getMse() {
		return mse;
	}
	
	public int getEpoch() {
		return epoch;
	}
	
	public double[][] getWeightInputHidden() {
		return this.wInputHidden.clone();
	}
	
	public double[][] getWeightHiddenOutput() {
		return this.wHiddenOutput.clone();
	}
	
	public void setWeightInputHidden(double[][] wInputHidden) {
		this.wInputHidden = wInputHidden;
	}
	
	public void setWeightHiddenOutput(double[][] wHiddenOutput) {
		this.wHiddenOutput = wHiddenOutput;
	}
	
	private double sigmoid(double x) {
		return 1 / (1 + Math.pow(Math.E, -x));
	}
 
	private double dsigmoid(double x) {
		return x * (1 - x);
	}
}

