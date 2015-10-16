package ro.pub.cs.aipi.lab01.general;

public interface Constants {
	final public static String DATABASE_CONNECTION = "jdbc:mysql://localhost/";
	final public static String DATABASE_USERNAME = "root";
	final public static String DATABASE_PASSWORD = "StudentAipi2015$";
	final public static String DATABASE_NAME = "bookstore";

	final public static boolean DEBUG = false;

	final public static String LOAD_DATABASE_SCRIPT = "scripts/Laborator01l.sql";
	final public static String UNLOAD_DATABASE_SCRIPT = "scripts/Laborator01u.sql";

	final public static String INPUT_DIRECTORY = "input";
	final public static String OUTPUT_DIRECTORY = "output";
	
    final public static long SEED = 20152015L;
}
