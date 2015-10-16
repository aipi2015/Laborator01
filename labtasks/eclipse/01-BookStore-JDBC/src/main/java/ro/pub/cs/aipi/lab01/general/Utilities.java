package ro.pub.cs.aipi.lab01.general;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Random;

public class Utilities {
	
	final private static int NUMBER_OF_CHARACTERS = 25;
    final private static Random random = new Random(Constants.SEED);
	
    private static char generateCharacter() {
        return (char)('A' + random.nextInt(NUMBER_OF_CHARACTERS));
    }

    private static String generateString(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(generateCharacter());
        }
        return result.toString();
    }

    public static String generateIdentificationNumber(int alphaLength, int numericLength) {
        int base = (int)Math.pow(10, numericLength - 1);
        return generateString(alphaLength) + (random.nextInt(9 * base) + base);
    }
    
    public static void logResult(String fileName, ArrayList<ArrayList<String>> result) {
    	Charset charset = Charset.forName("UTF-8");
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(Constants.OUTPUT_DIRECTORY + "/" + fileName), charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
        	if (result != null) {
                for (int currentRow = 0; currentRow < result.size(); currentRow++) {
                    StringBuilder record = new StringBuilder();
                    for (int currentColumn = 0; currentColumn < result.get(currentRow).size(); currentColumn++) {
                    	record.append(result.get(currentRow).get(currentColumn) + "\t");
                    }
                    record.setLength(record.length() - 1);
                    record.append("\n");
                    bufferedWriter.write(record.toString(), 0, record.length());
                }
            }
        } catch (IOException ioException) {
        	System.out.println ("Error opening / writing to file: " + ioException.getMessage());
        	if (Constants.DEBUG) {
        		ioException.printStackTrace();
        	}
        }
    }
    
}
