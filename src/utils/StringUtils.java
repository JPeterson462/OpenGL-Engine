package utils;

import java.util.Calendar;

public class StringUtils {
	
	public static String getDate() {
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		int year = calendar.get(Calendar.YEAR);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		return leadingZeros(month) + leadingZeros(day) + leadingZeros(year) + "_" + leadingZeros(hour) + leadingZeros(minute) + leadingZeros(second);
	}
	
	private static String leadingZeros(int number) {
		if (number < 10)
			return "0" + number;
		return "" + number;
	}

}
