package ro.pub.cs.aipi.lab01.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperations;
import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperationsImplementation;
import ro.pub.cs.aipi.lab01.general.Constants;

public class Exercise05Test {
	
	final private static int NUMBER_OF_RECORDS = 649;
	
	@BeforeClass
	public static void executeExercise05() {
		BookStore bookstore = new BookStore();
		int result = bookstore.exercise05();
		System.out.println("Test05 -DELETE- checkNumberOfRecords");
		if (result != NUMBER_OF_RECORDS) {
			fail("The records were not deleted! " + result);
		} else {
			System.out.println("Test passed!");
		}
	}
	
	@Test
	public void checkFileContent() {
		System.out.println("Test05 -DELETE- checkFileContent");
		DatabaseOperations databaseOperations = DatabaseOperationsImplementation.getInstance();
		Path inputFile = Paths.get(Constants.INPUT_DIRECTORY + "/05writers.txt");
		assertTrue("Reference file does not exist or cannot be accessed!", 
				inputFile != null && Files.isRegularFile(inputFile) && Files.isReadable(inputFile));
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader inputFileBufferedReader = Files.newBufferedReader(inputFile, charset)) {
			String inputFileCurrentLine = null;
    		do {
    			long writerId = -1;
    			inputFileCurrentLine = inputFileBufferedReader.readLine();
    			if (inputFileCurrentLine != null) {
    				writerId = Long.parseLong(inputFileCurrentLine);
    				ArrayList<String> attributes = new ArrayList<>();
    				attributes.add("COUNT(*)");
    				ArrayList<ArrayList<String>> content = databaseOperations.getTableContent("writer", attributes, "id=" + writerId, null, null, null);
    				assertEquals("writer " + writerId + " should have been deleted", 
    						0, 
    						Integer.parseInt(content.get(0).get(0)));
    			}
    		} while (inputFileCurrentLine != null);
    		System.out.println("Test passed!");
		} catch (SQLException sqlException) {
			fail("An exception has occured: " + sqlException.getMessage());
			if (Constants.DEBUG) {
				sqlException.printStackTrace();
			}
		} catch (IOException ioException) {
			fail("An exception has occurred!" + ioException.getMessage());
			if (Constants.DEBUG) {
				ioException.printStackTrace();
			}
		} finally {
			databaseOperations.releaseResources();
		}
	}
}
