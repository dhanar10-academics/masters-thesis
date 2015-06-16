package dhanar10.mastersthesisexperiment.algorithm.artificialbeecolony;

public interface IOptimizationProblem {
	public int length();
	public double[] upperBound();
	public double[] lowerBound();
	public double getFitness(double x[]);
	public double getOutput(double x[]);
}

