package ro.pub.cs.aipi.lab01.main;

import org.junit.BeforeClass;
import org.junit.Test;

public class Exercise07Test {

	@BeforeClass
	public static void executeExercise07() {
		BookStore bookstore = new BookStore();
		bookstore.exercise07();
	}

	@Test
	public void checkFileContent() {
		System.out.println("Test07 -STORED ROUTINE- checkFileContent");
		AllTests.fileComparison("07user_total_invoice_value.txt");
	}
}
