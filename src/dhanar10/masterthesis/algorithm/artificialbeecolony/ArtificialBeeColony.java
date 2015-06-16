package dhanar10.masterthesis.algorithm.artificialbeecolony;

public class ArtificialBeeColony {
	private int foodSource;
	private double[] bestSolution;
	
	public ArtificialBeeColony(int foodSource) {
		this.foodSource = foodSource;
	}
	
	public void optimize(IOptimizationProblem problem, int maximumCycleNumber) {
		double x[][] = new double[foodSource][problem.length()];
		int xlimit[] = new int[foodSource];
		double xbest[] = new double[problem.length()];
		
		// initialization
		
		for (int m = 0; m < x.length; m++) {
			for (int i = 0; i < x[0].length; i++) {
				x[m][i] = problem.lowerBound()[i] + Math.random() * (problem.upperBound()[i] - problem.lowerBound()[i]);
			}
		}
		
		for (int mcn = 1; mcn <= maximumCycleNumber; mcn++) {
			for (int m = 0; m < x.length; m++) {
				
				// employed
				
				double v[] = new double[problem.length()];
				int k = 0;
				
				do {
					k = (int) Math.round(Math.random() * (x.length - 1));
				} while (k == m);
				
				for (int i = 0; i < x[0].length; i++) {
					v[i] = x[m][i] + (Math.random() * 2 - 1) * (x[m][i] - x[k][i]);
				}
				
				for (int i = 0; i < x[0].length; i++) {
					v[i] = v[i] < problem.lowerBound()[i] ? problem.lowerBound()[i] : v[i];
					v[i] = v[i] > problem.upperBound()[i] ? problem.upperBound()[i] : v[i];
				}
				
				if (problem.getFitness(v) > problem.getFitness(x[m])) {
					x[m] = v;
				}
				else {
					xlimit[m]++;
				}
			}
			
			// onlooker
			
			for (int t = 0; t < x.length; t++) {
				double xfitmax = 0;
				double v[] = new double[problem.length()];
				int m = 0;
				int k = 0;
				
				for (int i = 0; i < x.length; i++) {
					if (problem.getFitness(x[i]) > xfitmax) {
						xfitmax = problem.getFitness(x[i]);
					}
				}
				
				while (true) {
					m = (int) (Math.random() * x.length);
					
					if (Math.random() < problem.getFitness(x[m]) / xfitmax)
						break;
				}
				
				do {
					k = (int) Math.round(Math.random() * (x.length - 1));
				} while (k == m);
				
				for (int i = 0; i < x[0].length; i++) {
					v[i] = x[m][i] + (Math.random() * 2 - 1) * (x[m][i] - x[k][i]);
				}
				
				for (int i = 0; i < x[0].length; i++) {
					v[i] = v[i] < problem.lowerBound()[i] ? problem.lowerBound()[i] : v[i];
					v[i] = v[i] > problem.upperBound()[i] ? problem.upperBound()[i] : v[i];
				}
				
				if (problem.getFitness(v) > problem.getFitness(x[m])) {
					x[m] = v;
				}
			}
			
			// scout
			
			for (int m = 0; m < x.length; m++) {
				if (xlimit[m] > foodSource * 2) {
					for (int i = 0; i < x[0].length; i++) {
						x[m][i] = problem.lowerBound()[i] + Math.random() * (problem.upperBound()[i] - problem.lowerBound()[i]);
					}
				}
			}
			
			// remember the best solution so far
			
			for (int m = 0; m < x.length; m++) {
				if (problem.getFitness(x[m]) > problem.getFitness(xbest)) {
					xbest = x[m].clone();
				}
			}
			
			System.out.print(mcn + "\t");
			
			for (int i = 0; i < xbest.length; i++) {
				System.out.print((int) xbest[i] + "\t");
			}
			
			System.out.println(problem.getFitness(xbest));
		}
		
		bestSolution = xbest;
	}
	
	public double[] getBestSolution() {
		return bestSolution;
	}
}

