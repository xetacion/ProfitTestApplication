package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class DatabaseActions {
	
	public void createTableifNeeded(Connection conn) {
		Statement stmt = null; 
		try {
			stmt = conn.createStatement(); 
			String sql =  "CREATE TABLE IF NOT EXISTS Customer ( " +
					"ID INT PRIMARY KEY AUTO_INCREMENT, " +
					"FirstName VARCHAR(255), " + 
					"LastName VARCHAR(255), " + 
					"DateofBirth DATE, " +
					"Username VARCHAR(255)," + 
					"Password VARCHAR(255))";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void readTable(Connection conn, HttpServletRequest request) {
		Statement stmt = null; 
		ResultSet rs = null;
		ArrayList<String[]> tableData = new ArrayList<String[]>();
		try {
			stmt = conn.createStatement(); 
			String sql = "SELECT * FROM Customer"; 
			rs = stmt.executeQuery(sql);
			Format formatter = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()) { 
	            String id  = Integer.toString(rs.getInt("ID")); 
	            String firstname = rs.getString("FirstName"); 
	            String lastname = rs.getString("LastName"); 
	            String date = formatter.format(rs.getDate("DateofBirth"));
	            String username = rs.getString("Username"); 
	            String password = rs.getString("Password");
	            String[] row = {id, firstname, lastname, date, username, password};
	            tableData.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.setAttribute("tableData", tableData);
	}
	
	public void insertCustomer(Connection conn, String firstname, String lastname, String dateofbirth, String username,
			String password) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sql = "INSERT INTO Customer(FirstName, LastName, DateofBirth, Username, Password) "
					+ "VALUES('" + firstname + "','" + lastname + "','" + dateofbirth + "','" + username + "','" + password + "')";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void updateCustomer(Connection conn, String id, String firstname, String lastname, String dateofbirth, String username,
	String password) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sql = "UPDATE Customer SET FirstName = '" + firstname + "', LastName = '" + lastname + "', DateofBirth = '" + dateofbirth + 
					"', Username = '" + username + "', Password = '" + password + "' WHERE ID = " + id;
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteCustomer(Connection conn, String id) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sql = "DELETE FROM Customer WHERE ID = " + id;
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean checkDataCorrectness(String firstname, String lastname, String dateofbirth, String username,
			String password) {
		Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher m = p.matcher(dateofbirth);
        if(firstname.length() > 0 && lastname.length() > 0 && username.length() > 0
        		&& password.length() > 0 && m.matches()) {
        	int birthMonth = Integer.parseInt(dateofbirth.subSequence(5, 7).toString());
            int birthDay = Integer.parseInt(dateofbirth.subSequence(9, 10).toString());
        	if(birthMonth > 0 && birthMonth <= 12 && birthDay > 0 && birthDay <= 31) {
        		return true;
        	}
        } 
		return false;
	}
}
