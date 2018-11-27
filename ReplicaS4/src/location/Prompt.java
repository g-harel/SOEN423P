package location;

import java.util.Scanner;

public class Prompt {
	private static Scanner scanner = new Scanner(System.in);

	public static String forValue(String valueName) {
		System.out.print(valueName + ": ");
		String value = Prompt.scanner.nextLine().trim();
		Logger.log(valueName + ": " + value);
		return value;
	}
}
