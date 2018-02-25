package main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class DatabaseActions {
	
	public final String SALT = "zombies";
	
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
	
	public ArrayList<String[]> readTable(Connection conn, HttpServletRequest request) {
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
		return tableData;
	}
	
	public void insertCustomer(Connection conn, String firstname, String lastname, String dateofbirth, String username,
			String password) {
		PreparedStatement pstmt = null;
		try {
			String saltedPassword = SALT + password;
			String hashedPassword = generateHash(saltedPassword);
			String sql = "INSERT INTO Customer(FirstName, LastName, DateofBirth, Username, Password) "
					+ "VALUES(?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, firstname);
			pstmt.setString(2, lastname);
			pstmt.setString(3, dateofbirth);
			pstmt.setString(4, username);
			pstmt.setString(5, hashedPassword);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void updateCustomer(Connection conn, String id, String firstname, String lastname, String dateofbirth, String username,
	String password) {
		PreparedStatement pstmt = null;
		try {
			String hashedPassword = null;
			String sql;
			if(password.length() == 0) {
				sql = "UPDATE Customer SET FirstName = ?, LastName = ?, DateofBirth = ?," +
						" Username = ? WHERE ID = ?";
			} else {
				String saltedPassword = SALT + password;
				hashedPassword = generateHash(saltedPassword);
				sql = "UPDATE Customer SET FirstName = ?, LastName = ?, DateofBirth = ?," +
						" Username = ?, Password = ? WHERE ID = ?";
			}
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, firstname);
			pstmt.setString(2, lastname);
			pstmt.setString(3, dateofbirth);
			pstmt.setString(4, username);
			if(password.length() == 0) {
				pstmt.setString(5, id);
			} else {
				pstmt.setString(5, hashedPassword);
				pstmt.setString(6, id);
			}
			
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
        if(firstname.length() > 0 && lastname.length() > 0 && username.length() > 0) {
        	if(dateMatcher.matches()) {
        		boolean isCorrectDate = false;
	        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    		sdf.setLenient(false);
	    		Date date = null;
	    		try {
					date = sdf.parse(dateofbirth);
					isCorrectDate = true;
				} catch (ParseException e) {
					isCorrectDate = false;
				}
	        	int birthYear = Integer.parseInt(dateofbirth.subSequence(0, 4).toString());
        		if(birthYear >= 1900 && isCorrectDate && date.before(new Date())) {
		        	if( passwordMatcher.matches()) {
		        		return 0;
		        	} else {
		        		return -4;
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
		return -5;
	}
	
	public String generateHash(String input) {
		StringBuilder hash = new StringBuilder();
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] hashedBytes = sha.digest(input.getBytes());
			char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
					'a', 'b', 'c', 'd', 'e', 'f' };
			for (int i = 0; i < hashedBytes.length; i++) {
				byte b = hashedBytes[i];
				hash.append(digits[(b & 0xf0) >> 4]);
				hash.append(digits[b & 0x0f]);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash.toString();
	}
}
