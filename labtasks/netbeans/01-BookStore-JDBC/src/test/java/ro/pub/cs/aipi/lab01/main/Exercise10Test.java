package ro.pub.cs.aipi.lab01.main;

import org.junit.BeforeClass;
import org.junit.Test;

public class Exercise10Test {
	
	@BeforeClass
	public static void executeExercise10() {
		BookStore bookstore = new BookStore();
		bookstore.exercise10();
	}

	@Test
	public void checkFileContent() {
		System.out.println("Test10 -SELECT&FILTER / ROWSETS- checkFileContent");
		AllTests.fileComparison("10books.txt");
	}
}
