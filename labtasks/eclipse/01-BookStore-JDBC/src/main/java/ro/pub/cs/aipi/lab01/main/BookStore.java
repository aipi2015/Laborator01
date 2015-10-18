package ro.pub.cs.aipi.lab01.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.ArrayList;

import ro.pub.cs.aipi.lab01.businesslogic.BookManager;
import ro.pub.cs.aipi.lab01.businesslogic.BookPresentationManager;
import ro.pub.cs.aipi.lab01.businesslogic.EntityManager;
import ro.pub.cs.aipi.lab01.businesslogic.UserManager;
import ro.pub.cs.aipi.lab01.businesslogic.WriterManager;
import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperations;
import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperationsImplementation;
import ro.pub.cs.aipi.lab01.general.Constants;
import ro.pub.cs.aipi.lab01.general.Utilities;
import ro.pub.cs.aipi.lab01.helper.Referrence;

public class BookStore {

	public int exercise01(String table) {
		EntityManager entityManager = new EntityManager(table);
		return entityManager.getTableSize();
	}

	public void exercise02() {
		BookManager bookManager = new BookManager();
		Utilities.logResult("02books.txt", bookManager.getBooksList1());
	}

	public int exercise03(String table) {
		UserManager userManager = new UserManager();
		Path inputFile = Paths.get(Constants.INPUT_DIRECTORY + "/03user.txt");
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader inputFileBufferedReader = Files.newBufferedReader(inputFile, charset)) {
			ArrayList<String> values = new ArrayList<>();
			String inputFileCurrentLine = null;
			do {
				inputFileCurrentLine = inputFileBufferedReader.readLine();
				if (inputFileCurrentLine != null) {
					values.add(inputFileCurrentLine);
				}
			} while (inputFileCurrentLine != null);
			return userManager.create(values);
		} catch (IOException ioException) {
			System.out.println("An exception has occurred while handling file: " + ioException.getMessage());
			if (Constants.DEBUG) {
				ioException.printStackTrace();
			}
		}
		return -1;
	}

	public int exercise04() {
		BookPresentationManager bookPresentationManager = new BookPresentationManager();
		ArrayList<ArrayList<String>> result = bookPresentationManager
				.updateBookPresentationPriceForBooksWithMultipleFormats(3, 1.15);
		Utilities.logResult("04book_presentation_prices.txt", result);
		return (result != null) ? result.size() : -1;
	}

	public int exercise05() {
		WriterManager writerManager = new WriterManager();
		return writerManager.deleteWritersWithoutBooks();
	}

	public void exercise06() {
		WriterManager writerManager = new WriterManager();
		Utilities.logResult("06writers_bibliography.txt", writerManager.getWritersBibliography(4, 2, 0));
	}

	public void exercise07() {
		UserManager userManager = new UserManager();
		Utilities.logResult("07user_total_invoice_value.txt", userManager.getUserListWithTotalInvoiceAmount());
	}

	public void exercise08() {
		DatabaseOperations databaseOperations = DatabaseOperationsImplementation.getInstance();
		try {
			Charset charset = Charset.forName("UTF-8");
			BufferedWriter bufferedWriter = Files.newBufferedWriter(
					Paths.get(Constants.OUTPUT_DIRECTORY + "/08referrences.txt"), charset, StandardOpenOption.CREATE,
					StandardOpenOption.APPEND);
			for (String table : databaseOperations.getTableNames()) {
				ArrayList<Referrence> referrences = databaseOperations.getReferrences(table);
				if (referrences != null) {
					StringBuilder result = new StringBuilder();
					for (Referrence referrence : referrences) {
						result.append(referrence.toString() + "\n");
					}
					bufferedWriter.write(result.toString(), 0, result.length());
				}
			}
			bufferedWriter.close();
		} catch (IOException ioException) {
			System.out.println("Error opening / writing to file: " + ioException.getMessage());
			if (Constants.DEBUG) {
				ioException.printStackTrace();
			}
		} catch (SQLException sqlException) {
			System.out
					.println("An exception has occurred while handling database records: " + sqlException.getMessage());
			if (Constants.DEBUG) {
				sqlException.printStackTrace();
			}
		} finally {
			databaseOperations.releaseResources();
		}
	}

	public void exercise09() {
		BookManager bookManager = new BookManager();
		Utilities.logResult("09books.txt", bookManager.getBooksList2());
	}

	public void exercise10() {
		BookManager bookManager = new BookManager();
		Utilities.logResult("10books.txt", bookManager.getBooksListFilteredByPriceRange(100, 500));
	}

	public void exercise11() {
		BookPresentationManager bookPresentationManager = new BookPresentationManager();
		Utilities.logResult("11supply_orders.txt", bookPresentationManager.makeSupplyOrderBasedOnStockpile(200));
	}

	public void exercise12() {
		BookManager bookManager = new BookManager();
		Utilities.logResult("12books.txt", bookManager.getBooksListWithStockpile(1000));
	}
}
