package utils;

import java.util.Arrays;

public class ArrayUtils {
	
	public static String toString(float[][] data) {
		String string = "[ ";
		for (int i = 0; i < data.length; i++) {
			float[] array = data[i];
			if (array == null) {
				string += "null ";
			} else {
				string += Arrays.toString(array) + " ";
			}
		}
		string += "]";
		return string;
	}
	
	public static void fill(float[][] data, float value) {
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				data[i][j] = value;
			}
		}
	}

}
