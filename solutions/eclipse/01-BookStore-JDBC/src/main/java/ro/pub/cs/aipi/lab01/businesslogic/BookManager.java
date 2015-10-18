package ro.pub.cs.aipi.lab01.businesslogic;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.JoinRowSet;

import com.sun.rowset.FilteredRowSetImpl;
import com.sun.rowset.JoinRowSetImpl;

import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperations;
import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperationsImplementation;
import ro.pub.cs.aipi.lab01.general.Constants;
import ro.pub.cs.aipi.lab01.helper.PriceRange;

public class BookManager extends EntityManager {

	public BookManager() {
		table = "book";
	}

	public ArrayList<ArrayList<String>> getBooksList1() {
		DatabaseOperations databaseOperations = null;
		ArrayList<String> attributes = new ArrayList<>();
		attributes.add("b.id AS id");
		attributes.add("b.title AS title");
		attributes.add(
				"(SELECT COALESCE(GROUP_CONCAT(DISTINCT CONCAT(w.first_name, \' \', w.last_name) SEPARATOR ', '), '* * *') FROM author a, writer w WHERE a.book_id = b.id AND w.id = a.writer_id) AS writers");
		attributes.add("cl.name AS collection");
		attributes.add("ph.name AS publishing_house");
		attributes.add("b.edition AS edition");
		attributes.add("b.printing_year AS printing_year");
		attributes.add("c.name AS country");
		try {
			databaseOperations = DatabaseOperationsImplementation.getInstance();
			return databaseOperations.getTableContent("book b, collection cl, publishing_house ph, country c",
					attributes, "cl.id = b.collection_id AND ph.id = cl.publishing_house_id AND c.id = ph.country_id",
					null, "b.id", null);
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

	public ArrayList<ArrayList<String>> getBooksList2() {
		JoinRowSet joinRowSet = null;
		CachedRowSet temporaryRowSet = null;
		try {
			ArrayList<String> bookAttributes = new ArrayList<>();
			bookAttributes.add("b.id");
			bookAttributes.add("b.title");
			bookAttributes.add(
					"(SELECT COALESCE(GROUP_CONCAT(DISTINCT CONCAT(w.first_name, \' \', w.last_name) SEPARATOR ', '), '* * *') FROM author a, writer w WHERE a.book_id = b.id AND w.id = a.writer_id) AS writers");
			bookAttributes.add("b.edition");
			bookAttributes.add("b.printing_year");
			bookAttributes.add("b.collection_id");
			int BOOK_ID_INDEX = 1;
			int BOOK_TITLE_INDEX = 2;
			int BOOK_WRITERS_INDEX = 3;
			int BOOK_EDITION_INDEX = 4;
			int BOOK_PRINTING_YEAR_INDEX = 5;
			// int BOOK_COLLECTION_ID_INDEX = 6;
			CachedRowSet books = getCollection("collection_id", bookAttributes, "b");

			ArrayList<String> collectionAttributes = new ArrayList<>();
			collectionAttributes.add("cl.id");
			collectionAttributes.add("cl.name");
			collectionAttributes.add("cl.publishing_house_id");
			int COLLECTION_NAME_INDEX = 7;
			// int COLLECTION_PUBLISHING_HOUSE_INDEX = 8;
			CollectionManager collectionManager = new CollectionManager();
			CachedRowSet collections = collectionManager.getCollection("id", collectionAttributes, "cl");

			ArrayList<String> publishingHouseAttributes = new ArrayList<>();
			publishingHouseAttributes.add("ph.id");
			publishingHouseAttributes.add("ph.name");
			publishingHouseAttributes.add("ph.country_id");
			int PUBLISHING_HOUSE_NAME_INDEX = 9;
			// int PUBLISHING_HOUSE_COUNTRY_ID_INDEX = 10;
			PublishingHouseManager publishingHouseManager = new PublishingHouseManager();
			CachedRowSet publishingHouses = publishingHouseManager.getCollection("id", publishingHouseAttributes, "ph");

			ArrayList<String> countryAttributes = new ArrayList<>();
			countryAttributes.add("c.id");
			countryAttributes.add("c.name");
			int COUNTRY_NAME_INDEX = 11;
			CountryManager countryManager = new CountryManager();
			CachedRowSet countries = countryManager.getCollection("id", countryAttributes, "c");

			joinRowSet = new JoinRowSetImpl();
			joinRowSet.addRowSet(books);
			joinRowSet.addRowSet(collections);

			temporaryRowSet = joinRowSet.createCopyNoConstraints();
			temporaryRowSet.setMatchColumn("publishing_house_id");
			joinRowSet.close();
			joinRowSet = new JoinRowSetImpl();
			joinRowSet.addRowSet(temporaryRowSet);
			joinRowSet.addRowSet(publishingHouses);

			temporaryRowSet = joinRowSet.createCopyNoConstraints();
			temporaryRowSet.setMatchColumn("country_id");
			joinRowSet.close();
			joinRowSet = new JoinRowSetImpl();
			joinRowSet.addRowSet(temporaryRowSet);
			joinRowSet.addRowSet(countries);
			joinRowSet.afterLast();

			ArrayList<ArrayList<String>> result = new ArrayList<>();
			while (joinRowSet.previous()) {
				ArrayList<String> currentRecord = new ArrayList<>();
				currentRecord.add(joinRowSet.getString(BOOK_ID_INDEX));
				currentRecord.add(joinRowSet.getString(BOOK_TITLE_INDEX));
				currentRecord.add(joinRowSet.getString(BOOK_WRITERS_INDEX));
				currentRecord.add(joinRowSet.getString(COLLECTION_NAME_INDEX));
				currentRecord.add(joinRowSet.getString(PUBLISHING_HOUSE_NAME_INDEX));
				currentRecord.add(joinRowSet.getString(BOOK_EDITION_INDEX));
				currentRecord.add(joinRowSet.getString(BOOK_PRINTING_YEAR_INDEX));
				currentRecord.add(joinRowSet.getString(COUNTRY_NAME_INDEX));
				result.add(currentRecord);
			}
			joinRowSet.close();
			return result;
		} catch (SQLException sqlException) {
			System.out
					.println("An exception has occurred while handling database records: " + sqlException.getMessage());
			if (Constants.DEBUG) {
				sqlException.printStackTrace();
			}
		}
		return null;
	}

	public ArrayList<ArrayList<String>> getBooksListFilteredByPriceRange(float lowValue, float highValue) {
		JoinRowSet joinRowSet = null;
		CachedRowSet temporaryRowSet = null;
		FilteredRowSet filteredRowSet = null;
		try {
			ArrayList<String> bookAttributes = new ArrayList<>();
			bookAttributes.add("b.id");
			bookAttributes.add("b.title");
			bookAttributes.add(
					"(SELECT COALESCE(GROUP_CONCAT(DISTINCT CONCAT(w.first_name, \' \', w.last_name) SEPARATOR ', '), '* * *') FROM author a, writer w WHERE a.book_id = b.id AND w.id = a.writer_id) AS writers");
			bookAttributes.add("b.edition");
			bookAttributes.add("b.printing_year");
			bookAttributes.add("b.collection_id");
			int BOOK_ID_INDEX = 1;
			int BOOK_TITLE_INDEX = 2;
			int BOOK_WRITERS_INDEX = 3;
			int BOOK_EDITION_INDEX = 4;
			int BOOK_PRINTING_YEAR_INDEX = 5;
			// int BOOK_COLLECTION_ID_INDEX = 6;
			CachedRowSet books = getCollection("id", bookAttributes, "b");

			ArrayList<String> bookPresentationAttributes = new ArrayList<>();
			bookPresentationAttributes.add("bp.book_id");
			bookPresentationAttributes.add("bp.isbn");
			bookPresentationAttributes.add("bp.price");
			bookPresentationAttributes.add("bp.stockpile");
			int BOOK_PRESENTATION_ISBN_INDEX = 7;
			int BOOK_PRESENTATION_PRICE_INDEX = 8;
			int BOOK_PRESENTATION_STOCKPILE_INDEX = 9;
			BookPresentationManager bookPresentationManager = new BookPresentationManager();
			CachedRowSet bookPresentations = bookPresentationManager.getCollection("book_id",
					bookPresentationAttributes, "bp");

			ArrayList<String> collectionAttributes = new ArrayList<>();
			collectionAttributes.add("cl.id");
			collectionAttributes.add("cl.name");
			collectionAttributes.add("cl.publishing_house_id");
			int COLLECTION_NAME_INDEX = 10;
			// int COLLECTION_PUBLISHING_HOUSE_INDEX = 11;
			CollectionManager collectionManager = new CollectionManager();
			CachedRowSet collections = collectionManager.getCollection("id", collectionAttributes, "cl");

			ArrayList<String> publishingHouseAttributes = new ArrayList<>();
			publishingHouseAttributes.add("ph.id");
			publishingHouseAttributes.add("ph.name");
			publishingHouseAttributes.add("ph.country_id");
			int PUBLISHING_HOUSE_NAME_INDEX = 12;
			// int PUBLISHING_HOUSE_COUNTRY_ID_INDEX = 13;
			PublishingHouseManager publishingHouseManager = new PublishingHouseManager();
			CachedRowSet publishingHouses = publishingHouseManager.getCollection("id", publishingHouseAttributes, "ph");

			ArrayList<String> countryAttributes = new ArrayList<>();
			countryAttributes.add("c.id");
			countryAttributes.add("c.name");
			int COUNTRY_NAME_INDEX = 14;
			CountryManager countryManager = new CountryManager();
			CachedRowSet countries = countryManager.getCollection("id", countryAttributes, "c");

			joinRowSet = new JoinRowSetImpl();
			joinRowSet.addRowSet(books);
			joinRowSet.addRowSet(bookPresentations);

			temporaryRowSet = joinRowSet.createCopyNoConstraints();
			temporaryRowSet.setMatchColumn("collection_id");
			joinRowSet.close();
			joinRowSet = new JoinRowSetImpl();
			joinRowSet.addRowSet(temporaryRowSet);
			joinRowSet.addRowSet(collections);

			temporaryRowSet = joinRowSet.createCopyNoConstraints();
			temporaryRowSet.setMatchColumn("publishing_house_id");
			joinRowSet.close();
			joinRowSet = new JoinRowSetImpl();
			joinRowSet.addRowSet(temporaryRowSet);
			joinRowSet.addRowSet(publishingHouses);

			temporaryRowSet = joinRowSet.createCopyNoConstraints();
			temporaryRowSet.setMatchColumn("country_id");
			joinRowSet.close();
			joinRowSet = new JoinRowSetImpl();
			joinRowSet.addRowSet(temporaryRowSet);
			joinRowSet.addRowSet(countries);

			filteredRowSet = new FilteredRowSetImpl();
			filteredRowSet.populate(joinRowSet);
			PriceRange priceRange = new PriceRange(lowValue, highValue, "price");
			filteredRowSet.setFilter(priceRange);

			ArrayList<ArrayList<String>> result = new ArrayList<>();
			while (filteredRowSet.next()) {
				ArrayList<String> currentRecord = new ArrayList<>();
				currentRecord.add(filteredRowSet.getString(BOOK_ID_INDEX));
				currentRecord.add(filteredRowSet.getString(BOOK_TITLE_INDEX));
				currentRecord.add(filteredRowSet.getString(BOOK_WRITERS_INDEX));
				currentRecord.add(filteredRowSet.getString(BOOK_PRESENTATION_ISBN_INDEX));
				currentRecord.add(filteredRowSet.getString(BOOK_PRESENTATION_PRICE_INDEX));
				currentRecord.add(filteredRowSet.getString(BOOK_PRESENTATION_STOCKPILE_INDEX));
				currentRecord.add(filteredRowSet.getString(COLLECTION_NAME_INDEX));
				currentRecord.add(filteredRowSet.getString(PUBLISHING_HOUSE_NAME_INDEX));
				currentRecord.add(filteredRowSet.getString(BOOK_EDITION_INDEX));
				currentRecord.add(filteredRowSet.getString(BOOK_PRINTING_YEAR_INDEX));
				currentRecord.add(filteredRowSet.getString(COUNTRY_NAME_INDEX));
				result.add(currentRecord);
			}
			joinRowSet.close();
			filteredRowSet.close();
			return result;
		} catch (SQLException sqlException) {
			System.out
					.println("An exception has occurred while handling database records: " + sqlException.getMessage());
			if (Constants.DEBUG) {
				sqlException.printStackTrace();
			}
		}
		return null;
	}

	public ArrayList<ArrayList<String>> getBooksListWithStockpile(int stockpile) {
		DatabaseOperations databaseOperations = null;
		ArrayList<String> attributes = new ArrayList<>();
		attributes.add("b.id AS id");
		attributes.add("b.title AS title");
		attributes.add("b.subtitle AS subtitle");
		attributes.add(
				"(SELECT GROUP_CONCAT(DISTINCT CONCAT(w.first_name, ' ', w.last_name) SEPARATOR ', ') FROM author a, writer w WHERE a.book_id = b.id AND w.id = a.writer_id) AS writers");
		attributes.add(
				"(SELECT GROUP_CONCAT(DISTINCT c.name SEPARATOR ', ') FROM category c, category_content cc WHERE cc.book_id = b.id AND c.id = cc.category_id) AS categories");
		attributes.add(
				"(SELECT GROUP_CONCAT(DISTINCT f.value SEPARATOR ', ') FROM book_presentation bp, format f WHERE bp.book_id = b.id AND f.id = bp.format_id) AS formats");
		attributes.add(
				"(SELECT GROUP_CONCAT(DISTINCT l.name SEPARATOR ', ') FROM book_presentation bp, language l WHERE bp.book_id = b.id AND l.id = bp.language_id) AS languages");
		try {
			databaseOperations = DatabaseOperationsImplementation.getInstance();
			return databaseOperations.getTableContent("book b", attributes,
					"(SELECT SUM(bp.stockpile) FROM book_presentation bp WHERE bp.book_id = b.id) > " + stockpile, null,
					"b.id", null);
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
