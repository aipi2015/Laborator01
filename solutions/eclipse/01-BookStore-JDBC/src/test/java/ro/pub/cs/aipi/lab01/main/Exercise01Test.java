package ro.pub.cs.aipi.lab01.main;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class Exercise01Test {

	final private static HashMap<String, Integer> tableNumberOfRecords = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 20152015L;

		{
			put("author", 3603);
			put("book", 2382);
			put("book_presentation", 7028);
			put("category", 156);
			put("category_content", 4749);
			put("collection", 160);
			put("country", 240);
			put("format", 30);
			put("invoice_header", 2000);
			put("invoice_line", 11076);
			put("language", 185);
			put("publishing_house", 547);
			put("supply_order_header", 2000);
			put("supply_order_line", 11124);
			put("user", 2000);
			put("writer", 2586);
		}
	};

	private String table;

	public Exercise01Test(String table) {
		this.table = table;
	}

	@Parameters
	public static Collection<Object[]> data() {
		ArrayList<Object[]> data = new ArrayList<>();
		Set<String> tables = tableNumberOfRecords.keySet();
		Iterator<String> tablesIterator = tables.iterator();
		while (tablesIterator.hasNext()) {
			String table = tablesIterator.next();
			data.add(new Object[] { table });
		}
		return data;
	}

	@Test
	public void checkNumberOfRecords() {
		System.out.println("Test01 -SELECT- checkNumberOfRecords - parameter: " + table);
		BookStore bookstore = new BookStore();
		int numberOfRecords = tableNumberOfRecords.get(table);
		assertEquals("Table " + table + " should have " + numberOfRecords + " records!", numberOfRecords,
				bookstore.exercise01(table));
		System.out.println("Test passed!");
	}

}
