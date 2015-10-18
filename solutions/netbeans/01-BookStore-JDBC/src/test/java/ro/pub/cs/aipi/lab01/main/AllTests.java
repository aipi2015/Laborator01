package ro.pub.cs.aipi.lab01.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperations;
import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperationsImplementation;
import ro.pub.cs.aipi.lab01.general.Constants;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    Exercise01Test.class,
    Exercise02Test.class,
    Exercise03Test.class,
    Exercise04Test.class,
    Exercise05Test.class,
    Exercise06Test.class,
    Exercise07Test.class,
    Exercise08Test.class,
    Exercise09Test.class,
    Exercise10Test.class,
    Exercise11Test.class,
    Exercise12Test.class
})
public class AllTests {

    private static boolean databaseLoaded = false;
    private static boolean databaseUnloaded = false;

    static class DirectoryScanner implements FileVisitor<Path> {

        @Override
        public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
            try {
                Files.delete(file);
            } catch (IOException ioException) {
                System.out.format("File %s has not been deleted: %s!%n", file.getFileName(), ioException.getMessage());
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path directory, IOException ioException) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException ioException) throws IOException {
            System.out.format("File %s has not been deleted: %s!%n", file.getFileName(), ioException.getMessage());
            return FileVisitResult.CONTINUE;
        }
    }

    @BeforeClass
    public static void loadDatabase() {
        if (databaseLoaded) {
            return;
        }
        DatabaseOperations databaseOperations = DatabaseOperationsImplementation.getInstance();
        try {
            if (Constants.DEBUG) {
                System.out.println("loading database...");
            }
            databaseOperations.runScript(Constants.LOAD_DATABASE_SCRIPT);
            databaseOperations.setDatabaseName(Constants.DATABASE_NAME);
        } catch (SQLException sqlException) {
            System.out.println("An exception has occured: " + sqlException.getMessage());
            if (Constants.DEBUG) {
                sqlException.printStackTrace();
            }
        } finally {
            databaseLoaded = true;
            databaseOperations.releaseResources();
        }
    }

    @BeforeClass
    public static void clean() {
        if (Constants.DEBUG) {
            System.out.println("started cleaning...");
        }
        Path path = Paths.get(Constants.OUTPUT_DIRECTORY);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException ioException) {
                System.out.println("Could not create output directory: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
        DirectoryScanner directoryScanner = new DirectoryScanner();
        try {
            Files.walkFileTree(path, directoryScanner);
        } catch (IOException ioException) {
            System.out.format("Directory could not be scanned: %s!%n", ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
        if (Constants.DEBUG) {
            System.out.println("finished cleaning...");
        }
    }

    @AfterClass
    public static void unloadDatabase() {
        if (databaseUnloaded) {
            return;
        }
        DatabaseOperations databaseOperations = DatabaseOperationsImplementation.getInstance();
        try {
            if (Constants.DEBUG) {
                System.out.println("unloading database...");
            }
            databaseOperations.runScript(Constants.UNLOAD_DATABASE_SCRIPT);
        } catch (SQLException sqlException) {
            System.out.println("An exception has occured: " + sqlException.getMessage());
            if (Constants.DEBUG) {
                sqlException.printStackTrace();
            }
        } finally {
            databaseUnloaded = true;
            databaseOperations.releaseResources();
        }
    }

    public static void fileComparison(String fileName) {
        Path outputFile = Paths.get(Constants.OUTPUT_DIRECTORY + "/" + fileName);
        assertTrue("File " + fileName + " does not exist or cannot be accessed in the output directory!",
                outputFile != null && Files.isRegularFile(outputFile) && Files.isReadable(outputFile));
        Path inputFile = Paths.get(Constants.INPUT_DIRECTORY + "/" + fileName);
        assertTrue("Reference file does not exist or cannot be accessed!",
                inputFile != null && Files.isRegularFile(inputFile) && Files.isReadable(inputFile));
        Charset charset = Charset.forName("UTF-8");
        try (BufferedReader outputFileBufferedReader = Files.newBufferedReader(outputFile, charset); BufferedReader inputFileBufferedReader = Files.newBufferedReader(inputFile, charset)) {
            String outputFileCurrentLine = null, inputFileCurrentLine = null;
            int currentLine = 1;
            do {
                outputFileCurrentLine = outputFileBufferedReader.readLine();
                inputFileCurrentLine = inputFileBufferedReader.readLine();
                if (outputFileCurrentLine != null && inputFileCurrentLine != null) {
                    assertEquals("Files do not match at line " + currentLine + "\n" + "found: " + outputFileCurrentLine + "instead of: " + inputFileCurrentLine,
                            outputFileCurrentLine,
                            inputFileCurrentLine);
                }
                if ((outputFileCurrentLine == null) ^ (inputFileCurrentLine == null)) {
                    assertEquals("Files do not match at line " + currentLine + "\n" + "found: " + outputFileCurrentLine + "instead of: " + inputFileCurrentLine,
                            outputFileCurrentLine,
                            inputFileCurrentLine);
                }
                currentLine++;
            } while (outputFileCurrentLine != null && inputFileCurrentLine != null);
            System.out.println("Test passed!");
        } catch (IOException ioException) {
            fail("An exception has occurred!" + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }
}
