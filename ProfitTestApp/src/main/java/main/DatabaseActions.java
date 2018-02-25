package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class DatabaseActions {
	
	public void createTableifNeeded(Connection conn) {
		PreparedStatement pstmt = null; 
		try {
			String sql =  "CREATE TABLE IF NOT EXISTS Customer ( " +
					"ID INT PRIMARY KEY AUTO_INCREMENT, " +
					"FirstName VARCHAR(255), " + 
					"LastName VARCHAR(255), " + 
					"DateofBirth DATE, " +
					"Username VARCHAR(255)," + 
					"Password VARCHAR(255))";
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void readTable(Connection conn, HttpServletRequest request) {
		PreparedStatement pstmt = null; 
		ResultSet rs = null;
		ArrayList<String[]> tableData = new ArrayList<String[]>();
		try {
			String sql = "SELECT * FROM Customer";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
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
		PreparedStatement pstmt = null;
		try {
			String sql = "INSERT INTO Customer(FirstName, LastName, DateofBirth, Username, Password) "
					+ "VALUES(?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, firstname);
			pstmt.setString(2, lastname);
			pstmt.setString(3, dateofbirth);
			pstmt.setString(4, username);
			pstmt.setString(5, password);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void updateCustomer(Connection conn, String id, String firstname, String lastname, String dateofbirth, String username,
	String password) {
		PreparedStatement pstmt = null;
		try {
			String sql = "UPDATE Customer SET FirstName = ?, LastName = ?, DateofBirth = ?," +
					" Username = ?, Password = ? WHERE ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, firstname);
			pstmt.setString(2, lastname);
			pstmt.setString(3, dateofbirth);
			pstmt.setString(4, username);
			pstmt.setString(5, password);
			pstmt.setString(6, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteCustomer(Connection conn, String id) {
		PreparedStatement pstmt = null;
		try {
			String sql = "DELETE FROM Customer WHERE ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int checkDataCorrectness(String firstname, String lastname, String dateofbirth, String username,
			String password) {
		Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher dateMatcher = datePattern.matcher(dateofbirth);
        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
        Matcher passwordMatcher = passwordPattern.matcher(password);
        if(firstname.length() > 0 && lastname.length() > 0 && username.length() > 0
        		&& password.length() > 0) {
        	if(dateMatcher.matches()) {
        		if(passwordMatcher.matches()) {
		        	int year = Calendar.getInstance().get(Calendar.YEAR);
		        	int birthYear = Integer.parseInt(dateofbirth.subSequence(0, 4).toString());
		        	int birthMonth = Integer.parseInt(dateofbirth.subSequence(5, 7).toString());
		            int birthDay = Integer.parseInt(dateofbirth.subSequence(9, 10).toString());
		        	if( birthYear >= 1900 && birthYear <= year && birthMonth > 0 && birthMonth <= 12 && birthDay > 0 && birthDay <= 31) {
		        		return 0;
		        	}
        		} else {
        			return -3;
        		}
        	} else {
        		if(passwordMatcher.matches()) {
        			return -1;
        		} else {
        			return -2;
        		}
        	}
        } 
		return -4;
	}
}
