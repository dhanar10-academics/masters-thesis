package dhanar10.mastersthesis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Utils {
	public static double[][] load(String file, int skip) {
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
	
	public static double[][] cut(double[][] data, int[] columns) {
		double[][] cdata = new double[data.length][columns.length];
		
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < columns.length; j++) {
				cdata[i][j] = data[i][columns[j]];
			}
		}
		
		return cdata;
	}
	
	public static double[][] normalize(double data[][]) {
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
