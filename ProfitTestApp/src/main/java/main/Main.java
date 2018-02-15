package main;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class Main extends HttpServlet{
	private static final long serialVersionUID = 16L;
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/profitApp";  
	static final String USER = "admin"; 
	static final String PASS = "";
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		Connection conn = null; 
	    DatabaseActions dba = new DatabaseActions();
		try {
			Class.forName(JDBC_DRIVER);
	        conn = DriverManager.getConnection(DB_URL, USER, PASS);
	        
	        
	        dba.createTableifNeeded(conn);
	        dba.readTable(conn, request);
	        
	        conn.close();
	        RequestDispatcher dispatcher 
	         = this.getServletContext().getRequestDispatcher("/WEB-INF/views/index.jsp");

			 dispatcher.forward(request, response);
	        
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() throws ServletException {
		System.out.println("Servlet " + this.getServletName() + " has started");
	}

	@Override
	public void destroy() {
		System.out.println("Servlet " + this.getServletName() + " has stopped");
	}
}
