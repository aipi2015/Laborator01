package ro.pub.cs.aipi.lab01.main;

import org.junit.BeforeClass;
import org.junit.Test;

public class Exercise11Test {

    @BeforeClass
    public static void executeExercise11() {
        BookStore bookstore = new BookStore();
        bookstore.exercise11();
    }

    @Test
    public void checkFileContent() {
        System.out.println("Test11 -TRANSACTION- checkFileContent");
        AllTests.fileComparison("11supply_orders.txt");
    }
}
