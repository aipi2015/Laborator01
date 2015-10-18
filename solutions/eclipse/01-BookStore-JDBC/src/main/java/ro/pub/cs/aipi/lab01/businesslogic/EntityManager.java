package ro.pub.cs.aipi.lab01.businesslogic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.sql.rowset.CachedRowSet;

import com.sun.rowset.CachedRowSetImpl;

import ro.pub.cs.aipi.lab01.dataaccess.DatabaseException;
import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperations;
import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperationsImplementation;
import ro.pub.cs.aipi.lab01.general.Constants;

public class EntityManager {

	protected String table;

	public EntityManager() {
		this.table = null;
	}

	public EntityManager(String table) {
		this.table = table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getTable() {
		return table;
	}

	public int create(ArrayList<String> values) {
		DatabaseOperations databaseOperations = null;
		try {
			databaseOperations = DatabaseOperationsImplementation.getInstance();
			int result = databaseOperations.insertValuesIntoTable(table, null, values, true);
			if (result != 1) {
				if (Constants.DEBUG) {
					System.out.println("Insert operation failed!");
				}
				return -1;
			}
			return result;
		} catch (DatabaseException | SQLException exception) {
			System.out.println("An exception has occurred: " + exception.getMessage());
			if (Constants.DEBUG) {
				exception.printStackTrace();
			}
		}
		return -1;
	}

	public ArrayList<String> read(long id) {
		DatabaseOperations databaseOperations = null;
		try {
			databaseOperations = DatabaseOperationsImplementation.getInstance();
			ArrayList<ArrayList<String>> result = databaseOperations.getTableContent(table, null,
					databaseOperations.getTablePrimaryKey(table) + "=" + id, null, null, null);
			if (result.size() != 1) {
				if (Constants.DEBUG) {
					System.out.println("The query returned more than one result!");
				}
				return null;
			}
			return result.get(0);
		} catch (SQLException sqlException) {
			System.out.println("An exception has occurred: " + sqlException.getMessage());
			if (Constants.DEBUG) {
				sqlException.printStackTrace();
			}
		}
		return null;
	}

	public int update(ArrayList<String> values, long id) {
		DatabaseOperations databaseOperations = null;
		try {
			databaseOperations = DatabaseOperationsImplementation.getInstance();
			int result = databaseOperations.updateRecordsIntoTable(table, null, values,
					databaseOperations.getTablePrimaryKey(table) + "=" + id);
			if (result != 1) {
				if (Constants.DEBUG) {
					System.out.println("Update operation failed!");
				}
			}
			return result;
		} catch (DatabaseException | SQLException exception) {
			System.out.println("An exception has occurred: " + exception.getMessage());
			if (Constants.DEBUG) {
				exception.printStackTrace();
			}
		}
		return -1;
	}

	public int delete(long id) {
		DatabaseOperations databaseOperations = null;
		try {
			databaseOperations = DatabaseOperationsImplementation.getInstance();
			int result = databaseOperations.deleteRecordsFromTable(table, null, null,
					databaseOperations.getTablePrimaryKey(table) + "=" + id);
			if (result != 1) {
				if (Constants.DEBUG) {
					System.out.println("Delete operation failed!");
				}
			}
			return result;
		} catch (DatabaseException | SQLException exception) {
			System.out.println("An exception has occurred: " + exception.getMessage());
			if (Constants.DEBUG) {
				exception.printStackTrace();
			}
		}
		return -1;
	}

	public ArrayList<ArrayList<String>> getCollection() {
		return getCollection((ArrayList<String>) null);
	}

	public ArrayList<ArrayList<String>> getCollection(ArrayList<String> attributes) {
		return getCollection(attributes, null);
	}

	public ArrayList<ArrayList<String>> getCollection(ArrayList<String> attributes, String tableAlias) {
		DatabaseOperations databaseOperations = null;
		try {
			databaseOperations = DatabaseOperationsImplementation.getInstance();
			return databaseOperations.getTableContent(table + (tableAlias != null ? " " + tableAlias : ""), attributes,
					null, null, null, null);
		} catch (SQLException sqlException) {
			System.out.println("An exception has occurred: " + sqlException.getMessage());
			if (Constants.DEBUG) {
				sqlException.printStackTrace();
			}
		} finally {
			databaseOperations.releaseResources();
		}
		return null;
	}

	public CachedRowSet getCollection(String matchColumn) {
		return getCollection(matchColumn, null);
	}

	public CachedRowSet getCollection(String matchColumn, ArrayList<String> attributes) {
		return getCollection(matchColumn, attributes, null);
	}

	public CachedRowSet getCollection(String matchColumn, ArrayList<String> attributes, String tableAlias) {
		try {
			CachedRowSet result = new CachedRowSetImpl();
			result.setUrl(Constants.DATABASE_CONNECTION + Constants.DATABASE_NAME);
			result.setUsername(Constants.DATABASE_USERNAME);
			result.setPassword(Constants.DATABASE_PASSWORD);
			StringBuilder attributesList = new StringBuilder();
			Iterator<String> attributesIterator = attributes.iterator();
			while (attributesIterator.hasNext()) {
				attributesList.append(attributesIterator.next() + ", ");
			}
			attributesList.setLength(attributesList.length() - 2);
			result.setCommand("SELECT " + attributesList.toString() + " FROM " + table
					+ (tableAlias != null ? " " + tableAlias : ""));
			result.setMatchColumn(matchColumn);
			result.execute();
			return result;
		} catch (SQLException sqlException) {
			System.out.println("An exception has occurred: " + sqlException.getMessage());
			if (Constants.DEBUG) {
				sqlException.printStackTrace();
			}
		}
		return null;
	}

	public int getTableSize() {
		DatabaseOperations databaseOperations = null;
		try {
			databaseOperations = DatabaseOperationsImplementation.getInstance();
			return databaseOperations.getTableNumberOfRows(table);
		} catch (SQLException sqlException) {
			System.out.println("An exception has occurred: " + sqlException.getMessage());
			if (Constants.DEBUG) {
				sqlException.printStackTrace();
			}
		} finally {
			databaseOperations.releaseResources();
		}
		return -1;
	}
}
