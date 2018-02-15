package main;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DatabaseActionsTests {
	
	private static Connection conn = null; 
	private DatabaseActions dba = new DatabaseActions();
	private Statement stmt = null; 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/test";  
	static final String USER = "sa"; 
	static final String PASS = "";
	
	private String firstname = "Test";
	private String lastname = "Customer";
	private String dateofbirth = "2018-02-15";
	private String username = "Test";
	private String password = "Cust";
	
	@BeforeClass
	public static void init() {
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
    public void testTableIsCreated() {
        dba.createTableifNeeded(conn);
        String sql = "SELECT COUNT(*) AS count FROM information_schema.tables WHERE table_name = 'CUSTOMER'";
        int tableCount = 0;
        try {
			stmt = conn.createStatement();
			ResultSet count = stmt.executeQuery(sql);
			while(count.next()) {
				tableCount = count.getInt("COUNT");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        assertEquals(1, tableCount);
	}
	
	@Test
	public void testCustomerIsInserted() {
        
        dba.insertCustomer(conn, firstname, lastname, dateofbirth, username, password);
        
        String sql = "SELECT COUNT(*) AS count FROM Customer WHERE firstname = 'Test'";
        int customerCount = 0;
        try {
			stmt = conn.createStatement();
			ResultSet count = stmt.executeQuery(sql);
			while(count.next()) {
				customerCount = count.getInt("COUNT");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        assertEquals(1, customerCount);
	}
	
	@Test
	public void testUpdateCustomer() {
		String sqlId = "SELECT ID FROM Customer WHERE firstname ='Test'";
		String sqlUpdated = "SELECT LastName FROM Customer WHERE firstname ='Test'";
		String customerLastName = null;
		String newLastName = "Changed";
		try {
			stmt = conn.createStatement();
			ResultSet customer = stmt.executeQuery(sqlId);
			String customerId = null;
			while(customer.next()) {
				customerId = customer.getString("ID");
			}
			dba.updateCustomer(conn, customerId, firstname, newLastName, dateofbirth, username, password);
			ResultSet changedCustomer = stmt.executeQuery(sqlUpdated);
			while(changedCustomer.next()) {
				customerLastName = changedCustomer.getString("LastName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(newLastName, customerLastName);
	}
	
	@Test
	public void testDeleteCustomer() {
		String sql = "SELECT ID FROM Customer WHERE firstname ='Test'";
		String customerId = null;
        try {
			stmt = conn.createStatement();
			ResultSet customer = stmt.executeQuery(sql);
			customerId = null;
			while(customer.next()) {
				customerId = customer.getString("ID");
			}
			dba.deleteCustomer(conn, customerId);
			customer = stmt.executeQuery(sql);
			customerId = null;
			while(customer.next()) {
				customerId = customer.getString("ID");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        assertEquals(null, customerId);
	}
	
	@Test
	public void testCheckDataInvalid() {
		String invalidDateofbirth = "2012-33-55";
		boolean correctness = dba.checkDataCorrectness(firstname, lastname, invalidDateofbirth, username, password);
		assertEquals(false, correctness);
	}
	
	@Test
	public void testCheckDataValid() {
		boolean correctness = dba.checkDataCorrectness(firstname, lastname, dateofbirth, username, password);
		assertEquals(true, correctness);
	}
	
	@AfterClass
	public static void closeDatabase() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
