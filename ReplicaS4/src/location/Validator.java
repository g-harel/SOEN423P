package location;

public abstract class Validator {
	public static boolean isLocationCode(String str) {
		return str.matches(AddressBook.locationPattern());
	}

	public static boolean isRecordID(String str) {
		return str.matches("[A-Z]{2}\\d{5}");
	}

	public static boolean isManagerRecordID(String str) {
		return Validator.isRecordID(str) && str.matches("MR\\d{5}");
	}

	public static boolean isEmployeeRecordID(String str) {
		return Validator.isRecordID(str) && str.matches("ER\\d{5}");
	}

	public static boolean isManagerID(String str) {
		return str.matches(AddressBook.locationPattern() + "\\d{5}");
	}

	public static boolean isEmployeeID(int num) {
		return num > 0;
	}

	public static boolean isEmail(String str) {
		return str.matches("[^\\s]+@[^\\s]+\\.[^\\s]{2,}");
	}

	public static boolean isFirstName(String str) {
		return str.matches("[\\w-]+(\\s[\\w-]+)*");
	}

	public static boolean isLastName(String str) {
		return str.matches("[\\w-]+(\\s[\\w-]+)*");
	}

	public static boolean isProjectID(String str) {
		return str.matches("P\\d{5}");
	}

	public static boolean isClientName(String str) {
		return str.matches("[\\w-]+(\\s[\\w-]+)*");
	}

	public static boolean isProjectName(String str) {
		return str.matches("[\\w-]+(\\s[\\w-]+)*");
	}

	public static boolean isFieldName(String str) {
		return str.matches("\\w+(\\.\\w+)?");
	}
}
