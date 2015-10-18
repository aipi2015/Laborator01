package ro.pub.cs.aipi.lab01.dataaccess;

import java.util.ArrayList;

import ro.pub.cs.aipi.lab01.helper.Referrence;

import java.sql.SQLException;

public interface DatabaseOperations {

	public void releaseResources();

	public void setDatabaseName(String currentDatabase);

	public ArrayList<String> getTableNames() throws SQLException;

	public int getTableNumberOfRows(String tableName) throws SQLException;

	public int getTableNumberOfColumns(String tableName) throws SQLException;

	public String getTablePrimaryKey(String tableName) throws SQLException;

	public ArrayList<String> getTablePrimaryKeys(String tableName) throws SQLException;

	public int getTablePrimaryKeyMaximumValue(String tableName) throws SQLException;

	public ArrayList<String> getTableColumns(String tableName) throws SQLException;

	public ArrayList<ArrayList<String>> getTableContent(String tableName, ArrayList<String> columnNames,
			String whereClause, String havingClause, String orderByClause, String groupByClause) throws SQLException;

	public int insertValuesIntoTable(String tableName, ArrayList<String> columnNames, ArrayList<String> values,
			boolean skipPrimaryKey) throws SQLException, DatabaseException;

	public int updateRecordsIntoTable(String tableName, ArrayList<String> columnNames, ArrayList<String> values,
			String whereClause) throws SQLException, DatabaseException;

	public int deleteRecordsFromTable(String tableName, ArrayList<String> columnNames, ArrayList<String> values,
			String whereClause) throws SQLException, DatabaseException;

	public boolean executeQuery(String query) throws SQLException;

	public ArrayList<String> executeStoredRoutine(String storedRoutineName, ArrayList<String> parameterTypes,
			ArrayList<String> inputParameterValues, ArrayList<Integer> outputParameterDataTypes) throws SQLException;

	public ArrayList<Referrence> getReferrences(String tableName) throws SQLException;

	public void runScript(String fileName) throws SQLException;

	public void startTransaction() throws SQLException;

	public void endTransactionWithCommit() throws SQLException;

	public void endTransactionWithRollback() throws SQLException;

}
