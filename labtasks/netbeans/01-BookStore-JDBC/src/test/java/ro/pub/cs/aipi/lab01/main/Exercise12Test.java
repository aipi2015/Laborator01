package ro.pub.cs.aipi.lab01.main;

import org.junit.BeforeClass;
import org.junit.Test;

public class Exercise12Test {
	
	@BeforeClass
	public static void executeExercise12() {
		BookStore bookstore = new BookStore();
		bookstore.exercise12();
	}

	@Test
	public void checkFileContent() {
		System.out.println("Test12 -SELECT- checkFileContent");
		AllTests.fileComparison("12books.txt");
	}
}
