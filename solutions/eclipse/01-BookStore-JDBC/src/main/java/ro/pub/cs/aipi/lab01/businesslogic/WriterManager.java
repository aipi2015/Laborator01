package ro.pub.cs.aipi.lab01.businesslogic;

import java.sql.SQLException;
import java.util.ArrayList;

import ro.pub.cs.aipi.lab01.dataaccess.DatabaseException;
import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperations;
import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperationsImplementation;
import ro.pub.cs.aipi.lab01.general.Constants;

public class WriterManager extends EntityManager {

	public WriterManager() {
		table = "writer";
	}

	public int deleteWritersWithoutBooks() {
		DatabaseOperations databaseOperations = null;
		try {
			databaseOperations = DatabaseOperationsImplementation.getInstance();
			return databaseOperations.deleteRecordsFromTable("writer", null, null,
					"(SELECT COUNT(*) FROM author a where a.writer_id = writer.id) = 0");
		} catch (DatabaseException dataBaseException) {
			System.out.println("Error deleting record from table: " + dataBaseException.getMessage());
			if (Constants.DEBUG) {
				dataBaseException.printStackTrace();
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
		return -1;
	}

	public ArrayList<ArrayList<String>> getWritersBibliography(int numberOfBooksTotal, int numberOfBooksAlone,
			int numberOfBooksCollaboration) {
		DatabaseOperations databaseOperations = null;
		ArrayList<String> attributes = new ArrayList<>();
		attributes.add("CONCAT(w.first_name, ' ', w.last_name) AS writer");
		attributes.add(
				"(SELECT GROUP_CONCAT(b.title SEPARATOR '; ') FROM book b, author a WHERE b.id = a.book_id AND a.writer_id = w.id) AS bibliography");
		attributes.add(
				"(SELECT COUNT(b.title) FROM book b, author a WHERE b.id = a.book_id AND a.writer_id = w.id) AS total");
		attributes.add(
				"(SELECT COUNT(b.title) FROM book b, author a WHERE b.id = a.book_id AND a.writer_id = w.id AND (SELECT COUNT(w1.id) FROM writer w1, author a1 WHERE a1.book_id = b.id AND w1.id = a1.writer_id) = 1) AS alone");
		attributes.add(
				"(SELECT COUNT(b.title) FROM book b, author a WHERE b.id = a.book_id AND a.writer_id = w.id AND (SELECT COUNT(w1.id) FROM writer w1, author a1 WHERE a1.book_id = b.id AND w1.id = a1.writer_id) <> 1) AS collaboration");
		try {
			databaseOperations = DatabaseOperationsImplementation.getInstance();
			StringBuilder whereByClause = new StringBuilder();
			if (numberOfBooksTotal > 0) {
				whereByClause.append("total > ");
				whereByClause.append(numberOfBooksTotal);
			}
			if (numberOfBooksAlone > 0) {
				if (whereByClause.length() > 0) {
					whereByClause.append(" AND ");
				}
				whereByClause.append("alone > ");
				whereByClause.append(numberOfBooksAlone);
			}
			if (numberOfBooksCollaboration > 0) {
				if (whereByClause.length() > 0) {
					whereByClause.append(" AND ");
				}
				whereByClause.append("collaboration > ");
				whereByClause.append(numberOfBooksCollaboration);
			}
			return databaseOperations.getTableContent("writer w", attributes, null, whereByClause.toString(),
					"CONCAT(w.first_name, \' \', w.last_name) ASC", null);
		} catch (SQLException sqlException) {
			System.out
					.println("An exception has occurred while handling database records: " + sqlException.getMessage());
			if (Constants.DEBUG) {
				sqlException.printStackTrace();
			}
		} finally {
			databaseOperations.releaseResources();
		}
		return null;
	}

}
