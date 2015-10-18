package ro.pub.cs.aipi.lab01.main;

import org.junit.BeforeClass;
import org.junit.Test;

public class Exercise06Test {

    @BeforeClass
    public static void executeExercise06() {
        BookStore bookstore = new BookStore();
        bookstore.exercise06();
    }

    @Test
    public void checkFileContent() {
        System.out.println("Test06 -SELECT- checkFileContent");
        AllTests.fileComparison("06writers_bibliography.txt");
    }

}
