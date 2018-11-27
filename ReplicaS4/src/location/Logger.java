package location;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Logger {
	private static BufferedWriter writer;
	private static String logPath = "./log/";

	public static void writeTo(String name) {
		new File(logPath).mkdirs();
		String filename = logPath + name + ".log";
		try {
			Logger.writer = new BufferedWriter(new FileWriter(filename));
		} catch (IOException e) {
			System.err.println("could not open log file: " + e);
		}
	}

	public static String log(String format, Object... args) {
		String str = String.format(format, args);
		String msg = String.format("%tY/%<tb/%<te %<tT - %s%n", LocalDateTime.now(), str);

		if (Logger.writer != null) {
			try {
				Logger.writer.write(msg);
				Logger.writer.flush();
			} catch (Exception e) {
				System.err.println("could not write logs to file: " + e);
			}
		}
		System.out.print(msg);
		return str;
	}

	public static String err(String format, Object... args) {
		String str = String.format(format, args);
		String msg = String.format("%tY/%<tb/%<te %<tT - ERROR: %s%n", LocalDateTime.now(), str);

		if (Logger.writer != null) {
			try {
				Logger.writer.write(msg);
				Logger.writer.flush();
			} catch (Exception e) {
				System.err.println("could not write logs to file: " + e);
			}
		}
		System.err.print("\u001B[1;31m" + msg + "\u001B[0m");
		return str;
	}
}
