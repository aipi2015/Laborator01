package ro.pub.cs.aipi.lab01.dataaccess;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ro.pub.cs.aipi.lab01.general.Constants;
import ro.pub.cs.aipi.lab01.helper.Referrence;

public class DatabaseOperationsImplementation implements DatabaseOperations {

    private Connection connection;
    private DatabaseMetaData databaseMetaData;
    private String databaseName;

    private static DatabaseOperationsImplementation instance;

    private DatabaseOperationsImplementation() {
    }

    public void releaseResources() {
        try {
            closeConnection();
        } catch (SQLException sqlException) {
            System.out.println("An exception has occurred: " + sqlException.getMessage());
            if (Constants.DEBUG) {
                sqlException.printStackTrace();
            }
        }
    }

    public static DatabaseOperationsImplementation getInstance() {
        if (instance == null) {
            instance = new DatabaseOperationsImplementation();
        }
        return instance;
    }

    private void openConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    Constants.DATABASE_CONNECTION + (databaseName != null ? databaseName : ""),
                    Constants.DATABASE_USERNAME, Constants.DATABASE_PASSWORD);
            databaseMetaData = connection.getMetaData();
        }
    }

    private void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private Statement createStatement() throws SQLException {
        openConnection();
        return connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
    }

    private void destroyStatement(Statement statement) throws SQLException {
        if (statement != null && !statement.isClosed()) {
            statement.close();
        }
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public ArrayList<String> getTableNames() throws SQLException {
        openConnection();
        ArrayList<String> tableNames = new ArrayList<>();
        ResultSet result = databaseMetaData.getTables(Constants.DATABASE_NAME, null, null, null);
        while (result.next()) {
            tableNames.add(result.getString("TABLE_NAME"));
        }
        return tableNames;
    }

    public int getTableNumberOfRows(String tableName) throws SQLException {
        int numberOfRows = -1;
        // TODO: exercise 1
        return numberOfRows;
    }

    public int getTableNumberOfColumns(String tableName) throws SQLException {
        openConnection();
        int numberOfColumns = 0;
        ResultSet result = databaseMetaData.getColumns(Constants.DATABASE_NAME, null, tableName, null);
        while (result.next()) {
            numberOfColumns++;
        }
        return numberOfColumns;
    }

    public String getTablePrimaryKey(String tableName) throws SQLException {
        ArrayList<String> result = getTablePrimaryKeys(tableName);
        if (result.size() != 1) {
            return null;
        }
        return result.get(0);
    }

    public ArrayList<String> getTablePrimaryKeys(String tableName) throws SQLException {
        openConnection();
        ArrayList<String> primaryKeys = new ArrayList<>();
        ResultSet result = databaseMetaData.getPrimaryKeys(Constants.DATABASE_NAME, null, tableName);
        while (result.next()) {
            primaryKeys.add(result.getString("COLUMN_NAME"));
        }
        return primaryKeys;
    }

    public int getTablePrimaryKeyMaximumValue(String tableName) throws SQLException {
        openConnection();
        String primaryKey = getTablePrimaryKey(tableName);
        Statement statement = createStatement();
        int primaryKeyMaximumValue = -1;
        try {
            String query = "SELECT MAX(" + primaryKey + ") FROM " + tableName;
            ResultSet result = statement.executeQuery(query);
            result.next();
            primaryKeyMaximumValue = result.getInt(1);
        } catch (SQLException sqlException) {
            System.out.println("An exception has occurred: " + sqlException.getMessage());
            if (Constants.DEBUG) {
                sqlException.printStackTrace();
            }
        } finally {
            destroyStatement(statement);
        }
        return primaryKeyMaximumValue;
    }

    public ArrayList<String> getTableColumns(String tableName) throws SQLException {
        openConnection();
        ArrayList<String> tableColumns = new ArrayList<>();
        ResultSet result = databaseMetaData.getColumns(Constants.DATABASE_NAME, null, tableName, null);
        while (result.next()) {
            tableColumns.add(result.getString("COLUMN_NAME"));
        }
        return tableColumns;
    }

    public ArrayList<ArrayList<String>> getTableContent(String tableName, ArrayList<String> attributes,
            String whereClause, String havingClause, String orderByClause, String groupByClause) throws SQLException {
        openConnection();
        Statement statement = createStatement();
        ArrayList<ArrayList<String>> databaseContent = null;
        try {
            StringBuilder query = new StringBuilder("SELECT ");
            int numberOfColumns = -1;
            if (attributes == null) {
                numberOfColumns = getTableNumberOfColumns(tableName);
                query.append("*");
            } else {
                numberOfColumns = attributes.size();
                for (String attribute : attributes) {
                    query.append(attribute + ", ");
                }
                query.setLength(query.length() - 2);
            }
            if (numberOfColumns == -1) {
                return null;
            }
            query.append(" FROM " + tableName);
            if (whereClause != null) {
                query.append(" WHERE " + whereClause);
            }
            if (havingClause != null) {
                query.append(" HAVING " + havingClause);
            }
            if (groupByClause != null) {
                query.append(" GROUP BY " + groupByClause);
            }
            if (orderByClause != null) {
                query.append(" ORDER BY " + orderByClause);
            }
            if (Constants.DEBUG) {
                System.out.println("query: " + query.toString());
            }
            databaseContent = new ArrayList<>();
            ResultSet result = statement.executeQuery(query.toString());
            int currentRow = 0;
            while (result.next()) {
                databaseContent.add(new ArrayList<String>());
                for (int currentColumn = 0; currentColumn < numberOfColumns; currentColumn++) {
                    databaseContent.get(currentRow).add(result.getString(currentColumn + 1));
                }
                currentRow++;
            }
        } catch (SQLException sqlException) {
            System.out.println("An exception has occurred: " + sqlException.getMessage());
            if (Constants.DEBUG) {
                sqlException.printStackTrace();
            }
        } finally {
            destroyStatement(statement);
        }
        return databaseContent;
    }

    public int insertValuesIntoTable(String tableName, ArrayList<String> attributes, ArrayList<String> values,
            boolean skipPrimaryKey) throws SQLException, DatabaseException {
        openConnection();
        Statement statement = createStatement();
        int result = -1;
        try {
            StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
            if (attributes == null) {
                attributes = getTableColumns(tableName);
                if (skipPrimaryKey) {
                    attributes.remove(0);
                }
            }
            if (attributes.size() != values.size()) {
                throw new DatabaseException("The number of attributes (" + attributes.size()
                        + ") does not match the number of values (" + values.size() + ")");
            }
            for (String attribute : attributes) {
                query.append(attribute + ", ");
            }
            query.setLength(query.length() - 2);
            query.append(") VALUES (");
            for (String currentValue : values) {
                query.append("\'" + currentValue + "\',");
            }
            query.setLength(query.length() - 1);
            query.append(")");
            if (Constants.DEBUG) {
                System.out.println("query: " + query.toString());
            }
            result = statement.executeUpdate(query.toString());
        } catch (SQLException sqlException) {
            System.out.println("An exception has occurred: " + sqlException.getMessage());
            if (Constants.DEBUG) {
                sqlException.printStackTrace();
            }
        } finally {
            destroyStatement(statement);
        }
        return result;
    }

    public int updateRecordsIntoTable(String tableName, ArrayList<String> attributes, ArrayList<String> values,
            String whereClause) throws SQLException, DatabaseException {
        openConnection();
        Statement statement = createStatement();
        int result = -1;
        try {
            StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
            if (attributes == null) {
                attributes = getTableColumns(tableName);
            }
            if (attributes.size() != values.size()) {
                throw new DatabaseException("The number of attributes (" + attributes.size()
                        + ") does not match the number of values (" + values.size() + ")");
            }
            for (int currentIndex = 0; currentIndex < values.size(); currentIndex++) {
                query.append(attributes.get(currentIndex) + "=" + values.get(currentIndex) + ", ");
            }
            query.setLength(query.length() - 2);
            if (whereClause != null) {
                query.append(" WHERE " + whereClause);
            }
            if (Constants.DEBUG) {
                System.out.println("query: " + query);
            }
            result = statement.executeUpdate(query.toString());
        } catch (SQLException sqlException) {
            System.out.println("An exception has occurred: " + sqlException.getMessage());
            if (Constants.DEBUG) {
                sqlException.printStackTrace();
            }
        } finally {
            destroyStatement(statement);
        }
        return result;
    }

    public int deleteRecordsFromTable(String tableName, ArrayList<String> attributes, ArrayList<String> values,
            String whereClause) throws SQLException, DatabaseException {
        int result = -1;
        // TODO: exercise 5a
        return result;
    }

    public boolean executeQuery(String query) throws SQLException {
        Statement statement = null;
        boolean result = false;
        try {
            statement = createStatement();
            result = statement.execute(query);
            if (Constants.DEBUG) {
                System.out.println("Query has been executed successfully");
            }
        } catch (SQLException sqlException) {
            System.out.println("Query has not been executed successfully: " + sqlException.getMessage());
            if (Constants.DEBUG) {
                sqlException.printStackTrace();
            }
        } finally {
            destroyStatement(statement);
        }
        return result;
    }

    public ArrayList<String> executeStoredRoutine(String storedRoutineName, ArrayList<String> parameterTypes,
            ArrayList<String> inputParameterValues, ArrayList<Integer> outputParameterDataTypes) throws SQLException {
        // TODO: exercise 7
        return null;
    }

    public ArrayList<Referrence> getReferrences(String tableName) throws SQLException {
        // TODO: exercise 8
        return null;
    }

    public void runScript(String fileName) throws SQLException {
        openConnection();
        Charset charset = Charset.forName("ISO-8859-1");
        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(fileName), charset)) {
            String currentLine;
            StringBuffer command = new StringBuffer();
            boolean insideStoredRoutine = false, insideComment = false;
            while ((currentLine = bufferedReader.readLine()) != null) {
                if ((!insideComment && currentLine.startsWith("/*")) || (insideComment && currentLine.endsWith("*/"))) {
                    insideComment = !insideComment;
                    continue;
                }
                if (currentLine == null || currentLine.isEmpty() || currentLine.startsWith("--")) {
                    continue;
                }
                if (currentLine.endsWith("//")) {
                    insideStoredRoutine = !insideStoredRoutine;
                    if (!insideStoredRoutine) {
                        command.append(currentLine.substring(0, currentLine.indexOf("//")).trim() + " ");
                    }
                } else {
                    command.append(currentLine.trim() + " ");
                }
                if ((currentLine.endsWith(";") || currentLine.endsWith("//")) && !insideStoredRoutine) {
                    if (Constants.DEBUG) {
                        System.out.println("Executing command: " + command.toString());
                    }
                    Statement statement = null;
                    try {
                        statement = createStatement();
                        statement.execute(command.toString());
                        if (Constants.DEBUG) {
                            System.out.println("Command has been executed successfully");
                        }
                    } catch (SQLException sqlException) {
                        System.out.println("Command has not been executed successfully: " + sqlException.getMessage());
                        if (Constants.DEBUG) {
                            sqlException.printStackTrace();
                        }
                    } finally {
                        destroyStatement(statement);
                    }
                    command = new StringBuffer();
                }
            }
        } catch (Exception exception) {
            System.out.println("An exception has occured: " + exception.getMessage());
            if (Constants.DEBUG) {
                exception.printStackTrace();
            }
        }
    }

    private void setAutoCommit(boolean autoCommit) throws SQLException {
        openConnection();
        connection.setAutoCommit(autoCommit);
    }

    public void startTransaction() throws SQLException {
        setAutoCommit(false);
    }

    public void endTransactionWithCommit() throws SQLException {
        try {
            connection.commit();
        } catch (SQLException sqlException) {
            System.out.println("An exception has occurred: " + sqlException.getMessage());
            if (Constants.DEBUG) {
                sqlException.printStackTrace();
            }
        } finally {
            setAutoCommit(true);
        }
    }

    public void endTransactionWithRollback() throws SQLException {
        try {
            connection.rollback();
        } catch (SQLException sqlException) {
            System.out.println("An exception has occurred: " + sqlException.getMessage());
            if (Constants.DEBUG) {
                sqlException.printStackTrace();
            }
        } finally {
            setAutoCommit(true);
        }
    }

}
