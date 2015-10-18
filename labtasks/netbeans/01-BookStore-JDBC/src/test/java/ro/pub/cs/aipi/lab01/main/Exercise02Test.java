package ro.pub.cs.aipi.lab01.main;

import org.junit.BeforeClass;
import org.junit.Test;

public class Exercise02Test {

    @BeforeClass
    public static void executeExercise02() {
        BookStore bookstore = new BookStore();
        bookstore.exercise02();
    }

    @Test
    public void checkFileContent() {
        System.out.println("Test02 -SELECT- checkFileContent");
        AllTests.fileComparison("02books.txt");
    }

}
