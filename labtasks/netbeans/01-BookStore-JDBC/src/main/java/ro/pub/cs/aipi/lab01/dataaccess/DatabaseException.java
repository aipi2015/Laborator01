package ro.pub.cs.aipi.lab01.dataaccess;

public class DatabaseException extends Exception {

	private static final long serialVersionUID = 2015L;

	private static final String MESSAGE = "An error has occurred while executing the query";

	public DatabaseException() {
		super(MESSAGE);
	}

	public DatabaseException(String message) {
		super(MESSAGE + ": " + message);
	}

	public DatabaseException(Throwable cause) {
		super(MESSAGE, cause);
	}

	public DatabaseException(String message, Throwable cause) {
		super(MESSAGE + ": " + message, cause);
	}

}