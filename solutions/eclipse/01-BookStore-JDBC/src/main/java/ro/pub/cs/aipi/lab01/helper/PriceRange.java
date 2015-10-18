package ro.pub.cs.aipi.lab01.helper;

import java.sql.SQLException;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.Predicate;

import ro.pub.cs.aipi.lab01.general.Constants;

public class PriceRange implements Predicate {

	private float lowValue, highValue;
	private String attributeName = null;
	private int attributeIndex = -1;

	public PriceRange(float lowValue, float highValue, String attributeName) {
		this.lowValue = lowValue;
		this.highValue = highValue;
		this.attributeName = attributeName;
	}

	public PriceRange(float lowValue, float highValue, int attributeIndex) {
		this.lowValue = lowValue;
		this.highValue = highValue;
		this.attributeIndex = attributeIndex;
	}

	@Override
	public boolean evaluate(Object value, String attributeName) {
		boolean result = true;
		if (attributeName.equalsIgnoreCase(this.attributeName)) {
			Object attributeValue = value;
			if (((Float) attributeValue).floatValue() >= this.lowValue
					&& ((Float) attributeValue).floatValue() <= this.highValue) {
				return true;
			}
			return false;
		}
		return result;
	}

	@Override
	public boolean evaluate(Object value, int attributeIndex) {
		boolean result = true;
		if (attributeIndex == this.attributeIndex) {
			Object attributeValue = value;
			if (((Float) attributeValue).floatValue() >= this.lowValue
					&& ((Float) attributeValue).floatValue() <= this.highValue) {
				return true;
			}
			return false;
		}
		return result;
	}

	@Override
	public boolean evaluate(RowSet rowSet) {
		boolean result = false;
		try {
			CachedRowSet cachedRowSet = (CachedRowSet) rowSet;
			Object attributeValue = null;
			if (!cachedRowSet.isAfterLast()) {
				if (this.attributeName != null) {
					attributeValue = cachedRowSet.getObject(this.attributeName);
				} else if (this.attributeIndex > 0) {
					attributeValue = cachedRowSet.getObject(this.attributeIndex);
				} else
					return false;
			}
			if (attributeValue != null && ((Float) attributeValue).floatValue() >= this.lowValue
					&& ((Float) attributeValue).floatValue() <= this.highValue) {
				result = true;
			}
		} catch (Exception exception) {
			System.out.println("An exception has occurred: " + exception.getMessage());
			if (Constants.DEBUG) {
				exception.printStackTrace();
			}
			if (exception instanceof SQLException) {
				System.out.println(((SQLException) exception).getSQLState() + " " + exception.getCause());
			}
		}
		return result;
	}
}
