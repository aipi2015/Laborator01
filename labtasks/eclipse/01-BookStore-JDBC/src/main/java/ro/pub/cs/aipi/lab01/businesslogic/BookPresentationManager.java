package ro.pub.cs.aipi.lab01.businesslogic;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

import ro.pub.cs.aipi.lab01.dataaccess.DatabaseException;
import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperations;
import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperationsImplementation;
import ro.pub.cs.aipi.lab01.general.Constants;
import ro.pub.cs.aipi.lab01.general.Utilities;

public class BookPresentationManager extends EntityManager {

	public BookPresentationManager() {
		table = "book_presentation";
	}

	public ArrayList<ArrayList<String>> updateBookPresentationPriceForBooksWithMultipleFormats(int numberOfFormats,
			double amount) {
		DatabaseOperations databaseOperations = null;
		try {
			databaseOperations = DatabaseOperationsImplementation.getInstance();
			databaseOperations.executeQuery(
					"CREATE TEMPORARY TABLE IF NOT EXISTS book_tmp ( " + "book_id INT(10) UNSIGNED NOT NULL " + ");");
			ArrayList<String> bookAttributes = new ArrayList<>();
			bookAttributes.add("DISTINCT b.id");
			ArrayList<ArrayList<String>> bookIdsResults = databaseOperations.getTableContent("book b", bookAttributes,
					"(SELECT COUNT(*) FROM book_presentation bp WHERE bp.book_id = b.id) > " + numberOfFormats, null,
					null, null);
			for (ArrayList<String> bookIds : bookIdsResults) {
				ArrayList<String> bookTmpAttributes = new ArrayList<>();
				bookTmpAttributes.add("book_id");
				databaseOperations.insertValuesIntoTable("book_tmp", bookTmpAttributes, bookIds, false);
			}

			ArrayList<String> attributes = new ArrayList<>();
			ArrayList<String> values = new ArrayList<>();
			attributes.add("price");
			values.add("price * " + amount);
			databaseOperations.updateRecordsIntoTable("book_presentation", attributes, values,
					"book_id IN (SELECT book_id FROM book_tmp)");

			ArrayList<String> bookPresentationAttributes = new ArrayList<>();
			bookPresentationAttributes.add("bp.id");
			bookPresentationAttributes.add("bp.book_id");
			bookPresentationAttributes.add("bp.format_id");
			bookPresentationAttributes.add("bp.price");
			ArrayList<ArrayList<String>> bookPresentationResults = databaseOperations.getTableContent(
					"book_presentation bp", bookPresentationAttributes, "bp.book_id IN (SELECT book_id FROM book_tmp)",
					null, null, null);
			databaseOperations.executeQuery("DROP TABLE book_tmp");
			return bookPresentationResults;
		} catch (DatabaseException dataBaseException) {
			System.out.println("Error updating records into table: " + dataBaseException.getMessage());
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
		return null;
	}

	public ArrayList<ArrayList<String>> makeSupplyOrderBasedOnStockpile(int stockpile) {
		DatabaseOperations databaseOperations = null;
		try {
			databaseOperations = DatabaseOperationsImplementation.getInstance();
			databaseOperations.startTransaction();
			ArrayList<String> attributes = new ArrayList<>();
			attributes.add("ph.id AS publishing_house_id");
			attributes.add("bp.id AS book_presentation_id");
			attributes.add("bp.stockpile AS stockpile");
			int PUBLISHING_HOUSE_ID_INDEX = 0;
			int BOOK_PRESENTATION_ID_INDEX = 1;
			int STOCKPILE_INDEX = 2;
			ArrayList<ArrayList<String>> result = databaseOperations.getTableContent(
					"book_presentation bp, book b, collection c, publishing_house ph", attributes,
					"bp.stockpile < " + stockpile
							+ " AND b.id = bp.book_id and c.id = b.collection_id and ph.id = c.publishing_house_id",
					null, "ph.id", null);
			if (result != null) {
				Long currentPublishingHouseId = new Long(-1);
				Long currentSupplyOrderHeaderId = new Long(-1);
				for (int currentRow = 0; currentRow < result.size(); currentRow++) {
					Long publishingHouseId = new Long(result.get(currentRow).get(PUBLISHING_HOUSE_ID_INDEX));
					if (!currentPublishingHouseId.equals(publishingHouseId)) {
						currentPublishingHouseId = publishingHouseId;
						ArrayList<String> values = new ArrayList<>();
						values.add(Utilities.generateIdentificationNumber(3, 6));
						values.add(new Date(System.currentTimeMillis()).toString());
						values.add("sent");
						values.add(currentPublishingHouseId.toString());
						SupplyOrderHeaderManager supplyOrderHeaderManager = new SupplyOrderHeaderManager();
						if (supplyOrderHeaderManager.create(values) != 1) {
							throw new DatabaseException(
									"The record could not be inserted in the supply_order_header table");
						} else {
							currentSupplyOrderHeaderId = Long
									.valueOf(databaseOperations.getTablePrimaryKeyMaximumValue("supply_order_header"));
						}
					}
					ArrayList<String> values = new ArrayList<>();
					values.add(currentSupplyOrderHeaderId.toString());
					values.add(result.get(currentRow).get(BOOK_PRESENTATION_ID_INDEX));
					int quantity = 200 - Integer.parseInt(result.get(currentRow).get(STOCKPILE_INDEX));
					values.add(String.valueOf(quantity));
					SupplyOrderLineManager supplyOrderLineManager = new SupplyOrderLineManager();
					if (supplyOrderLineManager.create(values) != 1) {
						throw new DatabaseException("The record could not be inserted in the supply_order_line table");
					}
				}
				result.clear();
				attributes.clear();

				attributes.add("soh.publishing_house_id");
				attributes.add("sol.book_presentation_id");
				attributes.add("sol.quantity");
				return databaseOperations.getTableContent("supply_order_header soh, supply_order_line sol", attributes,
						"sol.supply_order_header_id = soh.id AND soh.issue_date = \'"
								+ new Date(System.currentTimeMillis()).toString() + "\'",
						null, "soh.publishing_house_id, sol.book_presentation_id", null);
			}
		} catch (DatabaseException | SQLException exception) {
			System.out.println("An exception has occurred: " + exception.getMessage());
			if (Constants.DEBUG) {
				exception.printStackTrace();
			}
			try {
				databaseOperations.endTransactionWithRollback();
			} catch (SQLException sqlException) {
				System.out.println(
						"An exception has occurred while rolling back the connection: " + sqlException.getMessage());
				if (Constants.DEBUG) {
					sqlException.printStackTrace();
				}
			}
		} finally {
			try {
				databaseOperations.endTransactionWithCommit();
			} catch (SQLException sqlException) {
				System.out.println(
						"An exception has occurred while rolling back the connection: " + sqlException.getMessage());
				if (Constants.DEBUG) {
					sqlException.printStackTrace();
				}
			}
			databaseOperations.releaseResources();
		}
		return null;
	}

}
