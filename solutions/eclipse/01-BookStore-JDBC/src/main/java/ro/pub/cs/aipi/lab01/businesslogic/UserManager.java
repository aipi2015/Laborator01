package ro.pub.cs.aipi.lab01.businesslogic;

import java.sql.SQLException;
import java.util.ArrayList;

import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperations;
import ro.pub.cs.aipi.lab01.dataaccess.DatabaseOperationsImplementation;
import ro.pub.cs.aipi.lab01.general.Constants;

public class UserManager extends EntityManager {

	public UserManager() {
		table = "user";
	}

	public ArrayList<ArrayList<String>> getUserListWithTotalInvoiceAmount() {
		DatabaseOperations databaseOperations = null;
		try {
			databaseOperations = DatabaseOperationsImplementation.getInstance();
			ArrayList<ArrayList<String>> result = new ArrayList<>();
			ArrayList<String> userAttributes = new ArrayList<>();
			userAttributes.add("u.personal_identifier");
			int PERSONAL_IDENTIFIER_INDEX = 0;
			ArrayList<ArrayList<String>> tableContent = databaseOperations.getTableContent("user u", userAttributes,
					null, null, "u.id", null);
			for (int currentIndex = 0; currentIndex < tableContent.size(); currentIndex++) {
				ArrayList<String> currentRecord = new ArrayList<>();
				String personalIdentifier = tableContent.get(currentIndex).get(PERSONAL_IDENTIFIER_INDEX).toString();
				ArrayList<String> parameterTypes = new ArrayList<>();
				parameterTypes.add("IN");
				parameterTypes.add("OUT");
				ArrayList<String> inputParameterValues = new ArrayList<>();
				inputParameterValues.add(personalIdentifier);
				ArrayList<Integer> outputParameterDataTypes = new ArrayList<>();
				outputParameterDataTypes.add(java.sql.Types.FLOAT);
				String userTotalInvoiceValue = databaseOperations
						.executeStoredRoutine("calculate_user_total_invoice_value", parameterTypes,
								inputParameterValues, outputParameterDataTypes)
						.get(0);
				currentRecord.add(personalIdentifier);
				if (userTotalInvoiceValue != null) {
					currentRecord.add(userTotalInvoiceValue);
				} else {
					currentRecord.add("0.00");
				}
				result.add(currentRecord);
			}
			return result;
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
