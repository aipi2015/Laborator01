package ro.pub.cs.aipi.lab01.main;

import org.junit.BeforeClass;
import org.junit.Test;

public class Exercise09Test {
	
	@BeforeClass
	public static void executeExercise09() {
		BookStore bookstore = new BookStore();
		bookstore.exercise09();
	}

	@Test
	public void checkFileContent() {
		System.out.println("Test09 -SELECT&JOIN / ROWSETS- checkFileContent");
		AllTests.fileComparison("09books.txt");
	}
}
