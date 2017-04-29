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

}
