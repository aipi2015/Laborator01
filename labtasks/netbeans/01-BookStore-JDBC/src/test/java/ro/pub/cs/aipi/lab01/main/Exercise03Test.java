package ro.pub.cs.aipi.lab01.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;

import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperations;
import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperationsImplementation;
import ro.pub.cs.aipi.lab01.general.Constants;

public class Exercise03Test {

    final private static String TABLE = "user";
    final private static int TABLE_NUMBER_OF_RECORDS = 2000;

    @BeforeClass
    public static void executeExercise03() {
        BookStore bookstore = new BookStore();
        int result = bookstore.exercise03(TABLE);
        if (result != 1) {
            fail("The record was not inserted!");
        }
    }

    @Test
    public void checkNumberOfRecords() {
        System.out.println("Test03 -INSERT- checkNumberOfRecords");
        DatabaseOperations databaseOperations = DatabaseOperationsImplementation.getInstance();
        try {
            assertEquals(TABLE + " table should have " + (TABLE_NUMBER_OF_RECORDS + 1) + " records",
                    (TABLE_NUMBER_OF_RECORDS + 1),
                    databaseOperations.getTableNumberOfRows(TABLE));
            System.out.println("Test passed!");
        } catch (SQLException sqlException) {
            System.out.println("An exception has occured: " + sqlException.getMessage());
            if (Constants.DEBUG) {
                sqlException.printStackTrace();
            }
        } finally {
            databaseOperations.releaseResources();
        }
    }

}
