package ro.pub.cs.aipi.lab01.main;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

public class Exercise04Test {
	
	final private static int NUMBER_OF_RECORDS = 4104;
	
	@BeforeClass
	public static void executeExercise04() {
		BookStore bookstore = new BookStore();
		int result = bookstore.exercise04();
		System.out.println("Test04 -UPDATE- checkNumberOfRecords");
		if (result != NUMBER_OF_RECORDS) {
			fail("The records were not updated! " + result);
		} else {
			System.out.println("Test passed!");
		}
	}
	
	@Test
	public void checkFileContent() {
		System.out.println("Test04 -UPDATE- checkFileContent");
		AllTests.fileComparison("04book_presentation_prices.txt");
	}
}
