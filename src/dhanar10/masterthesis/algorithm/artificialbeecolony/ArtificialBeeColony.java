package dhanar10.masterthesis.algorithm.artificialbeecolony;

public class ArtificialBeeColony {
	private int foodSource;
	private double[] bestSolution;
	
	public ArtificialBeeColony(int foodSource) {
		this.foodSource = foodSource;
	}
	
	public void optimize(IOptimizationProblem optimizationProblem, int maximumCycleNumber) {
		double x[][] = new double[foodSource][optimizationProblem.length()];
		int xlimit[] = new int[foodSource];
		double xbest[] = new double[optimizationProblem.length()];
		
		// initialization
		
		for (int m = 0; m < x.length; m++) {
			for (int i = 0; i < x[0].length; i++) {
				x[m][i] = optimizationProblem.lowerBound()[i] + Math.random() * (optimizationProblem.upperBound()[i] - optimizationProblem.lowerBound()[i]);
			}
		}
		
		for (int mcn = 1; mcn <= maximumCycleNumber; mcn++) {
			for (int m = 0; m < x.length; m++) {
				
				// employed
				
				double v[] = new double[optimizationProblem.length()];
				int k = 0;
				
				do {
					k = (int) Math.round(Math.random() * (x.length - 1));
				} while (k == m);
				
				for (int i = 0; i < x[0].length; i++) {
					v[i] = x[m][i] + (Math.random() * 2 - 1) * (x[m][i] - x[k][i]);
				}
				
				for (int i = 0; i < x[0].length; i++) {
					v[i] = v[i] < optimizationProblem.lowerBound()[i] ? optimizationProblem.lowerBound()[i] : v[i];
					v[i] = v[i] > optimizationProblem.upperBound()[i] ? optimizationProblem.upperBound()[i] : v[i];
				}
				
				if (optimizationProblem.getFitness(v) > optimizationProblem.getFitness(x[m])) {
					x[m] = v;
				}
				else {
					xlimit[m]++;
				}
			}
			
			// onlooker
			
			for (int t = 0; t < x.length; t++) {
				double xfitmax = 0;
				double v[] = new double[optimizationProblem.length()];
				int m = 0;
				int k = 0;
				
				for (int i = 0; i < x.length; i++) {
					if (optimizationProblem.getFitness(x[i]) > xfitmax) {
						xfitmax = optimizationProblem.getFitness(x[i]);
					}
				}
				
				while (true) {
					m = (int) (Math.random() * x.length);
					
					if (Math.random() < optimizationProblem.getFitness(x[m]) / xfitmax)
						break;
				}
				
				do {
					k = (int) Math.round(Math.random() * (x.length - 1));
				} while (k == m);
				
				for (int i = 0; i < x[0].length; i++) {
					v[i] = x[m][i] + (Math.random() * 2 - 1) * (x[m][i] - x[k][i]);
				}
				
				for (int i = 0; i < x[0].length; i++) {
					v[i] = v[i] < optimizationProblem.lowerBound()[i] ? optimizationProblem.lowerBound()[i] : v[i];
					v[i] = v[i] > optimizationProblem.upperBound()[i] ? optimizationProblem.upperBound()[i] : v[i];
				}
				
				if (optimizationProblem.getFitness(v) > optimizationProblem.getFitness(x[m])) {
					x[m] = v;
				}
			}
			
			// scout
			
			for (int m = 0; m < x.length; m++) {
				if (xlimit[m] > foodSource * 2) {
					for (int i = 0; i < x[0].length; i++) {
						x[m][i] = optimizationProblem.lowerBound()[i] + Math.random() * (optimizationProblem.upperBound()[i] - optimizationProblem.lowerBound()[i]);
					}
				}
			}
			
			// remember the best solution so far
			
			for (int m = 0; m < x.length; m++) {
				if (optimizationProblem.getFitness(x[m]) > optimizationProblem.getFitness(xbest)) {
					xbest = x[m].clone();
				}
			}
			
			System.out.println(mcn + "\t" + optimizationProblem.getOutput(xbest) + "\t" + optimizationProblem.getFitness(xbest));
		}
		
		bestSolution = xbest;
	}
	
	public double[] getBestSolution() {
		return bestSolution;
	}
}

